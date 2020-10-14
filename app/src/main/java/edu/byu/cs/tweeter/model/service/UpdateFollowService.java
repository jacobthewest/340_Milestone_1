package edu.byu.cs.tweeter.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;

public class UpdateFollowService {

    public UpdateFollowResponse updateFollow(UpdateFollowRequest request) throws IOException {
        ServerFacade serverFacade = getServerFacade();
        UpdateFollowResponse updateFollowResponse = serverFacade.updateFollow(request);

        return updateFollowResponse;
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
