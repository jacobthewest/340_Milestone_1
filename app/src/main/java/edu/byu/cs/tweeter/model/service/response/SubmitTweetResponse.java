package edu.byu.cs.tweeter.model.service.response;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;


/**
 * A response for a {@link edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest}.
 */
public class SubmitTweetResponse extends Response {
    private User user;
    private Status status;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public SubmitTweetResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the user who created the status.
     * @param status the status created by the tweet
     */
    public SubmitTweetResponse(User user, Status status) {
        super(true, null);
        this.user = user;
        this.status = status;
    }

    /**
     * Returns the logged in user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the status.
     *
     * @return the status.
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmitTweetResponse that = (SubmitTweetResponse) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, status);
    }

    @Override
    public String toString() {
        return "SubmitTweetResponse{" +
                "user=" + user +
                ", status=" + status +
                '}';
    }
}
