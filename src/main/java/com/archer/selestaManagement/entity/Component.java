package com.archer.selestaManagement.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
public class Component {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String footprint;

    private String characteristic1;

    private String characteristic2;

    private String characteristic3;

    private String characteristic4;

    @NotNull
    private BigDecimal amount;

    public Component() {}

    public Component(String name, String footprint, String characteristic1, String characteristic2, String characteristic3, String characteristic4, BigDecimal amount) {
        this.name = name;
        this.footprint = footprint;
        this.characteristic1 = characteristic1;
        this.characteristic2 = characteristic2;
        this.characteristic3 = characteristic3;
        this.characteristic4 = characteristic4;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFootprint() {
        return footprint;
    }

    public void setFootprint(String lastName) {
        this.footprint = lastName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCharacteristic1() {
        return characteristic1;
    }

    public void setCharacteristic1(String characteristic1) {
        this.characteristic1 = characteristic1;
    }

    public String getCharacteristic2() {
        return characteristic2;
    }

    public void setCharacteristic2(String characteristic2) {
        this.characteristic2 = characteristic2;
    }

    public String getCharacteristic3() {
        return characteristic3;
    }

    public void setCharacteristic3(String characteristic3) {
        this.characteristic3 = characteristic3;
    }

    public String getCharacteristic4() {
        return characteristic4;
    }

    public void setCharacteristic4(String characteristic4) {
        this.characteristic4 = characteristic4;
    }
}
