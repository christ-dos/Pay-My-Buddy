package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.FriendAlreadyExistException;
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

        friendListMock.put("lili@email.fr", "belo@email.fr");
        friendListMock.put("lucitano@email.fr", "kikine@email.fr");

        userServiceTest = new UserService(userRepositoryMock);
    }

    @Test
    public void addFriendUserTest_whenFriendAddedLucindaDelasalleExist_thenSetShouldContainThreeElements() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "luciole@email.fr";
        User friend = new User(
                "luciole@email.fr", "monpassword", "Lucinda", "Delasalle", 50.00,
                256942, null, null);

        User user = new User("kikine@email.fr", "monTropToppassword", "Christine",
                "Deldalle", 30.50, 170974,
                new HashSet<>(Arrays.asList(new User(
                        "sara@email.fr", "1234", "François", "Dujardin", 10.00,
                        694281, null, null),
                        new User(
                        "balade@email.fr", "5689", "Albert", "Martin", 16.00,
                        894762, null, null))), null);

        doNothing().when(userRepositoryMock).saveFriend(isA(String.class), isA(String.class));
        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(friend);
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(user);
        //WHEN
        userServiceTest.addFriendUser(userEmail, friendEmail);
        //THEN
        verify(userRepositoryMock, times(1)).saveFriend(userEmail, friendEmail);
        //assertEquals(3, friendListMock.size());
    }

    @Test
    public void addFriendUserTest_whenFriendToAddAlreadyExist_thenThrowsFriendAlreadyExistException() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "sara@email.fr";
        User friend = new User(
                "sara@email.fr", "monpassword", "Lucinda", "Delasalle", 50.00,
                256942, null, null);

        User user = new User("kikine@email.fr", "monTropToppassword", "Christine",
                "Deldalle", 30.50, 170974,
                new HashSet<>(Arrays.asList(new User(
                                "sara@email.fr", "1234", "François", "Dujardin", 10.00,
                                694281, null, null),
                        new User(
                                "balade@email.fr", "5689", "Albert", "Martin", 16.00,
                                894762, null, null))), null);

        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(friend);
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(user);
        //WHEN
        //THEN
        verify(userRepositoryMock, times(0)).saveFriend(userEmail, friendEmail);
        assertThrows(FriendAlreadyExistException.class,
                () -> userServiceTest.addFriendUser(userEmail, friendEmail));
    }
}
