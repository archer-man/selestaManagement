package com.archer.selestaManagement.controllers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.archer.selestaManagement.dao.ComponentUpdateDAO;
import com.archer.selestaManagement.entity.Component;
import com.archer.selestaManagement.repository.ComponentsRepository;
import com.archer.selestaManagement.service.ExcelUploadService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/components")
public class ComponentController {

    private ComponentsRepository componentsRepo;

    @Autowired
    public void setComponentsRepo(ComponentsRepository componentsRepo) {
        this.componentsRepo = componentsRepo;
    }

    @GetMapping
    public List<Component> getAll() {
        return componentsRepo.findAll();
    }
    @PostMapping(value = "/get-same-components")
    public List<String> getAllSame(@RequestParam("file") MultipartFile file) {
        List components = new ArrayList<Component>();
        List result = new ArrayList<String>();
        var dbList = getAll();
        if (ExcelUploadService.isValidExcelFile(file)) {
            try {
                components = ExcelUploadService.getComponentsDataFromExcel1(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
            Iterator<Component> componentIterator = components.iterator();
            for (Component component : dbList) {
                if (componentIterator.hasNext()) {
                    Component componentToAdd = componentIterator.next();
                    if (component.getFootprint().equals(componentToAdd.getFootprint())) {
                        result.add(component.getAmount().toString());
                    } else {
                        result.add("0");
                    }
                } else {
                    break;
                }
            }
        }
        return result;
    }

    @PostMapping(value = "/find-component", consumes = "application/json")
    public List<Component> searchComponent(final @RequestBody List<String> name) {
        List components = new ArrayList<Component>();
        List result = new ArrayList<Component>();
        var dbList = getAll();
        for (Component component : dbList){
            if (component.getName().toLowerCase().equals(name.get(0).toLowerCase())){
                result.add(component);
            }
        }
        return result;
    }


    @PostMapping(consumes = "application/json", produces = "application/json")
    public List<Component> updatePayments(final @RequestBody List<ComponentUpdateDAO> list) {
        List<Component> toDelete = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.DELETE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());
        List<Component> toUpdate = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.UPDATE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());

        List<Component> result = new ArrayList<>();

        if(!toDelete.isEmpty()){
            componentsRepo.deleteAllInBatch(toDelete);
        }
        if(!toUpdate.isEmpty()){
            result = componentsRepo.saveAll(toUpdate);
        }

        return result;
    }

