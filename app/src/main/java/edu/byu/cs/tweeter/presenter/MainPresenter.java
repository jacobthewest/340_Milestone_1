package edu.byu.cs.tweeter.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.LogoutService;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;

public class MainPresenter {

    private final MainPresenter.View view;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        // If needed, specify methods here that will be called on the view in response to model updates
    }

    /**
     * Creates an instance.
     *
     * @param view the view for which this class is the presenter.
     */
    public MainPresenter(View view) {
        this.view = view;
    }

    /**
     * Returns the result of the logout operation for the user.
     *
     * @param request contains the data required to fulfill the request.
     * @return the statuses.
     */
    public LogoutResponse logout(LogoutRequest request) throws IOException {
        LogoutService logoutService = getLogoutService();
        return logoutService.logout(request);
    }

    /**
     * Returns an instance of {@link LogoutService}. Allows mocking of the LogoutService class
     * for testing purposes. All usages of LogoutService should get their LogoutService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    LogoutService getLogoutService() {
        return new LogoutService();
    }
}
