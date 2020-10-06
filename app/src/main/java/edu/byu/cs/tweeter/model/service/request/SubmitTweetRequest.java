package edu.byu.cs.tweeter.model.service.request;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains all the information needed to make a submitTweet request.
 */
public class SubmitTweetRequest {

    private User user;
    private Status status;

    /**
     * Creates an instance.
     *
     * @param user the user who created the post.
     * @param status the status created by the post.
     */
    public SubmitTweetRequest(User user, Status status) {
        this.user = user;
        this.status = status;
    }

    /**
     * Returns the user in this request.
     *
     * @return the user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the status created by the user of this request.
     *
     * @return the status.
     */
    public Status getStatus() {
        return status;
    }
}
