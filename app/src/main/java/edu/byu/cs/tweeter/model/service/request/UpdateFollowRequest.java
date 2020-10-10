package edu.byu.cs.tweeter.model.service.request;

import edu.byu.cs.tweeter.model.domain.User;

public class UpdateFollowRequest {

    private final User user;
    private final User followUser;
    private final boolean followTheFollowUser;

    public UpdateFollowRequest(User user, User followUser, boolean followTheFollowUser) {
        this.user = user;
        this.followUser = followUser;
        this.followTheFollowUser = followTheFollowUser;
    }

    public User getUser() {
        return user;
    }
    public User getFollowUser() {
        return followUser;
    }
    public boolean followTheFollowUser() { return followTheFollowUser; }
}
