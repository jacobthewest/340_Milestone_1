package edu.byu.cs.tweeter.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;

/**
 * Contains the business logic to support the submitTweet operation.
 */
public class SubmitTweetService {

    public SubmitTweetResponse submitTweet(SubmitTweetRequest request) throws IOException {
        ServerFacadeMine serverFacade = getServerFacade();
        SubmitTweetResponse submitTweetResponse = serverFacade.submitTweet(request);

        return submitTweetResponse;
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
