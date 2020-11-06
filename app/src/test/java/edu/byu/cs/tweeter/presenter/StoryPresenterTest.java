package edu.byu.cs.tweeter.presenter;

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
import edu.byu.cs.tweeter.model.service.StoryService;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;

public class StoryPresenterTest {
    private StoryRequest validRequest;
    private StoryResponse successResponse;
        private StoryService mockStoryService;
    private StoryPresenter presenter;
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";


    @BeforeEach
    public void setup() {
        ServerFacadeMine mockServerFacade = Mockito.mock(ServerFacadeMine.class);
        User currentUser = new User("Test", "User", MALE_IMAGE_URL, "password");
        List<Status> story = getStory(currentUser);

        // Setup request objects to use in the tests
        validRequest = new StoryRequest(currentUser, 5, null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new StoryResponse(story, false);

        // Create a mock FollowersService
        mockStoryService = Mockito.mock(StoryService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new StoryPresenter(new StoryPresenter.View() {}));
        Mockito.when(presenter.getStoryService()).thenReturn(mockStoryService);
    }

    @Test
    public void testGetStory_validRequest_correctResponse() throws IOException {
        Mockito.when(mockStoryService.getStory(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.getStory(validRequest));
    }

    @Test
    public void testGetStory_serviceThrowsIOException_presenterThrowsIOException() throws IOException {
        Mockito.when(mockStoryService.getStory(validRequest)).thenThrow(new IOException());

        Assertions.assertThrows(IOException.class, () -> {
            presenter.getStory(validRequest);
        });
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
