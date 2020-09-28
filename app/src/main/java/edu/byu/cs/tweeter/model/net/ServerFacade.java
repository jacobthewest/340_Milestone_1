package edu.byu.cs.tweeter.model.net;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import edu.byu.cs.tweeter.model.service.request.FollowersRequest;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.FollowersResponse;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;

/**
 * Acts as a Facade to the Tweeter server. All network requests to the server should go through
 * this class.
 */
public class ServerFacade {

    private static Map<User, List<User>> followeesByFollower;
    private static Map<User, List<User>> followersByFollower;
    private static Map<User, List<Status>> statusesByUser;

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

    //------------------------------------------------------------------------------------------------------------------------//

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
            statusesByUser = getStatusList(request.getUser());
        }

        List<Status> allStatuses = statusesByUser.get(request.getUser());
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

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
     * Gets a list of statuses to be returned.
     */
    private Map<User, List<Status>> getStatusList() {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();
        List<Status> statusList = new ArrayList<>();

        List<Date> timesPosted = get21DatesShuffled();
        List<String> postTexts = get21PostTexts();
        List<String> mentions = get21Mentions();
        List<User> users = get21Users();

        final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
        for(int i = 0; i < 21; i++) {
            String postText = postTexts.get(i);
            String mention = mentions.get(i);
            List<String> mentionForStatus = new ArrayList<>();
            mentionForStatus.add(mention);
            Date d = timesPosted.get(i);
            Calendar tempTime = Calendar.getInstance();
            tempTime.setTime(d);
            User user = users.get(i);
            Status s = new Status(user, postText, "", "", tempTime, mentionForStatus);
            statusList.add(s);
        }

        //returnMe.put(user, statusList); // TODO: make this work for the feed class with the correct key value pair.
        return returnMe;
    }

    /**
     * Gets a list of statuses by user to be returned.
     */
    private Map<User, List<Status>> getStatusList(User user) {
        Map<User, List<Status>> returnMe = new HashMap<User, List<Status>>();
        List<Status> statusList = new ArrayList<>();

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

        returnMe.add(new User("Rick", "James", "https://i.imgur.com/VZQQiQ1.jpg"));
        returnMe.add(new User("Mother", "Teresa", "https://tinyurl.com/yyuhyxtq"));
        returnMe.add(new User("Jenny", "Briggs", "https://tinyurl.com/y4etkgz3"));
        returnMe.add(new User("Lebron", "James", "https://tinyurl.com/y5mn7sb8"));
        returnMe.add(new User("Tom", "Hanks", "https://tinyurl.com/y3gbuq4e"));
        returnMe.add(new User("Asian", "Lady", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTolh7JsU_RK4jNdua6kswxV2_X1Amn9iQHCA&usqp=CAU"));
        returnMe.add(new User("Pretty", "Boy", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRLmQEVyIKTcUhYK2nnZgEbcRqwk4ivcIywzg&usqp=CAU"));
        returnMe.add(new User("Chicken", "Licken", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSyjfqnF7A73e3bk8DBSRSq8MBfKZlN-96xGw&usqp=CAU"));
        returnMe.add(new User("Bucktooth", "Baby", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcT7hiU-I6HLZIZIsI0LDx8KEw7DxZW8ob_1pw&usqp=CAU"));
        returnMe.add(new User("Skooby", "Doo", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRZwVm6oDbWFrt1SZ2DySHGmjeTped8k7qwMg&usqp=CAU"));
        returnMe.add(new User("Kevin", "West", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR1JeZ1OXzUTevF7SrczFvxLr-31jyvM99FvQ&usqp=CAU"));
        returnMe.add(new User("Rachel", "West", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcS3gmJ1S9MQtUBxvJxmxJjL5zbYGsQDzJMWXQ&usqp=CAU"));
        returnMe.add(new User("Albert", "Eistein", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQLpzI7TKwYun2SvUBiMSltJSxRwWcJtoe-uQ&usqp=CAU"));
        returnMe.add(new User("Brett", "West", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTc0MFBFdXoklbgKAzui11EXEPX6J_l2fJ--Q&usqp=CAU"));
        returnMe.add(new User("Bien", "Choro", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSqyc8DsTL5owJGi-JA4uLq_WqAck6U65qCMw&usqp=CAU"));
        returnMe.add(new User("Donald", "Trump", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTOtITMKdF-BXu_bHnREHyjdO2cM7A0KGW7Ug&usqp=CAU"));
        returnMe.add(new User("Sam", "Wright", "data:image/png;base64,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"));
        returnMe.add(new User("Sponge", "Bob", "data:image/jpeg;base64,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"));
        returnMe.add(new User("Nacho", "Libre", "data:image/jpeg;base64,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"));
        returnMe.add(new User("Homer", "Simpson", "data:image/jpeg;base64,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"));

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
