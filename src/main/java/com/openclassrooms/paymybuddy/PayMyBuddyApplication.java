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
public class PayMyBuddyApplication implements CommandLineRunner {

    @Autowired
    private ITransactionRepository transactionrepo;

    @Autowired
    private UserService userService;

    /**
     * Method main that initiate the application
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(PayMyBuddyApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        System.out.println("Hello World !");

//        User user  = userRepository.findUserByEmailByEmail("dada@email.fr");
//        System.out.println(user);
        // List<User> list = user.getFriends();
        // list.forEach(x-> x.getFriends().forEach(y-> System.out.println("coucou:" + y.getEmail())));
//        List<Transaction> liTrans = transactionrepo.findTransactionsByEmitterEmailOrReceiverEmailOrderByDateDesc("dada@email.fr", "dada@email.fr");
//        liTrans.forEach(x -> System.out.println(x.getReceiverEmail()));
//        User user = userService.getUserByEmail("ggpassain@email.fr");
//        List<User> users =  user.getUsers();
//        users.forEach(x->System.out.println(x.getEmail()));
//        Friend friend1 =  new Friend("dada@email.fr","lili@email.fr",LocalDateTime.now());
//        System.out.println(friendRepository.save(friend1));
    }
}


