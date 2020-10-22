package edu.byu.cs.tweeter.client.view.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.shared.model.service.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.client.presenter.UpdateFollowPresenter;

public class UpdateFollowTask extends AsyncTask<UpdateFollowRequest, Void, UpdateFollowResponse> {

    private final UpdateFollowPresenter presenter;
    private final UpdateFollowTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void updateFollowSuccessful(UpdateFollowResponse updateFollowResponse);
        void updateFollowUnsuccessful(UpdateFollowResponse updateFollowResponse);
        void handleException(Exception ex);
    }

    public UpdateFollowTask(UpdateFollowPresenter presenter, UpdateFollowTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    @Override
    protected UpdateFollowResponse doInBackground(UpdateFollowRequest... updateFollowRequests) {
        UpdateFollowResponse updateFollowResponse = null;

        try {
            updateFollowResponse = presenter.getUpdateFollow(updateFollowRequests[0]);
        } catch (IOException ex) {
            exception = ex;
        }

        return updateFollowResponse;
    }

    @Override
    protected void onPostExecute(UpdateFollowResponse updateFollowResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else if(updateFollowResponse.isSuccess()) {
            observer.updateFollowSuccessful(updateFollowResponse);
        } else {
            observer.updateFollowUnsuccessful(updateFollowResponse);
        }
    }
}
