package com.archer.selestaManagement.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
public class Resistor {
    @Id
    @GeneratedValue
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String partNumber;

    @NotNull
    private BigDecimal ohmValue;

    @NotNull
    private BigDecimal tolerance;
    @NotNull
    private BigDecimal amount;

    public Resistor() {}

    public Resistor(String partNumber, String name, BigDecimal ohmValue, BigDecimal tolerance, BigDecimal amount) {
        this.partNumber = partNumber;
        this.name = name;
        this.ohmValue = ohmValue;
        this.tolerance = tolerance;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOhmValue() {
        return ohmValue;
    }

    public void setOhmValue(BigDecimal ohmValue) {
        this.ohmValue = ohmValue;
    }

    public BigDecimal getTolerance() {
        return tolerance;
    }

    public void setTolerance(BigDecimal tolerance) {
        this.tolerance = tolerance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}