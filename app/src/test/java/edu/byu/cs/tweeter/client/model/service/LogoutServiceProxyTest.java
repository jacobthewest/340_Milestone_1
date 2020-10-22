package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.AuthToken;
import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.shared.model.service.response.LogoutResponse;

public class LogoutServiceProxyTest {

    private LogoutRequest validRequest;
    private LogoutRequest invalidRequestOne;
    private LogoutRequest invalidRequestTwo;
    private LogoutResponse successResponse;
    private LogoutResponse failureResponse;
    private LogoutServiceProxy logoutServiceProxySpy;

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

        // Create a LogoutService instance and wrap it with a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service
        logoutServiceProxySpy = Mockito.spy(new LogoutServiceProxy());
        Mockito.when(logoutServiceProxySpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testLogout_validRequest_correctResponse() throws IOException {
        LogoutResponse response = logoutServiceProxySpy.logout(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testLogout_invalidRequest_emptyUsername() throws IOException {
        LogoutResponse response = logoutServiceProxySpy.logout(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testLogout_invalidRequest_authTokenDoesNotMatch() throws IOException {
        LogoutResponse response = logoutServiceProxySpy.logout(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }
}
