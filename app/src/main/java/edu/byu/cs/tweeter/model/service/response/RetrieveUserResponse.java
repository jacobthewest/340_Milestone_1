package edu.byu.cs.tweeter.model.service.response;

import edu.byu.cs.tweeter.model.domain.User;

/**
 * A response for a {@link edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest}.
 */
public class RetrieveUserResponse extends Response {

    private User user;

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
     */
    public RetrieveUserResponse(User user) {
        super(true, null);
        this.user = user;
    }

    /**
     * Returns the retrieved user.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }
}
