package com.archer.selestaManagement.service;

import com.archer.selestaManagement.entity.Payment;
import com.archer.selestaManagement.repository.PaymentsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {
    private PaymentsRepository paymentsRepository;

    public void saveCustomersToDatabase(MultipartFile file){
        if(ExcelUploadService.isValidExcelFile(file)){
            try {
                List<Payment> payments = ExcelUploadService.getCustomersDataFromExcel(file.getInputStream());
                this.paymentsRepository.saveAll(payments);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
    }

    public List<Payment> getCustomers(){
        return paymentsRepository.findAll();
    }
}
