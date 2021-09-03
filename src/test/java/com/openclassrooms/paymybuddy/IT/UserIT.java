package com.openclassrooms.paymybuddy.IT;

import com.openclassrooms.paymybuddy.DTO.AddUser;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.UserAlreadyExistException;
import com.openclassrooms.paymybuddy.model.User;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class of Integration test for {@link User}
 *
 * @author Christine Duarte
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserIT {

    /**
     * An instance of {@link MockMvc} that permit simulate a request HTTP
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * An instance of {@link WebApplicationContext}
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * Method that build the mockMvc with the context and springSecurity
     */
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    /*------------------------------------------------------------------------------------------------------------
                                    Integration tests view sign Up
    --------------------------------------------------------------------------------------------------------------*/

    /**
     * Method that test signUp in view signup when user email not exist in DB
     * and field email match with  field confirmEmail
     * and field password match with  field confirmPassword
     * then return user added and view login
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBAndEmailMatchConfirmEmailAndPasswordMatchConfirmPassword_thenReturnUserAddedAndMessageSuccess() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(2))
                .andExpect(model().attributeExists("addUser", "message"))
                .andExpect(model().attribute("message", is("Account registered with success!")))
                .andExpect(model().attribute("addUser", hasProperty("firstName", is("Ines"))))
                .andExpect(model().attribute("addUser", hasProperty("lastName", is("Martin"))))
                .andExpect(model().attribute("addUser", hasProperty("email", is("inim@email.fr"))))
                .andExpect(model().attribute("addUser", hasProperty("confirmEmail", is("inim@email.fr"))))
                .andExpect(model().attribute("addUser", hasProperty("password", is("passpass"))))
                .andExpect(model().attribute("addUser", hasProperty("confirmPassword", is("passpass"))))
                .andDo(print());
    }

    //******************************Tests Errors in fields*************************************

    /**
     * Method that test signUp  when user not exist in DB but field firstName is blank
     * then return error in field firstName
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldFirstNameIsBlank_thenReturnErrorInFieldsFirstName() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr"
                , 123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "firstName", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field lastName is blank
     * then return error in field lastName
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldLastNameIsBlank_thenReturnErrorInFieldsLastName() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "lastName", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field email is blank
     * then return error in field email
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldEmailIsBlank_thenReturnErrorInFieldsEmail() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "email", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field confirmEmail is blank
     * then return error in field confirmEmail
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButFieldConfirmEmailIsBlank_thenReturnErrorInFieldsConfirmEmail() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmEmail", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field password is blank
     * then return error in field password
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsBlank_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "        ", "passpass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field password is less 8 characters
     * then return error in field password
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsLess8_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "pass", "passpass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "Size"))
                .andDo(print());
    }

    /**
     * Method that test signUp  when user not exist in DB but field password is greater than 100 characters
     * then return error in field password
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordIsGreaterThan100_thenReturnErrorInFieldsPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspass", "passpass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "password", "Size"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but field confirmPassword is blank
     * then return error in field confirmPassword
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsBlank_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "        ", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but field confirmPassword is less 8 characters
     * then return error in field confirmPassword
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsLess8_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "pass", "inim@email.fr", "inim@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but field confirmPassword is greater than 100 characters
     * then return error in field confirmPassword
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButConfirmPasswordIsGreaterThan100_thenReturnErrorInFieldsConfirmPassword() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass",
                "passpasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspass", "inim@email.fr",
                "inim@email.fr", 123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but field accountBank is null
     * then return error in field accountBank
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButAccountBankIsNull_thenReturnErrorInFieldsAccountBank() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Ines", "Martin", "passpass", "passpass", "inim@email.fr", "inim@email.fr",
                null);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "accountBank", "NotNull"))
                .andDo(print());
    }

    //************************************************Test Exceptions view signup**********************************************

    /**
     * Method that test signUp when user already exist in DB
     * then throw a {@link UserAlreadyExistException}
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserAlreadyExistInDB_thenThrowsUserAlreadyExistException() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Damien", "Sanchez", "passpass", "passpass", "dada@email.fr", "dada@email.fr",
                123456);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmPassword())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "email", "EmailAlreadyExist"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but email and confirmEmail not match
     * then throw EmailNotMatcherException
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButEmailAndConfirmEmailNotMatch_thenThrowsUserEmailNotMatcherException() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Albert", "Masarin", "passpass", "passpass", "albert@email.fr", "emailNotMatche@email.fr",
                0123654);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmEmail())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmEmail", "ConfirmEmailNotMatcher"))
                .andDo(print());
    }

    /**
     * Method that test signUp when user not exist in DB but password and confirmPassword not match
     * then throw PasswordNotMatcherException
     *
     * @throws Exception
     */
    @Test
    public void signUpUserViewSignUp_whenUserNotExistInDBButPasswordAndConfirmPasswordNotMatch_thenThrowsUserPasswordNotMatcherException() throws Exception {
        //GIVEN
        AddUser addUser = new AddUser(
                "Albert", "Masarin", "passpass", "notmatch", "albert@email.fr", "albert@email.fr",
                0123654);
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/signup").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", addUser.getEmail())
                        .param("confirmEmail", addUser.getConfirmEmail())
                        .param("firstName", addUser.getFirstName())
                        .param("lastName", addUser.getLastName())
                        .param("password", addUser.getPassword())
                        .param("confirmPassword", addUser.getConfirmEmail())
                        .param("accountBank", String.valueOf(addUser.getAccountBank())))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("addUser"))
                .andExpect(model().attributeHasFieldErrorCode("addUser", "confirmPassword", "ConfirmPasswordNotMatcher"))
                .andDo(print());
    }

    /*------------------------------------------------------------------------------------------------------------
                                    Integration tests view home
    --------------------------------------------------------------------------------------------------------------*/

    /**
     * Method that test get user information in view home when current user is "dada@email.fr
     * then return current user information in view home
     *
     * @throws Exception
     */
    @Test
    public void getUserInformationHomeViewTest_whenCurrentUserIsDada_thenReturnFirstNameDamienAndLastNameSanchez() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/home")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("user", "lastBuddy", "lastTransaction"))
                .andExpect(model().attribute("user", hasProperty("email", Matchers.is("dada@email.fr"))))
                .andExpect(model().attribute("user", hasProperty("balance", Matchers.is(200.0))))
                .andExpect(model().attribute("user", hasProperty("firstName", Matchers.is("Damien"))))
                .andExpect(model().attribute("user", hasProperty("lastName", Matchers.is("Sanches"))))
                .andExpect(model().attribute("lastBuddy", hasProperty("firstName", Matchers.is("Elisabeth"))))
                .andExpect(model().attribute("lastBuddy", hasProperty("lastName", Matchers.is("Dupond"))))
                .andExpect(model().attribute("lastTransaction", hasProperty("firstName", Matchers.is("Lubin"))))
                .andExpect(model().attribute("lastTransaction", hasProperty("amount", Matchers.is(-15.0))))
                .andDo(print());
    }

 /*------------------------------------------------------------------------------------------------------------
                                    Integration tests view addfriend
    --------------------------------------------------------------------------------------------------------------*/

    /**
     * Method that test get list connection when url is correct "/addfriend"
     * then display view addfriend
     *
     * @throws Exception
     */
    @Test
    public void getListConnectionsViewAddfriendTest_thenStatusOk() throws Exception {
        //GIVEN
        User user = User.builder()
                .email("fifi@email.fr")
                .firstName("Filipe")
                .lastName("Delarue")
                .build();
        //WHEN
        //THEN
        mockMvc.perform(get("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendLists", "friendList"))
                .andDo(print());
    }

    /**
     * Method that test get friend's list  when url is wrong "/friend"
     * then return status not found
     *
     * @throws Exception
     */
    @Test
    public void getListConnectionsViewAddfriendTest_whenUrlTemplateIsWrong_thenStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/friend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass")))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Method that test add friend to list connection when user exist in DB
     * and friend is not present in list of friends
     * then the user can be added
     *
     * @throws Exception
     */
    @Test
    public void addFriendToListConnectionTest_whenUsersExistAndFriendIsNotInListFriend_thenReturnStatusIsOk() throws Exception {
        //GIVEN
        String userEmail = "tela@email.fr";
        User userFriendToAdd = User.builder()
                .email("lili@email.fr").firstName("Elisabeth").lastName("Dupont").build();
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", userFriendToAdd.getEmail())
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().hasNoErrors())
                .andDo(print());
    }

    /**
     * Method that test add friend to list connection when friend to add not exist in DB
     * then display message error in form "user not exist in DB"
     *
     * @throws Exception
     */
    @Test
    public void addFriendToListConnectionTest_whenEmailFriendNotExistInDB_thenReturnErrorInFieldEmailUserNotExistDB() throws Exception {
        //GIVEN
        String friendEmailNotExist = "wiwi@email.fr";
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("passpass"))
                        .param("email", friendEmailNotExist))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().attributeHasErrors())
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserNotExistDB"))
                .andDo(print());
    }

    /**
     * Method that test add friend to list connection when field email is empty
     * then return error in fields NotBlank
     *
     * @throws Exception
     */
    @Test
    public void addFriendToListConnectionTest_whenFieldEmailIsEmpty_thenReturnErrorFieldCanNotBeNull() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test add friend to list connection when friend already exist in list of friends
     * then throw friendEmailAlreadyExist
     *
     * @throws Exception
     */
    @Test
    public void addFriendToListConnectionTest_whenFriendEmailAlreadyExistInListFriend_thenReturnErrorFieldFriendAlreadyExist() throws Exception {
        //GIVEN
        String friendEmailAlreadyExist = "ggpassain@email.fr";
        String userEmail = "tela@email.fr";
        //WHEN

        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", friendEmailAlreadyExist)
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(view().name("addfriend"))
                .andExpect(model().attributeExists("friendList", "friendLists"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UserAlreadyExist"))
                .andDo(print());
    }

    /**
     * Method that test add friend to list connection when friend email is equal to the current user
     * then return error in field unableAddingOwnEmail
     *
     * @throws Exception
     */
    @Test
    public void addFriendToListConnectionTest_whenEmailToAddAndCurrentUserEmailIsEquals_thenReturnErrorFieldUnableAddingOwnEmail() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("pass"))
                        .param("email", "dada@email.fr"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friendList"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("friendList", "email", "UnableAddingOwnEmail"))
                .andDo(print());
    }

    /**
     * Method that test get list connection when url is correct "/addfriend"
     * and user is "dada@email.fr"
     * then display view addfriend with pagination
     *
     * @throws Exception
     */
    @Test
    public void getListConnectionsTest_whenEmailIsDada_thenReturnListOfFriendList() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/addfriend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user("dada@email.fr").password("passpass"))
                        .param("size", String.valueOf(5))
                        .param("page", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("friendLists", "friendList", "totalPages", "currentPage"))
                .andExpect(model().attribute("totalPages", is(1)))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("friendLists", Matchers.hasProperty("totalElements", equalTo(3L))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("firstName", is("Geraldine")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("lastName", is("Passain")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("ggpassain@email.fr")))))
                .andExpect(model().attribute("friendLists", hasItem(hasProperty("email", is("lili@email.fr")))))
                .andDo(print());
    }

    /*------------------------------------------------------------------------------------------------------------
                                  Integration tests  view profile
  --------------------------------------------------------------------------------------------------------------*/

    /**
     * Method that test get current user information in view profile when url is correct "/profile"
     * then display view profile
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfileAndGood_thenReturnStatusOK() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/profile")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", SecurityUtilities.getCurrentUser())
                        .param("password", "passpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().size(2))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("currentUser", "updateCurrentUser"))
                .andExpect(model().attribute("currentUser", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("currentUser", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("currentUser", hasProperty("lastName", is("Sanches"))))
                .andExpect(model().attribute("currentUser", hasProperty("password", is(containsString("$2a$")))))
                .andDo(print());
    }

    /**
     * Method that test get current user information in view profile when url is wong "/prof"
     * then return status not found
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void getCurrentUserInformationInProfileViewTest_whenUrlIsSlashProfAndWrong_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvc.perform(get("/prof"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Method that test update current user information when current user is "dada@email.fr
     * then return user updated
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIsDada_thenReturnUserDadaUpdated() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanches");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", "dada@email.fr")
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("email", is("dada@email.fr"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("firstName", is("Damien"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("lastName", is("Sanches"))))
                .andExpect(model().attribute("updateCurrentUser", hasProperty("password", is("passpasspass"))))
                .andDo(print());
    }

    /**
     * Method that test update current user information when firstName is blank
     * then return error NotBlank in field firstname
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenFirstNameIsBlank_thenReturnFieldErrorNotBlankInFieldFirstName() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "firstName", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test update current user information when lastName is blank
     * then return error in fields NotBlank in field lastname
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenLastNameIsBlank_thenReturnFieldErrorNotBlankInFieldLastName() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("");
        updateProfileCurrentUser.setPassword("passpasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "lastName", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test update current user information when password is less 8 characters
     * then return error in fields size in field password
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsLess8_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("pass");
        updateProfileCurrentUser.setConfirmPassword("pass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    /**
     * Method that test update current user information when password is greater than 100 characters
     * then return error in fields Size in field password
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenCurrentUserIsDadaAndPassWordIsGreaterThan100_thenReturnErrorSizeInFieldPassWord() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("passpasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspass");
        updateProfileCurrentUser.setConfirmPassword("passpasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspasspass");

        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "Size"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "Size"))
                .andDo(print());
    }

    /**
     * Method that test update current user information when password is blank
     * then return error in fields NotBlank in field password
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Christine");
        updateProfileCurrentUser.setLastName("Duhamel");
        updateProfileCurrentUser.setPassword("        ");
        updateProfileCurrentUser.setConfirmPassword("passpass");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "password", "NotBlank"))
                .andDo(print());

    }

    /**
     * Method that test update current user information when confirmPassword is blank
     * then return error in fields NotBlank in field confirmPassword
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenConfirmPassWordIsBlank_thenReturnErrorInFieldPassWordNotBlank() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("passpass");
        updateProfileCurrentUser.setConfirmPassword("         ");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "NotBlank"))
                .andDo(print());
    }

    /**
     * Method that test update current user information when confirmPassword not match with password
     * then throw PasswordNotMatcherException
     *
     * @throws Exception
     */
    @WithMockUser(username = "dada@email.fr", password = "passpass")
    @Test
    public void updateCurrentUserInformationTest_whenConfirmPassWordNotMatchWithPassword_thenThrowsPasswordNotMatcherException() throws Exception {
        //GIVEN
        AddUser updateProfileCurrentUser = new AddUser();
        updateProfileCurrentUser.setFirstName("Damien");
        updateProfileCurrentUser.setLastName("Sanchez");
        updateProfileCurrentUser.setPassword("password");
        updateProfileCurrentUser.setConfirmPassword("passwordnotmatch");
        //WHEN
        //THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/profile").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("email", updateProfileCurrentUser.getEmail())
                        .param("firstName", updateProfileCurrentUser.getFirstName())
                        .param("lastName", updateProfileCurrentUser.getLastName())
                        .param("password", updateProfileCurrentUser.getPassword())
                        .param("confirmPassword", updateProfileCurrentUser.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists("updateCurrentUser", "currentUser"))
                .andExpect(model().attributeHasFieldErrorCode("updateCurrentUser", "confirmPassword", "ConfirmPasswordNotMatch"))
                .andDo(print());
    }
}
