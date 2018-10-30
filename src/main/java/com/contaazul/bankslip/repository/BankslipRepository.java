package com.contaazul.bankslip.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contaazul.bankslip.model.Bankslip;

public interface BankslipRepository extends JpaRepository<Bankslip, UUID>{

}
