package edu.byu.cs.tweeter.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.SubmitTweetService;
import edu.byu.cs.tweeter.model.service.LoginService;
import edu.byu.cs.tweeter.model.service.SubmitTweetService;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;

/**
 * The presenter for the login functionality of the application.
 */
public class SubmitTweetPresenter {

    private final SubmitTweetPresenter.View view;

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
    public SubmitTweetPresenter(SubmitTweetPresenter.View view) {
        this.view = view;
    }

    /**
     * Makes a submitTweet request.
     *
     * @param submitTweetRequest the request.
     */
    public SubmitTweetResponse submitTweet(SubmitTweetRequest submitTweetRequest) throws IOException {
        SubmitTweetService submitTweetService = new SubmitTweetService();
        return submitTweetService.submitTweet(submitTweetRequest);
    }

    public SubmitTweetService getSubmitTweetService() {
        return new SubmitTweetService();
    }
}
