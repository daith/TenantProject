package com.example.cruddata.constant;

import java.util.*;

public class SheetConsts {

    public final static String SHEET_TABLE="00_table";
    public final static String SHEET_COLUMN ="00_table-column";
    public final static String SHEET_CODE_LIST="00_code-list";

    public static final String COLUMN_NAME = "columnName";
    public static final String NULLABLE = "nullable";
    public static final String DATA_TYPE = "dataType";


    public static final String SHEET_TABLE_CATEGORY = "CATEGORY";
    public static final String SHEET_TABLE_NAME = "TABLE_NAME";
    public static final String SHEET_TABLE_DESCRIPTION =  "DESCRIPTION";

    public static final String SHEET_TABLE_MODULE_NAME=  "MODULE_NAME";


    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String SHEET_COLUMN_TABLE_NAME = "TABLE_NAME";
    public static final String SHEET_COLUMN_ORDER = "ORDER";
    public static final String SHEET_COLUMN_COLUMN_NAME = "COLUMN_NAME";
    public static final String SHEET_COLUMN_TYPE = "TYPE";
    public static final String SHEET_COLUMN_LENGTH = "LENGTH";
    public static final String SHEET_COLUMN_NULLABLE = "NULLABLE";
    public static final String SHEET_COLUMN_PK_TYPE = "PK_TYPE";
    public static final String SHEET_COLUMN_FK_TABLE_NAME = "FK_TABLE_NAME";
    public static final String SHEET_COLUMN_FK_COLUMN = "FK_COLUMN";
    public static final String SHEET_COLUMN_DESCRIPTION = "DESCRIPTION";

    public static final String SHEET_CODE_LIST_TABLE_NAME = "TABLE_NAME";
    public static final String SHEET_CODE_LIST_KEY = "KEY";
    public static final String SHEET_CODE_LIST_NAME = "NAME";

    public static Map<String, Map<String,Map<String,Object>>> getSheetColumnSetting(){

        Map<String, Map<String,Map<String,Object>>> sheetColumnSetting = new LinkedHashMap<>();
        sheetColumnSetting.put(SHEET_TABLE,new HashMap<>());
        extractedTableSetting(SHEET_TABLE , sheetColumnSetting);

        sheetColumnSetting.put(SHEET_COLUMN,new HashMap<>());
        extractedTableColumnSetting(SHEET_COLUMN, sheetColumnSetting);

        return sheetColumnSetting;
    }


    public static Map<String, Map<String,Map<String,Object>>> getCodeListSetting(){

        Map<String, Map<String,Map<String,Object>>> sheetColumnSetting = new LinkedHashMap<>();
        sheetColumnSetting.put(SHEET_CODE_LIST,new HashMap<>());
        extractedCodeListSetting(SHEET_CODE_LIST , sheetColumnSetting);

        return sheetColumnSetting;
    }

    private static void extractedTableSetting(String key,Map<String, Map<String, Map<String, Object>>> sheetColumnSetting) {

        extractedSetting(key, SHEET_TABLE_CATEGORY, sheetColumnSetting , Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);

        extractedSetting(key, SHEET_TABLE_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);

        extractedSetting(key, SHEET_TABLE_DESCRIPTION, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_TABLE_MODULE_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
    }

    private static void extractedTableColumnSetting(String key,Map<String, Map<String, Map<String, Object>>> sheetColumnSetting) {
        extractedSetting(key, SHEET_COLUMN_TABLE_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_COLUMN_ORDER, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_INT,99);
        extractedSetting(key, SHEET_COLUMN_COLUMN_NAME, sheetColumnSetting , Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_COLUMN_DESCRIPTION, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_COLUMN_TYPE, sheetColumnSetting , Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_COLUMN_LENGTH, sheetColumnSetting ,  Boolean.TRUE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_COLUMN_NULLABLE, sheetColumnSetting , Boolean.FALSE,ColumnConsts.DATA_TYPE_BOOL,null);
        extractedSetting(key, SHEET_COLUMN_FK_TABLE_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_BOOL,Boolean.FALSE);
        extractedSetting(key, SHEET_COLUMN_FK_COLUMN, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_BOOL,Boolean.FALSE);
        extractedSetting(key, SHEET_COLUMN_PK_TYPE, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_BOOL,Boolean.FALSE);
    }

    private static void extractedCodeListSetting(String key,Map<String, Map<String, Map<String, Object>>> sheetColumnSetting) {
        extractedSetting(key, SHEET_CODE_LIST_TABLE_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_CODE_LIST_KEY, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);
        extractedSetting(key, SHEET_CODE_LIST_NAME, sheetColumnSetting ,  Boolean.FALSE,ColumnConsts.DATA_TYPE_VARCHAR,null);

    }

    private static void extractedSetting(String key,String column, Map<String, Map<String, Map<String, Object>>> sheetColumnSetting, Boolean nullable , String dataType,Object defaultValue) {
        sheetColumnSetting.get(key).put(column,new HashMap<>());
        sheetColumnSetting.get(key).get(column).put(NULLABLE,nullable);
        sheetColumnSetting.get(key).get(column).put(DATA_TYPE,dataType);
        if(null != defaultValue){
            sheetColumnSetting.get(key).get(column).put(DEFAULT_VALUE,defaultValue);
        }
    }

    public static String getSheetColumnDefault(String sheetName,String columnName){
        return null != SheetConsts.getSheetColumnSetting().get(sheetName).get(columnName).get(SheetConsts.DEFAULT_VALUE) ? SheetConsts.getSheetColumnSetting().get(sheetName).get(columnName).get(SheetConsts.DEFAULT_VALUE).toString():null;
    }

    public static Boolean getSheetColumnNullable(String sheetName,String columnName){
        return Boolean.valueOf(SheetConsts.getSheetColumnSetting().get(sheetName).get(columnName).get(SheetConsts.COLUMN_NAME).toString());
    }

}
