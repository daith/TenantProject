package com.example.cruddata.service.imp;

import com.example.cruddata.constant.Status;
import com.example.cruddata.dto.web.RoleFunctionData;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.TableConfigRepository;
import com.example.cruddata.service.DataService;
import com.example.cruddata.service.DataSourceService;
import com.example.cruddata.service.RoleService;
import com.example.cruddata.service.TableColumnService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TableColumnServiceImp implements TableColumnService{

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);

    public final ColumnConfigRepository columnConfigRepository;

    public final TableConfigRepository tableConfigRepository;

    public final RoleService roleService;

    public final DataService dataService;

    public final DataSourceService dataSourceService;

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
            List<RoleFunction>  roleFunctions =  this.roleService.getRoleFunctionsByTenantIdAndNameAndDataSource(tenantId ,tableName , dataSourceId );

            if(roleFunctions.size()>0){
                throw new BusinessException("table set role auth {}!",roleFunctions);
            }

            DataSourceConfig dataSourceConfig = dataSourceService.getDataSourceById(dataSourceId);
            if(Boolean.FALSE == dataSourceConfig.getIsDeleted() && dataSourceConfig.getStatus().equals(Status.ACTIVE) && dataSourceConfig.getTenantId() == tenantId){
                deleteTableProcess(tenantId, dataSourceId, tableName, tableConfig, dataSourceConfig);
            }

        }else{
            throw new BusinessException("table-{}/- in db data is not correct!",tableConfig);
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
        this.roleService.deleteFunction( tableName ,  tenantId, dataSourceId);
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
            List<RoleFunctionData> roles = roleService.getRoleFunctionsByTenantId( tenantId);
            roles.forEach(roleData->{
                if(roleData.getFunctionActions().size() >0){
                    throw new BusinessException("Pleaser remove All Role function auth setting");
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
            throw new BusinessException("the datasource info not found correct object tenantId-{} ,dataSourceId- {}" , tenantId ,dataSourceId );
        }



    }


}
