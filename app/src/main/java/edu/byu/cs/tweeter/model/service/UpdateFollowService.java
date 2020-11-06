package edu.byu.cs.tweeter.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;

public class UpdateFollowService {

    public UpdateFollowResponse updateFollow(UpdateFollowRequest request) throws IOException {
        ServerFacadeMine serverFacade = getServerFacade();
        UpdateFollowResponse updateFollowResponse = serverFacade.updateFollow(request);

        return updateFollowResponse;
    }

    /**
     * Returns an instance of {@link ServerFacadeMine}. Allows mocking of the ServerFacade class for
     * testing purposes. All usages of ServerFacade should get their ServerFacade instance from this
     * method to allow for proper mocking.
     *
     * @return the instance.
     */
    public ServerFacadeMine getServerFacade() {
        return new ServerFacadeMine();
    }
}
