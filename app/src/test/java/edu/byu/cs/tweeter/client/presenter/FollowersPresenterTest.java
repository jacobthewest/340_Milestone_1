package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.service.FollowersServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.FollowersRequest;
import edu.byu.cs.tweeter.shared.model.service.response.FollowersResponse;

public class FollowersPresenterTest {

    private FollowersRequest request;
    private FollowersResponse response;
    private FollowersServiceProxy mockFollowersServiceProxy;
    private FollowersPresenter presenter;

    @BeforeEach
    public void setup() throws IOException {
        User currentUser = new User("FirstName", "LastName", null, "password");

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png", "password");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png", "password");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png", "password");

        request = new FollowersRequest(currentUser, 3, null);
        response = new FollowersResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);

        // Create a mock FollowersService
        mockFollowersServiceProxy = Mockito.mock(FollowersServiceProxy.class);

        // Wrap a FollowersPresenter in a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service.
        presenter = Mockito.spy(new FollowersPresenter(new FollowersPresenter.View() {}));
        Mockito.when(presenter.getFollowersService()).thenReturn(mockFollowersServiceProxy);
    }

    @Test
    public void testGetFollowers_returnsServiceResult() throws IOException {
        Mockito.when(mockFollowersServiceProxy.getFollowers(request)).thenReturn(response);

        // Assert that the presenter returns the same response as the edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(response, presenter.getFollowers(request));
    }

    @Test
    public void testGetFollowers_serviceThrowsIOException_presenterThrowsIOException() throws IOException {
        Mockito.when(mockFollowersServiceProxy.getFollowers(request)).thenThrow(new IOException());

        Assertions.assertThrows(IOException.class, () -> {
            presenter.getFollowers(request);
        });
    }
}
