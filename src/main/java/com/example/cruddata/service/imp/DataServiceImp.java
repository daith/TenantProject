package com.example.cruddata.service.imp;

import com.example.cruddata.config.MultiDataSourceManager;
import com.example.cruddata.constant.DataSourceInfo;
import com.example.cruddata.constant.TemplateBase;
import com.example.cruddata.controller.TableController;
import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.service.DataService;
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
    MultiDataSourceManager multiDataSourceManager;
    @Autowired
    TemplateParse templateParse;


    @Override
    public List<Map<String, Object>> list() {
        return null;
    }


    @Override
    public void createTable(Long dataSource ,  String dbName  , CreateEntityData createEntity) throws SQLException {

        try{
            DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSource));

            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.CREATE_TABLE, createEntity);
            log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.CREATE_TABLE);
            PreparedStatement preparedStatement = multiDataSourceManager.dataSource().getConnection().prepareStatement(sql);
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }

    }

    @Override
    public void insertData(Long dataSource , String dbName, InsertEntityData insertDataEntity) throws SQLException {
        try{
            if(insertDataEntity.getRecordList().size()!=0){
                DataSourceInfo.setTenant(this.multiDataSourceManager,String.valueOf(dataSource));

                String sql =  templateParse.processTemplateToString(dbName , TemplateBase.INSERT_DATA, insertDataEntity);
                log.info("[i] Build SQL'{}' from template '{}'.", sql , TemplateBase.INSERT_DATA);
                PreparedStatement preparedStatement = multiDataSourceManager.dataSource().getConnection().prepareStatement(sql);
                preparedStatement.execute();
            }else {
                log.info("[insertData-i] No data need import");
            }

        }catch (SQLException e) {
            log.info("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
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
            PreparedStatement preparedStatement = multiDataSourceManager.dataSource().getConnection().prepareStatement(sqlBuilder.toString());
            preparedStatement.execute();

        }catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new SQLException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
