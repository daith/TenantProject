package com.example.cruddata.service.imp;

import com.example.cruddata.dto.SystemConfigDto;
import com.example.cruddata.repository.system.SystemConfigRepository;
import com.example.cruddata.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemServiceImp implements SystemService {
    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Override
    public List<SystemConfigDto> getSystemConfigs(){
        List<SystemConfigDto> result = new ArrayList<>();
        systemConfigRepository.findAll().forEach(item->{
            SystemConfigDto data = new SystemConfigDto();
            data.setId(item.getId());
            data.setName(item.getName());
            result.add(data);
        });

        return result;
    }
}
