package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.shared.model.service.request.CountRequest;
import edu.byu.cs.tweeter.shared.model.service.response.CountResponse;

public class CountServiceProxy {

    public CountResponse getCount(CountRequest request) {
        ServerFacade serverFacade = getServerFacade();
        CountResponse countResponse = serverFacade.getCount(request);

        return countResponse;
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