    /*@PostMapping(value = "/upload-customers-data")
    public void uploadCustomersData(@RequestParam("file") MultipartFile file){
        if(ExcelUploadService.isValidExcelFile(file)){
            try {
                List<Component> components = ExcelUploadService.getComponentsDataFromExcel1(file.getInputStream());
                componentsRepo.saveAll(components);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        } else if (file.isEmpty()) {
            System.out.println("please select a file!");
        } else {
            throw new IllegalArgumentException("The file is not a valid excel file");
        }
    }*/
    @PostMapping(value = "/upload-file")
    public List<Component> uploadFileForImport(@RequestParam("file") MultipartFile file){
        List components1 = new ArrayList<Component>();
        if(ExcelUploadService.isValidExcelFile(file)){
            try {
                List<Component> components = ExcelUploadService.getComponentsDataFromExcel1(file.getInputStream());
                return components;
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        } else if (file.isEmpty()) {
            System.out.println("please select a file!");
        } else {
            throw new IllegalArgumentException("The file is not a valid excel file");
        }
        return components1;
    }

    /*@PostMapping(value = "/addition-customers-data")
    public void addCustomersData(@RequestParam("file") MultipartFile file){
        if (!getAll().isEmpty()) {
            if (ExcelUploadService.isValidExcelFile(file)) {
                try {
                    List<Component> components = ExcelUploadService.addComponentsDataFromExcelToDB(file.getInputStream(), getAll());
                    componentsRepo.saveAll(components);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The file is not a valid excel file");
                }
            } else if (file.isEmpty()) {
                System.out.println("please select a file!");
            } else {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        } else {
            throw new IllegalArgumentException("База пустая.");
        }
    }*/

    @PostMapping(value = "/addition-components-data")
    public List<Component> addData(final @RequestBody List<ComponentUpdateDAO> list) {

        List<Component> toDelete = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.DELETE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());
        List<Component> toUpdate = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.UPDATE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());
        List<Component> result = new ArrayList<>();
        if (!toDelete.isEmpty()) {
            componentsRepo.deleteAllInBatch(toDelete);
        }
        if (!toUpdate.isEmpty()) {
            var dbList = getAll();
            Iterator<ComponentUpdateDAO> componentIterator = list.iterator();
            Iterator<Component> dblistIterator = dbList.iterator();
            for (Component component : dbList) {
                if (componentIterator.hasNext()) {
                    ComponentUpdateDAO componentToAdd = componentIterator.next();
                    if (component.getFootprint().equals(componentToAdd.getData().getFootprint())) {
                        component.setAmount(component.getAmount().add(componentToAdd.getData().getAmount()));
                    } else {
                        result.add(componentToAdd.getData());
                    }
                } else {
                    break;
                }
            }
            for (Component component : result){
                dbList.add(component);
            }
            componentsRepo.saveAll(dbList);
        }
        return result;
    }

    @PostMapping(value = "/substract-components-data")
    public List<Component> substractData(final @RequestBody List<ComponentUpdateDAO> list) {

        List<Component> toDelete = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.DELETE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());
        List<Component> toUpdate = list.stream().filter(o -> o.getAction() == ComponentUpdateDAO.Action.UPDATE)
                .map(ComponentUpdateDAO::getData).collect(Collectors.toList());

        List<Component> result = new ArrayList<>();

        if (!toDelete.isEmpty()) {
            componentsRepo.deleteAllInBatch(toDelete);
        }
        if (!toUpdate.isEmpty()) {
            var dbList = getAll();
            Iterator<ComponentUpdateDAO> componentIterator = list.iterator();
            Iterator<Component> dblistIterator = dbList.iterator();

            for (Component component : dbList) {
                if (componentIterator.hasNext()) {
                    ComponentUpdateDAO componentToAdd = componentIterator.next();
                    if (component.getFootprint().equals(componentToAdd.getData().getFootprint())) {
                        component.setAmount(component.getAmount().subtract(componentToAdd.getData().getAmount()));
                    }
                } else {
                    break;
                }
            }
            for (Component component : result){
                dbList.add(component);
            }
            componentsRepo.saveAll(dbList);
        }
        return result;
    }

    /*@PostMapping(value = "/substraction-customers-data")
    public void substractCustomersData(@RequestParam("file") MultipartFile file) {
        if (!getAll().isEmpty()) {
            if (ExcelUploadService.isValidExcelFile(file)) {
                try {
                    List<Component> components = ExcelUploadService.substractComponentsDataFromExcelToDB(file.getInputStream(), getAll());
                    componentsRepo.saveAll(components);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The file is not a valid excel file");
                }
            } else if (file.isEmpty()) {
                System.out.println("please select a file!");
            } else {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        } else {
            throw new IllegalArgumentException("База пустая.");
        }
    }*/

    @GetMapping("/excel")
    public void generateExcelReport(HttpServletResponse response) throws Exception{
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment;filename=components.xls";
        response.setHeader(headerKey, headerValue);
        ExcelUploadService.generateExcel(response, componentsRepo.findAll());
        response.flushBuffer();
    }

    public Page<Component> getComponentPagination(Integer pageNumber, Integer pageSize, String sortProperty) {
        Pageable pageable = null;
        if(null!=sortProperty){
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,sortProperty);
        }else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,"id");
        }
        return componentsRepo.findAll(pageable);
    }

    @GetMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}")
    public Page<Component> paymentPagination(@PathVariable Integer pageNumber, @PathVariable Integer pageSize){

        return getComponentPagination(pageNumber,pageSize, null);
    }

    @GetMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}/{sortProperty}")
    public Page<Component> paymentPagination(@PathVariable Integer pageNumber,
                                             @PathVariable Integer pageSize,
                                             @PathVariable String sortProperty) {
        return getComponentPagination(pageNumber, pageSize, sortProperty);
    }
}
