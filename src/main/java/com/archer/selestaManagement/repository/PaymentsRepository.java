package com.archer.selestaManagement.repository;

import com.archer.selestaManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payment, Integer> {

}
