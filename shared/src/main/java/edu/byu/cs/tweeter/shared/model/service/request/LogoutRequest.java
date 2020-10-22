package edu.byu.cs.tweeter.shared.model.service.request;

import edu.byu.cs.tweeter.shared.model.domain.AuthToken;
import edu.byu.cs.tweeter.shared.model.domain.User;

public class LogoutRequest {

    private final User user;
    private AuthToken authToken;

    /**
     * Creates an instance.
     *
     * @param user the username of the user to be logged out.
     */
    public LogoutRequest(User user, AuthToken authToken) {
        this.user = user;
        this.authToken = authToken;
    }

    /**
     * Returns the username of the user to be logged out by this request.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the authToken of the user to be logged out by this request.
     *
     * @return the authToken.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }
}
