package edu.byu.cs.tweeter.model.service.request;

/**
 * Contains all the information needed to make a register request.
 */
public class RegisterRequest {

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String imageUrl;
    private final byte[] imageBytes;

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be registered.
     * @param password the password of the user to be registered.
     * @param firstName the password of the user to be registered.
     * @param lastName the password of the user to be registered.
     */
    public RegisterRequest(String username, String password, String firstName, String lastName, String imageUrl, byte[] imageBytes) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.imageBytes = imageBytes;
    }

    /**
     * Returns the username of the user to be registered by this request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user to be registered by this request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the firstName of the user to be registered by this request.
     *
     * @return the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the lastName of the user to be registered by this request.
     *
     * @return the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the imageUrl of the user to be registered by this request.
     *
     * @return the imageUrl.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the imageBytes of the user to be registered by this request.
     *
     * @return the imageUrl.
     */
    public byte[] getImageBytes() {
        return imageBytes;
    }
}

