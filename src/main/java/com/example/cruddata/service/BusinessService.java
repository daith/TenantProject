package com.example.cruddata.service;

import java.util.List;
import java.util.Map;

public interface BusinessService {
    List<Map<String, Object>> list(String resourceName);
}
