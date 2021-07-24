package com.openclassrooms.paymybuddy;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openclassrooms.paymybuddy.repository.IUserRepository;
import com.openclassrooms.paymybuddy.service.IUserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

	@Autowired
	private IUserService userService;

	@Autowired
	private IUserRepository userRepo;

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
		userService.addFriendUser("dada@email.fr","lili@email.fr" );
		//System.out.println(user1);
		userService.addFriendUser("dada@email.fr","luluM@email.fr");
		//Optional<User> userByEmail = userRepo.findUserByEmail("lili@email.fr");
		//System.out.println(userByEmail.get());
		//Set<IFriendList> listUser = userService.getFriendListByEmail("lili@email.fr");
		//listUser.forEach(user -> System.out.println(user.getEmail()));

	}
}


