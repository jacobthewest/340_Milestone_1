package edu.byu.cs.tweeter.client.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.RegisterServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.RegisterRequest;
import edu.byu.cs.tweeter.shared.model.service.response.RegisterResponse;

public class RegisterPresenter {

    private final RegisterPresenter.View view;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        // If needed, specify methods here that will be called on the view in response to edu.byu.cs.tweeter.shared.model updates
    }

    /**
     * Creates an instance.
     *
     * @param view the view for which this class is the presenter.
     */
    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of statuses returned and to return the next set of
     * statuses after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @return the statuses.
     */
    public RegisterResponse getRegister(RegisterRequest request) throws IOException {
        RegisterServiceProxy registerServiceProxy = getRegisterService();
        return registerServiceProxy.getRegister(request);
    }

    /**
     * Returns an instance of {@link RegisterServiceProxy}. Allows mocking of the RegisterService class
     * for testing purposes. All usages of RegisterService should get their RegisterService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    RegisterServiceProxy getRegisterService() {
        return new RegisterServiceProxy();
    }
}
