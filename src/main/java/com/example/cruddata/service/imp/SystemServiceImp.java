package com.example.cruddata.service.imp;

import com.example.cruddata.config.MultiDataSourceManager;
import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.dto.business.ColumnValidatorResultData;
import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.repository.system.TableConfigRepository;
import com.example.cruddata.service.DataService;
import com.example.cruddata.service.SystemService;
import com.example.cruddata.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemServiceImp implements SystemService {

    private static final Logger log = LoggerFactory.getLogger(SystemServiceImp.class);

    @Autowired
    ColumnConfigRepository columnConfigRepository;
    @Autowired
    DataSourceConfigRepository dataSourceConfigRepository;
    @Autowired
    TableConfigRepository tableConfigRepository;

    @Autowired
    DataService dataService;

    @Autowired
    TenantService tenantService;

    @Autowired
    MultiDataSourceManager dataSourceManager;



    /*
    * 1. confirm data 是否存在:
    *   - 所有資料比較完成，比較欄位， ID + tenantId + tableId + isDeleted
    *   - loop 取得目前 list 的 distinct table + tenantId
    *   - get columns from distinct table + tenantId and isDeleted false
    *   - 比較資料
    * 1.1. 若資料，有一筆不存在，整筆資料不進行操作
    * 1.2. 若資料都存在，將存在的資料做刪除
    *  */
    @Override
    public void deleteColumnConfig(Long dataSourceId,List<ColumnConfig> recordList) throws SQLException {
        Map<Long,Map<Long,List<ColumnConfig>>> ColumnConfigByTableIdTable = new HashMap<>();

        recordList.forEach(item->{
            if (!ColumnConfigByTableIdTable.containsKey(item.getTenantId())){
                ColumnConfigByTableIdTable.put(item.getTenantId() , new HashMap<>());
            }
            if(!ColumnConfigByTableIdTable.get(item.getTenantId()).containsKey(item.getTableId())){
                ColumnConfigByTableIdTable.get(item.getTenantId()).put(item.getTableId(),new ArrayList<>());
            }
            ColumnConfigByTableIdTable.get(item.getTenantId()).get(item.getTableId()).add(item);
        });


        ColumnValidatorResultData columnValidatorResult =columnsDataValidationProcess(ColumnConfigByTableIdTable);

        if(!columnValidatorResult.isValidateCorrect()){
            Map<String,Object> errorMsg = new HashMap<>();
            errorMsg.put(ApiErrorCode.COLUMNS_OF_TABLE_DELETE_NOT_CLEAR,columnValidatorResult.getTotalColumnsBySituationTableTenant());
            errorMsg.put(ApiErrorCode.TENANT_TABLE_NOT_EXITS_ERROR,columnValidatorResult.getNotExitTableByTenant());
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , errorMsg);
        }

        // delete column
        Map<String ,List<String>> dropColumns = new HashMap<>();
        List<ColumnConfig> maintain = new ArrayList<>();

        columnValidatorResult.getExistColumnsByTableName().forEach
                ((tableName,item)->{item.forEach(column -> {
                    column.setIsDeleted(true);
                    maintain.add(column);
                    if(!dropColumns.containsKey(tableName)){
                        dropColumns.put(tableName, new ArrayList<>());
                    }
                    dropColumns.get(tableName).add(column.getName());
                });
                });

        dataService.dropColumns(dataSourceId,dropColumns);
        this.columnConfigRepository.saveAll(maintain);


    }

    private ColumnValidatorResultData columnsDataValidationProcess(Map<Long, Map<Long, List<ColumnConfig>>> ColumnConfigByTableIdTable) {
        ColumnValidatorResultData columnValidatorResult = new ColumnValidatorResultData();
        Map<Long,List<String>> notExitTableByTenant = new HashMap<>();
        Map<Long,Map<String,Map<String,List<String>>>> totalColumnsBySituationTableTenant  = new HashMap<>();
        columnValidatorResult.setTotalColumnsBySituationTableTenant(totalColumnsBySituationTableTenant );
        columnValidatorResult.setNotExitTableByTenant(notExitTableByTenant);
        columnValidatorResult.setExistColumnsByTableName(new HashMap<>());

        ColumnConfigByTableIdTable.forEach((key, record)->{

            List<Long> tableIdList = record.keySet().stream().toList();
            tableConfigRepository.findAllById(tableIdList).forEach(table->{
                /* 判斷此客戶是否擁有這些表的權限 */
                if (!key.equals(table.getTenantId())){
                    if(!notExitTableByTenant.containsKey(table.getTenantId())){
                        notExitTableByTenant.put(key,new ArrayList<>());
                    }
                    notExitTableByTenant.get(table.getTenantId()).add(table.getName());
                    columnValidatorResult.setValidateCorrect(false);
                }

                /* 判斷此客戶的表下，確認欄位是否都刪除，若未都刪除，不給刪除 */
                if(key.equals(table.getTenantId()) &&  !table.getIsDeleted()){
                    List<ColumnConfig> columnUsedInDb =this.columnConfigRepository.findByTenantIdAndIsDeletedAndTableId(table.getTenantId(),Boolean.FALSE,table.getId()).stream().filter(columnConfig -> !columnConfig.getIsDeleted()).collect(Collectors.toList());
                    List<ColumnConfig> userDeleteColumns = ColumnConfigByTableIdTable.get(table.getTenantId()).get(table.getId());

                    if(!columnUsedInDb.retainAll(userDeleteColumns)){
                        if(!totalColumnsBySituationTableTenant.containsKey(table.getTenantId())){
                            totalColumnsBySituationTableTenant.put(table.getTenantId(),new HashMap<>());
                        }
                        if(!totalColumnsBySituationTableTenant.get(table.getTenantId()).containsKey(table.getName())){
                            totalColumnsBySituationTableTenant.get(table.getTenantId()).put(table.getName(),new HashMap<>());
                        }
                        totalColumnsBySituationTableTenant.get(table.getTenantId()).get(table.getName()).put("columnUsedInDb",columnUsedInDb.stream()
                                .map(ColumnConfig::getName)
                                .collect(Collectors.toList()));
                        totalColumnsBySituationTableTenant.get(table.getTenantId()).get(table.getName()).put("userDeleteColumns",userDeleteColumns.stream()
                                .map(ColumnConfig::getName)
                                .collect(Collectors.toList()));
                        columnValidatorResult.setValidateCorrect(false);
                    }else {
                        columnValidatorResult.getExistColumnsByTableName().put(table.getName(),columnUsedInDb);
                    }

                }
            });
        });
        return columnValidatorResult;
    }



    @Override
    public void createTable(CreateEntityData createEntity, Long tenantId) throws SQLException {
        log.info("process createTable start!");

        tenantService.validatedTenantProcess(tenantId);

        Optional<DataSourceConfig> dataSourceConfig = this.dataSourceConfigRepository.findByIdAndTenantIdAndIsDeleted(createEntity.getDatasourceId(),tenantId,Boolean.FALSE);
        if(!dataSourceConfig.isPresent()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "datasource is not exist or you cant use this datasource");
        }


       List<TableConfig> tables =this.tableConfigRepository.findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(createEntity.getTableName(),tenantId,Boolean.FALSE,createEntity.getDatasourceId());
       if(tables.size() > 0){
           throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "table name is exist in this datasource",tables);
       }

        log.info("process data validator done!");

        dataService.createTable(dataSourceConfig.get().getId() , dataSourceConfig.get().getDatabaseType() , createEntity);

        log.info("process create physical table done!");

        TableConfig tableConfig = new TableConfig();
        tableConfig.setCaption(createEntity.getCaption());
        tableConfig.setName(createEntity.getTableName());
        tableConfig.setDataSourceId(createEntity.getDatasourceId());
        tableConfig.setTenantId(tenantId);
        this.tableConfigRepository.save(tableConfig);

        log.info("process insert tableConfig date done!");

        List<ColumnConfig> columnConfigs = new ArrayList<>();
        createEntity.columnEntityList.forEach(item->{
            ColumnConfig record = new ColumnConfig();
            record.setCaption(item.getCaption());
            record.setName(item.getName());
            record.setTableId(tableConfig.getId());
            record.setTenantId(tenantId);
            record.setDataType(item.getDataType());
            record.setIsDeleted(Boolean.FALSE);
            columnConfigs.add(record);
        });
        this.columnConfigRepository.saveAll(columnConfigs);
        log.info("process insert columnConfigs done!");

        log.info("process createTable end!");
    }


    @Override
    public List<TableConfig> getTableConfigs(Long dataSourceId, String tableName, Long tenantId) {
        tenantService.validatedTenantProcess(tenantId);

        Optional<DataSourceConfig> dataSourceConfig = this.dataSourceConfigRepository.findByIdAndTenantIdAndIsDeleted(dataSourceId, tenantId,Boolean.FALSE);
        if(!dataSourceConfig.isPresent()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "datasource is not exist or you cant use this datasource");
        }

        List<TableConfig> tables =this.tableConfigRepository.findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(tableName, tenantId,Boolean.FALSE, dataSourceId);
        if(tables.size() != 1 && null != tableName){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "table name  multiple in this datasource",tables);
        }
        return tables;
    }


}
