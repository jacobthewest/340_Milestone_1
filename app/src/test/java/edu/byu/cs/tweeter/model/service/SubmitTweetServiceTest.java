package edu.byu.cs.tweeter.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;

public class SubmitTweetServiceTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private SubmitTweetRequest validRequest;
    private SubmitTweetRequest invalidRequest1;
    private SubmitTweetRequest invalidRequest2;
    private SubmitTweetRequest invalidRequest3;
    private SubmitTweetResponse successResponse;
    private SubmitTweetResponse failureResponse;
    private SubmitTweetService submitTweetServiceSpy;

    @BeforeEach
    public void setup() {
        User recognizedUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");
        User unRecognizedUser = new User("Not", "Recognized", "@TestUser", MALE_IMAGE_URL, "password");
        Status recognizedStatus = getRecognizedStatus(recognizedUser);
        Status unRecognizedStatus = getUnRecognizedStatus(unRecognizedUser);

        // Setup request objects to use in the tests
        validRequest = new SubmitTweetRequest(recognizedUser, recognizedStatus);
        invalidRequest1 = new SubmitTweetRequest(recognizedUser, unRecognizedStatus);
        invalidRequest2 = new SubmitTweetRequest(null, recognizedStatus);
        invalidRequest3 = new SubmitTweetRequest(recognizedUser, null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new SubmitTweetResponse(recognizedUser, recognizedStatus);
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        Mockito.when(mockServerFacade.submitTweet(validRequest)).thenReturn(successResponse);

        failureResponse = new SubmitTweetResponse("An exception occured");
        Mockito.when(mockServerFacade.submitTweet(invalidRequest1)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.submitTweet(invalidRequest2)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.submitTweet(invalidRequest3)).thenReturn(failureResponse);

        // Create a SubmitTweetService instance and wrap it with a spy that will use the mock service
        submitTweetServiceSpy = Mockito.spy(new SubmitTweetService());
        Mockito.when(submitTweetServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testSubmitTweet_validRequest_correctResponse() throws IOException {
        SubmitTweetResponse response = submitTweetServiceSpy.submitTweet(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testSubmitTweet_invalidRequest_userDoesNotMatchStatus() throws IOException {
        SubmitTweetResponse response = submitTweetServiceSpy.submitTweet(invalidRequest1);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testSubmitTweet_invalidRequest_userIsNull() throws IOException {
        SubmitTweetResponse response = submitTweetServiceSpy.submitTweet(invalidRequest2);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testSubmitTweet_invalidRequest_statusIsNull() throws IOException {
        SubmitTweetResponse response = submitTweetServiceSpy.submitTweet(invalidRequest3);
        Assertions.assertEquals(failureResponse, response);
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
