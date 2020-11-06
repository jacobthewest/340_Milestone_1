package edu.byu.cs.tweeter.model.service;

import edu.byu.cs.tweeter.model.net.ServerFacadeMine;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;

/**
 * Contains the business logic to support the logout operation.
 */
public class LogoutService {

    public LogoutResponse logout(LogoutRequest request) {
        ServerFacadeMine serverFacade = getServerFacade();
        LogoutResponse logoutResponse = serverFacade.logout(request);

        return logoutResponse;
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
