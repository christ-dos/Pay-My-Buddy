package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.UpdateCurrentUser;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.EmailNotMatcherException;
import com.openclassrooms.paymybuddy.exception.PasswordNotMatcherException;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.exception.UserNotFoundException;
import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IFriendRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(SecurityUtilities.class)
public class UserServiceTest {
    @Mock
    private IUserRepository userRepositoryMock;

    @Mock
    private IFriendRepository friendRepositoryMock;

    private UserService userServiceTest;

    private Pageable pageable;


    @BeforeEach
    public void setPerTest() {

        userServiceTest = new UserService(userRepositoryMock, friendRepositoryMock);
        pageable = PageRequest.of(1, 5);
    }

    @Test
    public void getUsersTest_thenReturnListWithTwoElements() {
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

    /*-----------------------------------------------------------------------------------------------------
                                            Tests View addfriend
         ------------------------------------------------------------------------------------------------------*/
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

    @Test
    public void addFriendCurrentUserListTest_whenFriendAddedFrancoisExistInDBAndIsNotPresentInListFriend_thenVerifyAddFriendIsCalled() {
        //GIVEN
        String userEmail = SecurityUtilities.getCurrentUser();
        String friendEmail = "françois@email.fr";

        User user = User.builder()
                .email("françois@email.fr")
                .password("monTropToppassword")
                .firstName("François")
                .lastName("Dujardin")
                .balance(30.50)
                .accountBank(170974).build();

        List<Friend> friends = new ArrayList<>(Arrays.asList(
                new Friend(userEmail, "sara@email.fr", LocalDateTime.now()),
                new Friend(userEmail, "amartin@email.fr", LocalDateTime.now())
        ));

        Friend friendToAdd = new Friend(userEmail, friendEmail, LocalDateTime.now());

        Page<Friend> friendsPage = new PageImpl<>(friends);

        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(user);
        when(friendRepositoryMock.findByUserEmailOrderByDateAddedDesc(userEmail, null)).thenReturn(friendsPage);
        when(friendRepositoryMock.save(isA(Friend.class))).thenReturn(friendToAdd);
        //WHEN
        Friend userAdded = userServiceTest.addFriendCurrentUserList(friendEmail);
        //THEN
        verify(friendRepositoryMock, times(1)).save(isA(Friend.class));
        assertEquals(userEmail, userAdded.getUserEmail());
        assertEquals("françois@email.fr", userAdded.getFriendEmail());
    }

    @Test
    public void addFriendCurrentUserListTest_whenFriendAddedFrancoisExistInDBAndIsPresentInListFriend_thenThrowsUserAlreadyExistException() {
        //GIVEN
        String userEmail = SecurityUtilities.getCurrentUser();
        String friendEmail = "françois@email.fr";

        User user = User.builder()
                .email("françois@email.fr")
                .password("monTropToppassword")
                .firstName("François")
                .lastName("Dujardin")
                .balance(30.50)
                .accountBank(170974).build();

        List<Friend> friends = new ArrayList<>(Arrays.asList(
                new Friend(userEmail, "françois@email.fr", LocalDateTime.now()),
                new Friend(userEmail, "amartin@email.fr", LocalDateTime.now())
        ));

        Page<Friend> friendsPage = new PageImpl<>(friends);

        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(user);
        when(friendRepositoryMock.findByUserEmailOrderByDateAddedDesc(userEmail, null)).thenReturn(friendsPage);
        //WHEN
        //THEN
        assertThrows(UserAlreadyExistException.class, () -> userServiceTest.addFriendCurrentUserList(friendEmail));
        verify(friendRepositoryMock, times(0)).save(isA(Friend.class));
    }

    @Test
    public void addFriendCurrentUserListTest_whenFriendAddedNotExistInDB_thenThrowsUserNotFoundException() {
        //GIVEN
        String userEmail = SecurityUtilities.getCurrentUser();
        String friendEmail = "wiwi@email.fr";

        when(userRepositoryMock.findByEmail(friendEmail)).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(UserNotFoundException.class, () -> userServiceTest.addFriendCurrentUserList(friendEmail));
        verify(friendRepositoryMock, times(0)).save(isA(Friend.class));
    }


    //******************************Test getFriendListCurrentUserEmailPaged*********************************
    @Test
    public void getFriendListByCurrentUserEmailPagedTest_whenUserEmailIsCurrentUser_thenReturnListFriend() {
        //GIVEN
//        String userEmail = "dada@email.fr";

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
        Page<Friend> friendsPage = new PageImpl<>(friends);

        when(friendRepositoryMock.findByUserEmailOrderByDateAddedDesc(isA(String.class), isA(Pageable.class))).thenReturn(friendsPage);
        friends.stream().map(friend -> {
            when(userRepositoryMock.findByEmail(anyString())).thenReturn(user1, user2);
            return new FriendList(userMock.getEmail(), userMock.getFirstName(), userMock.getLastName());
        }).collect(Collectors.toList());
        //WHEN
        Page<FriendList> resultListFriend = userServiceTest.getFriendListByCurrentUserEmailPaged(pageable);
        //THEN
        assertEquals(2, resultListFriend.stream().count());
        assertEquals("françois@email.fr", resultListFriend.getContent().get(0).getEmail());
        assertEquals("François", resultListFriend.getContent().get(0).getFirstName());
        assertEquals("Dujardin", resultListFriend.getContent().get(0).getLastName());
        assertEquals("amartin@email.fr", resultListFriend.getContent().get(1).getEmail());
        assertEquals("Albert", resultListFriend.getContent().get(1).getFirstName());
        assertEquals("Martin", resultListFriend.getContent().get(1).getLastName());
        verify(userRepositoryMock, times(2)).findByEmail(any(String.class));
    }

    //******************************Test getFriendListCurrentUserEmail************************************
    @Test
    public void getFriendListByCurrentUserEmailTest_whenUserEmailIsCurrentUser_thenReturnListFriend() {
        //GIVEN
        String userEmail = SecurityUtilities.getCurrentUser();

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
        Page<Friend> friendsPage = new PageImpl<>(friends);

        when(friendRepositoryMock.findByUserEmailOrderByDateAddedDesc(userEmail, null)).thenReturn(friendsPage);
        friends.stream().map(friend -> {
            when(userRepositoryMock.findByEmail(anyString())).thenReturn(user1, user2);
            return new FriendList(userMock.getEmail(), userMock.getFirstName(), userMock.getLastName());
        }).collect(Collectors.toList());
        //WHEN
        List<FriendList> resultListFriend = userServiceTest.getFriendListByCurrentUserEmail();
        //THEN
        assertEquals(2, resultListFriend.stream().count());
        assertEquals("françois@email.fr", resultListFriend.get(0).getEmail());
        assertEquals("François", resultListFriend.get(0).getFirstName());
        assertEquals("Dujardin", resultListFriend.get(0).getLastName());
        assertEquals("amartin@email.fr", resultListFriend.get(1).getEmail());
        assertEquals("Albert", resultListFriend.get(1).getFirstName());
        assertEquals("Martin", resultListFriend.get(1).getLastName());
        verify(userRepositoryMock, times(2)).findByEmail(any(String.class));
    }



/*-----------------------------------------------------------------------------------------------------
                                        Tests View profile
     ------------------------------------------------------------------------------------------------------*/

    @Test
    public void updateProfileTest_whenUserExistInDB_thenReturnUserUpdated() {
        //GIVEN
        UpdateCurrentUser updateCurrentUser = new UpdateCurrentUser();
        updateCurrentUser.setEmail(SecurityUtilities.getCurrentUser());
        updateCurrentUser.setFirstName("Damien");
        updateCurrentUser.setLastName("Sanches");
        updateCurrentUser.setPassword("passpass");
        updateCurrentUser.setConfirmPassword("passpass");

        User userToUpdate = User.builder()
                .email(SecurityUtilities.getCurrentUser())
                .password("pass")
                .firstName("Damien")
                .lastName("Sanchez")
                .balance(30.50)
                .accountBank(170974).build();

        User userUpdated = User.builder()
                .email(SecurityUtilities.getCurrentUser())
                .firstName("Damien")
                .lastName("Sanches")
                .password("passpass")
                .balance(30.50)
                .accountBank(170974)
                .build();

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userToUpdate);
        when(userRepositoryMock.save(isA(User.class))).thenReturn(userUpdated);
        //WHEN
        User userSavedResult = userServiceTest.updateProfile(updateCurrentUser);
        //THEN
        assertEquals(SecurityUtilities.getCurrentUser(), userSavedResult.getEmail());
        assertEquals("Damien", userSavedResult.getFirstName());
        assertEquals("Sanches", userSavedResult.getLastName());
        assertEquals("passpass", userSavedResult.getPassword());
        verify(userRepositoryMock, times(1)).save(isA(User.class));

    }

