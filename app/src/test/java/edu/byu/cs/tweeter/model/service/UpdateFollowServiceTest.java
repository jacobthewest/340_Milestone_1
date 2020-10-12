package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.view.asyncTasks.UpdateFollowTask;

public class UpdateFollowServiceTest {


    private UpdateFollowRequest validRequestFollow;
    private UpdateFollowRequest validRequestUnFollow;
    private UpdateFollowRequest unFollowUserWeDontFollow;
    private UpdateFollowRequest followUserWeAlreadyFollow;
    private UpdateFollowResponse followSuccessResponse;
    private UpdateFollowResponse unFollowSuccessResponse;
    private UpdateFollowResponse failureResponse;
    private UpdateFollowService updateFollowServiceSpy;
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
        validRequestUnFollow = new UpdateFollowRequest(user, unFollowThisUser, false);
        unFollowUserWeDontFollow = new UpdateFollowRequest(user, unRecognizedUser, false);
        followUserWeAlreadyFollow = new UpdateFollowRequest(user, Rudy, true);

        // Setup a mock ServerFacade that will return known responses
        followSuccessResponse = new UpdateFollowResponse(user, followThisUser, follow_following);
        unFollowSuccessResponse = new UpdateFollowResponse(user, unFollowThisUser, unFollow_following);
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        Mockito.when(mockServerFacade.updateFollow(validRequestFollow)).thenReturn(followSuccessResponse);
        Mockito.when(mockServerFacade.updateFollow(validRequestUnFollow)).thenReturn(unFollowSuccessResponse);

        failureResponse = new UpdateFollowResponse("An exception occured");
        Mockito.when(mockServerFacade.updateFollow(unFollowUserWeDontFollow)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.updateFollow(followUserWeAlreadyFollow)).thenReturn(failureResponse);

        // Create a UpdateFollowService instance and wrap it with a spy that will use the mock service
        updateFollowServiceSpy = Mockito.spy(new UpdateFollowService());
        Mockito.when(updateFollowServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testUpdateFollow_validRequest_validRequestFollow() throws IOException {
        UpdateFollowResponse response = updateFollowServiceSpy.updateFollow(validRequestFollow);
        Assertions.assertEquals(followSuccessResponse, response);
    }

    @Test
    public void testUpdateFollow_validRequest_validRequestUnFollow() throws IOException {
        UpdateFollowResponse response = updateFollowServiceSpy.updateFollow(validRequestUnFollow);
        Assertions.assertEquals(unFollowSuccessResponse, response);
    }

    @Test
    public void testUpdateFollow_invalidRequest_unFollowUserWeDontFollow() throws IOException {
        UpdateFollowResponse response = updateFollowServiceSpy.updateFollow(unFollowUserWeDontFollow);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testUpdateFollow_invalidRequest_followUserWeAlreadyFollow() throws IOException {
        UpdateFollowResponse response = updateFollowServiceSpy.updateFollow(followUserWeAlreadyFollow);
        Assertions.assertEquals(failureResponse, response);
    }


    private List<User> getFollowing() {
        ServerFacade sf = new ServerFacade();
        List<User> returnMe = new ArrayList<>(sf.getDummyFollowees());
        return returnMe;
    }

}
