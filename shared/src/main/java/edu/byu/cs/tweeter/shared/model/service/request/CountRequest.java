package edu.byu.cs.tweeter.shared.model.service.request;

import edu.byu.cs.tweeter.shared.model.domain.User;

public class CountRequest {

    private final User user;

    public CountRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
