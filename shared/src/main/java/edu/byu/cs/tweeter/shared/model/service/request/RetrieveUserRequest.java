package edu.byu.cs.tweeter.shared.model.service.request;

/**
 * Contains all the information needed to make a RetrieveUser request.
 */
public class RetrieveUserRequest {

    private final String username;

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be retrieved.
     */
    public RetrieveUserRequest(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the user to be retrieved by this request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }
}
