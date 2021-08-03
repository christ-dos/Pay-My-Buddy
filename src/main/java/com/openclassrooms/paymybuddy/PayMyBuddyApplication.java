package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Class that start the application
 *
 * @author Christine Duarte
 */
@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

//    @Autowired
//    private ITransactionService transactionService;
//
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

        //Optional<User> user = userService.getUserByEmail("tela@email.fr");
        //Iterable<User> users = userService.getUsers();
        //users.forEach(user1 -> System.out.println(user1));
        //System.out.println(user);
        //User user = userService.addUser("tela@email.fr");
        //System.out.println(user);
        //User user1 = userRepo.getUser("lili@email.fr");
//		System.out.println(user1.getFirstName());
//		User user2 = userRepo.getUser("tela@email.fr");
//		System.out.println(user2.getFirstName());
        //transactionService.addTransaction("dada@email.fr","luluM@email.fr",55.0 ,"movies");
        //System.out.println(user1);
        //userService.addFriendUser("dada@email.fr","ggpassain@email.fr");
        //Optional<User> userByEmail = userRepo.findUserByEmail("lili@email.fr");
        //System.out.println(userByEmail.get());
//		Set<IDisplayingTransaction> listtrans = transactionService.getTransactionsByEmail("dada@email.fr");
//		listtrans.forEach(trans -> System.out.println(trans.getFirstName()));
//        User user  = userRepository.findUserByEmailByEmail("dada@email.fr");
//        System.out.println(user);
       // List<User> list = user.getFriends();
       // list.forEach(x-> x.getFriends().forEach(y-> System.out.println("coucou:" + y.getEmail())));
//          Friend friend =  new Friend("dada@email.fr","tela@email.fr",LocalDateTime.now());
//            System.out.println(userService.addFriendUser("dada@email.fr","tela@email.fr"));
//           User friend = userService.getUserByEmail("dada@email.fr");
//        List<User> friends =  friend.getFriends();
//        friends.forEach(x->System.out.println(x.getEmail()));
//
//        User user = userService.getUserByEmail("ggpassain@email.fr");
//        List<User> users =  user.getUsers();
//        users.forEach(x->System.out.println(x.getEmail()));
//        Friend friend1 =  new Friend("dada@email.fr","lili@email.fr",LocalDateTime.now());
//        System.out.println(friendRepository.save(friend1));
    }
}


