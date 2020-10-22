package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.shared.model.domain.Status;
import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.client.model.service.SubmitTweetServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.shared.model.service.response.SubmitTweetResponse;

public class SubmitTweetPresenterTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private SubmitTweetRequest validRequest;
    private SubmitTweetResponse successResponse;
    private SubmitTweetServiceProxy mockSubmitTweetServiceProxy;
    private SubmitTweetPresenter presenter;

    @BeforeEach
    public void setup() {
        User recognizedUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");
        User unRecognizedUser = new User("Not", "Recognized", "@TestUser", MALE_IMAGE_URL, "password");
        Status recognizedStatus = getRecognizedStatus(recognizedUser);
        Status unRecognizedStatus = getUnRecognizedStatus(unRecognizedUser);

        // Setup request objects to use in the tests
        validRequest = new SubmitTweetRequest(recognizedUser, recognizedStatus);
        // Setup a mock ServerFacade that will return known responses
        successResponse = new SubmitTweetResponse(recognizedUser, recognizedStatus);

        // Create a mock FollowersService
        mockSubmitTweetServiceProxy = Mockito.mock(SubmitTweetServiceProxy.class);

        // Wrap a FollowersPresenter in a spy that will use the mock edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service.
        presenter = Mockito.spy(new SubmitTweetPresenter(new SubmitTweetPresenter.View() {}));
        Mockito.when(presenter.getSubmitTweetService()).thenReturn(mockSubmitTweetServiceProxy);
    }

    @Test
    public void testSubmitTweet_validRequest_correctResponse() throws IOException {
        Mockito.when(mockSubmitTweetServiceProxy.submitTweet(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the edu.byu.cs.server.edu.byu.cs.tweeter.server.edu.byu.cs.shared.edu.byu.cs.tweeter.shared.model.edu.byu.cs.tweeter.server.service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.submitTweet(validRequest));
    }

    private Status getRecognizedStatus(User user) {
        List<String> uOne = new ArrayList<>();
        uOne.add("multiply.com");
        List<String> mOne = new ArrayList<>();
        mOne.add("@JacobWest");
        mOne.add("@RickyMartin");
        Date d = createDate(2020, 0, 11, 0, 13);
        Calendar a = Calendar.getInstance();
        a.setTime(d);
        Status s = new Status(user, "Recognized status", uOne, a, mOne);
        return s;
    }

    private Status getUnRecognizedStatus(User user) {
        List<String> uOne = new ArrayList<>();
        uOne.add("multiply.com");
        List<String> mOne = new ArrayList<>();
        mOne.add("@MartinShort");
        mOne.add("@JamesBond");
        Date d = createDate(2007, 0, 11, 0, 13);
        Calendar a = Calendar.getInstance();
        a.setTime(d);
        Status s = new Status(user, "UnRecognized status", uOne, a, mOne);
        return s;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Date d = new Date(year - 1900, month, day);
        d.setHours(hour);
        d.setMinutes(minute);
        return d;
    }
}
