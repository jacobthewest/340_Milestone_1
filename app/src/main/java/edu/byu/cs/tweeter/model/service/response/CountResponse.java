package edu.byu.cs.tweeter.model.service.response;

import edu.byu.cs.tweeter.model.domain.User;

public class CountResponse extends Response {

    private User user;
    private int followingCount;
    private int followersCount;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public CountResponse(String message) {
        super(false, message);
    }

    public CountResponse(User user, int followingCount, int followersCount) {
        super(true, null);
        this.user = user;
        this.followingCount = followingCount;
        this.followersCount = followersCount;
    }

    public User getUser() {
        return user;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }
}
