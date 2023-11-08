package com.example.cruddata.service.imp;

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
    public void genSwaggerDoc(Long roleId) throws JsonProcessingException {
        Optional<Role> roleInDb = roleRepository.findById(roleId);
        if(!roleInDb.isPresent()){
            throw new BusinessException("no role in use {}",roleId);
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

    }

    private SwaggerDocData dataArrangeProcess( Map<String,TableConfig> tableConfigHashMap , Map<String,List<ColumnConfig>> columnConfigHashMap , Map<String,List<Function>> urlAction){
        SwaggerDocData swaggerDocData = new SwaggerDocData();
        swaggerDocData.setInfo(getSwaggerInfoData());
        swaggerDocData.setServers(getServersData());
        swaggerDocData.setTags(getTagsData(tableConfigHashMap));
        swaggerDocData.setPaths(getPathInfo(tableConfigHashMap ,  columnConfigHashMap , urlAction));

        return swaggerDocData;
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

    private Map<String, Map<String, SwaggerUrlEntityData>> getPathInfo(Map<String, TableConfig> tableConfigHashMap, Map<String, List<ColumnConfig>> columnConfigHashMap, Map<String, List<Function>> urlActions) {
        Map<String, Map<String,SwaggerUrlEntityData>> result = new HashMap<>();

        tableConfigHashMap.forEach((tableNameKey , tableConfig)->{
            String moduleName = tableConfig.getModuleName();
            moduleName = moduleName.substring(0, 1).toLowerCase() + moduleName.substring(1);
            String returnKey ="/api/v1/"+moduleName+"/"+tableConfig.getName();
            result.put(returnKey,new HashMap<>());

            urlActions.get(tableNameKey).forEach(function -> {
                String urlAction = FunctionType.getUrlAction(function.getFunctionType());
                if(null != urlAction){
                    SwaggerUrlEntityData swaggerUrlEntityData = new SwaggerUrlEntityData();
                    swaggerUrlEntityData.setSummary(tableConfig.getName() +"-" +urlAction );
                    swaggerUrlEntityData.setOperationId(tableConfig.getName() +"-" +urlAction);
                    swaggerUrlEntityData.setDescription(function.getDescription());
                    swaggerUrlEntityData.setSummary(function.getDescription());
                    swaggerUrlEntityData.setTags(Arrays.asList(tableConfig.getName()));
                    if(urlAction.equals(FunctionType.CREATE.toString())){
                        swaggerUrlEntityData.setRequestBody(new HashMap<>());
                    }
                    swaggerUrlEntityData.setResponses(new HashMap<>());
                    result.get(returnKey).put(urlAction,swaggerUrlEntityData);

                }

            });




        });

        return result;
    }

    private List<SwaggerServerData> getServersData() {
        List<SwaggerServerData> serverDataList= new ArrayList<>();
        SwaggerServerData swaggerServerData = new SwaggerServerData();
        swaggerServerData.setUrl("http://localhost:8888/");
        swaggerServerData.setDescription("demo for test");
        return serverDataList;
    }

    private SwaggerInfoData getSwaggerInfoData(){
        SwaggerInfoData  swaggerInfoData = new SwaggerInfoData();
        swaggerInfoData.setTitle("DataGret");
        swaggerInfoData.setVersion("v1.0.1");
        return swaggerInfoData;
    }
}
