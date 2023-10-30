package com.example.cruddata.constant;

import java.util.ArrayList;
import java.util.List;

public class RedisKeyConsts {
    public static final String DATA_SOURCE = "data_source";

    public static final String TENANT = "tenant";

    public static final String SELECTION_VALUE = "selection_value";



    public static List<String> getRedisInitKeys(){
        List<String> keys = new ArrayList<>();
        keys.add(RedisKeyConsts.DATA_SOURCE);
        keys.add(RedisKeyConsts.TENANT);
        keys.add(RedisKeyConsts.SELECTION_VALUE);
        return  keys;
    }
}
