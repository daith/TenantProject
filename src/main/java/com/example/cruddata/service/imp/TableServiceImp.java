package com.example.cruddata.service.imp;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.constant.TemplateBase;
import com.example.cruddata.controller.TableController;
import com.example.cruddata.dto.ColumnCreated;
import com.example.cruddata.dto.CreateEntityInfo;
import com.example.cruddata.service.TableService;
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
public class TableServiceImp implements TableService {

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
    public void createTable( String dbName  , CreateEntityInfo createEntityInfo ) throws SQLException {

        try{
            String sql =  templateParse.processTemplateToString(dbName , TemplateBase.CREATE_TABLE, createEntityInfo);
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
}
