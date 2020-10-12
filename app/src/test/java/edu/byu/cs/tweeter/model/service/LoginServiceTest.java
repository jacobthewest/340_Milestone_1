package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;

public class LoginServiceTest {

    private LoginRequest validRequest;
    private LoginRequest invalidRequestOne;
    private LoginRequest invalidRequestTwo;
    private LoginResponse successResponse;
    private LoginResponse failureResponse;
    private LoginService loginServiceSpy;

    @BeforeEach
    public void setup() throws IOException {
        String username = "@TestUser";
        String password = "password";
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg", "password");
        AuthToken authToken = new AuthToken(username);

        // Setup request objects to use in the tests
        validRequest = new LoginRequest(username, password);
        invalidRequestOne = new LoginRequest("", "password");
        invalidRequestTwo = new LoginRequest("@TestUser", "");

        // Setup a mock ServerFacade that will return known responses
        successResponse = new LoginResponse(user, authToken);
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        Mockito.when(mockServerFacade.login(validRequest)).thenReturn(successResponse);

        failureResponse = new LoginResponse("An exception occured");
        Mockito.when(mockServerFacade.login(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.login(invalidRequestTwo)).thenReturn(failureResponse);

        // Create a LoginService instance and wrap it with a spy that will use the mock service
        loginServiceSpy = Mockito.spy(new LoginService());
        Mockito.when(loginServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testLogin_validRequest_correctResponse() throws IOException {
        LoginResponse response = loginServiceSpy.login(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_emptyUsername() throws IOException {
        LoginResponse response = loginServiceSpy.login(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_emptyAuthToken() throws IOException {
        LoginResponse response = loginServiceSpy.login(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }
}
