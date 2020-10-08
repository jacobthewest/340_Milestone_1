package edu.byu.cs.tweeter.model.service.request;

/**
 * Contains all the information needed to make a RetrieveUser request.
 */
public class RetrieveUserRequest {

    private final String username;
    private final String password;

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be retrieved.
     * @param password the password of the user to be retrieved.
     */
    public RetrieveUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username of the user to be retrieved by this request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user to be retrieved by this request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }
}
