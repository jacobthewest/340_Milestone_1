package edu.byu.cs.tweeter.model.service.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * A response for a {@link edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest}.
 */
public class RetrieveUserResponse extends Response {

    private User user;
    private AuthToken authToken;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public RetrieveUserResponse(String message) {
        super(false, message);
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     *
     * @param user the now retrieved user.
     * @param authToken the auth token representing this user's session with the server.
     */
    public RetrieveUserResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
    }

    /**
     * Returns the retrieved user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the auth token.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }
}
