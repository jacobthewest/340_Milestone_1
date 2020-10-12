package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;

public class LogoutServiceTest {

    private LogoutRequest validRequest;
    private LogoutRequest invalidRequestOne;
    private LogoutRequest invalidRequestTwo;
    private LogoutResponse successResponse;
    private LogoutResponse failureResponse;
    private LogoutService logoutServiceSpy;

    @BeforeEach
    public void setup() throws IOException {
        String username = "@TestUser";
        String password = "password";
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg", "password");
        AuthToken authTokenMatching = new AuthToken(username);
        AuthToken authTokenNotMatching = new AuthToken("@NotMatchingUser");

        // Setup request objects to use in the tests
        validRequest = new LogoutRequest(user, authTokenMatching);
        invalidRequestOne = new LogoutRequest(null, authTokenMatching);
        invalidRequestTwo = new LogoutRequest(user, authTokenNotMatching);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new LogoutResponse(user, authTokenMatching);
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        Mockito.when(mockServerFacade.logout(validRequest)).thenReturn(successResponse);

        failureResponse = new LogoutResponse("An exception occured");
        Mockito.when(mockServerFacade.logout(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.logout(invalidRequestTwo)).thenReturn(failureResponse);

        // Create a LogoutService instance and wrap it with a spy that will use the mock service
        logoutServiceSpy = Mockito.spy(new LogoutService());
        Mockito.when(logoutServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testLogout_validRequest_correctResponse() throws IOException {
        LogoutResponse response = logoutServiceSpy.logout(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_emptyUsername() throws IOException {
        LogoutResponse response = logoutServiceSpy.logout(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testGetCount_invalidRequest_authTokenDoesNotMatch() throws IOException {
        LogoutResponse response = logoutServiceSpy.logout(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }
}
