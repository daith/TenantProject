package com.example.cruddata.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public interface DocumentService {

    public String processFile2Str(File pdfFile);

    public ByteArrayOutputStream excelDownloadＶaiTable(Long dataSourceId, String tableName, Long tenantId) throws Exception;

    public List<Object> importDataViaFile(Long dataSourceId, String tableName, Long tenantId , MultipartFile file) throws Exception;
}
