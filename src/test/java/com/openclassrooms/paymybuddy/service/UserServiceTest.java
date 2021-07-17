package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    IUserRepository userRepositoryMock;

    UserService userServiceTest;
    @Mock
    Set<User> userListMock;

    @Mock
    Map<String, String> friendListMock;

    public void setPerTest(){
        userListMock = new HashSet<>();
        friendListMock = new HashMap<>();
        userListMock.add(User.builder()
                .email("kikine@email.fr").password("password59")
                .firstName("laetitia").lastName("Debus")
                .balance(56.56).accountBank(896572)
                .build());
        userListMock.add(User.builder()
                .email("luciole@email.fr").password("passpassword59")
                .firstName("Antonio").lastName("Banderas")
                .balance(16.25).accountBank(6921489)
                .build());
        friendListMock.put("lili@email.fr" , "belo@email.fr");
        friendListMock.put("lucitano@email.fr" , "kikine@email.fr");
        userServiceTest = UserService.builder()
                .userRepository(userRepositoryMock).build();
    }

    @Test
    public void addFriendUserTest_whenFriendAddedIsRomyVandermeerche_thenSetShouldContainThreeElements(){
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "luciole@email.fr";
        //WHEN
        userServiceTest.addFriendUser(userEmail,friendEmail);
        //THEN
    }
}
