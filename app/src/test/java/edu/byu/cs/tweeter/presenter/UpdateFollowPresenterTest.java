package edu.byu.cs.tweeter.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.UpdateFollowService;
import edu.byu.cs.tweeter.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;

public class UpdateFollowPresenterTest {

    private UpdateFollowRequest validRequestFollow;
    private UpdateFollowResponse followSuccessResponse;
    private UpdateFollowService mockUpdateFollowService;
    private UpdateFollowPresenter presenter;
    final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    final String MIKE = "https://i.imgur.com/VZQQiQ1.jpg";
    final User Rudy = new User("Rudy", "Gobert", "@Rudy", MIKE, "password");
    final User JacobWest = new User("Jacob", "West", "@JacobWest", MIKE, "password");
    final User user = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");

    @BeforeEach
    public void setup() {
        User unRecognizedUser = new User("Not", "Recognized", MIKE, "password");
        User unFollowThisUser = Rudy;
        User followThisUser = JacobWest;

        List<User> follow_following = getFollowing();
        follow_following.add(JacobWest);

        List<User> unFollow_following = getFollowing();
        unFollow_following.remove(Rudy);

        // Setup request objects to use in the tests
        validRequestFollow = new UpdateFollowRequest(user, followThisUser, true);
        // Setup a mock ServerFacade that will return known responses

        followSuccessResponse = new UpdateFollowResponse(user, followThisUser, follow_following);
        // Create a mock FollowersService
        mockUpdateFollowService = Mockito.mock(UpdateFollowService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new UpdateFollowPresenter(new UpdateFollowPresenter.View() {}));
        Mockito.when(presenter.getUpdateFollowService()).thenReturn(mockUpdateFollowService);
    }

    @Test
    public void testUpdateFollow_validRequest_correctResponse() throws IOException {
        Mockito.when(mockUpdateFollowService.updateFollow(validRequestFollow)).thenReturn(followSuccessResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(followSuccessResponse, presenter.getUpdateFollow(validRequestFollow));
    }

    private List<User> getFollowing() {
        ServerFacadeMine sf = new ServerFacadeMine();
        List<User> returnMe = new ArrayList<>(sf.getDummyFollowees());
        return returnMe;
    }
}

