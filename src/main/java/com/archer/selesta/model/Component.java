package com.archer.selesta.model;

import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

@Data
@Entity
@Table(name = "components")
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long quantity;
}
