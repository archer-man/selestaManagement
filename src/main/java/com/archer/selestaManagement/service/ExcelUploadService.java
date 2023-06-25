package com.archer.selestaManagement.service;

import com.archer.selestaManagement.entity.Component;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<Component> getComponentsDataFromExcel(InputStream inputStream) {
        List<Component> components = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Component component = new Component();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0 -> component.setName(cell.getStringCellValue());
                        case 2 -> component.setFootprint(cell.getStringCellValue());
                        case 3 -> component.setAmount(BigDecimal.valueOf((cell.getNumericCellValue())));
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                components.add(component);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return components;
    }

    public static List<Component> getComponentsDataFromExcel1(InputStream inputStream) {
        List<Component> components = new ArrayList<>();
        var name = "";
        var designator = "";
        var isResistor = false;
        String resRegex = "\\b[R]{1}\\d*\\b";
        String resNameRegex = "\\b[R]{1}\\d*\\b";
        Pattern resPattern = Pattern.compile(resRegex);

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;
                Component component = new Component();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 1:
                            name = cell.getStringCellValue();
                            var divided = name.split(", ");
                            component.setName(divided[0]);
                            if (divided.length == 2) {
                                component.setCharacteristic1(divided[1]);
                            } else if (divided.length == 3) {
                                component.setCharacteristic1(divided[1]);
                                component.setCharacteristic2(divided[2]);
                            } else if (divided.length == 4) {
                                component.setCharacteristic1(divided[1]);
                                component.setCharacteristic2(divided[2]);
                                component.setCharacteristic3(divided[3]);
                            } else if (divided.length == 5) {
                                component.setCharacteristic1(divided[1]);
                                component.setCharacteristic2(divided[2]);
                                component.setCharacteristic3(divided[3]);
                                component.setCharacteristic4(divided[4]);
                            } else {
                                var string = divided[0];
                                string = string + " " + String.join(" ", Arrays.copyOfRange(divided, 5, divided.length));
                                component.setName(string);
                                component.setCharacteristic1(divided[1]);
                                component.setCharacteristic2(divided[2]);
                                component.setCharacteristic3(divided[3]);
                                component.setCharacteristic4(divided[4]);
                            }
                            break;
                        case 2:
                            component.setFootprint(cell.getStringCellValue());
                            break;
                        case 4:
                            component.setAmount(BigDecimal.valueOf((cell.getNumericCellValue())));
                            break;
                        default:
                            break;
                    }
                    cellIndex++;
                }
                components.add(component);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
        return components;
    }

    public static List<Component> addComponentsDataFromExcelToDB(InputStream inputStream, List<Component> dbList) {

        List<Component> componentsToAdd = getComponentsDataFromExcel(inputStream);
        Iterator<Component> componentIterator = componentsToAdd.iterator();
        for (Component component : dbList) {
            if (componentIterator.hasNext()) {
                Component componentToAdd = componentIterator.next();
                if (component.getName().equals(componentToAdd.getName())) {
                    component.setAmount(component.getAmount().add(componentToAdd.getAmount()));
                }
            } else {
                break;
            }
        }
        return dbList;
    }

    public static List<Component> substractComponentsDataFromExcelToDB(InputStream inputStream, List<Component> dbList) {

        List<Component> componentsToSubstract = getComponentsDataFromExcel(inputStream);
        Iterator<Component> componentIterator = componentsToSubstract.iterator();
        Iterator<Component> dblistIterator = dbList.iterator();
        for (Component component : dbList) {
            if (componentIterator.hasNext()) {
                Component componentToSubstract = componentIterator.next();
                if (component.getName().equals(componentToSubstract.getName())) {
                    component.setAmount(component.getAmount().subtract(componentToSubstract.getAmount()));
                }
            } else {
                break;
            }
        }
        return dbList;
    }

    public static void generateExcel(HttpServletResponse response, List<Component> dbList) throws Exception {
        List<Component> components = dbList;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Компоненты");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Имя");
        row.createCell(1).setCellValue("Part Number");
        row.createCell(2).setCellValue("Характеристика 1");
        row.createCell(3).setCellValue("Характеристика 2");
        row.createCell(4).setCellValue("Характеристика 3");
        row.createCell(5).setCellValue("Характеристика 4");
        row.createCell(6).setCellValue("Количество");
        int dataRowIndex = 1;
        for (Component component : dbList) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(component.getName());
            dataRow.createCell(1).setCellValue(component.getFootprint());
            dataRow.createCell(2).setCellValue(component.getCharacteristic1());
            dataRow.createCell(3).setCellValue(component.getCharacteristic2());
            dataRow.createCell(4).setCellValue(component.getCharacteristic3());
            dataRow.createCell(5).setCellValue(component.getCharacteristic4());
            dataRow.createCell(6).setCellValue(component.getAmount().toString());
            dataRowIndex++;
        }
        ServletOutputStream ops = response.getOutputStream();
        workbook.write(ops);
        workbook.close();
        ops.close();

    }

}
