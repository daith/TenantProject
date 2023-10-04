package com.example.cruddata.service.imp;

import com.example.cruddata.service.CrudService;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class CrudServiceImp implements CrudService {
    @Override
    public String getSqlQuotation() {
        return null;
    }

    @Override
    public String getDateBaseName() {
        return null;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return null;
    }

    @Override
    public void execute(String sql) {

    }

    @Override
    public List<Map<String, Object>> getMetaDatas() {
        return null;
    }

    @Override
    public Map<String, Object> getMetaData(String tableName) {
        return null;
    }

    @Override
    public String processTemplateToString(String templateName, String key, Object value) {
        return null;
    }

    @Override
    public String processTemplateToString(String templateName, Object dataModel) {
        return null;
    }

    @Override
    public String processTemplateToString(String templateBase, String templateName, String key, Object value) {
        return null;
    }

    @Override
    public String processTemplateToString(String templateBase, String templateName, Map<String, Object> map) {
        return null;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) {
        return null;
    }
}
