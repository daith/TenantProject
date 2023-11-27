package com.example.cruddata.service.imp;

import com.example.cruddata.constant.*;
import com.example.cruddata.dto.web.*;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.service.*;
import com.example.cruddata.util.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImp implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);


    @Autowired
    private SystemService systemService;

    @Autowired
    DataSourceConfigRepository dataSourceConfigRepository;

    @Autowired
    TableColumnService tableColumnService;

    @Autowired
    DataService dataService;

    @Autowired
    SelectionValueService selectionValueService;
    @Autowired
    TenantService tenantService;

    @Autowired
    RoleService roleService;



    @Override
    public String processFile2Str(File pdfFile) {
        return null;
    }

    @Override
    public ByteArrayOutputStream excelDownloadＶaiTable(Long dataSourceId, String tableName, Long tenantId) throws Exception{


        log.info("process excelDownloadＶaiTable start!");

        List<TableConfig> tables = systemService.getTableConfigs(dataSourceId, tableName, tenantId);

        List<ColumnConfig> columns =  tableColumnService.getActiveColumnByTenantIdAndTableId(tenantId , tables.get(0).getId());
        List<String> excelHeaders = new ArrayList<String>();
        columns.forEach(item->{excelHeaders.add(item.getName().trim());});
        ByteArrayOutputStream file = FileUtils.genExcelFile(excelHeaders, tableName);
        log.info("process data validator done!");
        return file;
    }

    private String processPDF2Str(File pdfFile) {

        return null;
    }

    private String processWord2Str(File pdfFile) {
        return null;
    }

    public List<Object> importDataViaFile(Long dataSourceId, String tableName, Long tenantId , MultipartFile file) throws Exception {

        log.info("process createTable start!");

        tenantService.validatedTenantProcess(tenantId);

        Optional<DataSourceConfig> dataSourceConfig = this.dataSourceConfigRepository.findByIdAndTenantIdAndIsDeleted(dataSourceId,tenantId,Boolean.FALSE);
        if(!dataSourceConfig.isPresent()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "datasource is not exist or you cant use this datasource");
        }


        /*
         * step 1 of data process of validate of authority , if pass , get the table info
         * */
        log.info("process importDataViaFile start!");

        List<TableConfig> tables = systemService.getTableConfigs(dataSourceId, tableName, tenantId);

        /*
         * step 2 of data process of validate of file basic condition
         * */

        // read column list to map of list
        List<ColumnConfig> columnConfigs =  tableColumnService.getActiveColumnByTenantIdAndTableId(tenantId , tables.get(0).getId());
        Map<String, ColumnConfig> columnConfigsMap=  columnConfigs.stream()
                .collect(Collectors.toMap(ColumnConfig::getName, columnConfig -> columnConfig));

        byte[] buf =  IOUtils.toByteArray(file.getInputStream());//execelIS爲InputStream流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);

        XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream);
        // if sheet more than 1 is not correct

        if( 1 > workbook.getNumberOfSheets()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "file sheet could not more than 1 sheet");
        }
        Sheet sheet = workbook.getSheetAt(0);

        Map<String, AtomicReference<Integer>> cellIndex = new HashMap<>();
        AtomicReference<Integer> index = new AtomicReference<>(0);

        // 先確定 cell 是否有滿足 我們要的欄位
        sheet.getRow(0).cellIterator().forEachRemaining(cell->{
            String columnName = cell.getStringCellValue().trim();
            if(columnConfigsMap.containsKey(columnName) && !cellIndex.containsKey(columnName)){
                cellIndex.put(columnName , index);
                index.getAndSet(index.get());
            }
        });

        if( tables.size() != cellIndex.size()) {
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "data column not collect please check!", columnConfigsMap.keySet());
        }

        if( 1 > sheet.getLastRowNum()) {
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "data no data import!");
        }

        InsertEntityData insertEntityData = new InsertEntityData();
        insertEntityData.setTableName(tableName);
        insertEntityData.setRecordList(new LinkedList<>());
        insertEntityData.setColumnNameList(new LinkedList<>());

        extractedDataToInsertObject(columnConfigsMap, sheet, cellIndex ,  tableName,  tenantId , selectionValueService , insertEntityData);

        dataService.insertData(dataSourceId , dataSourceConfig.get().getDatabaseType(),insertEntityData);

        return null;
    }

    @Override

    public List<Object> importTablesViaFile(Long dataSourceId, Long tenantId, MultipartFile file) throws Exception {

        log.info("process importTablesViaFile start!");

        tenantService.validatedTenantProcess(tenantId);

        Optional<DataSourceConfig> dataSourceConfig = this.dataSourceConfigRepository.findByIdAndTenantIdAndIsDeleted(dataSourceId,tenantId,Boolean.FALSE);
        if(!dataSourceConfig.isPresent()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "datasource is not exist or you cant use this datasource");
        }



        byte[] buf =  IOUtils.toByteArray(file.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);

        XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream);
        // check sheet is correct
        if( SheetConsts.getSheetColumnSetting().size() != workbook.getNumberOfSheets()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "file sheet most "+ SheetConsts.getSheetColumnSetting().size() +" sheet");
        }


        // check each sheet column is correct
        Map<String ,Map<String, Integer> > cellIndexBySheet = new HashMap<>();
        Map<String ,String> sheetColumnError = new HashMap<>();
        sheetValidatorProcess(workbook, cellIndexBySheet, sheetColumnError , SheetConsts.getSheetColumnSetting());


        Map<String, CreateEntityData> createTableEntityDataMap = new HashMap<>();
        sheetCreateTableDataValidatorProcess(dataSourceId, workbook, cellIndexBySheet, createTableEntityDataMap , SheetConsts.getSheetColumnSetting());

        List<TableConfig> tableList = this.tableColumnService.getActiveTablesByTenantIdAndDataSourceId(tenantId,dataSourceId);

        if(tableList.size()>0){
           this.tableColumnService.deleteAllTableByDataSource(tenantId,dataSourceId);
        }


        // create table
        // 先除理 沒有 FK 的 Table
        // 除理 FK 已經建立的 Table
        List<String> createdTables = new ArrayList<>();
        Integer executeProcess = 0;
        while (createdTables.size() != createTableEntityDataMap.size() && executeProcess <= 5){
            log.info("go while process ={}=",executeProcess);
            createTableEntityDataMap.forEach((tableName,entity)->{

                try {
                    if(!createdTables.contains(tableName) && entity.getFkTables().size() == 0){
                        createTableProcess(dataSourceId, tenantId, dataSourceConfig, tableName, entity , createdTables);
                    } else {
                        if(!createdTables.contains(tableName) && createdTables.containsAll(entity.getFkTables())){
                            createTableProcess(dataSourceId, tenantId, dataSourceConfig,  tableName, entity , createdTables);
                        }
                    }


                } catch (SQLException e) {
                    log.info(e.getMessage());
                }
            });
            executeProcess+=1;
        }

        return null;
    }

    @Override
    public List<Object> importCodeListViaFile(Long dataSourceId, Long tenantId, MultipartFile file) throws Exception {
        // code list //
        log.info("importCodeListViaFile.....dataSourceId:{} , tenantId:{} , ",dataSourceId , tenantId);

        tenantService.validatedTenantProcess(tenantId);

        Optional<DataSourceConfig> dataSourceConfig = this.dataSourceConfigRepository.findByIdAndTenantIdAndIsDeleted(dataSourceId,tenantId,Boolean.FALSE);
        if(!dataSourceConfig.isPresent()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "datasource is not exist or you cant use this datasource");
        }

        byte[] buf =  IOUtils.toByteArray(file.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);

        XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream);
        // check sheet is correct
        if( null == workbook.getSheet(SheetConsts.SHEET_CODE_LIST) ){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "file sheet name should be: "+ SheetConsts.SHEET_CODE_LIST);
        }

        // check  sheet column is correct
        Map<String ,Map<String, Integer> > cellIndexBySheet = new HashMap<>();
        Map<String ,String> sheetColumnError = new HashMap<>();
        sheetValidatorProcess(workbook, cellIndexBySheet, sheetColumnError , SheetConsts.getCodeListSetting());

        // extract map data to entity
        Map<String, InsertEntityData> insertEntityDataMap = new HashMap<>();
        sheetInsertDataValidatorProcess(  workbook,  cellIndexBySheet,  insertEntityDataMap,SheetConsts.getCodeListSetting());

        // conform table name type
        List<TableConfig> tableConfigList = tableColumnService.getActiveTablesByTenantIdAndDataSourceId(tenantId,dataSourceId);

        tableConfigList.forEach(table ->{
            if(insertEntityDataMap.containsKey(table.getName())){
                List<ColumnConfig> columnConfigs =  tableColumnService.getActiveColumnByTenantIdAndTableId(tenantId , table.getId());

                insertEntityDataMap.get(table.getName()).setColumnNameList(columnConfigs);
            }
        });

        List<String> noExistTables= new ArrayList<>();

        insertEntityDataMap.forEach((table,entity)->{
           if(  entity.getColumnNameList().size()  == 0){
               noExistTables.add(table);
           }
        });

        if(noExistTables.size()!=0){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "table not exist : "+ noExistTables);
        }

        // query -> delete -> insert
        insertEntityDataMap.forEach((table,entity)->{
            try {
                // Long dataSourceId, String dbName, SampleSelectionEntityData entityData
                SampleSelectionEntityData data = new SampleSelectionEntityData();
                data.setColumnSelectedList(new ArrayList<>());
                data.getColumnSelectedList().add(table.replace("Type","Cd"));
                data.setConditionForEq(new HashMap<>());
                data.setTableName(table);
                List<Map<String,Object>> result = this.dataService.queryDataBySampleCondition(dataSourceId ,dataSourceConfig.get().getDatabaseType() , data  );
                if(result.size()>0){
                    DeleteEntityData deleteEntityData = new DeleteEntityData();
                    deleteEntityData.setTableName(table);
                    deleteEntityData.setRecordList(result);
                    this.dataService.deleteData(dataSourceId,dataSourceConfig.get().getDatabaseType(),deleteEntityData);
                }
                this.dataService.insertData(dataSourceId,dataSourceConfig.get().getDatabaseType(),entity);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return null;
    }

    @Transactional
    public void createTableProcess(Long dataSourceId, Long tenantId, Optional<DataSourceConfig> dataSourceConfig,  String tableName, CreateEntityData entity , List<String> createdTables) throws SQLException {
        log.info("createTableProcess.....{}",tableName);
        dataService.createTable(dataSourceId, dataSourceConfig.get().getDatabaseType(), entity);

        TableConfig tableConfig = new TableConfig();
        tableConfig.setName(tableName);
        tableConfig.setCaption(entity.getCaption());
        tableConfig.setCategory(entity.getCategory());
        tableConfig.setTenantId(tenantId);
        tableConfig.setDataSourceId(dataSourceId);
        tableConfig.setCreateById(SystemConsts.SYSTEM_ADMIN);
        tableConfig.setModuleName(entity.getModuleName());
        tableColumnService.saveTableConfig(tableConfig);

        entity.getColumnEntityList().forEach(column->{
            int disorder = 0;

            try {

                disorder = Integer.parseInt(column.getOrder());

            } catch(NumberFormatException e) {

                double data = Double.parseDouble(column.getOrder());

                disorder = (int) data;
            }
            ColumnConfig columnConfig= new ColumnConfig();
            columnConfig.setTableId(tableConfig.getId());
            columnConfig.setName(column.getName());
            columnConfig.setTenantId(tenantId);
            columnConfig.setCreateById(SystemConsts.SYSTEM_ADMIN);
            columnConfig.setPkType(column.getPkType());
            columnConfig.setFkColumn(column.getFkColumn());
            columnConfig.setDataType(column.getDataType());
            columnConfig.setDisplayOrder(disorder);
            columnConfig.setLength(column.getLength());
            columnConfig.setDefaultValue(column.getDefaultValue());
            columnConfig.setFkTableName(column.getFkTableName());
            columnConfig.setNullable(column.getNullable());
            columnConfig.setCaption(column.getCaption());
            tableColumnService.saveColumnConfig(columnConfig);
        });

        roleService.deleteFunction( tableName,  tenantId,dataSourceId);
        roleService.createFunction(tableName, tableConfig.getCaption(),  tableConfig.getTenantId() ,FunctionType.getApiCommonFuncType(),dataSourceId , tableConfig.getId());
        createdTables.add(tableName);
    }



    private static void sheetCreateTableDataValidatorProcess(Long dataSourceId, XSSFWorkbook workbook, Map<String, Map<String, Integer>> cellIndexBySheet, Map<String, CreateEntityData> createTableEntityDataMap , Map<String, Map<String,Map<String,Object>>> sheetSettingMap) {
        Map<String ,List<Object>> dataError = new HashMap<>();

        // check each record of the sheet is correct
        sheetSettingMap.forEach((sheetName,columnSetting)->{

            Map<String, Integer> cellIndex  =  cellIndexBySheet.get(sheetName);
            Sheet sheet =  workbook.getSheet(sheetName);

            for(int i = 1; i <= sheet.getLastRowNum() ; i++){
                Row record = sheet.getRow(i);
                if(SheetConsts.SHEET_TABLE.equals(sheetName)){
                    // sheet name for table name
                    if(dataError.size() == 0 ){
                        sheetTableEntityValidatorProcess(dataSourceId, createTableEntityDataMap, dataError, sheetName, cellIndex, i, record);
                    }
                } else if (SheetConsts.SHEET_COLUMN.equals(sheetName)) {
                    if(dataError.size() == 0 ){
                        columnInfoValidatorProcess(createTableEntityDataMap, dataError, sheetName, cellIndex, i, record);
                    }

                }
            }
        });

        if( 0!= dataError.keySet().size()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR, "data error" , dataError);
        }
    }

    private static void sheetInsertDataValidatorProcess( XSSFWorkbook workbook, Map<String, Map<String, Integer>> cellIndexBySheet, Map<String, InsertEntityData> insertEntityDataMap , Map<String, Map<String,Map<String,Object>>> sheetSettingMap) {
        Map<String ,List<Object>> dataError = new HashMap<>();
        Map<String,List<String>> tableRecords = new HashMap<>();

        // check each record of the sheet is correct
        sheetSettingMap.forEach((sheetName,columnSetting)->{

            Map<String, Integer> cellIndex  =  cellIndexBySheet.get(sheetName);
            Sheet sheet =  workbook.getSheet(sheetName);

            for(int i = 1; i <= sheet.getLastRowNum() ; i++){
                Row record = sheet.getRow(i);
                 if (SheetConsts.SHEET_CODE_LIST.equals(sheetName)) {
                    if(dataError.size() == 0 ){
                        String tableName = getCellData(record, cellIndex, SheetConsts.SHEET_CODE_LIST_TABLE_NAME, sheetName);
                        String name = getCellData(record, cellIndex, SheetConsts.SHEET_CODE_LIST_NAME, sheetName);
                        String key = getCellData(record, cellIndex, SheetConsts.SHEET_CODE_LIST_KEY, sheetName);

                        Boolean tableNameValueCheck = !(null == tableName && Boolean.TRUE == SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_TABLE_NAME));
                        Boolean nameValueCheck = !(null == name && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_CODE_LIST_NAME));
                        Boolean keyValueCheck = !(null == key && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_CODE_LIST_KEY));
                        Boolean dataNotDuplicateInfileCheck = Boolean.TRUE;


                        String dataKey = tableName+"-"+key;
                        if(!tableRecords.containsKey(tableName)){
                            tableRecords.put(tableName,new ArrayList<>());
                        }

                        if(tableRecords.get(tableName).contains(dataKey)){
                            dataNotDuplicateInfileCheck = Boolean.FALSE;
                        }

                        if( tableNameValueCheck && nameValueCheck && keyValueCheck && dataNotDuplicateInfileCheck){
                            if(!insertEntityDataMap.containsKey(tableName)){
                                InsertEntityData entityData = new InsertEntityData();
                                entityData.setTableName(tableName);
                                entityData.setRecordList(new ArrayList<>());
                                entityData.setColumnNameList(new ArrayList<>());
                                insertEntityDataMap.put(tableName ,entityData);
                            }
                            Map recordData = new HashMap<>();
                            recordData.put(tableName.replace("Type","Cd"),key);
                            recordData.put(tableName.replace("Type","Name"),name);
                            insertEntityDataMap.get(tableName).getRecordList().add(recordData);
                            tableRecords.get(tableName).add(key);
                        } else {
                            if(!dataError.containsKey(sheetName)){
                                dataError.put(sheetName,new ArrayList<>());
                            }
                            if(!dataNotDuplicateInfileCheck){
                                dataError.get(sheetName).add("row("+ i +") data duplicate in file  (tableName:"+tableName+" / name:"+name+" / key:"+key+")");
                            }else {
                                dataError.get(sheetName).add("row("+ i +") column not correct (tableName:"+tableName+" / name:"+name+" / key:"+key+")");
                            }
                        }


                    }

                }
            }
        });

        if( 0!= dataError.keySet().size()){
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR,"dataError" , dataError);
        }
    }

    private static void columnInfoValidatorProcess(Map<String, CreateEntityData> createTableEntityDataMap, Map<String, List<Object>> dataError, String sheetName, Map<String, Integer> cellIndex, int i, Row record) {
        String tableName = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_TABLE_NAME, sheetName);
        String order = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_ORDER, sheetName);
        String columnName = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_COLUMN_NAME, sheetName);
        String description = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_DESCRIPTION, sheetName);
        String dataType = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_TYPE, sheetName);
        String length = getCellIntData(record, cellIndex, SheetConsts.SHEET_COLUMN_LENGTH, sheetName);
        String nullable = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_NULLABLE, sheetName);
        String fkTableName = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_FK_TABLE_NAME, sheetName);
        String fkColumn = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_FK_COLUMN, sheetName);
        String pkType = getCellData(record, cellIndex, SheetConsts.SHEET_COLUMN_PK_TYPE, sheetName);

        Boolean tableNameValueCheck = !(null == tableName && Boolean.TRUE == SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_TABLE_NAME));
        Boolean orderValueCheck = !(null == order && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_ORDER));
        Boolean columnNameValueCheck = !(null == columnName && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_COLUMN_NAME));
        Boolean descriptionValueCheck = !(null == description && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_DESCRIPTION));
        Boolean dataTypeValueCheck = !(null == dataType && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_TYPE));
        Boolean lengthValueCheck = !(null == length && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_LENGTH));
        Boolean nullableValueCheck = !(null == nullable && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_NULLABLE));
        Boolean fkTableNameValueCheck = !(null == fkTableName && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_FK_TABLE_NAME));
        Boolean fkColumnValueCheck = !(null == fkColumn && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_FK_COLUMN));
        Boolean pkTypeValueCheck  = !(null == fkColumn && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_COLUMN_PK_TYPE));
        Boolean columnTableExitsInfileCheck = (null != tableName && createTableEntityDataMap.containsKey(tableName));
        Boolean tableColumnNotDuplicateInfileCheck = Boolean.TRUE;

        if(columnTableExitsInfileCheck){
            for (ColumnInfoData item : createTableEntityDataMap.get(tableName).getColumnEntityList()) {
                if (item.getName().equals(columnName)) {
                    tableColumnNotDuplicateInfileCheck = Boolean.FALSE;
                }
            }
        }

        if(tableNameValueCheck && orderValueCheck && columnNameValueCheck && pkTypeValueCheck && descriptionValueCheck && dataTypeValueCheck && lengthValueCheck && nullableValueCheck && fkTableNameValueCheck && fkColumnValueCheck && tableColumnNotDuplicateInfileCheck &&  columnTableExitsInfileCheck ){
            ColumnInfoData infoData = new ColumnInfoData();
            infoData.setNullable(Boolean.valueOf(nullable));
            infoData.setName(columnName);
            infoData.setCaption(description);
            infoData.setFkTableName(fkTableName);
            infoData.setFkColumn(fkColumn);
            infoData.setLength(length);
            infoData.setPkType(pkType);
            infoData.setDataType(dataType);
            infoData.setOrder(order);
            createTableEntityDataMap.get(tableName).getColumnEntityList().add(infoData);

            if(null != fkTableName && null != fkTableName && !fkTableName.isEmpty() && !fkColumn.isEmpty()){
                createTableEntityDataMap.get(tableName).getFkTables().add(fkTableName);
            }
        } else {
            if(!dataError.containsKey(sheetName)){
                dataError.put(sheetName,new ArrayList<>());
            }
            if(!tableColumnNotDuplicateInfileCheck){
                dataError.get(sheetName).add("row("+ i +") column duplicate in file  (tableName:"+tableName+" / columnName:"+columnName+" / description:"+description+")");
            }else if(!columnTableExitsInfileCheck){
                dataError.get(sheetName).add("row("+ i +") table not exist in file  (tableName:"+tableName+" / columnName:"+columnName+" / description:"+description+")");
            }else {
                dataError.get(sheetName).add("row("+ i +") column not correct (tableName:"+tableName+" / columnName:"+columnName+" / description:"+description+")");
            }
        }
    }

    private static void sheetTableEntityValidatorProcess(Long dataSourceId, Map<String, CreateEntityData> createTableEntityDataMap, Map<String, List<Object>> dataError, String sheetName, Map<String, Integer> cellIndex, int i, Row record) {
        if(null != cellIndex.get(SheetConsts.SHEET_TABLE_NAME) && null != cellIndex.get(SheetConsts.SHEET_TABLE_CATEGORY)&& null != cellIndex.get(SheetConsts.SHEET_TABLE_DESCRIPTION)){

            String tableName = getCellData(record, cellIndex, SheetConsts.SHEET_TABLE_NAME,  sheetName);
            String category = getCellData(record, cellIndex, SheetConsts.SHEET_TABLE_CATEGORY,  sheetName);
            String tableDescription = getCellData(record, cellIndex, SheetConsts.SHEET_TABLE_DESCRIPTION, sheetName);
            String moduleName = getCellData(record, cellIndex, SheetConsts.SHEET_TABLE_MODULE_NAME, sheetName);

            Boolean tableNameValueCheck = !(null == tableName && Boolean.TRUE == SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_TABLE_NAME));
            Boolean categoryValueCheck = !(null == category && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_TABLE_CATEGORY));
            Boolean tableDescriptionValueCheck = !(null == tableDescription && Boolean.TRUE ==  SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_TABLE_DESCRIPTION));
            Boolean tableNotDuplicateInfileCheck = (null != tableName && !createTableEntityDataMap.containsKey(tableName));
            Boolean moduleNameValueCheck  = !(null == moduleName && Boolean.TRUE == SheetConsts.getSheetColumnNullable(sheetName,SheetConsts.SHEET_TABLE_MODULE_NAME));

            if(tableNameValueCheck && categoryValueCheck && tableDescriptionValueCheck && tableNotDuplicateInfileCheck && moduleNameValueCheck){
                CreateEntityData entityData = new CreateEntityData();
                entityData.setTableName(tableName);
                entityData.setCategory(category);
                entityData.setCaption(tableDescription);
                entityData.setDatasourceId(dataSourceId);
                entityData.setModuleName(moduleName);
                entityData.setColumnEntityList(new ArrayList<>());
                entityData.setFkTables(new ArrayList<>());
                createTableEntityDataMap.put(tableName , entityData);

            }else {
                if(!dataError.containsKey(sheetName)){
                    dataError.put(sheetName,new ArrayList<>());
                }
                if(!tableNotDuplicateInfileCheck){
                    dataError.get(sheetName).add("row("+ i +") table duplicate in file  (tableName:"+tableName+" / category:"+category+" / tableDescription:"+tableDescription+")");
                }else{
                    dataError.get(sheetName).add("row("+ i +") data not correct(tableName:"+tableName+" / category:"+category+" / tableDescription:"+tableDescription+")");
                }
            }
        }
    }

    private static String getCellData(Row record, Map<String, Integer> cellIndex, String sheetTableName,  String sheetName) {
        String value = null;
        if (null != record.getCell(cellIndex.get(sheetTableName))) {
            record.getCell(cellIndex.get(sheetTableName)).setCellType(Cell.CELL_TYPE_STRING);
            value = record.getCell(cellIndex.get(sheetTableName)).getStringCellValue().trim();
        } else if (null != SheetConsts.getSheetColumnDefault(sheetName, sheetTableName)) {
            record.getCell(cellIndex.get(sheetTableName)).setCellType(Cell.CELL_TYPE_STRING);
            value = SheetConsts.getSheetColumnDefault(sheetName, sheetTableName).trim();
        }
        return value;
    }

    private static String getCellIntData(Row record, Map<String, Integer> cellIndex, String sheetTableName,  String sheetName) {
        String value = null;

        if (null != record.getCell(cellIndex.get(sheetTableName))) {
            record.getCell(cellIndex.get(sheetTableName)).setCellType(Cell.CELL_TYPE_STRING);
            value = record.getCell(cellIndex.get(sheetTableName)).getStringCellValue().trim();
        } else if (null != SheetConsts.getSheetColumnDefault(sheetName, sheetTableName)) {
            record.getCell(cellIndex.get(sheetTableName)).setCellType(Cell.CELL_TYPE_STRING);
            value = SheetConsts.getSheetColumnDefault(sheetName, sheetTableName).trim();
        }

        try {
            if(value.contains(".")){
                double d = Double.parseDouble(value);
                value = String.valueOf((int) d);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    private static void sheetValidatorProcess(XSSFWorkbook workbook, Map<String, Map<String, Integer>> cellIndexBySheet, Map<String, String> sheetColumnError , Map<String, Map<String,Map<String,Object>>> sheetSettingMap) {
        sheetSettingMap.forEach((sheetName,columnSetting)->{

            Map<String, Integer> cellIndex = new HashMap<>();
            AtomicReference<Integer> index = new AtomicReference<>(0);
            cellIndexBySheet.put(sheetName,cellIndex);

            Sheet sheet =  workbook.getSheet(sheetName);

            if( null == sheet) {
                sheetColumnError.put(sheetName+"-not-exist-error" , "sheet-not-exist! <"+ sheetName+">");
            }else{
                HashMap columnSettingMap = (HashMap)columnSetting;
                // 先確定 cell 是否有滿足 我們要的欄位
                sheet.getRow(0).cellIterator().forEachRemaining(cell->{
                    String columnName = cell.getStringCellValue().trim();
                    if(columnSetting.containsKey(columnName) && !cellIndex.containsKey(columnName)){
                        cellIndex.put(columnName , index.get());
                    }
                    index.getAndSet(index.get()+1);
                });

                if( cellIndex.size() != cellIndex.size()) {
                    sheetColumnError.put(sheetName+"-title-define-error" , "data column not collect please check! <"+ columnSetting.keySet().toString()+">");
                }
            }

        });

        if( sheetColumnError.size() != 0) {
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "sheet error", sheetColumnError);
        }
    }


    public static void extractedDataToInsertObject(Map<String, ColumnConfig> columnConfigsMap, Sheet sheet, Map<String, AtomicReference<Integer>> cellIndex ,String tableName, Long tenantId,SelectionValueService selectionValueService , InsertEntityData insertEntityData  ) {
        /*
         * step 3 of data process of validate of data validate
         *
         * list of map
         * */

        Map<String,List<String>> errorMsg = new HashMap<>();
        List<Map<String,Object>> insetDataList = insertEntityData.getRecordList();

        for(int i = 1; i <= sheet.getLastRowNum() ; i++){
            Row record = sheet.getRow(i);
            Map<String,Object> insetData = new HashMap<>();
            AtomicReference<Boolean> dataValidated = new AtomicReference<>(Boolean.TRUE);
            AtomicReference<String> Key = new AtomicReference<>("index:"+i+"/");
            columnConfigsMap.forEach((columnKey , config)->{

                AtomicReference<Integer> recordIndex = cellIndex.get(columnKey);
                Cell cell = record.getCell(recordIndex.get());
                String value = cell.getStringCellValue();
                if(null != value ){
                    Object data = String.valueOf(value);
                    Key.set(Key.get()+"column:"+columnKey+"/value:"+value);

                    if(config.getNullable() == Boolean.FALSE && null == config.getDefaultValue() && null == value){
                        if(!errorMsg.containsKey(Key.get())){
                            errorMsg.put(Key.get(), new ArrayList<>());
                        }
                        errorMsg.get(Key.get()).add("Data Could Not Be Null");
                        dataValidated.set(false);
                    }
                    try{
                        if(config.getDataType().equals(ColumnConsts.DATA_TYPE_BIGINT)){
                            data = new BigInteger(value);
                        } else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_INT)) {
                            data = Integer.valueOf(value);
                        }else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_FLOAT)) {
                            data = Float.valueOf(value);
                        }else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_DOUBLE)) {
                            data = Double.valueOf(value);
                        }else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_DECIMAL)) {
                            data =new BigDecimal(value);
                        }else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_DOUBLE)) {
                            data =Boolean.valueOf(value);
                        }else if (config.getDataType().equals(ColumnConsts.DATA_TYPE_DATE)) {
                            data =new Date(value);
                        }
                        data = selectionValueService.mappingSystemSelectionValue( tenantId ,   tableName,  columnKey,  data);
                        insetData.put(columnKey , data);
                        insertEntityData.getColumnNameList().add(config);


                    }catch (Exception exception){
                        if(!errorMsg.containsKey(Key.get())){
                            errorMsg.put(Key.get(), new ArrayList<>());
                        }
                        dataValidated.set(false);
                        errorMsg.get(Key.get()).add("Data Not Correct(DataType:"+config.getDataType()+"/Data Value:"+value+")");
                    }
                }
            });
            if (dataValidated.get()){
                insetDataList.add(insetData);
            }
        }
    }



}
