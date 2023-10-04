package com.example.cruddata.service;

import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface CrudService {

    String getSqlQuotation();

    String getDateBaseName();

    JdbcTemplate getJdbcTemplate();

    void execute(String sql);

    List<Map<String, Object>> getMetaDatas();

    Map<String, Object> getMetaData(String tableName);

    String processTemplateToString(String templateName, String key, Object value);

    String processTemplateToString(String templateName, Object dataModel);

    String processTemplateToString(String templateBase, String templateName, String key, Object value);

    String processTemplateToString(String templateBase, String templateName, Map<String, Object> map);

    List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap);

}
