package edu.byu.cs.tweeter.model.service;

import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;

public class CountService {

    public CountResponse getCount(CountRequest request) {
        ServerFacadeMine serverFacade = getServerFacade();
        CountResponse countResponse = serverFacade.getCount(request);

        return countResponse;
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
