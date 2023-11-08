package com.example.cruddata.util;

import com.example.cruddata.exception.BusinessException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;

public class EncryptUtil {
    public static final String DATA_DATE = "data_date";
    public static final String DATA_VALUE = "data_value";
    public static final String DATA_SOAP = "data_soap";
    public static final String DATA_SOAP_VALUE = "datagret";

    public static String getMD5(String str) {
        String ret = null;
        try {
            // 生成一個MD5加密計算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 計算md5函數
            md.update(str.getBytes());
            // digest()最後確定返回md5 hash值，返回值為8為字符串。因為md5 hash值是16位的hex值，實際上就是8位的字符
            // BigInteger函數則將8位的字符串轉換成16位hex值，用字符串來表示；得到字符串形式的hash值
            ret = new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            //throw new SpeedException("MD5加密出現錯誤");
            e.printStackTrace();
        }
        return ret;
    }

    public static String genToken(Map<String,String> parameters) {
        String ret = null;
            if(null != parameters.get(DATA_DATE) && null != parameters.get(DATA_VALUE) && null != parameters.get(DATA_SOAP)){
                ret = getMD5(parameters.get(DATA_DATE)+"_"+parameters.get(DATA_VALUE)+"_"+parameters.get(DATA_SOAP));
            }else{
                throw new BusinessException("data not complete!",parameters );
            }
        return ret;
    }
}
