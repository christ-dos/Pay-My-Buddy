package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private IUserRepository userRepositoryMock;

    @Mock
    private IFriendRepository friendRepositoryMock;

    private UserService userServiceTest;


    @BeforeEach
    public void setPerTest() {

        userServiceTest = new UserService(userRepositoryMock, friendRepositoryMock);
    }

    @Test
    public void getUserstest_thenReturnListWithTwoElements() {
        //GIVEN
        List<User> usersSetMock = new ArrayList<>(Arrays.asList(
                User.builder()
                        .email("vanessa@email.fr").firstName("Vanessa")
                        .lastName("Paradis").password("vava2020")
                        .balance(15.58).accountBank(897235)
                        .build(),
                User.builder()
                        .email("kelly@email.fr").firstName("Kelly")
                        .lastName("Minogue").password("kiki89")
                        .balance(55.58).accountBank(890365)
                        .build(),
                User.builder()
                        .email("beber@email.fr").firstName("Justine")
                        .lastName("Biber").password("bebe2896")
                        .balance(85.98).accountBank(100358)
                        .build()));
        int count = 0;
        when(userRepositoryMock.findAll()).thenReturn(usersSetMock);
        //WHEN
        Iterable<User> usersIterable = userServiceTest.getUsers();
        Iterator<User> it = usersIterable.iterator();
        //method that count the iterable
        while (it.hasNext()) {
            it.next();
            count++;
        }
        //THEN
        //userIterable contain 3 elements
        assertEquals(3, count);
        assertEquals(usersSetMock, usersIterable);
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void addFriendUserTest_whenFriendAddedfrancoisExistInDBButIsNotPresentInListFriend_thenVerifyAddFriendIsCalled() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "françois@email.fr";

        Friend friendToAdd = new Friend(userEmail, friendEmail, LocalDateTime.now());
        when(friendRepositoryMock.save(isA(Friend.class))).thenReturn(friendToAdd);
        //WHEN
        Friend userAdded = userServiceTest.addFriendUser(userEmail, friendEmail);
        //THEN
        verify(friendRepositoryMock, times(1)).save(isA(Friend.class));
        assertEquals("kikine@email.fr", userAdded.getUserEmail());
        assertEquals("françois@email.fr", userAdded.getFriendEmail());
    }

    @Test
    public void getFriendListByEmailTest_whenUserEmailIsKikine_thenReturnListFriend() {
        //GIVEN
        String user = "kikine@email.fr";

        User user1 = User.builder()
                .email("françois@email.fr")
                .password("monTropToppassword")
                .firstName("François")
                .lastName("Dujardin")
                .balance(30.50)
                .accountBank(170974).build();
        User user2 = User.builder()
                .email("amartin@email.fr")
                .password("monTropToppassword")
                .firstName("Albert")
                .lastName("Martin")
                .balance(30.50)
                .accountBank(170974).build();

        List<Friend> friends = new ArrayList<>(Arrays.asList(
                new Friend("kikine@email.fr", "sara@email.fr", LocalDateTime.now()),
                new Friend("kikine@email.fr", "amartin@email.fr", LocalDateTime.now())
        ));
        User userMock = mock(User.class);
        when(friendRepositoryMock.findByUserEmailOrderByDateAddedDesc(user)).thenReturn(friends);
        friends.stream().map(friend -> {
            when(userRepositoryMock.findByEmail(anyString())).thenReturn(user1, user2);
            return new FriendList(userMock.getEmail(), userMock.getFirstName(), userMock.getLastName());
        }).collect(Collectors.toList());
        //WHEN
        List<FriendList> resultListFriend = userServiceTest.getFriendListByEmail("kikine@email.fr");
        //THEN
        assertEquals(2, resultListFriend.size());
        assertEquals("françois@email.fr", resultListFriend.get(0).getEmail());
        assertEquals("François", resultListFriend.get(0).getFirstName());
        assertEquals("Dujardin", resultListFriend.get(0).getLastName());
        assertEquals("amartin@email.fr", resultListFriend.get(1).getEmail());
        assertEquals("Albert", resultListFriend.get(1).getFirstName());
        assertEquals("Martin", resultListFriend.get(1).getLastName());
        verify(userRepositoryMock, times(2)).findByEmail(any(String.class));
    }

    @Test
    public void getUserByEmailTest_whenUserIsKikineAndExistInDB_thenReturnUserKikine() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        User user = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(30.50)
                .accountBank(170974).build();
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(user);
        //WHEN
        User userResult = userServiceTest.getUserByEmail(user.getEmail());
        //THEN
        assertEquals(userEmail, userResult.getEmail());
        assertEquals("monTropToppassword", userResult.getPassword());
        assertEquals("Christine", userResult.getFirstName());
        assertEquals("Deldalle", userResult.getLastName());
        verify(userRepositoryMock, times(1)).findByEmail(userEmail);
    }

    @Test
    public void getUserByEmailTest_whenUserNotExistInDB_thenReturnUserNull() {
        //GIVEN
        String userEmail = "lisa@email.fr";
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        //WHEN
        User userResult = userServiceTest.getUserByEmail(userEmail);
        //THEN
        assertNull(userResult);
    }



}
