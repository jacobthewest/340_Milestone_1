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
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

public class StoryServiceTest {
    private StoryRequest validRequest;
    private StoryRequest invalidRequestOne;
    private StoryRequest invalidRequestTwo;
    private StoryResponse successResponse;
    private StoryResponse failureResponse;
    private StoryService storyServiceSpy;
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";

    @BeforeEach
    public void setup() {
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        User currentUser = new User("Test", "User", MALE_IMAGE_URL, "password");
        List<Status> story = getStory(currentUser);

        // Setup request objects to use in the tests
        validRequest = new StoryRequest(currentUser, 5, null);
        invalidRequestOne = new StoryRequest(null, 5, null);
        invalidRequestTwo = new StoryRequest(currentUser, -1, null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new StoryResponse(story, false);
        Mockito.when(mockServerFacade.getStory(validRequest)).thenReturn(successResponse);

        failureResponse = new StoryResponse("An exception occured");
        Mockito.when(mockServerFacade.getStory(invalidRequestOne)).thenReturn(failureResponse);
        Mockito.when(mockServerFacade.getStory(invalidRequestTwo)).thenReturn(failureResponse);

        // Create a StoryService instance and wrap it with a spy that will use the mock service
        storyServiceSpy = Mockito.spy(new StoryService());
        Mockito.when(storyServiceSpy.getServerFacade()).thenReturn(mockServerFacade);
    }

    @Test
    public void testGetStory_validRequest_correctResponse() throws IOException {
        StoryResponse response = storyServiceSpy.getStory(validRequest);
        Assertions.assertEquals(successResponse, response);
    }

    @Test
    public void testGetStory_validRequest_loadsProfileImages() throws IOException {
        StoryResponse response = storyServiceSpy.getStory(validRequest);

        for(Status status : response.getStory()) {
            byte [] bytes = ByteArrayUtils.bytesFromUrl(status.getUser().getImageUrl());
            status.getUser().setImageBytes(bytes);
            Assertions.assertNotNull(status.getUser().getImageBytes());
        }
    }

    @Test
    public void testGetStory_invalidRequest_nullUser() throws IOException {
        StoryResponse response = storyServiceSpy.getStory(invalidRequestOne);
        Assertions.assertEquals(failureResponse, response);
    }

    @Test
    public void testGetStory_invalidRequest_negativeLimit() throws IOException {
        StoryResponse response = storyServiceSpy.getStory(invalidRequestTwo);
        Assertions.assertEquals(failureResponse, response);
    }

    private List<Status> getStory(User definedUser) {
        List<Status> story = new ArrayList<>();

        // --------------------- 1--------------------- //
        List<String> uOne = new ArrayList<>();
        uOne.add("multiply.com");
        List<String> mOne = new ArrayList<>();
        mOne.add("@JacobWest");
        mOne.add("@RickyMartin");
        Date d = createDate(2020, 0, 11, 0, 13);
        Calendar a = Calendar.getInstance();
        a.setTime(d);
        Status s = new Status(definedUser, "This is a text @JacobWest @RickyMartin multiply.com", uOne, a, mOne);
        story.add(s); // # 1

        // --------------------- 2 --------------------- //
        List<String> uTwo = new ArrayList<>();
        uTwo.add("tinyurl.com");
        d = createDate(2020, 0, 11, 0, 14);
        Calendar b = Calendar.getInstance();
        b.setTime(d);
        s = new Status(definedUser, "You should visit tinyurl.com", uTwo, b, null);
        story.add(s);

        // --------------------- 3 --------------------- //
        List<String> mThree = new ArrayList<>();
        mThree.add("@JacobWest");
        d = createDate(2019, 3, 16, 3, 34);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        s = new Status(definedUser, "Dolphins @JacobWest have Tua", null, c, mThree);
        story.add(s);

        // --------------------- 4 --------------------- //
        d = createDate(2014, 7, 30, 17, 01);
        Calendar de = Calendar.getInstance();
        de.setTime(d);
        s = new Status(definedUser, "Jacksonville will draft third", null, de, null);
        story.add(s);

        // --------------------- 5 --------------------- //
        List<String> uFive = new ArrayList<>();
        uFive.add("dell.com");
        d = createDate(2012, 3, 3, 18, 21);
        Calendar e = Calendar.getInstance();
        e.setTime(d);
        s = new Status(definedUser, "I endorse dell.com", uFive, e, null);
        story.add(s);

        return story;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Date d = new Date(year - 1900, month, day);
        d.setHours(hour);
        d.setMinutes(minute);
        return d;
    }
}
