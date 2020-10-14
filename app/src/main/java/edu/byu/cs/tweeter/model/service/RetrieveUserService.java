package edu.byu.cs.tweeter.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.model.service.response.RetrieveUserResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

/**
 * Contains the business logic to support the retrieveUser operation.
 */
public class RetrieveUserService {

    public RetrieveUserResponse retrieveUser(RetrieveUserRequest request) throws IOException {
        ServerFacade serverFacade = getServerFacade();
        RetrieveUserResponse retrieveUserResponse = serverFacade.retrieveUser(request);

        if(retrieveUserResponse.isSuccess()) {
            loadImage(retrieveUserResponse.getUser());
        }

        return retrieveUserResponse;
    }

    /**
     * Loads the profile image data for the user.
     *
     * @param user the user whose profile image data is to be loaded.
     */
    private void loadImage(User user) throws IOException {
        byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
        user.setImageBytes(bytes);
    }

    /**
     * Returns an instance of {@link ServerFacade}. Allows mocking of the ServerFacade class for
     * testing purposes. All usages of ServerFacade should get their ServerFacade instance from this
     * method to allow for proper mocking.
     *
     * @return the instance.
     */
    public ServerFacade getServerFacade() {
        return new ServerFacade();
    }
}
