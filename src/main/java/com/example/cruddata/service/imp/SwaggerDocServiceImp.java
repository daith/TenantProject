package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.ColumnConsts;
import com.example.cruddata.constant.FunctionType;
import com.example.cruddata.dto.swagger.*;
import com.example.cruddata.entity.authroty.Function;
import com.example.cruddata.entity.authroty.Role;
import com.example.cruddata.entity.authroty.RoleFunction;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.authroty.FunctionRepository;
import com.example.cruddata.repository.authroty.RoleFunctionRepository;
import com.example.cruddata.repository.authroty.RoleRepository;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.TableConfigRepository;
import com.example.cruddata.service.SwaggerDocService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SwaggerDocServiceImp implements SwaggerDocService {
    @Autowired
    RoleFunctionRepository roleFunctionRepository;
    @Autowired
    FunctionRepository functionRepository;
    @Autowired
    TableConfigRepository tableConfigRepository;
    @Autowired
    ColumnConfigRepository columnConfigRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public String genSwaggerDoc(Long roleId) throws JsonProcessingException {
        Optional<Role> roleInDb = roleRepository.findById(roleId);
        if(!roleInDb.isPresent()){
            throw new BusinessException(ApiErrorCode.AUTH_ERROR,"no role in use {}",roleId);
        }
        List<RoleFunction> roleFunctions = roleFunctionRepository.findByRoleId(roleId);
        List<Long> funstionIdList = roleFunctions.stream().map(RoleFunction::getFunctionId).collect(Collectors.toList());
        List<Function> functions =  functionRepository.findAllById(funstionIdList);
        List<Long> tableIdList = functions.stream().map(Function::getTableId).collect(Collectors.toList());
        List<TableConfig> tableList =  tableConfigRepository.findAllById(tableIdList);
        Map<Long,TableConfig> tableMap =  tableList.stream().collect(Collectors.toMap(TableConfig::getId, TableConfig -> TableConfig));
        List<ColumnConfig> columnConfigs  = columnConfigRepository.findByTableIdIn(tableIdList);

        Map<String,TableConfig> tableConfigHashMap =new HashMap<>();
        Map<String,List<ColumnConfig>> columnConfigHashMap =new HashMap<>();
        Map<String,List<Function>> urlAction =new HashMap<>();

        for(ColumnConfig columnConfig:columnConfigs){
            if(tableMap.containsKey(columnConfig.getTableId())){
                TableConfig tableConfig =  tableMap.get(columnConfig.getTableId());
                String key = tableConfig.getName()+"_"+tableConfig.getDataSourceId();
                if(!tableConfigHashMap.containsKey(key)){
                    tableConfigHashMap.put(key,tableConfig);
                }
                if(!columnConfigHashMap.containsKey(key)){
                    columnConfigHashMap.put(key, new ArrayList<>());
                }
                columnConfigHashMap.get(key).add(columnConfig);
            }
        }

        functions.forEach(function -> {
            String tableNameWithDataSource = function.getFunctionName()+"_"+function.getDataSourceId();
            if(!urlAction.containsKey(tableNameWithDataSource)){
                urlAction.put(tableNameWithDataSource, new ArrayList<>());
            }
            urlAction.get(tableNameWithDataSource).add(function);

        });

        SwaggerDocData swaggerDocData = dataArrangeProcess( tableConfigHashMap , columnConfigHashMap , urlAction);
        swaggerDocData.setOpenapi("3.0.1");

        ObjectMapper mapper = new ObjectMapper();
        String data=  mapper.writeValueAsString(swaggerDocData);

        try {

            FileWriter fileWriter = new FileWriter("src/main/resources/static/swagger/"+roleInDb.get().getRoleName()+".json");
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return "/web/"+roleInDb.get().getRoleName()+"/swagger#/";

    }

    private SwaggerDocData dataArrangeProcess( Map<String,TableConfig> tableConfigHashMap , Map<String,List<ColumnConfig>> columnConfigHashMap , Map<String,List<Function>> urlAction){
        SwaggerDocData swaggerDocData = new SwaggerDocData();
        swaggerDocData.setInfo(getSwaggerInfoData());
        swaggerDocData.setServers(getServersData());
        swaggerDocData.setTags(getTagsData(tableConfigHashMap));
        swaggerDocData.setPaths(new HashMap<String, Map<String,SwaggerUrlEntityData>> ());
        swaggerDocData.setSecurity(new ArrayList<>());
        setPathInfo(tableConfigHashMap ,  columnConfigHashMap , urlAction,swaggerDocData.getPaths());
        swaggerDocData.setComponents(new HashMap<>());
        setSecuritySchemes(swaggerDocData);
        setSchemas(swaggerDocData.getComponents(),columnConfigHashMap);


        return swaggerDocData;
    }

    private void setSchemas(Map<String, Map<String,?>> components, Map<String, List<ColumnConfig>> columnConfigHashMap) {
        components.put("schemas", new HashMap<String,SwaggerSchemasData>());
        for (String tableName : columnConfigHashMap.keySet()) {
            // Basic
            SwaggerSchemasData schemasData = new SwaggerSchemasData();
            Map<String,SwaggerSchemasData>  entitySetting = (Map<String,SwaggerSchemasData>)  components.get("schemas");
            entitySetting.put(tableName,schemasData);
            schemasData.setType("object");
            schemasData.setProperties( new HashMap<>());
            schemasData.setItems( new HashMap<>());
            List<ColumnConfig> dataList = columnConfigHashMap.get(tableName);
            dataList.forEach(column->{
                Map<String,String> columInfo = new HashMap<String,  String>();
                columInfo.put("type", ColumnConsts.getDataTypeToCode( column.getDataType()));
                columInfo.put("description", column.getCaption());
                schemasData.getProperties().put(column.getName(),columInfo);
            });

            // List
            SwaggerSchemasData schemasListData = new SwaggerSchemasData();
            entitySetting.put(tableName+"-list",schemasListData);
            schemasListData.setProperties( new HashMap<>());
            schemasListData.setType("array");
            Map listItem = new HashMap();
            listItem.put("$ref","#/components/schemas/"+tableName);
            schemasListData.setItems(listItem);
        }
    }

    private void setSecuritySchemes(SwaggerDocData  swaggerDocData) {

        String apiKeyPara = HttpHeaders.AUTHORIZATION;
        Map<String ,Object > security = new HashMap<>();
        security.put(apiKeyPara,new ArrayList<>());
        swaggerDocData.getSecurity().add(security);


        HashMap apiKey = new HashMap();
        apiKey.put("type","http");
        apiKey.put("scheme","bearer");
        apiKey.put("bearerFormat","JWT");
        apiKey.put("description","authentication account key");

        swaggerDocData.getComponents().put("securitySchemes",new HashMap<>());
        Map<String, Map> entitySetting = (Map<String, Map>) swaggerDocData.getComponents().get("securitySchemes");
        entitySetting.put(apiKeyPara,apiKey);


    }

    private List<SwaggerTagData> getTagsData(Map<String, TableConfig> tableConfigHashMap) {
        List<SwaggerTagData>  tags = new ArrayList<>();
        tableConfigHashMap.values().forEach(item->{
            SwaggerTagData tagData = new SwaggerTagData();
            tagData.setName(item.getName());
            tagData.setDescription(item.getCaption());
            tags.add(tagData);
        });

        return tags;
    }

    private void setPathInfo(Map<String, TableConfig> tableConfigHashMap, Map<String, List<ColumnConfig>> columnConfigHashMap,
                                                                       Map<String, List<Function>> urlActions,Map<String, Map<String,SwaggerUrlEntityData>> paths) {
        Map<String, Map<String,SwaggerUrlEntityData>> result = paths;

        tableConfigHashMap.forEach((tableNameKey , tableConfig)->{
            String moduleName = tableConfig.getModuleName();
            moduleName = moduleName.substring(0, 1).toLowerCase() + moduleName.substring(1);
            String returnKey ="/api/v1/"+moduleName+"/"+tableConfig.getName();
            result.put(returnKey,new HashMap<>());

            String finalModuleName = moduleName;
            urlActions.get(tableNameKey).forEach(function -> {
                String urlAction = FunctionType.getUrlAction(function.getFunctionType());
                if(null != urlAction){
                    String dataKey = tableConfig.getName() +"-" +urlAction;
                    SwaggerUrlEntityData swaggerUrlEntityData = new SwaggerUrlEntityData();
                    setPathParameter(swaggerUrlEntityData, finalModuleName, tableConfig.getName());
                    swaggerUrlEntityData.setParameters(new ArrayList<>());
                    swaggerUrlEntityData.setRequestBody(new HashMap<>());

                    swaggerUrlEntityData.setSummary(dataKey );
                    swaggerUrlEntityData.setOperationId(tableConfig.getName() +"-" +urlAction);
                    swaggerUrlEntityData.setDescription(function.getDescription());
                    swaggerUrlEntityData.setSummary(function.getDescription());
                    swaggerUrlEntityData.setTags(Arrays.asList(tableConfig.getName()));
                    swaggerUrlEntityData.setResponses(new HashMap<>());
                    if(FunctionType.getActionType(urlAction).equals(FunctionType.CREATE)){

                        setCreateParameterProcess(tableNameKey, swaggerUrlEntityData);

                    } else if (FunctionType.getActionType(urlAction).equals(FunctionType.QUERY)) {
                        SwaggerUrlContentSchemaData responseData_200  =new SwaggerUrlContentSchemaData();
                        responseData_200.setDescription(tableConfig.getCaption());
                        responseData_200.setContent(new HashMap<>());
                        responseData_200.getContent().put("application/json",new HashMap<>());
                        SwaggerUrlResponseSchemaData schemaData = new SwaggerUrlResponseSchemaData();
                        schemaData.setType("array");
                        schemaData.setItems(new HashMap<>());
                        schemaData.getItems().put("$ref","#/components/schemas/"+tableNameKey);
                        responseData_200.getContent().get("application/json").put("schema",schemaData);

                        swaggerUrlEntityData.getResponses().put("200", responseData_200);
                        setBasicResponseData(swaggerUrlEntityData);
                    }else if (FunctionType.getActionType(urlAction).equals(FunctionType.DELETE)) {
                        setDeleteParameterProcess(tableNameKey, swaggerUrlEntityData);
                    }else if (FunctionType.getActionType(urlAction).equals(FunctionType.UPDATE)) {
                        setUpdateParameterProcess(tableNameKey, swaggerUrlEntityData);
                    }
                    result.get(returnKey).put(urlAction,swaggerUrlEntityData);

                }
            });

        });

    }

    private static void setUpdateParameterProcess(String tableNameKey, SwaggerUrlEntityData swaggerUrlEntityData){
        setCreateParameterProcess(tableNameKey, swaggerUrlEntityData);
    }

    private static void setDeleteParameterProcess(String tableNameKey, SwaggerUrlEntityData swaggerUrlEntityData){
        setCreateParameterProcess(tableNameKey, swaggerUrlEntityData);
    }
    private static void setCreateParameterProcess(String tableNameKey, SwaggerUrlEntityData swaggerUrlEntityData) {
        Map<String,Map<String,Map<String,Map<String,String>>>> requestBody  =new HashMap();
        requestBody.put("content",new HashMap<>());
        requestBody.get("content").put("application/json",new HashMap<>());
        requestBody.get("content").get("application/json").put("schema",new HashMap<>());
        requestBody.get("content").get("application/json").get("schema").put("$ref","#/components/schemas/"+ tableNameKey+"-list");

        swaggerUrlEntityData.setRequestBody(requestBody);
        setBasicResponseData(swaggerUrlEntityData);
    }

    private static void setBasicResponseData(SwaggerUrlEntityData swaggerUrlEntityData) {
        SwaggerUrlContentSchemaData responseData_401  =new SwaggerUrlContentSchemaData();
        responseData_401.setDescription("Unauthorized");
        SwaggerUrlContentSchemaData responseData_403  =new SwaggerUrlContentSchemaData();
        responseData_403.setDescription("Forbidden");
        SwaggerUrlContentSchemaData responseData_404  =new SwaggerUrlContentSchemaData();
        responseData_404.setDescription("Not Found");
        swaggerUrlEntityData.getResponses().put("401",responseData_401);
        swaggerUrlEntityData.getResponses().put("403",responseData_403);
        swaggerUrlEntityData.getResponses().put("404",responseData_404);

        if(null == swaggerUrlEntityData.getResponses().get("200")){
            SwaggerUrlContentSchemaData responseData_200  =new SwaggerUrlContentSchemaData();
            responseData_200.setDescription("OK");
            swaggerUrlEntityData.getResponses().put("200",responseData_200);
        }
    }

    private void setPathParameter(SwaggerUrlEntityData swaggerUrlEntityData, String moduleName, String tableName) {
        if(null == swaggerUrlEntityData.getParameters()){
            swaggerUrlEntityData.setParameters(new ArrayList<>());
        }


            Map<String ,Object> moduleNameParameter = new HashMap<>();
            Map<String ,String> schemaPara = new HashMap<>();
            schemaPara.put("type","string");
            moduleNameParameter.put("name",moduleName);
            moduleNameParameter.put("in","path");
            moduleNameParameter.put("required",Boolean.TRUE);
            moduleNameParameter.put("default",moduleName);
            moduleNameParameter.put("schema",schemaPara);

            swaggerUrlEntityData.getParameters().add(moduleNameParameter);

            Map<String ,Object> tableNameParameter = new HashMap<>();
            tableNameParameter.put("name",tableName);
            tableNameParameter.put("in","path");
            tableNameParameter.put("required",Boolean.TRUE);
            tableNameParameter.put("default",tableName);
            tableNameParameter.put("schema",schemaPara);

            swaggerUrlEntityData.getParameters().add(tableNameParameter);
    }

    private List<SwaggerServerData> getServersData() {
        List<SwaggerServerData> serverDataList= new ArrayList<>();
        SwaggerServerData swaggerServerData = new SwaggerServerData();
        swaggerServerData.setUrl("http://localhost:8888");
        swaggerServerData.setDescription("demo for test");
        serverDataList.add(swaggerServerData);
        return serverDataList;
    }

    private SwaggerInfoData getSwaggerInfoData(){
        SwaggerInfoData  swaggerInfoData = new SwaggerInfoData();
        swaggerInfoData.setTitle("DataGret");
        swaggerInfoData.setVersion("v1.0.1");
        return swaggerInfoData;
    }
}
