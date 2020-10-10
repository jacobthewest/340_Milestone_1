package edu.byu.cs.tweeter.model.net;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.BuildConfig;
import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.request.FeedRequest;
import edu.byu.cs.tweeter.model.service.request.FollowersRequest;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;
import edu.byu.cs.tweeter.model.service.response.FeedResponse;
import edu.byu.cs.tweeter.model.service.response.FollowersResponse;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.model.service.response.RetrieveUserResponse;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {
    private static Map<User, List<Status>> storyStatusesByUser;
    private static Map<User, List<Status>> feedStatusesByUser;

    // This is the hard coded followee data returned by the 'getFollowees()' method
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String FEMALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";
    private static final String MIKE = "https://i.imgur.com/VZQQiQ1.jpg";

    private final User user1 = new User("Allen", "Anderson", MALE_IMAGE_URL, "password");
    private final User user2 = new User("Amy", "Ames", FEMALE_IMAGE_URL, "password");
    private final User user3 = new User("Bob", "Bobson", MALE_IMAGE_URL, "password");
    private final User user4 = new User("Bonnie", "Beatty", FEMALE_IMAGE_URL, "password");
    private final User user5 = new User("Chris", "Colston", MALE_IMAGE_URL, "password");
    private final User user6 = new User("Cindy", "Coats", FEMALE_IMAGE_URL, "password");
    private final User user7 = new User("Dan", "Donaldson", MALE_IMAGE_URL, "password");
    private final User user8 = new User("Dee", "Dempsey", FEMALE_IMAGE_URL, "password");
    private final User user9 = new User("Elliott", "Enderson", MALE_IMAGE_URL, "password");
    private final User user10 = new User("Elizabeth", "Engle", FEMALE_IMAGE_URL, "password");
    private final User user11 = new User("Frank", "Frandson", MALE_IMAGE_URL, "password");
    private final User user12 = new User("Fran", "Franklin", FEMALE_IMAGE_URL, "password");
    private final User user13 = new User("Gary", "Gilbert", MALE_IMAGE_URL, "password");
    private final User user14 = new User("Giovanna", "Giles", FEMALE_IMAGE_URL, "password");
    private final User user15 = new User("Henry", "Henderson", MALE_IMAGE_URL, "password");
    private final User user16 = new User("Helen", "Hopwell", FEMALE_IMAGE_URL, "password");
    private final User user17 = new User("Igor", "Isaacson", MALE_IMAGE_URL, "password");
    private final User user18 = new User("Isabel", "Isaacson", FEMALE_IMAGE_URL, "password");
    private final User user19 = new User("Justin", "Jones", MALE_IMAGE_URL, "password");
    private final User user20 = new User("Jill", "Johnson", FEMALE_IMAGE_URL, "password");
    private final User JacobWest = new User("Jacob", "West", "@JacobWest", MIKE, "password");
    private final User RickyMartin = new User("Ricky", "Martin", "@RickyMartin", MIKE, "password");
    private final User RobertGardner = new User("Robert", "Gardner", "@RobertGardner", MIKE, "password");
    private final User Snowden = new User("The", "Snowden", "@Snowden", MIKE, "password");
    private final User TristanThompson = new User("Tristan", "Thompson", "@TristanThompson", MIKE, "password");
    private final User KCP = new User("Kontavius", "Caldwell Pope", "@KCP", MIKE, "password");
    private final User theMedia = new User("the", "Media", "@theMedia", MIKE, "password");
    private final User Rudy = new User("Rudy", "Gobert", "@Rudy", MIKE, "password");
    private final User BillBelichick = new User("Bill", "Belichick", "@BillBelichick", MIKE, "password");
    private final User TestUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");


    public UpdateFollowResponse updateFollow(UpdateFollowRequest request) {
        List<User> following = getDummyFollowees();

        if(request.followTheFollowUser()) { // Then follow the followUser
            if(!following.contains(request.getFollowUser())) {
                following.add(request.getFollowUser());
            }
        } else { // Unfollow the followUser
            if(following.contains(request.getFollowUser())) {
                following.remove(request.getFollowUser());
            }
        }
        return new UpdateFollowResponse(request.getUser(), request.getFollowUser(), following);
    }


    public CountResponse getCount(CountRequest request) {
        int followingCount = (getDummyFollowees().size());
        int followersCount = (getDummyFollowers().size());
        return new CountResponse(request.getUser(), followingCount, followersCount);
    }

    /**
     * Performs a save of the status to the database. This function doesn't actually make a network request.
     *
     * @param request contains all information needed to save a status.
     * @return the submit tweet response.
     */
    public SubmitTweetResponse submitTweet(SubmitTweetRequest request) {
        return new SubmitTweetResponse(request.getUser(), request.getStatus());
    }

    /**
     * Performs a login and if successful, returns the logged in user and an auth token. The current
     * implementation is hard-coded to return a dummy user and doesn't actually make a network
     * request.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LoginResponse login(LoginRequest request) {
        //"https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg", "password");
        return new LoginResponse(user, new AuthToken(user.getAlias()));
    }

    /**
     * Attempts to retrieve a user and if successful, returns the retrieved user and an auth token. The current
     * implementation is hard-coded to return a dummy user and doesn't actually make a network
     * request.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public RetrieveUserResponse retrieveUser(RetrieveUserRequest request) {
        if (request.getUsername().equals(user1.getAlias())) {
            return new RetrieveUserResponse(user1);
        } else if (request.getUsername().equals(user2.getAlias())) {
            return new RetrieveUserResponse(user2);
        } else if (request.getUsername().equals(user3.getAlias())) {
            return new RetrieveUserResponse(user3);
        } else if (request.getUsername().equals(user4.getAlias())) {
            return new RetrieveUserResponse(user4);
        } else if (request.getUsername().equals(user5.getAlias())) {
            return new RetrieveUserResponse(user5);
        } else if (request.getUsername().equals(user6.getAlias())) {
            return new RetrieveUserResponse(user6);
        } else if (request.getUsername().equals(user7.getAlias())) {
            return new RetrieveUserResponse(user7);
        } else if (request.getUsername().equals(user8.getAlias())) {
            return new RetrieveUserResponse(user8);
        } else if (request.getUsername().equals(user9.getAlias())) {
            return new RetrieveUserResponse(user9);
        } else if (request.getUsername().equals(user10.getAlias())) {
            return new RetrieveUserResponse(user10);
        } else if (request.getUsername().equals(user11.getAlias())) {
            return new RetrieveUserResponse(user11);
        } else if (request.getUsername().equals(user12.getAlias())) {
            return new RetrieveUserResponse(user12);
        } else if (request.getUsername().equals(user13.getAlias())) {
            return new RetrieveUserResponse(user13);
        } else if (request.getUsername().equals(user14.getAlias())) {
            return new RetrieveUserResponse(user14);
        } else if (request.getUsername().equals(user15.getAlias())) {
            return new RetrieveUserResponse(user15);
        } else if (request.getUsername().equals(user16.getAlias())) {
            return new RetrieveUserResponse(user16);
        } else if (request.getUsername().equals(user17.getAlias())) {
            return new RetrieveUserResponse(user17);
        } else if (request.getUsername().equals(user18.getAlias())) {
            return new RetrieveUserResponse(user18);
        } else if (request.getUsername().equals(user19.getAlias())) {
            return new RetrieveUserResponse(user19);
        } else if (request.getUsername().equals(user20.getAlias())) {
            return new RetrieveUserResponse(user20);
        } else if (request.getUsername().equals(JacobWest.getAlias())) {
            return new RetrieveUserResponse(JacobWest);
        } else if (request.getUsername().equals(RickyMartin.getAlias())) {
            return new RetrieveUserResponse(RickyMartin);
        } else if (request.getUsername().equals(RobertGardner.getAlias())) {
            return new RetrieveUserResponse(RobertGardner);
        } else if (request.getUsername().equals(Snowden.getAlias())) {
            return new RetrieveUserResponse(Snowden);
        } else if (request.getUsername().equals(TristanThompson.getAlias())) {
            return new RetrieveUserResponse(TristanThompson);
        } else if (request.getUsername().equals(KCP.getAlias())) {
            return new RetrieveUserResponse(KCP);
        } else if (request.getUsername().equals(theMedia.getAlias())) {
            return new RetrieveUserResponse(theMedia);
        } else if (request.getUsername().equals(Rudy.getAlias())) {
            return new RetrieveUserResponse(Rudy);
        } else if (request.getUsername().equals(BillBelichick.getAlias())) {
            return new RetrieveUserResponse(BillBelichick);
        } else {
            return null;
        }
    }

    /**
     * Performs a logout and if successful, returns the logged out user and the expired auth token. The current
     * implementation is hard-coded to return a dummy user and doesn't actually make a network
     * request.
     *
     * @param request contains all information needed to perform a login.
     * @return the login response.
     */
    public LogoutResponse logout(LogoutRequest request) {
        User user = request.getUser();
        AuthToken authToken = getAuthTokenByUsername(user.getAlias(), request.getAuthToken()); // Will be replaced with a server call later on.
        authToken.deactivate();
        return new LogoutResponse(user, authToken);
    }

    /**
     *
     * @param userName of the user to logout
     * @param authToken just needed for the non-server implemented functionality.
     * @return
     */
    public AuthToken getAuthTokenByUsername(String userName, AuthToken authToken) {
        return authToken;
    }

    /**
     * Performs a register and if successful, returns the registered user and an auth token. The current
     * implementation is hard-coded to return a dummy user and doesn't actually make a network
     * request.
     *
     * @param request contains all information needed to perform a login.
     * @return the register response.
     */
    public RegisterResponse register(RegisterRequest request) {
        User user = new User(request.getFirstName(), request.getLastName(), request.getImageUrl(), request.getImageBytes(), request.getPassword());
        return new RegisterResponse(user, new AuthToken(user.getAlias()));
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. The current implementation
     * returns generated data and doesn't actually make a network request.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the following response.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {

        // Used in place of assert statements because Android does not support them
        if(BuildConfig.DEBUG) {
            if(request.getLimit() < 0) {
                throw new AssertionError();
            }

            if(request.getUser() == null) {
                throw new AssertionError();
            }
        }

        List<User> allFollowees = getDummyFollowees();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            int followeesIndex = getFolloweesStartingIndex(request.getLastFollowee(), allFollowees);

            for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                responseFollowees.add(allFollowees.get(followeesIndex));
            }

            hasMorePages = followeesIndex < allFollowees.size();
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFollowee the last followee that was returned in the previous request or null if
     *                     there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(User lastFollowee, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFollowee != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFollowee.equals(allFollowees.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Generates the followee data.
     */
    private Map<User, List<User>> initializeFollowees() {

        Map<User, List<User>> followeesByFollower = new HashMap<>();

        List<Follow> follows = getFollowGenerator().generateUsersAndFollows(100,
                0, 50, FollowGenerator.Sort.FOLLOWER_FOLLOWEE);

        // Populate a map of followees, keyed by follower so we can easily handle followee requests
        for(Follow follow : follows) {
            List<User> followees = followeesByFollower.get(follow.getFollower());

            if(followees == null) {
                followees = new ArrayList<>();
                followeesByFollower.put(follow.getFollower(), followees);
            }

            followees.add(follow.getFollowee());
        }

        return followeesByFollower;
    }

    /**
     * Returns an instance of FollowGenerator that can be used to generate Follow data. This is
     * written as a separate method to allow mocking of the generator.
     *
     * @return the generator.
     */
    FollowGenerator getFollowGenerator() {
        return FollowGenerator.getInstance();
    }

    /**
     * Returns the users who follow the user specified in the request. Uses information in
     * the request object to limit the number of followers returned and to return the next set of
     * followers after any that were returned in a previous request. The current implementation
     * returns generated data and doesn't actually make a network request.
     *
     * @param request contains information about the user whose followers are to be returned and any
     *                other information required to satisfy the request.
     * @return the following response.
     */
    public FollowersResponse getFollowers(FollowersRequest request) {

        // Used in place of assert statements because Android does not support them
        if(BuildConfig.DEBUG) {
            if(request.getLimit() < 0) {
                throw new AssertionError();
            }

            if(request.getUser() == null) {
                throw new AssertionError();
            }
        }

        List<User> allFollowers = getDummyFollowers();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            int followersIndex = getFollowersStartingIndex(request.getLastFollower(), allFollowers);

            for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                responseFollowers.add(allFollowers.get(followersIndex));
            }

            hasMorePages = followersIndex < allFollowers.size();
        }

        return new FollowersResponse(responseFollowers, hasMorePages);
    }

    /**
     * Determines the index for the first follower in the specified 'allFollowers' list that should
     * be returned in the current request. This will be the index of the next follower after the
     * specified 'lastFollower'.
     *
     * @param lastFollower the last follower that was returned in the previous request or null if
     *                     there was no previous request.
     * @param allFollowers the generated list of followers from which we are returning paged results.
     * @return the index of the first follower to be returned.
     */
    private int getFollowersStartingIndex(User lastFollower, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollower != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollower.equals(allFollowers.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                }
            }
        }

        return followersIndex;
    }

    /**
     * Returns the statuses of the user specified in the request. Uses information in
     * the request object to limit the number of statuses returned and to return the next set of
     * statuses after any that were returned in a previous request. The current implementation
     * returns generated data and doesn't actually make a network request.
     *
     * @param request contains information about the user whose statuses are to be returned and any
     *                other information required to satisfy the request.
     * @return the following response.
     */
    public StoryResponse getStory(StoryRequest request) {

        // Used in place of assert statements because Android does not support them
        if(BuildConfig.DEBUG) {
            if(request.getLimit() < 0) {
                throw new AssertionError();
            }

            if(request.getUser() == null) {
                throw new AssertionError();
            }
        }

        if(storyStatusesByUser == null) {
            storyStatusesByUser = initializeStory();
        }

        List<Status> allStatuses = storyStatusesByUser.get(request.getUser());
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (allStatuses == null) {
            return new StoryResponse(responseStatuses, hasMorePages);
        }

        if(request.getLimit() > 0) {
            if (responseStatuses != null) {
                int statusesIndex = getStoryStartingIndex(request.getLastStatus(), allStatuses);

                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex < allStatuses.size();
            }
        }

        return new StoryResponse(responseStatuses, hasMorePages);
    }

    /**
     * Returns the statuses of the user specified in the request. Uses information in
     * the request object to limit the number of statuses returned and to return the next set of
     * statuses after any that were returned in a previous request. The current implementation
     * returns generated data and doesn't actually make a network request.
     *
     * @param request contains information about the user whose statuses are to be returned and any
     *                other information required to satisfy the request.
     * @return the following response.
     */
    public FeedResponse getFeed(FeedRequest request) {

        // Used in place of assert statements because Android does not support them
        if(BuildConfig.DEBUG) {
            if(request.getLimit() < 0) {
                throw new AssertionError();
            }

            if(request.getUser() == null) {
                throw new AssertionError();
            }
        }

        if(feedStatusesByUser == null) {
            feedStatusesByUser = initializeFeed();
        }

        List<Status> allStatuses = feedStatusesByUser.get(request.getUser());
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (allStatuses == null) {
            return new FeedResponse(responseStatuses, hasMorePages);
        }

        if(request.getLimit() > 0) {
            if (responseStatuses != null) {
                int statusesIndex = getFeedStartingIndex(request.getLastStatus(), allStatuses);

                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
                    responseStatuses.add(allStatuses.get(statusesIndex));
                }

                hasMorePages = statusesIndex < allStatuses.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }


    private List<Status> get21Statuses(User definedUser) {
        List<Status> feed = new ArrayList<>();
        String imageURL = "https://i.imgur.com/VZQQiQ1.jpg";

        if(definedUser == null) {
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

            // --------------------- 6 --------------------- //
            List<String> mSix = new ArrayList<>();
            mSix.add("@RobertGardner");
            mSix.add("@Snowden");
            mSix.add("@TristanThompson");
            d = createDate(2002, 10, 19, 14, 59);
            Calendar f = Calendar.getInstance();
            f.setTime(d);
            s = new Status(theMedia, "@RobertGardner @Snowden @TristanThompson", null, f, mSix);
            feed.add(s);

            // --------------------- 7 --------------------- //
            d = createDate(2000, 10, 19, 14, 59);
            Calendar g = Calendar.getInstance();
            g.setTime(d);
            s = new Status(KCP, ";)", null, g, null);
            feed.add(s);

            // --------------------- 8 --------------------- //
            d = createDate(2003, 5, 30, 16, 11);
            Calendar h = Calendar.getInstance();
            h.setTime(d);
            s = new Status(TristanThompson, "One, two, pick and roll", null, h, null);
            feed.add(s);

            // --------------------- 9 --------------------- //
            d = createDate(2001, 9, 4, 18, 29);
            Calendar i = Calendar.getInstance();
            i.setTime(d);
            s = new Status(Snowden, "A lot of old guys past their prime.", null, i, null);
            feed.add(s);

            // --------------------- 10 --------------------- //
            d = createDate(2019, 8, 12, 19, 1);
            Calendar j = Calendar.getInstance();
            j.setTime(d);
            s = new Status(TristanThompson, "I remember being a role player.", null, j, null);
            feed.add(s);

            // --------------------- 11 --------------------- //
            List<String> uEleven = new ArrayList<>();
            List<String> mEleven = new ArrayList<>();
            uEleven.add("salon.com");
            mEleven.add("@KCP");
            d = createDate(2007, 4, 15, 4, 43);
            Calendar k = Calendar.getInstance();
            k.setTime(d);
            s = new Status(theMedia, "Why did we sign him? @KCP. salon.com", uEleven, k, mEleven);
            feed.add(s);

            // --------------------- 12 --------------------- //
            List<String> mTwelve = new ArrayList<>();
            mTwelve.add("@theMedia");
            mTwelve.add("@Rudy");
            d = createDate(2016, 8, 9, 8, 5);
            Calendar l = Calendar.getInstance();
            l.setTime(d);
            s = new Status(BillBelichick, "Rudy and I are chill @theMedia @Rudy", null, l, mTwelve);
            feed.add(s);

            // --------------------- 13 --------------------- //
            d = createDate(2013, 3, 13, 9, 56);
            Calendar m = Calendar.getInstance();
            m.setTime(d);
            s = new Status(JacobWest, "I am the tinker man!", null, m, null);
            feed.add(s);

            // --------------------- 14 --------------------- //
            List<String> uFourteen = new ArrayList<>();
            List<String> mFourteen = new ArrayList<>();
            uFourteen.add("https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg");
            mFourteen.add("@BillBelichick");
            d = createDate(2013, 3, 13, 9, 55);
            Calendar n = Calendar.getInstance();
            n.setTime(d);
            s = new Status(JacobWest, "We are the new power couple @BillBelichick https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg", uFourteen, n, mFourteen);
            feed.add(s);

            // --------------------- 15 --------------------- //
            d = createDate(2012, 3, 13, 9, 55);
            Calendar o = Calendar.getInstance();
            o.setTime(d);
            s = new Status(Snowden, "That takes a lot of ownership!", null, o, null);
            feed.add(s);

            // --------------------- 16 --------------------- //
            d = createDate(2012, 3, 12, 9, 55);
            Calendar p = Calendar.getInstance();
            p.setTime(d);
            s = new Status(TristanThompson, "We beat the clippers!", null, p, null);
            feed.add(s);

            // --------------------- 17 --------------------- //
            d = createDate(2012, 3, 12, 9, 45);
            Calendar q = Calendar.getInstance();
            q.setTime(d);
            s = new Status(BillBelichick, "I lift bro!", null, q, null);
            feed.add(s);

            // --------------------- 18 --------------------- //
            d = createDate(2010, 8, 17, 9, 55);
            Calendar r = Calendar.getInstance();
            r.setTime(d);
            s = new Status(Rudy, "The truth is an acquired taste.", null, r, null);
            feed.add(s);

            // --------------------- 19 --------------------- //
            d = createDate(2020, 8, 17, 9, 55);
            Calendar sa = Calendar.getInstance();
            sa.setTime(d);
            s = new Status(RickyMartin, "Encuentra la buena vida baby! #CoronaLite", null, sa, null);
            feed.add(s);

            // --------------------- 20 --------------------- //
            d = createDate(2020, 0, 27, 23, 55);
            Calendar t = Calendar.getInstance();
            t.setTime(d);
            s = new Status(KCP, "Me calle bien el Snoop Dogg", null, t, null);
            feed.add(s);

            // --------------------- 21 --------------------- //
            d = createDate(2020, 3, 7, 9, 4);
            Calendar u = Calendar.getInstance();
            u.setTime(d);
            s = new Status(RobertGardner, "Hago buena musica", null, u, null);
            feed.add(s);
        } else {
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
            feed.add(s); // # 1

            // --------------------- 2 --------------------- //
            List<String> uTwo = new ArrayList<>();
            uTwo.add("tinyurl.com");
            d = createDate(2020, 0, 11, 0, 14);
            Calendar b = Calendar.getInstance();
            b.setTime(d);
            s = new Status(definedUser, "You should visit tinyurl.com", uTwo, b, null);
            feed.add(s);

            // --------------------- 3 --------------------- //
            List<String> mThree = new ArrayList<>();
            mThree.add("@JacobWest");
            d = createDate(2019, 3, 16, 3, 34);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            s = new Status(definedUser, "Dolphins @JacobWest have Tua", null, c, mThree);
            feed.add(s);

            // --------------------- 4 --------------------- //
            d = createDate(2014, 7, 30, 17, 01);
            Calendar de = Calendar.getInstance();
            de.setTime(d);
            s = new Status(definedUser, "Jacksonville will draft third", null, de, null);
            feed.add(s);

            // --------------------- 5 --------------------- //
            List<String> uFive = new ArrayList<>();
            uFive.add("dell.com");
            d = createDate(2012, 3, 3, 18, 21);
            Calendar e = Calendar.getInstance();
            e.setTime(d);
            s = new Status(definedUser, "I endorse dell.com", uFive, e, null);
            feed.add(s);

            // --------------------- 6 --------------------- //
            List<String> mSix = new ArrayList<>();
            mSix.add("@RobertGardner");
            mSix.add("@Snowden");
            mSix.add("@TristanThompson");
            d = createDate(2002, 10, 19, 14, 59);
            Calendar f = Calendar.getInstance();
            f.setTime(d);
            s = new Status(definedUser, "@RobertGardner @Snowden @TristanThompson", null, f, mSix);
            feed.add(s);

            // --------------------- 7 --------------------- //
            d = createDate(2000, 10, 19, 14, 59);
            Calendar g = Calendar.getInstance();
            g.setTime(d);
            s = new Status(definedUser, ";)", null, g, null);
            feed.add(s);

            // --------------------- 8 --------------------- //
            d = createDate(2003, 5, 30, 16, 11);
            Calendar h = Calendar.getInstance();
            h.setTime(d);
            s = new Status(definedUser, "One, two, pick and roll", null, h, null);
            feed.add(s);

            // --------------------- 9 --------------------- //
            d = createDate(2001, 9, 4, 18, 29);
            Calendar i = Calendar.getInstance();
            i.setTime(d);
            s = new Status(definedUser, "A lot of old guys past their prime.", null, i, null);
            feed.add(s);

            // --------------------- 10 --------------------- //
            d = createDate(2019, 8, 12, 19, 1);
            Calendar j = Calendar.getInstance();
            j.setTime(d);
            s = new Status(definedUser, "I remember being a role player.", null, j, null);
            feed.add(s);

            // --------------------- 11 --------------------- //
            List<String> uEleven = new ArrayList<>();
            List<String> mEleven = new ArrayList<>();
            uEleven.add("salon.com");
            mEleven.add("@KCP");
            d = createDate(2007, 4, 15, 4, 43);
            Calendar k = Calendar.getInstance();
            k.setTime(d);
            s = new Status(definedUser, "Why did we sign him? @KCP. salon.com", uEleven, k, mEleven);
            feed.add(s);

            // --------------------- 12 --------------------- //
            List<String> mTwelve = new ArrayList<>();
            mTwelve.add("@theMedia");
            mTwelve.add("@Rudy");
            d = createDate(2016, 8, 9, 8, 5);
            Calendar l = Calendar.getInstance();
            l.setTime(d);
            s = new Status(definedUser, "Rudy and I are chill @theMedia @Rudy", null, l, mTwelve);
            feed.add(s);

            // --------------------- 13 --------------------- //
            d = createDate(2013, 3, 13, 9, 56);
            Calendar m = Calendar.getInstance();
            m.setTime(d);
            s = new Status(definedUser, "I am the tinker man!", null, m, null);
            feed.add(s);

            // --------------------- 14 --------------------- //
            List<String> uFourteen = new ArrayList<>();
            List<String> mFourteen = new ArrayList<>();
            uFourteen.add("https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg");
            mFourteen.add("@BillBelichick");
            d = createDate(2013, 3, 13, 9, 55);
            Calendar n = Calendar.getInstance();
            n.setTime(d);
            s = new Status(definedUser, "We are the new power couple @BillBelichick https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg", uFourteen, n, mFourteen);
            feed.add(s);

            // --------------------- 15 --------------------- //
            d = createDate(2012, 3, 13, 9, 55);
            Calendar o = Calendar.getInstance();
            o.setTime(d);
            s = new Status(definedUser, "That takes a lot of ownership!", null, o, null);
            feed.add(s);

            // --------------------- 16 --------------------- //
            d = createDate(2012, 3, 12, 9, 55);
            Calendar p = Calendar.getInstance();
            p.setTime(d);
            s = new Status(definedUser, "We beat the clippers!", null, p, null);
            feed.add(s);

            // --------------------- 17 --------------------- //
            d = createDate(2012, 3, 12, 9, 45);
            Calendar q = Calendar.getInstance();
            q.setTime(d);
            s = new Status(definedUser, "I lift bro!", null, q, null);
            feed.add(s);

            // --------------------- 18 --------------------- //
            d = createDate(2010, 8, 17, 9, 55);
            Calendar r = Calendar.getInstance();
            r.setTime(d);
            s = new Status(definedUser, "The truth is an acquired taste.", null, r, null);
            feed.add(s);

            // --------------------- 19 --------------------- //
            d = createDate(2020, 8, 17, 9, 55);
            Calendar sa = Calendar.getInstance();
            sa.setTime(d);
            s = new Status(definedUser, "Encuentra la buena vida baby! #CoronaLite", null, sa, null);
            feed.add(s);

            // --------------------- 20 --------------------- //
            d = createDate(2020, 0, 27, 23, 55);
            Calendar t = Calendar.getInstance();
            t.setTime(d);
            s = new Status(definedUser, "Me calle bien el Snoop Dogg", null, t, null);
            feed.add(s);

            // --------------------- 21 --------------------- //
            d = createDate(2020, 3, 7, 9, 4);
            Calendar u = Calendar.getInstance();
            u.setTime(d);
            s = new Status(definedUser, "Hago buena musica", null, u, null);
            feed.add(s);
        }
        return feed;
    }

    /**
     * Gets a list of statuses by user to be returned.
     */
    private Map<User, List<Status>> initializeFeed() {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();
        List<Status> statuses = get21Statuses(null);
        returnMe.put(user1, statuses);
        returnMe.put(user2, statuses);
        returnMe.put(user3, statuses);
        returnMe.put(user4, statuses);
        returnMe.put(user5, statuses);
        returnMe.put(user6, statuses);
        returnMe.put(user7, statuses);
        returnMe.put(user8, statuses);
        returnMe.put(user9, statuses);
        returnMe.put(user10, statuses);
        returnMe.put(user11, statuses);
        returnMe.put(user12, statuses);
        returnMe.put(user13, statuses);
        returnMe.put(user14, statuses);
        returnMe.put(user15, statuses);
        returnMe.put(user16, statuses);
        returnMe.put(user17, statuses);
        returnMe.put(user18, statuses);
        returnMe.put(user19, statuses);
        returnMe.put(user20, statuses);
        returnMe.put(JacobWest, statuses);
        returnMe.put(RickyMartin, statuses);
        returnMe.put(RobertGardner, statuses);
        returnMe.put(Snowden, statuses);
        returnMe.put(TristanThompson, statuses);
        returnMe.put(KCP, statuses);
        returnMe.put(theMedia, statuses);
        returnMe.put(Rudy, statuses);
        returnMe.put(BillBelichick, statuses);
        returnMe.put(TestUser, statuses);
        return returnMe;
    }

    /**
     * Gets a list of statuses by user to be returned.
     */
    private Map<User, List<Status>> initializeStory() {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();
        List<Status> jacobStatusList = get21Statuses(JacobWest);
        returnMe.put(JacobWest, jacobStatusList);
        List<Status> rickyStatusList = get21Statuses(RickyMartin);
        returnMe.put(RickyMartin, rickyStatusList);
        List<Status> robertStatusList = get21Statuses(RobertGardner);
        returnMe.put(RobertGardner, robertStatusList);
        List<Status> snowdenStatusList = get21Statuses(Snowden);
        returnMe.put(Snowden, snowdenStatusList);
        List<Status> tristanStatusList = get21Statuses(TristanThompson);
        returnMe.put(TristanThompson, tristanStatusList);
        List<Status> kcpStatusList = get21Statuses(KCP);
        returnMe.put(KCP, kcpStatusList);
        List<Status> mediaStatusList = get21Statuses(theMedia);
        returnMe.put(theMedia, mediaStatusList);
        List<Status> rudyStatusList = get21Statuses(Rudy);
        returnMe.put(Rudy, rudyStatusList);
        List<Status> billStatusList = get21Statuses(BillBelichick);
        returnMe.put(BillBelichick, billStatusList);
        List<Status> testStatusList = get21Statuses(TestUser);
        returnMe.put(TestUser, testStatusList);
        List<Status> a = get21Statuses(user1);
        returnMe.put(user1, a);
        List<Status> b = get21Statuses(user2);
        returnMe.put(user2, b);
        List<Status> c = get21Statuses(user3);
        returnMe.put(user3, c);
        List<Status> d = get21Statuses(user4);
        returnMe.put(user4, d);
        List<Status> e = get21Statuses(user5);
        returnMe.put(user5, e);
        List<Status> f = get21Statuses(user6);
        returnMe.put(user6, f);
        List<Status> g = get21Statuses(user7);
        returnMe.put(user7, g);
        List<Status> h = get21Statuses(user8);
        returnMe.put(user8, h);
        List<Status> i = get21Statuses(user9);
        returnMe.put(user9, i);
        List<Status> j = get21Statuses(user10);
        returnMe.put(user10, j);
        List<Status> k = get21Statuses(user11);
        returnMe.put(user11, k);
        List<Status> l = get21Statuses(user12);
        returnMe.put(user12, l);
        List<Status> m = get21Statuses(user13);
        returnMe.put(user13, m);
        List<Status> n = get21Statuses(user14);
        returnMe.put(user14, n);
        List<Status> o = get21Statuses(user15);
        returnMe.put(user15, o);
        List<Status> p = get21Statuses(user16);
        returnMe.put(user16, p);
        List<Status> q = get21Statuses(user17);
        returnMe.put(user17, q);
        List<Status> r = get21Statuses(user18);
        returnMe.put(user18, r);
        List<Status> s = get21Statuses(user19);
        returnMe.put(user19, s);
        List<Status> t = get21Statuses(user20);
        returnMe.put(user20, t);
        return returnMe;
    }


    private Date createDate(int year, int month, int day, int hour, int minute) {
        Date d = new Date(year - 1900, month, day);
        d.setHours(hour);
        d.setMinutes(minute);
        return d;
    }

    /**
     * Determines the index for the first status in the specified 'allStatuses' list that should
     * be returned in the current request. This will be the index of the next status after the
     * specified 'lastStatus'.
     *
     * @param lastStatus the last follower that was returned in the previous request or null if
     *                     there was no previous request.
     * @param allStatuses the generated list of followers from which we are returning paged results.
     * @return the index of the first follower to be returned.
     */
    private int getStoryStartingIndex(Status lastStatus, List<Status> allStatuses) {

        int statusesIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusesIndex = i + 1;
                }
            }
        }

        return statusesIndex;
    }

    /**
     * Determines the index for the first status in the specified 'allStatuses' list that should
     * be returned in the current request. This will be the index of the next status after the
     * specified 'lastStatus'.
     *
     * @param lastStatus the last follower that was returned in the previous request or null if
     *                     there was no previous request.
     * @param allStatuses the generated list of followers from which we are returning paged results.
     * @return the index of the first follower to be returned.
     */
    private int getFeedStartingIndex(Status lastStatus, List<Status> allStatuses) {

        int statusesIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusesIndex = i + 1;
                }
            }
        }

        return statusesIndex;
    }

    /**
     * Generates the follower data.
     */
    private Map<User, List<User>> initializeFollowers() {

        Map<User, List<User>> followersByFollower = new HashMap<>();

        List<Follow> follows = getFollowGenerator().generateUsersAndFollows(100,
                0, 50, FollowGenerator.Sort.FOLLOWER_FOLLOWEE);

        // Populate a map of followers, keyed by follower so we can easily handle follower requests
        for(Follow follow : follows) {
            List<User> followers = followersByFollower.get(follow.getFollower());

            if(followers == null) {
                followers = new ArrayList<>();
                followersByFollower.put(follow.getFollower(), followers);
            }

            followers.add(follow.getFollowee());
        }

        return followersByFollower;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the generator.
     */
    List<User> getDummyFollowees() {
        return Arrays.asList(user1, user2, theMedia, user4, user5, user6, user7, RickyMartin, RobertGardner, TristanThompson,
                user8, user9, user10, user11, JacobWest, Snowden, user12, user13, user14, user15, user16, user17, user18, TestUser,
                user19, user20, BillBelichick, KCP, Rudy, TestUser);
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the generator.
     */
    List<User> getDummyFollowers() {
        return Arrays.asList(user3, JacobWest, user1, RickyMartin, user4, RobertGardner, user2, Snowden, user5, user6, user7,
                user17, user9, user13, user11, user12, user10, user14, user15, user8, user19, TristanThompson, KCP, theMedia, Rudy,
                user18, user20, BillBelichick, TestUser);
    }
}
