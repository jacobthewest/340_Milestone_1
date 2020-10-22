package edu.byu.cs.tweeter.client.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.SubmitTweetServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.shared.model.service.response.SubmitTweetResponse;

/**
 * The presenter for the login functionality of the application.
 */
public class SubmitTweetPresenter {

    private final SubmitTweetPresenter.View view;

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
    public SubmitTweetPresenter(SubmitTweetPresenter.View view) {
        this.view = view;
    }

    /**
     * Makes a submitTweet request.
     *
     * @param submitTweetRequest the request.
     */
    public SubmitTweetResponse submitTweet(SubmitTweetRequest submitTweetRequest) throws IOException {
        SubmitTweetServiceProxy submitTweetServiceProxy = new SubmitTweetServiceProxy();
        return submitTweetServiceProxy.submitTweet(submitTweetRequest);
    }

    public SubmitTweetServiceProxy getSubmitTweetService() {
        return new SubmitTweetServiceProxy();
    }
}
