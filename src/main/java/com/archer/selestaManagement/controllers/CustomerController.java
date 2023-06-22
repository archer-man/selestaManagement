package ru.mmote.crudexample.controllers;

import com.archer.selestaManagement.service.ComponentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("customers")
public class CustomerController {
    private ComponentService componentService;




}
