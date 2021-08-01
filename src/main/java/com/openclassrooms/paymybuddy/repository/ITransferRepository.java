package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that handles database queries for transfert
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransferRepository extends CrudRepository<Transfer,Integer> {

}
