package edu.byu.cs.tweeter.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.LogoutService;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;

public class LogoutPresenterTest {

    private LogoutRequest validRequest;
    private LogoutResponse successResponse;
    private LogoutService mockLogoutService;
    private LogoutPresenter presenter;

    @BeforeEach
    public void setup() throws IOException {
        String username = "@TestUser";
        String password = "password";
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg", "password");
        AuthToken authTokenMatching = new AuthToken(username);
        AuthToken authTokenNotMatching = new AuthToken("@NotMatchingUser");

        // Setup request objects to use in the tests
        validRequest = new LogoutRequest(user, authTokenMatching);
        successResponse = new LogoutResponse(user, authTokenMatching);

        // Create a mock FollowersService
        mockLogoutService = Mockito.mock(LogoutService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new LogoutPresenter(new LogoutPresenter.View() {
        }));
        Mockito.when(presenter.getLogoutService()).thenReturn(mockLogoutService);
    }

    @Test
    public void testLogout_validRequest_correctResponse() throws IOException {
        Mockito.when(mockLogoutService.logout(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.logout(validRequest));
    }
}
