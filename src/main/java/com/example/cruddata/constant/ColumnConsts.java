package com.example.cruddata.constant;

public class ColumnConsts {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_ORDER = "display_order";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String OWNER_ID = "owner_id";
    public static final String CREATE_BY_ID = "create_by_id";
    public static final String UPDATE_BY_ID = "update_by_id";
    public static final String IS_DEFAULT = "is_default";
    public static final String STATUS = "status";
    public static final String IS_DELETED = "is_deleted";

    public static final String DATABASE_TYPE = "database_type";
    public static final String DRIVER_CLASS_NAME = "driver_class_name";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String METADATA_TABLE_PREFIX = "metadata_table_prefix";
    public static final String METADATA_DATABASE_NAMING = "metadata_database_naming";
    public static final String BUSINESS_TABLE_PREFIX = "business_table_prefix";
    public static final String BUSINESS_DATABASE_NAMING = "business_database_naming";

    public static final String DATA_TYPE_INT = "INT";
    public static final String DATA_TYPE_BOOL = "BOOL";

    public static final String DATA_TYPE_BIGINT = "BIGINT";

    public static final String DATA_TYPE_FLOAT = "FLOAT";

    public static final String DATA_TYPE_DOUBLE = "DOUBLE";

    public static final String DATA_TYPE_DECIMAL = "DECIMAL";

    public static final String DATA_TYPE_DATE = "DATE";

    public static final String DATA_TYPE_CHAR = "CHAR";

    public static final String DATA_TYPE_VARCHAR = "VARCHAR";

    public static final String DATA_TYPE_TEXT = "TEXT";

    public static String getDataTypeToCode(String dataType){
        if(dataType.equals(DATA_TYPE_INT)||dataType.equals(DATA_TYPE_BIGINT)){
            return "integer";
        }else if(dataType.equals(DATA_TYPE_BOOL)){
            return "boolean";
        } else{
            return "string";
        }
    }

}
