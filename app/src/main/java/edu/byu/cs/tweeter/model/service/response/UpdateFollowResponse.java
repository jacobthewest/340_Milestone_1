package edu.byu.cs.tweeter.model.service.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public class UpdateFollowResponse extends Response {

    private User user;
    private User followUser;
    private List<User> following;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public UpdateFollowResponse(String message) {
        super(false, message);
    }

    public UpdateFollowResponse(User user, User followUser, List<User> following) {
        super(true, null);
        this.user = user;
        this.followUser = followUser;
        this.following = following;
    }

    public User getUser() {
        return this.user;
    }

    public User getFollowUser() {
        return this.followUser;
    }

    public List<User> getFollowing() {
        return this.following;
    }
}
