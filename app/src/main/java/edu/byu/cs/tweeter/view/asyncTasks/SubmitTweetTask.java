package edu.byu.cs.tweeter.view.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;
import edu.byu.cs.tweeter.presenter.SubmitTweetPresenter;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

public class SubmitTweetTask extends AsyncTask<SubmitTweetRequest, Void, SubmitTweetResponse> {

    private final SubmitTweetPresenter presenter;
    private final SubmitTweetTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void submitTweetSuccessful(SubmitTweetResponse submitTweetResponse);
        void submitTweetUnsuccessful(SubmitTweetResponse submitTweetResponse);
        void handleException(Exception ex);
    }

    /**
     * Creates an instance.
     *
     * @param presenter the presenter this task should use to submitTweet.
     * @param observer the observer who wants to be notified when this task completes.
     */
    public SubmitTweetTask(SubmitTweetPresenter presenter, SubmitTweetTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    /**
     * The method that is invoked on a background thread to save the tweet to the database. This method is
     * invoked indirectly by calling {@link #execute(SubmitTweetRequest...)}.
     *
     * @param submitTweetRequests the request object (there will only be one).
     * @return the response.
     */
    @Override
    protected SubmitTweetResponse doInBackground(SubmitTweetRequest... submitTweetRequests) {
        SubmitTweetResponse submitTweetResponse = null;

        try {
            submitTweetResponse = presenter.submitTweet(submitTweetRequests[0]);
        } catch (IOException ex) {
            exception = ex;
        }

        return submitTweetResponse;
    }

    /**
     * Notifies the observer (on the thread of the invoker of the
     * {@link #execute(SubmitTweetRequest...)} method) when the task completes.
     *
     * @param submitTweetResponse the response that was received by the task.
     */
    @Override
    protected void onPostExecute(SubmitTweetResponse submitTweetResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else if(submitTweetResponse.isSuccess()) {
            observer.submitTweetSuccessful(submitTweetResponse);
        } else {
            observer.submitTweetUnsuccessful(submitTweetResponse);
        }
    }
}
