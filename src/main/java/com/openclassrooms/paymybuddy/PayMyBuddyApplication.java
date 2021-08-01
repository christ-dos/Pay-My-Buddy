package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

	@Autowired
	private ITransactionService transactionService;

	@Autowired
	private ITransactionRepository transactionRepository;

	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

	@Override
	//@Transactional
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

	}
}


