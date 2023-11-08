package com.example.cruddata.service.imp;

import com.example.cruddata.config.MultiDataSourceManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.constant.TemplateBase;
import com.example.cruddata.controller.TableController;
import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.dto.web.DeleteEntityData;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.dto.web.SampleSelectionEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.service.DataService;
import com.example.cruddata.util.TemplateParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataServiceImp implements DataService {

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    @Autowired
    MultiDataSourceManager multiDataSourceManager;
    @Autowired
    TemplateParse templateParse;

    @Override
    public void createTable(Long dataSource ,  String dbName  , CreateEntityData createEntity) throws SQLException {
        log.info("[i] createTable {} start.", dbName );

        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try{
            DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSource));
            connection = multiDataSourceManager.dataSource().getConnection();

            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.CREATE_TABLE, createEntity);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            multiDataSourceManager.dataSource().getConnection().close();
            preparedStatement.closeOnCompletion();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
            log.info("[i] createTable {} end.", dbName );

        }



    }

    @Override
    public void dropTable(Long dataSource, String dbName , String tableName) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try{
            HashMap table = new HashMap();
            table.put("tableName",tableName);
            DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSource));
            connection = multiDataSourceManager.dataSource().getConnection();
            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.DROP_TABLE ,table);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.DROP_TABLE);
            preparedStatement =  connection.prepareStatement(sql);
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
        }
    }

    @Override
    public void insertData(Long dataSource , String dbName, InsertEntityData insertDataEntity) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;

        try{
            if(insertDataEntity.getRecordList().size()!=0){
                DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSource));
                connection = multiDataSourceManager.dataSource().getConnection();
                String sql =  templateParse.processTemplateToString(dbName , TemplateBase.INSERT_DATA, insertDataEntity);
                log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.INSERT_DATA);
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
            }else {
                log.info("[insertData-i] No data need import");
            }

        }catch (SQLException e) {
            log.info("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
        }
    }

    @Override
    public void dropColumns(Long dataSourceId,Map<String,List<String>> columnConfigsByTableName) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;

        try{
            DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSourceId));
            StringBuilder sqlBuilder = new StringBuilder();
            columnConfigsByTableName.forEach((tableName, columns)->{
                columns.forEach(column ->{
                    String sql =  templateParse.processTemplateToString(tableName, TemplateBase.DROP_COLUMN, column);
                    log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
                    sqlBuilder.append(sql);

                });

            });
            connection = multiDataSourceManager.dataSource().getConnection();

            log.info("[i] Build SQL'{}' from template '{}'.", sqlBuilder.toString() , TemplateBase.DROP_COLUMN);
            preparedStatement = connection.prepareStatement(sqlBuilder.toString());
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
        }

    }

    @Override
    public List<Map<String,Object>>  queryDataBySampleCondition(Long dataSourceId, String dbName, SampleSelectionEntityData entityData) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try{
            DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSourceId));

                connection = multiDataSourceManager.dataSource().getConnection();
                String sql =  templateParse.processTemplateToString(dbName , TemplateBase.SELECT_DATA, entityData);
                log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.INSERT_DATA);
                preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData meta = resultSet.getMetaData();
                while (resultSet.next()) {
                    Map map = new HashMap();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String key = meta.getColumnName(i);
                        String value = resultSet.getString(key);
                        map.put(key, value);
                    }
                    result.add(map);
                }


        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
            return result;
        }
    }

    @Override
    public void deleteData(Long dataSourceId, String dbName, DeleteEntityData records) throws SQLException {
        PreparedStatement preparedStatement = null;
        Connection connection = null;

        try{


            connection = multiDataSourceManager.dataSource().getConnection();
            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.DELETE_DATA, records);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.DELETE_DATA);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) { /* Ignored */
                }

            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) { /* Ignored */
                }
            }
        }
    }
}
