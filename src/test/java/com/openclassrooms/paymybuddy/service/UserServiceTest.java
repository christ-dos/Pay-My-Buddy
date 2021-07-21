package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    IUserRepository userRepositoryMock;

    UserService userServiceTest;
    @Mock
    Set<User> userListMock;

    @Mock
    Map<String, String> friendListMock;

    @BeforeEach
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

        userServiceTest = new UserService(userRepositoryMock);
    }

    @Test
    public void addFriendUserTest_whenFriendAddedIsRomyVandermeerche_thenSetShouldContainThreeElements(){
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "luciole@email.fr";
        Optional<User> optionalUser = Optional.of(new User("luciole@email.fr", "monpassword", "Lucinda", "Delasalle", 50.00, 256942, null, null));
        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(optionalUser);
        doNothing().when(userRepositoryMock).saveFriend(isA(String.class), isA(String.class));
        //WHEN
        userServiceTest.addFriendUser(userEmail,friendEmail);
        //THEN
        verify(userRepositoryMock, times(1)).saveFriend(userEmail, friendEmail);
        //assertEquals(3, friendListMock.size());
    }
}
