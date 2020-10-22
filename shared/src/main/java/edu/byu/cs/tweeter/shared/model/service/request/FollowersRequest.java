package edu.byu.cs.tweeter.shared.model.service.request;

import edu.byu.cs.tweeter.shared.model.domain.User;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followers for a specified follower.
 */
public class FollowersRequest {

    private final User user;
    private final int limit;
    private final User lastFollower;

    /**
     * Creates an instance.
     *
     * @param user the {@link User} whose followers are to be returned.
     * @param limit the maximum number of followers to return.
     * @param lastFollower the last followers that was returned in the previous request (null if
     *                     there was no previous request or if no followers were returned in the
     *                     previous request).
     */
    public FollowersRequest(User user, int limit, User lastFollower) {
        this.user = user;
        this.limit = limit;
        this.lastFollower = lastFollower;
    }

    /**
     * Returns the follower whose followers are to be returned by this request.
     *
     * @return the follower.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the number representing the maximum number of followers to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Returns the last follower that was returned in the previous request or null if there was no
     * previous request or if no followers were returned in the previous request.
     *
     * @return the last follower.
     */
    public User getLastFollower() {
        return lastFollower;
    }
}
