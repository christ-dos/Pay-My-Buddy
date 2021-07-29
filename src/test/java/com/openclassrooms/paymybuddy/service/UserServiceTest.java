package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private IUserRepository userRepositoryMock;

    private UserService userServiceTest;


    @BeforeEach
    public void setPerTest() {

        userServiceTest = new UserService(userRepositoryMock);
    }

    @Test
    public void getUserstest_thenReturnListWithTwoElements() {
        //GIVEN
        Set<User> usersSetMock = new HashSet<>(Arrays.asList(
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
    public void addFriendUserTest_whenFriendAddedLucindaDelasalleExist_thenVerifyAddFriendIsCalled() {
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
        Set<IFriendList> friendListMock = new HashSet<>();

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
        verify(userRepositoryMock, times(1)).findFriendListByEmail(userEmail);

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
