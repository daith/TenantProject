package com.example.cruddata.constant;

import com.example.cruddata.config.MultiDataSourceManager;
import com.example.cruddata.exception.InvalidDbPropertiesException;
import com.example.cruddata.exception.InvalidTenantIdException;
import com.example.cruddata.exception.TenantNotFoundException;
import com.example.cruddata.exception.TenantResolvingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DataSourceInfo {

    private static final Logger log = LoggerFactory.getLogger(DataSourceInfo.class);

    public static final String MSG_INVALID_TENANT_ID = "[!] DataSource not found for given tenant Id '{}'!";
    public static final String MSG_INVALID_DB_PROPERTIES_ID = "[!] DataSource properties related to the given tenant ('{}') is invalid!";
    public static final String MSG_RESOLVING_TENANT_ID = "[!] Could not resolve tenant ID '{}'!";

    public  static void setTenant(MultiDataSourceManager tenantManager , String dataSourceId) {
        try {
            tenantManager.setCurrentDataSource(dataSourceId);
        } catch (SQLException e) {
            log.error(MSG_INVALID_DB_PROPERTIES_ID, dataSourceId);
            throw new InvalidDbPropertiesException();
        } catch (TenantNotFoundException e) {
            log.error(MSG_INVALID_TENANT_ID, dataSourceId);
            throw new InvalidTenantIdException();
        } catch (TenantResolvingException e) {
            log.error(MSG_RESOLVING_TENANT_ID, dataSourceId);
            throw new InvalidTenantIdException();
        }
    }
}
