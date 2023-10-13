package com.example.cruddata.service.imp;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.TemplateBase;
import com.example.cruddata.controller.TableController;
import com.example.cruddata.dto.web.CreateEntity;
import com.example.cruddata.dto.web.InsertDataEntity;
import com.example.cruddata.service.DataService;
import com.example.cruddata.service.SystemService;
import com.example.cruddata.util.TemplateParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class DataServiceImp implements DataService {

    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    @Autowired
    MultiTenantManager multiTenantManager;
    @Autowired
    TemplateParse templateParse;


    @Override
    public List<Map<String, Object>> list() {
        return null;
    }


    @Override
    public void createTable( String dbName  , CreateEntity createEntity) throws SQLException {

        try{
            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.CREATE_TABLE, createEntity);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
            PreparedStatement preparedStatement = multiTenantManager.dataSource().getConnection().prepareStatement(sql);
            preparedStatement.execute();



        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }

    }

    @Override
    public void insertData(String dbName, List<Map<String, Object>> recordList) throws SQLException {
        try{
            InsertDataEntity insertDataEntity = new InsertDataEntity();
            insertDataEntity.setRecordList(recordList);
            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.INSERT_DATA, insertDataEntity);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
            PreparedStatement preparedStatement = multiTenantManager.dataSource().getConnection().prepareStatement(sql);
            preparedStatement.execute();
        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dropColumns(Map<String,List<String>> columnConfigsByTableName) throws SQLException {
        try{

            StringBuilder sqlBuilder = new StringBuilder();
            columnConfigsByTableName.forEach((tableName, columns)->{
                columns.forEach(column ->{
                    String sql =  templateParse.processTemplateToString(tableName, TemplateBase.DROP_COLUMN, column);
                    log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
                    sqlBuilder.append(sql);

                });

            });
            log.info("[i] Build SQL'{}' from template '{}'.", sqlBuilder.toString() , TemplateBase.DROP_COLUMN);
            PreparedStatement preparedStatement = multiTenantManager.dataSource().getConnection().prepareStatement(sqlBuilder.toString());
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
