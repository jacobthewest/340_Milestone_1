package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.AuthToken;
import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.shared.model.service.response.LoginResponse;

public class LoginServiceProxyTest {

    private LoginRequest validRequest;
    private LoginRequest invalidRequestOne;
    private LoginRequest invalidRequestTwo;
    private LoginResponse successResponse;
    private LoginResponse failureResponse;
    private LoginServiceProxy loginServiceProxySpy;

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

        // Create a LoginService instance and wrap it with a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service
        loginServiceProxySpy = Mockito.spy(new LoginServiceProxy());
        Mockito.when(loginServiceProxySpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testLogin_validRequest_correctResponse() throws IOException {
        LoginResponse response = loginServiceProxySpy.login(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testLogin_invalidRequest_emptyUsername() throws IOException {
        LoginResponse response = loginServiceProxySpy.login(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testLogin_invalidRequest_emptyAuthToken() throws IOException {
        LoginResponse response = loginServiceProxySpy.login(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }
}
