package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.service.CountServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.CountRequest;
import edu.byu.cs.tweeter.shared.model.service.response.CountResponse;

public class CountPresenterTest {

    private CountRequest request;
    private CountRequest invalidRequest;
    private CountResponse response;
    private CountResponse failureResponse;
    private CountServiceProxy mockCountServiceProxy;
    private CountPresenter presenter;
    private int followingCount = 29;
    private int followersCount = 29;

    @BeforeEach
    public void setup() throws IOException {
        User currentUser = new User("Test", "User", null, "password");

        // Setup request objects to use in the tests
        request = new CountRequest(currentUser);
        invalidRequest = new CountRequest(null);
        response = new CountResponse(currentUser, followingCount, followersCount);
        failureResponse = new CountResponse("An exception occured");

        // Create a mock FollowersService
        mockCountServiceProxy = Mockito.mock(CountServiceProxy.class);

        // Wrap a FollowersPresenter in a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service.
        presenter = Mockito.spy(new CountPresenter(new CountPresenter.View() {}));
        Mockito.when(presenter.getCountService()).thenReturn(mockCountServiceProxy);
    }

    @Test
    public void testGetCount_validRequest_correctResponse() throws IOException {
        Mockito.when(mockCountServiceProxy.getCount(request)).thenReturn(response);

        // Assert that the presenter returns the same response as the edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(response, presenter.getCount(request));
    }

    @Test
    public void testGetCount_invalidRequest_correctResponse() {
        Mockito.when(mockCountServiceProxy.getCount(invalidRequest)).thenReturn(failureResponse);

        Assertions.assertThrows(AssertionError.class, () -> {
            presenter.getCount(invalidRequest);
        });
    }
}
