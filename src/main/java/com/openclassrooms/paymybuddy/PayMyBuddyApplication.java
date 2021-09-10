package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;

/**
 * Class that start the application
 *
 * @author Christine Duarte
 */
@SpringBootApplication
public class PayMyBuddyApplication {

    /**
     * Method main that initiate the application
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(PayMyBuddyApplication.class, args);
    }

}


