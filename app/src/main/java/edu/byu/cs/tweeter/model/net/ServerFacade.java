package edu.byu.cs.tweeter.model.net;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.BuildConfig;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.FeedRequest;
import edu.byu.cs.tweeter.model.service.request.FollowersRequest;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.FeedResponse;
import edu.byu.cs.tweeter.model.service.response.FollowersResponse;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.model.service.response.RetrieveUserResponse;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    private static Map<User, List<User>> followeesByFollower;
    private static Map<User, List<User>> followersByFollower;
    private static Map<User, List<Status>> statusesByUser;
    private static Map<User, List<Status>> feedStatuses;

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
        User user = new User("Test", "User", "https://i.imgur.com/VZQQiQ1.jpg");
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

        return new LoginResponse(user, new AuthToken(user.getAlias()));
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
        User user = new User(request.getFirstName(), request.getLastName(), request.getImageUrl(), request.getImageBytes());
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

        if(followeesByFollower == null) {
            followeesByFollower = initializeFollowees();
        }

        List<User> allFollowees = followeesByFollower.get(request.getUser());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowees != null) {
                int followeesIndex = getFolloweesStartingIndex(request.getLastFollowee(), allFollowees);

                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
                    responseFollowees.add(allFollowees.get(followeesIndex));
                }

                hasMorePages = followeesIndex < allFollowees.size();
            }
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

        if(followersByFollower == null) {
            followersByFollower = initializeFollowers();
        }

        List<User> allFollowers = followersByFollower.get(request.getUser());
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollower(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
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

        if(statusesByUser == null) {
            statusesByUser = getStoryStatusList(request.getUser());
        }

        List<Status> allStatuses = statusesByUser.get(request.getUser());
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

        if(feedStatuses == null) {
            feedStatuses = getFeedStatusList(request.getUser());
        }

        List<Status> allStatuses = feedStatuses.get(request.getUser());
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

    /**
     * Gets a list of statuses to be returned.
     */
    private Map<User, List<Status>> getFeedStatusList(User user) {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();

        // Here to make a newly registered user have no followers/followees
        if(!user.getFirstName().equals("Test") || !user.getLastName().equals("User")) {
            List<Status> statusList = new ArrayList<>();
            returnMe.put(new User("new", "regristration", ""), statusList);
            return returnMe;
        }

        returnMe.put(user, get21Statuses(null));
        return returnMe; // Won't ever get past this.

//        List<Date> timesPosted = get21DatesSorted();
//        List<String> postTexts = get21PostTexts();
//        List<String> mentions = get21Mentions();
//        List<User> users = get21Users();
//
//        for(int i = 0; i < 21; i++) {
//            String postText = postTexts.get(i);
//            String mention = mentions.get(i);
//            List<String> mentionForStatus = new ArrayList<>();
//            mentionForStatus.add(mention);
//            Date d = timesPosted.get(i);
//            Calendar tempTime = Calendar.getInstance();
//            tempTime.setTime(d);
//            User tempUser = users.get(i);
//            Status s = new Status(tempUser, postText, null, tempTime, mentionForStatus);
//            statusList.add(s);
//        }
//
//        returnMe.put(user, statusList);
//        return returnMe;
    }


    private List<Status> get21Statuses(User definedUser) {
        List<Status> feed = new ArrayList<>();
        String imageURL = "https://i.imgur.com/VZQQiQ1.jpg";

        if(definedUser == null) {
            // --------------------- 1--------------------- //
            User user = new User("Bill", "Adams", imageURL);
            List<String> uOne = new ArrayList<>();
            uOne.add("multiply.com");
            List<String> mOne = new ArrayList<>();
            mOne.add("@JacobWest");
            mOne.add("@RickyMartin");
            Date d = createDate(2020, 0, 11, 0, 13);
            Calendar a = Calendar.getInstance();
            a.setTime(d);
            Status s = new Status(user, "This is a text @JacobWest @RickyMartin multiply.com", uOne, a, mOne);
            feed.add(s); // # 1

            // --------------------- 2 --------------------- //
            user = new User("Colin", "Cowherd", imageURL);
            List<String> uTwo = new ArrayList<>();
            uTwo.add("tinyurl.com");
            d = createDate(2020, 0, 11, 0, 14);
            Calendar b = Calendar.getInstance();
            b.setTime(d);
            s = new Status(user, "You should visit tinyurl.com", uTwo, b, null);
            feed.add(s);

            // --------------------- 3 --------------------- //
            user = new User("James", "Shulte", imageURL);
            List<String> mThree = new ArrayList<>();
            mThree.add("@JacobWest");
            d = createDate(2019, 3, 16, 3, 34);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            s = new Status(user, "Dolphins @JacobWest have Tua", null, c, mThree);
            feed.add(s);

            // --------------------- 4 --------------------- //
            user = new User("Joe", "Flacco", imageURL);
            d = createDate(2014, 7, 30, 17, 01);
            Calendar de = Calendar.getInstance();
            de.setTime(d);
            s = new Status(user, "Jacksonville will draft third", null, de, null);
            feed.add(s);

            // --------------------- 5 --------------------- //
            user = new User("Lebron", "James", imageURL);
            List<String> uFive = new ArrayList<>();
            uFive.add("dell.com");
            d = createDate(2012, 3, 3, 18, 21);
            Calendar e = Calendar.getInstance();
            e.setTime(d);
            s = new Status(user, "I endorse dell.com", uFive, e, null);
            feed.add(s);

            // --------------------- 6 --------------------- //
            user = new User("Aaron", "Rodgers", imageURL);
            List<String> mSix = new ArrayList<>();
            mSix.add("@RobertGardner");
            mSix.add("@Snowden");
            mSix.add("@TristanThompson");
            d = createDate(2002, 10, 19, 14, 59);
            Calendar f = Calendar.getInstance();
            f.setTime(d);
            s = new Status(user, "@RobertGardner @Snowden @TristanThompson", null, f, mSix);
            feed.add(s);

            // --------------------- 7 --------------------- //
            user = new User("Kyle", "West", imageURL);
            d = createDate(2000, 10, 19, 14, 59);
            Calendar g = Calendar.getInstance();
            g.setTime(d);
            s = new Status(user, ";)", null, g, null);
            feed.add(s);

            // --------------------- 8 --------------------- //
            user = new User("Random", "Dude", imageURL);
            d = createDate(2003, 5, 30, 16, 11);
            Calendar h = Calendar.getInstance();
            h.setTime(d);
            s = new Status(user, "One, two, pick and roll", null, h, null);
            feed.add(s);

            // --------------------- 9 --------------------- //
            user = new User("Another", "Dude", imageURL);
            d = createDate(2001, 9, 4, 18, 29);
            Calendar i = Calendar.getInstance();
            i.setTime(d);
            s = new Status(user, "A lot of old guys past their prime.", null, i, null);
            feed.add(s);

            // --------------------- 10 --------------------- //
            user = new User("Tristan", "Thompson", imageURL);
            d = createDate(2019, 8, 12, 19, 1);
            Calendar j = Calendar.getInstance();
            j.setTime(d);
            s = new Status(user, "I remember being a role player.", null, j, null);
            feed.add(s);

            // --------------------- 11 --------------------- //
            user = new User("Tristan", "Thompson", imageURL);
            List<String> uEleven = new ArrayList<>();
            List<String> mEleven = new ArrayList<>();
            uEleven.add("salon.com");
            mEleven.add("@KCP");
            d = createDate(2007, 4, 15, 4, 43);
            Calendar k = Calendar.getInstance();
            k.setTime(d);
            s = new Status(user, "Why did we sign him? @KCP. salon.com", uEleven, k, mEleven);
            feed.add(s);

            // --------------------- 12 --------------------- //
            user = new User("Donnovan", "Mitchell", imageURL);
            List<String> mTwelve = new ArrayList<>();
            mTwelve.add("@theMedia");
            mTwelve.add("@Rudy");
            d = createDate(2016, 8, 9, 8, 5);
            Calendar l = Calendar.getInstance();
            l.setTime(d);
            s = new Status(user, "Rudy and I are chill @theMedia @Rudy", null, l, mTwelve);
            feed.add(s);

            // --------------------- 13 --------------------- //
            user = new User("Mr.", "Tinkerer", imageURL);
            d = createDate(2013, 3, 13, 9, 56);
            Calendar m = Calendar.getInstance();
            m.setTime(d);
            s = new Status(user, "I am the tinker man!", null, m, null);
            feed.add(s);

            // --------------------- 14 --------------------- //
            user = new User("Cam", "Newton", imageURL);
            List<String> uFourteen = new ArrayList<>();
            List<String> mFourteen = new ArrayList<>();
            uFourteen.add("https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg");
            mFourteen.add("@BillBelichick");
            d = createDate(2013, 3, 13, 9, 55);
            Calendar n = Calendar.getInstance();
            n.setTime(d);
            s = new Status(user, "We are the new power couple @BillBelichick https://www.bostonherald.com/wp-content/uploads/2019/09/patsnl037.jpg", uFourteen, n, mFourteen);
            feed.add(s);

            // --------------------- 15 --------------------- //
            user = new User("Jason", "Kidd", imageURL);
            d = createDate(2012, 3, 13, 9, 55);
            Calendar o = Calendar.getInstance();
            o.setTime(d);
            s = new Status(user, "That takes a lot of ownership!", null, o, null);
            feed.add(s);

            // --------------------- 16 --------------------- //
            user = new User("Nikola", "Yokic", imageURL);
            d = createDate(2012, 3, 12, 9, 55);
            Calendar p = Calendar.getInstance();
            p.setTime(d);
            s = new Status(user, "We beat the clippers!", null, p, null);
            feed.add(s);

            // --------------------- 17 --------------------- //
            user = new User("Taysom", "Hill", imageURL);
            d = createDate(2012, 3, 12, 9, 45);
            Calendar q = Calendar.getInstance();
            q.setTime(d);
            s = new Status(user, "I lift bro!", null, q, null);
            feed.add(s);

            // --------------------- 18 --------------------- //
            user = new User("Jenny", "Briggs", imageURL);
            d = createDate(2010, 8, 17, 9, 55);
            Calendar r = Calendar.getInstance();
            r.setTime(d);
            s = new Status(user, "The truth is an acquired taste.", null, r, null);
            feed.add(s);

            // --------------------- 19 --------------------- //
            user = new User("Bad", "Bunny", imageURL);
            d = createDate(2020, 8, 17, 9, 55);
            Calendar sa = Calendar.getInstance();
            sa.setTime(d);
            s = new Status(user, "Encuentra la buena vida baby! #CoronaLite", null, sa, null);
            feed.add(s);

            // --------------------- 20 --------------------- //
            user = new User("Bad", "Bunny", imageURL);
            d = createDate(2020, 0, 27, 23, 55);
            Calendar t = Calendar.getInstance();
            t.setTime(d);
            s = new Status(user, "Me calle bien el Snoop Dogg", null, t, null);
            feed.add(s);

            // --------------------- 21 --------------------- //
            user = new User("Bad", "Bunny", imageURL);
            d = createDate(2020, 3, 7, 9, 4);
            Calendar u = Calendar.getInstance();
            u.setTime(d);
            s = new Status(user, "Hago buena musica", null, u, null);
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
    private Map<User, List<Status>> getStoryStatusList(User user) {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();
        List<Status> statusList = new ArrayList<>();

        // Here to make a newly registered user have no followers/followees
        if(!user.getFirstName().equals("Test") || !user.getLastName().equals("User")) {
            returnMe.put(new User("", "", ""), statusList);
            return returnMe;
        }

        statusList = get21Statuses(user);

        returnMe.put(user, statusList);
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
}
