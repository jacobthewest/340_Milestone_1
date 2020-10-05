package edu.byu.cs.tweeter.model.net;

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
import edu.byu.cs.tweeter.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.FeedResponse;
import edu.byu.cs.tweeter.model.service.response.FollowersResponse;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.service.response.RegisterResponse;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;

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
        return new LoginResponse(user, new AuthToken());
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
        return new RegisterResponse(user, new AuthToken());
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
        List<Status> statusList = new ArrayList<>();

        // Here to make a newly registered user have no followers/followees
        if(!user.getFirstName().equals("Test") || !user.getLastName().equals("User")) {
            returnMe.put(new User("new", "regristration", ""), statusList);
            return returnMe;
        }

        List<Date> timesPosted = get21DatesShuffled();
        List<String> postTexts = get21PostTexts();
        List<String> mentions = get21Mentions();
        List<User> users = get21Users();

        for(int i = 0; i < 21; i++) {
            String postText = postTexts.get(i);
            String mention = mentions.get(i);
            List<String> mentionForStatus = new ArrayList<>();
            mentionForStatus.add(mention);
            Date d = timesPosted.get(i);
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTime(d);
            User tempUser = users.get(i);
            Status s = new Status(tempUser, postText, "", "", tempTime, mentionForStatus);
            statusList.add(s);
        }

        returnMe.put(user, statusList);
        return returnMe;
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

        List<Date> timesPosted = get21DatesShuffled();
        List<String> postTexts = get21PostTexts();
        List<String> mentions = get21Mentions();

        final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
        for(int i = 0; i < 21; i++) {
            String postText = postTexts.get(i);
            String mention = mentions.get(i);
            List<String> mentionForStatus = new ArrayList<>();
            mentionForStatus.add(mention);
            Date d = timesPosted.get(i);
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTime(d);
            Status s = new Status(user, postText, "", "", tempTime, mentionForStatus);
            statusList.add(s);
        }

        returnMe.put(user, statusList);
        return returnMe;
    }

    /**
     * Gets mentions for creating statuses
     * @return a list of 21 mentions
     */
    private List<String> get21Mentions() {
        List<String> mentions = new ArrayList<>();
        mentions.add("@TomHanks");
        mentions.add("");
        mentions.add("");
        mentions.add("@RusselWilson");
        mentions.add("@LamarJackson");
        mentions.add("");
        mentions.add("@JerryJudy");
        mentions.add("");
        mentions.add("@DonaldTrump");
        mentions.add("@ElonMusk");
        mentions.add("@OprahWinfrey");
        mentions.add("");
        mentions.add("");
        mentions.add("@Wendy's");
        mentions.add("");
        mentions.add("");
        mentions.add("@JennyBriggsWest");
        mentions.add("");
        mentions.add("@YourMom");
        mentions.add("");
        mentions.add("@CountryTimeLemonade");
        return mentions;
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Date d = new Date(year - 1900, month, day);
        d.setHours(hour);
        d.setMinutes(minute);
        return d;
    }

    /**
     * Gets date objects for creating statuses
     * @return
     */
    private List<Date> get21DatesShuffled() {
        List<Date> returnMe = new ArrayList<>();

        returnMe.add(createDate(2020, 0, 11, 0, 13));
        returnMe.add(createDate(1996, 1, 1, 1, 4));
        returnMe.add(createDate(2012, 2, 21, 2, 13));
        returnMe.add(createDate(2013, 3, 13, 3, 56));
        returnMe.add(createDate(2007, 4, 15, 4, 43));
        returnMe.add(createDate(2000, 5, 28, 5, 13));
        returnMe.add(createDate(2018, 6, 30, 6, 12));
        returnMe.add(createDate(2017, 7, 12, 7, 21));
        returnMe.add(createDate(2016, 8, 9, 8, 5));
        returnMe.add(createDate(2015, 9, 2, 9, 59));
        returnMe.add(createDate(2014, 10, 3, 10, 33));
        returnMe.add(createDate(2013, 11, 5, 11, 30));
        returnMe.add(createDate(2009, 2, 16, 12, 17));
        returnMe.add(createDate(2010, 3, 24, 13, 9));
        returnMe.add(createDate(2015, 4, 29, 14, 3));
        returnMe.add(createDate(2014, 1, 28, 15, 31));
        returnMe.add(createDate(2003, 5, 30, 16, 11));
        returnMe.add(createDate(2002, 6, 8, 17, 36));
        returnMe.add(createDate(2001, 9, 4, 18, 29));
        returnMe.add(createDate(2019, 8, 12, 19, 1));
        returnMe.add(createDate(2005, 1, 4, 21, 2));

        Collections.sort(returnMe, Collections.reverseOrder());
        return returnMe;
    }

    /**
     * Gets user objects for creating statuses for the feed view
     * @return random users
     */
    private List<User> get21Users() {
        List<User> returnMe = new ArrayList<>();
        String MIKE = "https://i.imgur.com/VZQQiQ1.jpg";
        returnMe.add(new User("Rick", "James", MIKE));
        returnMe.add(new User("Mother", "Teresa", MIKE));
        returnMe.add(new User("Jenny", "Briggs", MIKE));
        returnMe.add(new User("Lebron", "James", MIKE));
        returnMe.add(new User("Tom", "Hanks", MIKE));
        returnMe.add(new User("Asian", "Lady", MIKE));
        returnMe.add(new User("Pretty", "Boy", MIKE));
        returnMe.add(new User("Chicken", "Licken", MIKE));
        returnMe.add(new User("Bucktooth", "Baby", MIKE));
        returnMe.add(new User("Skooby", "Doo", MIKE));
        returnMe.add(new User("Kevin", "West", MIKE));
        returnMe.add(new User("Rachel", "West", MIKE));
        returnMe.add(new User("Albert", "Eistein", MIKE));
        returnMe.add(new User("Brett", "West", MIKE));
        returnMe.add(new User("Bien", "Choro", MIKE));
        returnMe.add(new User("Donald", "Trump", MIKE));
        returnMe.add(new User("Sam", "Wright", MIKE));
        returnMe.add(new User("Sponge", "Bob", MIKE));
        returnMe.add(new User("Nacho", "Libre", MIKE));
        returnMe.add(new User("Homer", "Simpson", MIKE));
        returnMe.add(new User("Lord", "Farquaad", MIKE));

        Collections.shuffle(returnMe);
        return returnMe;
    }

    /**
     * Gets post texts for creating users
     * @return a list of texts for tweets
     */
    private List<String> get21PostTexts() {
        List<String> returnMe = new ArrayList<>();
        returnMe.add("This is my first tweet");
        returnMe.add("How is the weather up there");
        returnMe.add("Bears, beats, Battle Star Galactica");
        returnMe.add("The STEM fair was crazy today! Seriously, it was all virtual, but it was still crazy.");
        returnMe.add("Helaman 5:12");
        returnMe.add("Telestrations is a fun game. Give it a play!");
        returnMe.add("I like collecting house plants, succulents, and cacti");
        returnMe.add("I bought glassware at D.I.");
        returnMe.add("Chacos aren't basic white girl shoes, they are actually functional people!");
        returnMe.add("Mini Wheats are on top of my fridge...");
        returnMe.add("Trash Pandas are funny.");
        returnMe.add("Pr0p3r Punctuat10n");
        returnMe.add("My wife is bored and wants to hang out.");
        returnMe.add("MyPillow is such a heavenly pillow!");
        returnMe.add("I have a porcelain chicken");
        returnMe.add("My hands hurt from typing");
        returnMe.add("I got a new phone case.....................................");
        returnMe.add("Silicone rings are more comfortable than metal rings");
        returnMe.add("My sisters and mom don't have Covid!");
        returnMe.add("I'm going to the gym tomorrow");
        returnMe.add("I need to water my plants now.");
        return returnMe;
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
