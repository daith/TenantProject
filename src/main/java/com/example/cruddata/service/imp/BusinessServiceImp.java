//package com.example.cruddata.service.imp;
//
//import com.example.cruddata.service.BusinessService;
//import com.example.cruddata.service.ConfigService;
//import com.example.cruddata.service.CrudService;
//import com.example.cruddata.service.DataSourceService;
//import com.example.cruddata.util.CrudapiUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class BusinessServiceImp implements BusinessService {
//
//    @Autowired
//    private CrudService crudService;
//
//    @Autowired
//    private ConfigService configService;
//
//    @Autowired
//    private DataSourceService dataSourceService;
//
//
//    @Override
//    public List<Map<String, Object>> list(String resourceName) {
//
//        String tableName = this.getTableName(resourceName);
//
//        Map<String, Object> paramsMap = new HashMap<String, Object>();
//        List<Map<String, Object>> mapList = crudService.queryForList("SELECT * FROM `" + tableName + "`", paramsMap);
//        return mapList;
//    }
//
//    /*
//    * ProjectNmae -> Query DB Setting -> Use  Config setting to query tablenamse
//    * */
//    public String getTableName(String dataSourceName, String resourceName) {
//
//
//        String tableName = CrudapiUtils.convert(resourceName, apiResourceNaming, businessDatabaseNaming);
//
//        return tableName;
//    }
//
//    public String getTableName(String resourceName) {
//        return this.getTableName(DataSourceContextHolder.getDataSource(), resourceName);
//    }
//}