    @Test
    public void updateProfileTest_whenPasswordNotMatchWithConfirmPassword_thenThrowsPasswordNotMatcherException() {
        //GIVEN
        User currentUser = User.builder()
                .email(SecurityUtilities.getCurrentUser())
                .firstName("Damien")
                .lastName("Sanchez")
                .password("passpass")
                .build();

        UpdateCurrentUser updateCurrentUser = new UpdateCurrentUser();
        updateCurrentUser.setFirstName("Damien");
        updateCurrentUser.setLastName("Sanchez");
        updateCurrentUser.setPassword("passpasspass");
        updateCurrentUser.setConfirmPassword("monpassword");

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(currentUser);
        //WHEN
        //THEN
        assertThrows(PasswordNotMatcherException.class, () -> userServiceTest.updateProfile(updateCurrentUser));
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }
    /*-----------------------------------------------------------------------------------------------------
                                        Tests View signUp
     ------------------------------------------------------------------------------------------------------*/
    @Test
    public void addUserTest_UserNotExistInDBAndEmailAndPasswordMatch_thenReturnUserAdded() {
        //GIVEN
        AddUser userToAdd = new AddUser(
                "Leina","Machin","passpass","passpass","emailnotexist@email.fr","emailnotexist@email.fr",123456);

        User userAdded = User.builder()
                .firstName("Leina")
                .lastName("Machin")
                .password("passpass")
                .email("emailnotexist@email.fr")
                .build();
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        when(userRepositoryMock.save(isA(User.class))).thenReturn(userAdded);
        //WHEN
        User useAdded = userServiceTest.addUser(userToAdd);
        //THEN
        assertEquals("emailnotexist@email.fr",useAdded.getEmail());
        assertEquals("Leina",useAdded.getFirstName());
        assertEquals("Machin",useAdded.getLastName());
        assertEquals("passpass",useAdded.getPassword());
        verify(userRepositoryMock,times(1)).findByEmail(isA(String.class));
        verify(userRepositoryMock,times(1)).save(isA(User.class));

    }

