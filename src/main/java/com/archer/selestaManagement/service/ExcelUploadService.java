package com.archer.selestaManagement.service;

import com.archer.selestaManagement.entity.Payment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }
   public static List<Payment> getCustomersDataFromExcel(InputStream inputStream){
        List<Payment> customers = new ArrayList<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
           XSSFSheet sheet = workbook.getSheet("Customers");
           int rowIndex =0;
           for (Row row : sheet){
               if (rowIndex ==0){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Payment payment = new Payment();
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> payment.setFirstName(cell.getStringCellValue());
                       case 1 -> payment.setLastName(cell.getStringCellValue());
                       case 2 -> payment.setAmount(BigDecimal.valueOf((cell.getNumericCellValue())));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               customers.add(payment);
           }
       } catch (IOException e) {
           e.getStackTrace();
       }
       return customers;
   }

}
