package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.Status;
import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.repository.system.TableConfigRepository;
import com.example.cruddata.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class TableColumnServiceImp implements TableColumnService{

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);
    @Autowired
    public  ColumnConfigRepository columnConfigRepository;
    @Autowired
    public  TableConfigRepository tableConfigRepository;
    @Autowired
    public  RoleService roleService;
    @Autowired
    public  DataService dataService;
    @Autowired
    public  DataSourceService dataSourceService;

    @Autowired
    DataSourceConfigRepository dataSourceConfigRepository;

    @Autowired
    public TenantService tenantService;

    @Autowired
    public FunctionService functionService;
    @Override
    public List<ColumnConfig> getActiveColumnByTenantIdAndTableId(Long tenantId, Long tableId) {
        return columnConfigRepository.findByTenantIdAndIsDeletedAndTableId(tenantId , Boolean.FALSE , tableId);
    }

    @Override
    public List<TableConfig> getActiveTablesByTenantIdAndDataSourceId(Long tenantId, Long dataSourceId) {
        return tableConfigRepository.findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(null,tenantId,Boolean.FALSE,dataSourceId);
    }

    @Override
    public void saveTableConfig(TableConfig tableConfig) {
        tableConfigRepository.save(tableConfig);


    }

    @Override
    public void saveColumnConfig(ColumnConfig columnConfig) {
        columnConfigRepository.save(columnConfig);
    }

    @Override
    public void deleteTableConfig(Long tenantId, Long dataSourceId, String tableName) throws SQLException {
        //confirm table is exits
        List<TableConfig> tableConfig =  this.tableConfigRepository.findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(tableName,tenantId,Boolean.FALSE,dataSourceId);
        if(tableConfig.size()==1){
            //confirm roleFunction
            List<RoleFunction>  roleFunctions =  this.functionService.getRoleFunctionsByTenantIdAndNameAndDataSource(tenantId ,tableName , dataSourceId );

            if(roleFunctions.size()>0){
                throw new BusinessException(ApiErrorCode.AUTH_ERROR,"table set role auth {}!",roleFunctions);
            }

            DataSourceConfig dataSourceConfig = dataSourceService.getDataSourceById(dataSourceId);
            if(Boolean.FALSE == dataSourceConfig.getIsDeleted() && dataSourceConfig.getStatus().equals(Status.ACTIVE) && dataSourceConfig.getTenantId() == tenantId){
                deleteTableProcess(tenantId, dataSourceId, tableName, tableConfig, dataSourceConfig);
            }

        }else{
            throw new BusinessException(ApiErrorCode.SQL_ERROR,"table-{}/- in db data is not correct!",tableConfig);
        }
    }

    private void deleteTableProcess(Long tenantId, Long dataSourceId, String tableName, List<TableConfig> tableConfig, DataSourceConfig dataSourceConfig) throws SQLException {



        dataService.dropTable(dataSourceId,  dataSourceConfig.getDatabaseType(), tableName) ;
        List<ColumnConfig> columnConfigs = columnConfigRepository.findByTenantIdAndIsDeletedAndTableId(tenantId,Boolean.FALSE, tenantId);
        columnConfigs.forEach(item->{
            item.setIsDeleted(Boolean.TRUE);
            item.setUpdateTime(new Date());
        });
        columnConfigRepository.saveAll(columnConfigs);
        tableConfig.forEach(item->{
            item.setIsDeleted(Boolean.TRUE);
            item.setUpdateTime(new Date());
        });
        tableConfigRepository.saveAll(tableConfig);
        this.functionService.deleteFunction( tableName ,  tenantId, dataSourceId);
    }

    private void deleteTableProcess(Long tenantId, Long dataSourceId, String tableName, TableConfig tableConfig, DataSourceConfig dataSourceConfig) throws SQLException {
        deleteTableProcess( tenantId,  dataSourceId,  tableName, Arrays.asList(tableConfig),  dataSourceConfig);
    }

    @Override
    public void deleteAllTableByDataSource(Long tenantId, Long dataSourceId) {
        DataSourceConfig dataSourceConfig = dataSourceService.getDataSourceById(dataSourceId);
        if(Boolean.FALSE == dataSourceConfig.getIsDeleted() && dataSourceConfig.getStatus().equals(Status.ACTIVE) && dataSourceConfig.getTenantId() == tenantId){
            List<TableConfig> tableConfigs =  this.tableConfigRepository.findTableConfigByNameAndTenantIdAndIsDeletedAndDataSourceId(null,tenantId,Boolean.FALSE,dataSourceId);

            // confirm table autho is in use.
            List<RoleFunctionData> roles = functionService.getRoleFunctionsByTenantId( tenantId);
            roles.forEach(roleData->{
                if(roleData.getFunctionActions().size() >0){
                    throw new BusinessException(ApiErrorCode.AUTH_ERROR,"Pleaser remove All Role function auth setting");
                }
            });

            List<String> deleteTables = new ArrayList<>();
            Integer executeProcess = 0;
            while (deleteTables.size() != tableConfigs.size() && executeProcess <= 10){
                log.info("go while process ={}=",executeProcess);
                tableConfigs.forEach((tableConfig)->{

                    try {
                        if(!deleteTables.contains(tableConfig.getName())){
                            deleteTableProcess( tenantId,  dataSourceId,  tableConfig.getName(), tableConfig,  dataSourceConfig);
                            deleteTables.add(tableConfig.getName());
                        }

                    } catch (SQLException e) {
                        log.info(e.getMessage());
                    }
                });
                executeProcess+=1;
            }
        }else {
            throw new InvalidDbPropertiesException();
        }



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

    @Override
    public TableConfig getTableConfig(Long dataSourceId, String tableName, Long tenantId) {
        List<TableConfig> result = getTableConfigs( dataSourceId,  tableName,  tenantId);
        return result.size()>0?result.get(1): null;
    }


}
