package com.example.cruddata.service.imp;

import com.example.cruddata.constant.ApiErrorCode;
import com.example.cruddata.constant.ColumnConsts;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;
import com.example.cruddata.exception.BusinessException;
import com.example.cruddata.repository.system.ColumnConfigRepository;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.repository.system.TableConfigRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImp implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImp.class);


    @Autowired
    private SystemService systemService;

    @Autowired
    TableConfigRepository tableConfigRepository;

    @Autowired
    DataSourceConfigRepository dataSourceConfigRepository;

    @Autowired
    ColumnConfigRepository columnConfigRepository;

    @Autowired
    DataService dataService;

    @Autowired
    SelectionValueService selectionValueService;
    @Autowired
    TenantService tenantService;


    @Override
    public String processFile2Str(File pdfFile) {
        return null;
    }

    @Override
    public ByteArrayOutputStream excelDownloadＶaiTable(Long dataSourceId, String tableName, Long tenantId) throws Exception{


        log.info("process excelDownloadＶaiTable start!");

        List<TableConfig> tables = systemService.getTableConfigs(dataSourceId, tableName, tenantId);

        List<ColumnConfig> columns =  columnConfigRepository.findByTenantIdAndIsDeletedAndTableId(tenantId , Boolean.FALSE , tables.get(0).getId());
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
        List<ColumnConfig> columnConfigs = this.columnConfigRepository.findByTenantIdAndIsDeletedAndTableId(tenantId,Boolean.FALSE, tables.get(0).getId());
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
            throw new BusinessException(ApiErrorCode.VALIDATED_ERROR , "data no data import!", columnConfigsMap.keySet());
        }

        InsertEntityData insertEntityData = new InsertEntityData();
        insertEntityData.setTableName(tableName);
        insertEntityData.setRecordList(new LinkedList<>());
        insertEntityData.setColumnNameList(new LinkedList<>());

        extracted(columnConfigsMap, sheet, cellIndex ,  tableName,  tenantId , selectionValueService , insertEntityData);

        dataService.insertData(dataSourceId , dataSourceConfig.get().getDatabaseType(),insertEntityData);

        return null;
    }



    public static void extracted(Map<String, ColumnConfig> columnConfigsMap, Sheet sheet, Map<String, AtomicReference<Integer>> cellIndex ,String tableName, Long tenantId,SelectionValueService selectionValueService , InsertEntityData insertEntityData  ) {
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
