package edu.byu.cs.tweeter.view.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.request.FollowersRequest;
import edu.byu.cs.tweeter.model.service.response.FollowersResponse;
import edu.byu.cs.tweeter.presenter.FollowersPresenter;

/**
 * An {@link AsyncTask} for retrieving followers for a user.
 */
public class GetFollowersTask extends AsyncTask<FollowersRequest, Void, FollowersResponse> {

    private final FollowersPresenter presenter;
    private final GetFollowersTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void followersRetrieved(FollowersResponse followersResponse);
        void handleException(Exception exception);
    }

    /**
     * Creates an instance.
     *
     * @param presenter the presenter from whom this task should retrieve followers.
     * @param observer the observer who wants to be notified when this task completes.
     */
    public GetFollowersTask(FollowersPresenter presenter, GetFollowersTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    /**
     * The method that is invoked on the background thread to retrieve followers. This method is
     * invoked indirectly by calling {@link #execute(FollowersRequest...)}.
     *
     * @param followersRequests the request object (there will only be one).
     * @return the response.
     */
    @Override
    protected FollowersResponse doInBackground(FollowersRequest... followersRequests) {

        FollowersResponse response = null;

        try {
            response = presenter.getFollowers(followersRequests[0]);
        } catch (IOException ex) {
            exception = ex;
        }

        return response;
    }

    /**
     * Notifies the observer (on the UI thread) when the task completes.
     *
     * @param followersResponse the response that was received by the task.
     */
    @Override
    protected void onPostExecute(FollowersResponse followersResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else {
            observer.followersRetrieved(followersResponse);
        }
    }
}
