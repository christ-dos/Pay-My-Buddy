package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * Interface that handles database queries for transfert
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransferRepository extends CrudRepository<Transfer,Integer> {

    List<Transfer> findTransfersByUserEmailOrderByDateDesc(String userEmail);
}
