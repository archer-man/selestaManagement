package com.archer.selestaManagement.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.archer.selestaManagement.dao.ComponentUpdateDAO;
import com.archer.selestaManagement.entity.Component;
import com.archer.selestaManagement.repository.ComponentsRepository;
import com.archer.selestaManagement.service.ExcelUploadService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.archer.selestaManagement.service.ComponentService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/components")
public class ComponentController {

    private ComponentsRepository componentsRepo;
    private ComponentService componentService;

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
        List<Component> components = new ArrayList<Component>();
        List result = new ArrayList<String>();
        var dbList = getAll();
        if (ExcelUploadService.isValidExcelFile(file)) {
            try {
                components = ExcelUploadService.getComponentsDataFromExcel1(file.getInputStream());
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
            var partnumbersOfDbComponents = new ArrayList<String>();
            var partnumbersOfAddComponents = new ArrayList<String>();
            for (Component component : dbList) {
                partnumbersOfDbComponents.add(component.getFootprint());
            }
            for (Component item : components) {
                partnumbersOfAddComponents.add(item.getFootprint());
            }
            Iterator<Component> componentIterator = components.iterator();
            for (String addComponent : partnumbersOfAddComponents) {
                if (partnumbersOfDbComponents.contains(addComponent)){
                   var index = partnumbersOfDbComponents.indexOf(addComponent);
                   for (int i=0; i<=index; i++){
                       result.add("0");
                   }
                    for (Component item : dbList) {
                        if (item.getFootprint() == addComponent){
                            result.add(item.getAmount().toString());
                        } else {
                            result.add("0");
                        }
                    }
                }
            }
            for (Component component : dbList) {
                if (componentIterator.hasNext()) {
                    Component componentToAdd = componentIterator.next();
                    //if (component.getFootprint().equals(componentToAdd.getFootprint())) {
                    if (dbList.getFootprint().equals(componentToAdd.getFootprint())) {
                        result.add(component.getAmount().toString());
                        //.setAmount(component.getAmount().add(componentToAdd.getData().getAmount()));
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

    @PostMapping(value = "/upload-customers-data")
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
        //this.customerService.saveCustomersToDatabase(file);
        /*return ResponseEntity;
                .ok(Map.of("Message" , " Customers data uploaded and saved to database successfully"));*/
    }
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

   /* @PostMapping(value = "/upload-file", produces = "application/json")
    public List<String> uploadFileForImport(@RequestParam("file") MultipartFile file){
        List components1 = new ArrayList<Component>();
        List components2 = new ArrayList<String>();
        if(ExcelUploadService.isValidExcelFile(file)){
            try {
                List<Component> components = ExcelUploadService.getComponentsDataFromExcel1(file.getInputStream());
                return getAllSame(components);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        } else if (file.isEmpty()) {
            System.out.println("please select a file!");
        } else {
            throw new IllegalArgumentException("The file is not a valid excel file");
        }
        return components2;
    }*/
    @PostMapping(value = "/addition-customers-data")
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
        //this.customerService.saveCustomersToDatabase(file);
        /*return ResponseEntity;
                .ok(Map.of("Message" , " Customers data uploaded and saved to database successfully"));*/
    }

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

    @PostMapping(value = "/substraction-customers-data")
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
            //this.customerService.saveCustomersToDatabase(file);
        /*return ResponseEntity;
                .ok(Map.of("Message" , " Customers data uploaded and saved to database successfully"));*/
        } else {
            throw new IllegalArgumentException("База пустая.");
        }
    }

   /* @GetMapping("/page/{pageno}")
    public String findPaginated(@PathVariable int pageno, Model m) {

        Page<Payment> paylist = customerService.getByPaginate(pageno, 2);
        m.addAttribute("emp", paylist);
        m.addAttribute("currentPage", pageno);
        m.addAttribute("totalPages", paylist.getTotalPages());
        m.addAttribute("totalItem", paylist.getTotalElements());
        return "index";
    }*/
    //@RequestMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}", method = RequestMethod.GET)
    @GetMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}")
    public Page<Component> paymentPagination(@PathVariable Integer pageNumber, @PathVariable Integer pageSize){

        return componentService.getEmployeePagination(pageNumber,pageSize, null);
    }

    /*@RequestMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}/{sortProperty}",
            method = RequestMethod.GET)*/
    @GetMapping(value = "/pagingAndShortingEmployees/{pageNumber}/{pageSize}/{sortProperty}")
    public Page<Component> paymentPagination(@PathVariable Integer pageNumber,
                                             @PathVariable Integer pageSize,
                                             @PathVariable String sortProperty) {
        return componentService.getEmployeePagination(pageNumber, pageSize, sortProperty);
    }

    /*@GetMapping
    public ResponseEntity<List<Customer>> getCustomers(){
        return new ResponseEntity<>(customerService.getCustomers(), HttpStatus.FOUND);
    }*/
}
