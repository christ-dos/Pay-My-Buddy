package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    IUserRepository userRepositoryMock;

    UserService userService;

    List<User> userListMock;

    public void setPerTest(){
        userListMock = new ArrayList<User>() ;
        //TODO creer les nouvelles instance avec un Builder et enlever le constructeur ds User
        userListMock.add(new User("kikine@email.fr","password59","laetitia","Debus",56.56,359803));
        userListMock.add(new User("liciole@email.fr","passpassword59","Antonio","Banderas",16.25,6921489));

        UserService.builder()
                .userRepository(userRepositoryMock).build();
    }

    @Test
    public void addUserTest_whenUserRomyVandermeerche_thenReturnUserAddedAndArrayListShouldContainThreeElements(){
        //GIVEN
        User userTest = User.builder()
                .email("lolo@email.fr")
                .password("toppassword")
                .firstName("Louis")
                .lastName("Vuiton")
                .balance(150.89)
                .accountBank(225600)
                .build();
        //WHEN
        //User user = userService.addUser(userTest.getEmail());
        //THEN
    }
}
