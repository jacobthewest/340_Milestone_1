package edu.byu.cs.tweeter.model.service.request;

import edu.byu.cs.tweeter.model.domain.User;

public class CountRequest {

    private final User user;

    public CountRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
