package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void setPerTest() {
//        userListMock = new HashSet<>();
//        friendListMock = new HashMap<>();
//        userListMock.add(User.builder()
//                .email("kikine@email.fr").password("password59")
//                .firstName("laetitia").lastName("Debus")
//                .balance(56.56).accountBank(896572)
//                .build());
//        userListMock.add(User.builder()
//                .email("luciole@email.fr").password("passpassword59")
//                .firstName("Antonio").lastName("Banderas")
//                .balance(16.25).accountBank(6921489)
//                .build());
//
//        friendListMock.put("lili@email.fr", "belo@email.fr");
//        friendListMock.put("lucitano@email.fr", "kikine@email.fr");

        userServiceTest = new UserService(userRepositoryMock);
    }

    @Test
    public void addFriendUserTest_whenFriendAddedLucindaDelasalleExist_thenVerifyAddFriendIsCalled() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "luciole@email.fr";

        User friend = User.builder()
                .email("luciole@email.fr")
                .password("monpassword")
                .firstName("Lucinda")
                .lastName("Delasalle")
                .balance(50.00)
                .accountBank(256942)
                .build();
        User user = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(30.50)
                .accountBank(170974)
                .friends(
                        new HashSet<>(Arrays.asList(
                                User.builder()
                                .email("sara@email.fr")
                                .password("1234")
                                .firstName("François")
                                .lastName("Dujardin")
                                .balance(10.50)
                                .accountBank(694281).build(),
                                User.builder()
                                .email("balade@email.fr")
                                .password("5689")
                                .firstName("Albert")
                                .lastName("Martin")
                                .balance(16.00)
                                .accountBank(894762).build())))
                .build();
        doNothing().when(userRepositoryMock).saveFriend(isA(String.class), isA(String.class));
        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(friend);
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(user);
        //WHEN
        userServiceTest.addFriendUser(userEmail, friendEmail);
        //THEN
        verify(userRepositoryMock, times(1)).saveFriend(userEmail, friendEmail);
    }

    @Test
    public void addFriendUserTest_whenFriendToAddNotFound_thenThrowsUserNotFoundException() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = null;
        //WHEN
        //THEN
        verify(userRepositoryMock, times(0)).saveFriend(userEmail, friendEmail);
        assertThrows(UserNotFoundException.class,
                () -> userServiceTest.addFriendUser(userEmail, friendEmail));
    }
}
