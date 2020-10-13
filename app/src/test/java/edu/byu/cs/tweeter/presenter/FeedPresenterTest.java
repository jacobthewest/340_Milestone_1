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
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.CountService;
import edu.byu.cs.tweeter.model.service.FeedService;
import edu.byu.cs.tweeter.model.service.request.FeedRequest;
import edu.byu.cs.tweeter.model.service.response.FeedResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

public class FeedPresenterTest {

    private FeedRequest validRequest;
    private FeedRequest invalidRequestOne;
    private FeedRequest invalidRequestTwo;
    private FeedResponse successResponse;
    private FeedResponse failureResponse;
    private FeedService mockFeedService;
    private FeedPresenter presenter;
    private String imageUrl = "https://i.imgur.com/VZQQiQ1.jpg";
    private final User JacobWest = new User("Jacob", "West", "@JacobWest", imageUrl, "password");
    private final User RickyMartin = new User("Ricky", "Martin", "@RickyMartin", imageUrl, "password");
    private final User theMedia = new User("the", "Media", "@theMedia", imageUrl, "password");
    private final User BillBelichick = new User("Bill", "Belichick", "@BillBelichick", imageUrl, "password");
    private final User Rudy = new User("Rudy", "Gobert", "@Rudy", imageUrl, "password");

    @BeforeEach
    public void setup() {
        ServerFacade mockServerFacade = Mockito.mock(ServerFacade.class);
        User currentUser = new User("Test", "User", null, "password");
        List<Status> feed = getFeed();

        // Setup request objects to use in the tests
        validRequest = new FeedRequest(currentUser, 5, null);

        // Setup a mock ServerFacade that will return known responses
        successResponse = new FeedResponse(feed, false);

        // Create a mock FollowersService
        mockFeedService = Mockito.mock(FeedService.class);

        // Wrap a FollowersPresenter in a spy that will use the mock service.
        presenter = Mockito.spy(new FeedPresenter(new FeedPresenter.View() {}));
        Mockito.when(presenter.getFeedService()).thenReturn(mockFeedService);

    }

    @Test
    public void testGetFeed_validRequest_correctResponse() throws IOException {
        Mockito.when(mockFeedService.getFeed(validRequest)).thenReturn(successResponse);

        // Assert that the presenter returns the same response as the service (it doesn't do
        // anything else, so there's nothing else to test).
        Assertions.assertEquals(successResponse, presenter.getFeed(validRequest));
    }

    @Test
    public void testGetFeed_serviceThrowsIOException_presenterThrowsIOException() throws IOException {
        Mockito.when(mockFeedService.getFeed(validRequest)).thenThrow(new IOException());

        Assertions.assertThrows(IOException.class, () -> {
            presenter.getFeed(validRequest);
        });
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
