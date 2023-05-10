package com.archer.selesta.service;

import com.archer.selesta.model.Component;
import com.archer.selesta.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentService {

    private final ComponentRepository componentRepository;

    @Autowired
    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public Component findById(Long id){
        return componentRepository.getReferenceById(id);
    }

    public List<Component> findAll(){
        return componentRepository.findAll();
    }

    public Component saveComponent(Component component){
        return componentRepository.save(component);
    }

    public void deleteById(Long id){
        componentRepository.deleteById(id);
    }
}
