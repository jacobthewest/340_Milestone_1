package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.AuthToken;
import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.service.LoginServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.shared.model.service.response.LoginResponse;

public class LoginPresenterTest {

    private LoginRequest validRequest;
    private LoginResponse successResponse;
    private LoginServiceProxy mockLoginServiceProxy;
    private LoginPresenter presenter;

    @BeforeEach
    public void setup() throws IOException {
        String username = "@TestUser";
        String password = "password";
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg", "password");
        AuthToken authToken = new AuthToken(username);

        validRequest = new LoginRequest(username, password);
        successResponse = new LoginResponse(user, authToken);

        // Create a mock FollowersService
        mockLoginServiceProxy = Mockito.mock(LoginServiceProxy.class);

        // Wrap a FollowersPresenter in a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service.
        presenter = Mockito.spy(new LoginPresenter(new LoginPresenter.View() {}));
        Mockito.when(presenter.getLoginService()).thenReturn(mockLoginServiceProxy);
    }

    @Test
    public void testLogin_returnsServiceResult() throws IOException {
        Mockito.when(mockLoginServiceProxy.login(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.login(validRequest));
    }
}
