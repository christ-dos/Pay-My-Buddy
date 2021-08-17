package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface that handles database queries for transfert
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransferRepository extends JpaRepository<Transfer,Integer> {

    Page<Transfer> findTransfersByUserEmailOrderByDateDesc(String userEmail, Pageable pageable);

}
