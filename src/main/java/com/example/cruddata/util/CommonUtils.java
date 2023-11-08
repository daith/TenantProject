package com.example.cruddata.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {

    public static  <T> boolean equals(List<T> list1, List<T> list2) {

        if (list1 == list2) {
            return true;
        }

        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        // to prevent wrong results on {a,a,a} and {a,b,c}
        // iterate over list1 and then list2
        return list1.stream()
                .filter(val -> !list2.contains(val))
                .collect(Collectors.toList())
                .isEmpty()  &&
                list2.stream()
                        .filter(val -> !list1.contains(val))
                        .collect(Collectors.toList())
                        .isEmpty();
    }

    public static String objectToJsonStr(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String data=  mapper.writeValueAsString(obj);
        return data;

    }
}
