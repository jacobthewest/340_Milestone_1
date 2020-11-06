package edu.byu.cs.tweeter.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.RetrieveUserService;
import edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.model.service.response.RetrieveUserResponse;

public class RetrieveUserPresenterTest {

    String username = "@TestUser";
    final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    final User TestUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");
    private RetrieveUserRequest validRequest;
    private RetrieveUserResponse successResponse;
    private RetrieveUserService mockRetrieveUserService;
    private RetrieveUserPresenter presenter;

    @BeforeEach
    public void setup() {
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        User currentUser = new User("Test", "User", MALE_IMAGE_URL, "password");

        // Setup request objects to use in the tests
        validRequest = new RetrieveUserRequest(username);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new RetrieveUserResponse(TestUser);
        
        
        // Create a mock FollowersService
        mockRetrieveUserService = Mockito.mock(RetrieveUserService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new RetrieveUserPresenter(new RetrieveUserPresenter.View() {}));
        Mockito.when(presenter.getRetrieveUserService()).thenReturn(mockRetrieveUserService);
    }

    @Test
    public void testRetrieveUser_validRequest_correctResponse() throws IOException {
        Mockito.when(mockRetrieveUserService.retrieveUser(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.retrieveUser(validRequest));
    }
}
