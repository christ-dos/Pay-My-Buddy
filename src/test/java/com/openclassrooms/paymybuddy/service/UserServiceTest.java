package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private IUserRepository userRepositoryMock;

    private UserService userServiceTest;

    @Mock
    private User user;

    @Mock
    private User friend;

    private Set<IFriendList> friendListMock;


    @BeforeEach
    public void setPerTest() {
        friend = User.builder()
                .email("luciole@email.fr")
                .password("monpassword")
                .firstName("Lucinda")
                .lastName("Delasalle")
                .balance(50.00)
                .accountBank(256942)
                .build();
        user = User.builder()
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

        userServiceTest = new UserService(userRepositoryMock);
    }
    @Test
    public void getUserstest_thenReturnListWithTwo(){
        //GIVEN
        Set<User>usersSetMock = new HashSet<>(Arrays.asList(
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
        Iterable<User> usersIterable= userServiceTest.getUsers();
        Iterator it = usersIterable.iterator();
        //method that count the iterable
        while (it.hasNext()) {
            it.next();
            count++;
        }
        //THEN
        //set contain 3 elements
        assertEquals(3,count);
        assertEquals(usersSetMock,usersIterable);

    }
    @Test
    public void addFriendUserTest_whenFriendAddedLucindaDelasalleExist_thenVerifyAddFriendIsCalled()  {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "luciole@email.fr";

        doNothing().when(userRepositoryMock).saveFriend(isA(String.class), isA(String.class));
        //WHEN
        userServiceTest.addFriendUser(userEmail, friendEmail);

        //THEN
        verify(userRepositoryMock, times(1)).saveFriend(userEmail, friendEmail);
    }

    @Test
    public void getFriendListByEmailTest_whenUserEmailIsKikine_thenReturnListFriend() {
        //GIVEN
        FriendList friend1 = new FriendList();
        FriendList friend2 = new FriendList();
        friendListMock = new HashSet<IFriendList>();

        friend1.setEmail("sara@email.fr");
        friend1.setFirstName("François");
        friend1.setLastName("Dujardin");

        friend2.setEmail("amartin@email.fr");
        friend2.setFirstName("Albert");
        friend2.setLastName("Martin");

        friendListMock.add(friend1);
        friendListMock.add(friend2);
        String userEmail = "kikine@email.fr";
        when(userRepositoryMock.findFriendListByEmail(isA(String.class))).thenReturn(friendListMock);
        //WHEN
        Set<IFriendList> resultListFriend = userServiceTest.getFriendListByEmail(userEmail);
        //THEN
        assertEquals(2, resultListFriend.size());
        assertEquals(friendListMock, resultListFriend);

    }
}
