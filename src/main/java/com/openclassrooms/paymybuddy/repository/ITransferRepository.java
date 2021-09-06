package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that handles database queries for transfer's table
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransferRepository extends JpaRepository<Transfer, Integer> {
    /**
     * Query that permit find the list of transfer for a user email
     * the result is ordered in descending order by date of addition
     *
     * @param userEmail userEmail A String containing the email of the user
     * @param pageable  Abstract interface for pagination information.
     * @returnA list of {@link Transfer} object
     */
    Page<Transfer> findTransfersByUserEmailOrderByDateDesc(String userEmail, Pageable pageable);

}