    @Test
    public void addUserTest_UserExistInDB_thenThrowsUserAlreadyExistException() {
        //GIVEN
        AddUser userToAdd = new AddUser(
                "Leina","Machin","passpass","passpass","emailnotexist@email.fr","emailnotexist@email.fr",123456);

        User user = User.builder()
                .firstName("Leina")
                .lastName("Machin")
                .password("passpass")
                .email("emailnotexist@email.fr")
                .build();
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(user);
        //WHEN
        //THEN
        assertThrows(UserAlreadyExistException.class, () -> userServiceTest.addUser(userToAdd));
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }

    @Test
    public void addUserTest_UserNotExistInDbButEmailNotMatchConfirmEmail_thenThrowsEmailNotMatcherException() {
        //GIVEN
        AddUser userToAdd = new AddUser(
                "Leina","Machin","passpass","passpass","emailnotexist@email.fr","emailnotmatch@email.fr",123456);

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(EmailNotMatcherException.class, () -> userServiceTest.addUser(userToAdd));
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }

    @Test
    public void addUserTest_UserNotExistInDbButPasswordNotMatchConfirmPassword_thenThrowsPasswordNotMatcherException() {
        //GIVEN
        AddUser userToAdd = new AddUser(
                "Leina","Machin","pass","passpass","emailnotexist@email.fr","emailnotexist@email.fr",123456);

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(PasswordNotMatcherException.class, () -> userServiceTest.addUser(userToAdd));
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }

}
