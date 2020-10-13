package edu.byu.cs.tweeter.model.service.response;

import android.database.DatabaseUtils;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * A response for a {@link edu.byu.cs.tweeter.model.service.request.LogoutRequest}.
 */
public class LogoutResponse extends Response {

    private User user;
    private AuthToken authToken;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public LogoutResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the now logged out user.
     * @param authToken the now expired auth token.
     */
    public LogoutResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
    }

    /**
     * Returns the logged out user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the expired auth token.
     *
     * @return the authToken.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogoutResponse that = (LogoutResponse) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, authToken);
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "user=" + user +
                ", authToken=" + authToken +
                '}';
    }
}
