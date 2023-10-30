package com.example.cruddata.util;

import com.example.cruddata.service.imp.SystemServiceImp;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


    public static ByteArrayOutputStream genExcelFile(List<String> columns , String fileName) throws Exception{
            String filename = fileName+".xls" ;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(fileName);

            AtomicInteger index = new AtomicInteger();

            HSSFRow rowHead = sheet.createRow((short)0);
            columns.forEach( item ->{
                rowHead.createCell(index.get()).setCellValue(item);
                index.getAndIncrement();
            });

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                workbook.write(bos);
            } finally {
                bos.close();
            }

            workbook.close();
            log.info("Your excel file has been generated!");
            return bos;
    }
}
