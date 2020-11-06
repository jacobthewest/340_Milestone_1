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
import edu.byu.cs.tweeter.model.service.request.FeedRequest;
import edu.byu.cs.tweeter.model.service.response.FeedResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

public class FeedServiceTest {

    private FeedRequest validRequest;
    private FeedRequest invalidRequestOne;
    private FeedRequest invalidRequestTwo;
    private FeedResponse successResponse;
    private FeedResponse failureResponse;
    private FeedService feedServiceSpy;
    private String imageUrl = "https://i.imgur.com/VZQQiQ1.jpg";
    private final User JacobWest = new User("Jacob", "West", "@JacobWest", imageUrl, "password");
    private final User RickyMartin = new User("Ricky", "Martin", "@RickyMartin", imageUrl, "password");
    private final User theMedia = new User("the", "Media", "@theMedia", imageUrl, "password");
    private final User BillBelichick = new User("Bill", "Belichick", "@BillBelichick", imageUrl, "password");
    private final User Rudy = new User("Rudy", "Gobert", "@Rudy", imageUrl, "password");

    @BeforeEach
    public void setup() {
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        User currentUser = new User("Test", "User", null, "password");
        List<Status> feed = getFeed();

        // Setup request objects to use in the tests
        validRequest = new FeedRequest(currentUser, 5, null);
        invalidRequestOne = new FeedRequest(null, 5, null);
        invalidRequestTwo = new FeedRequest(currentUser, -1, null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new FeedResponse(feed, false);
        Mockito.when(mockServerFacade.getFeed(validRequest)).thenReturn(successResponse);

        failureResponse = new FeedResponse("An exception occured");
        Mockito.when(mockServerFacade.getFeed(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.getFeed(invalidRequestTwo)).thenReturn(failureResponse);

        // Create a FeedService instance and wrap it with a spy that will use the mock service
        feedServiceSpy = Mockito.spy(new FeedService());
        Mockito.when(feedServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testGetFeed_validRequest_correctResponse() throws IOException {
        FeedResponse response = feedServiceSpy.getFeed(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetFeed_validRequest_loadsProfileImages() throws IOException {
        FeedResponse response = feedServiceSpy.getFeed(validRequest);

        for(Status status : response.getFeed()) {
            byte [] bytes = ByteArrayUtils.bytesFromUrl(status.getUser().getImageUrl());
            status.getUser().setImageBytes(bytes);
            Assertions.assertNotNull(status.getUser().getImageBytes());
        }
    }

    @Test
    public void testGetFeed_invalidRequest_nullUser() throws IOException {
        FeedResponse response = feedServiceSpy.getFeed(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testGetFeed_invalidRequest_negativeLimit() throws IOException {
        FeedResponse response = feedServiceSpy.getFeed(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }

    private List<Status> getFeed() {
        List<Status> feed = new ArrayList<>();

        // --------------------- 1--------------------- //
        List<String> uOne = new ArrayList<>();
        uOne.add("multiply.com");
        List<String> mOne = new ArrayList<>();
        mOne.add("@JacobWest");
        mOne.add("@RickyMartin");
        Date d = createDate(2020, 0, 11, 0, 13);
        Calendar a = Calendar.getInstance();
        a.setTime(d);
        Status s = new Status(BillBelichick, "This is a text @JacobWest @RickyMartin multiply.com", uOne, a, mOne);
        feed.add(s); // # 1

        // --------------------- 2 --------------------- //
        List<String> uTwo = new ArrayList<>();
        uTwo.add("tinyurl.com");
        d = createDate(2020, 0, 11, 0, 14);
        Calendar b = Calendar.getInstance();
        b.setTime(d);
        s = new Status(Rudy, "You should visit tinyurl.com", uTwo, b, null);
        feed.add(s);

        // --------------------- 3 --------------------- //
        List<String> mThree = new ArrayList<>();
        mThree.add("@JacobWest");
        d = createDate(2019, 3, 16, 3, 34);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        s = new Status(theMedia, "Dolphins @JacobWest have Tua", null, c, mThree);
        feed.add(s);

        // --------------------- 4 --------------------- //
        d = createDate(2014, 7, 30, 17, 01);
        Calendar de = Calendar.getInstance();
        de.setTime(d);
        s = new Status(JacobWest, "Jacksonville will draft third", null, de, null);
        feed.add(s);

        // --------------------- 5 --------------------- //
        List<String> uFive = new ArrayList<>();
        uFive.add("dell.com");
        d = createDate(2012, 3, 3, 18, 21);
        Calendar e = Calendar.getInstance();
        e.setTime(d);
        s = new Status(RickyMartin, "I endorse dell.com", uFive, e, null);
        feed.add(s);

        return feed;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Date d = new Date(year - 1900, month, day);
        d.setHours(hour);
        d.setMinutes(minute);
        return d;
    }
}
