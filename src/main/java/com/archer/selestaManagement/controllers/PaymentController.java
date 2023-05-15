package com.archer.selestaManagement.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.archer.selestaManagement.dao.PaymentUpdateDAO;
import com.archer.selestaManagement.entity.Payment;
import com.archer.selestaManagement.repository.PaymentsRepository;
import com.archer.selestaManagement.service.ExcelUploadService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import com.archer.selestaManagement.service.CustomerService;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private PaymentsRepository paymentsRepo;
    private CustomerService customerService;

    @Autowired
    public void setPaymentsRepo(PaymentsRepository paymentsRepo) {
        this.paymentsRepo = paymentsRepo;
    }

    @GetMapping
    public List<Payment> getAll() {
        return paymentsRepo.findAll();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public List<Payment> updatePayments(final @RequestBody List<PaymentUpdateDAO> list) {
        List<Payment> toDelete = list.stream().filter(o -> o.getAction() == PaymentUpdateDAO.Action.DELETE)
                .map(PaymentUpdateDAO::getData).collect(Collectors.toList());
        List<Payment> toUpdate = list.stream().filter(o -> o.getAction() == PaymentUpdateDAO.Action.UPDATE)
                .map(PaymentUpdateDAO::getData).collect(Collectors.toList());

        List<Payment> result = new ArrayList<>();

        if(!toDelete.isEmpty()){
            paymentsRepo.deleteAllInBatch(toDelete);
        }
        if(!toUpdate.isEmpty()){
            result = paymentsRepo.saveAll(toUpdate);
        }

        return result;
    }

    @PostMapping(value = "/upload-customers-data")
    public void uploadCustomersData(@RequestParam("file") MultipartFile file){
        if(ExcelUploadService.isValidExcelFile(file)){
            try {
                List<Payment> payments = ExcelUploadService.getCustomersDataFromExcel(file.getInputStream());
                paymentsRepo.saveAll(payments);
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

    /*@GetMapping
    public ResponseEntity<List<Customer>> getCustomers(){
        return new ResponseEntity<>(customerService.getCustomers(), HttpStatus.FOUND);
    }*/
}
