package com.archer.selestaManagement.service;

import com.archer.selestaManagement.entity.Component;
import com.archer.selestaManagement.repository.ComponentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ComponentService {
    private ComponentsRepository componentsRepository;

    public List<Component> getCustomers(){
        return componentsRepository.findAll();
    }

    public Page<Component> getComponentPagination(Integer pageNumber, Integer pageSize, String sortProperty) {
        Pageable pageable = null;
        if(null!=sortProperty){
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,sortProperty);
        }else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,"id");
        }
        return componentsRepository.findAll(pageable);
    }
}
