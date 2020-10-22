package edu.byu.cs.tweeter.shared.model.service.response;

import java.util.Objects;

import edu.byu.cs.tweeter.shared.model.domain.User;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountResponse that = (CountResponse) o;
        return followingCount == that.followingCount &&
                followersCount == that.followersCount &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, followingCount, followersCount);
    }
}
