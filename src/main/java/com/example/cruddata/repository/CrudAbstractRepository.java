package com.example.cruddata.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class CrudAbstractRepository {

    private static final Logger log = LoggerFactory.getLogger(CrudAbstractRepository.class);

    public static final String COLUMN_ID = "id";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public JdbcTemplate getJdbcTemplate() {
        return namedParameterJdbcTemplate.getJdbcTemplate();
    }

    public void execute(String sql) {
        namedParameterJdbcTemplate.getJdbcTemplate().execute(sql);
    }


}
