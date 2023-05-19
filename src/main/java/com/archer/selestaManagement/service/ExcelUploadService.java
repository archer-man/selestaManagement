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
           XSSFSheet sheet = workbook.getSheetAt(0);
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
                       //case 1 -> payment.setLastName(cell.getStringCellValue());
                       case 2 -> payment.setLastName(cell.getStringCellValue());
                       //case 2 -> payment.setAmount(BigDecimal.valueOf((cell.getNumericCellValue())));
                       case 3 -> payment.setAmount(BigDecimal.valueOf((cell.getNumericCellValue())));
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

    public static List<Payment> addCustomersDataFromExcelToDB(InputStream inputStream, List<Payment> dbList) {

        List<Payment> customersToAdd = getCustomersDataFromExcel(inputStream);
        Iterator<Payment> paymentIterator = customersToAdd.iterator();
        Iterator<Payment> dblistIterator = dbList.iterator();

            //String processedData = String.format("%s: %s", nameIterator.next(), codeIterator.next());
        //while () {
            for (Payment payment : dbList) {
                if (paymentIterator.hasNext()) {
                    Payment paymentToAdd = paymentIterator.next();
                    if (payment.getFirstName().equals(paymentToAdd.getFirstName())) {
                        payment.setAmount(payment.getAmount().add(paymentToAdd.getAmount()));
                    }
                } else {
                    break;
                }
            }
        //}
        return dbList;
    }
    public static List<Payment> substractCustomersDataFromExcelToDB(InputStream inputStream, List<Payment> dbList) {

        List<Payment> customersToSubstract = getCustomersDataFromExcel(inputStream);
        Iterator<Payment> paymentIterator = customersToSubstract.iterator();
        Iterator<Payment> dblistIterator = dbList.iterator();
        //while (paymentIterator.hasNext()) {
            //String processedData = String.format("%s: %s", nameIterator.next(), codeIterator.next());
            for (Payment payment : dbList) {
                if (paymentIterator.hasNext()) {
                    Payment paymentToSubstract = paymentIterator.next();
                    if (payment.getFirstName().equals(paymentToSubstract.getFirstName())) {
                        payment.setAmount(payment.getAmount().subtract(paymentToSubstract.getAmount()));
                    }
                } else {
                    break;
                }
            }
        //}
        return dbList;
    }

}
