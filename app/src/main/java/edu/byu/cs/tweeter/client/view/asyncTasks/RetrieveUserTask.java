package edu.byu.cs.tweeter.client.view.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.shared.model.domain.User;
import edu.byu.cs.tweeter.shared.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.shared.model.service.response.RetrieveUserResponse;
import edu.byu.cs.tweeter.client.presenter.RetrieveUserPresenter;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;

public class RetrieveUserTask extends AsyncTask<RetrieveUserRequest, Void, RetrieveUserResponse> {

    private final RetrieveUserPresenter presenter;
    private final RetrieveUserTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void retrieveUserSuccessful(RetrieveUserResponse retrieveUserResponse);
        void retrieveUserUnsuccessful(RetrieveUserResponse retrieveUserResponse);
        void handleException(Exception ex);
    }

    /**
     * Creates an instance.
     *
     * @param presenter the presenter this task should use to retrieveUser.
     * @param observer the observer who wants to be notified when this task completes.
     */
    public RetrieveUserTask(RetrieveUserPresenter presenter, RetrieveUserTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    /**
     * The method that is invoked on a background thread to retrieve the user. This method is
     * invoked indirectly by calling {@link #execute(RetrieveUserRequest...)}.
     *
     * @param retrieveUserRequests the request object (there will only be one).
     * @return the response.
     */
    @Override
    protected RetrieveUserResponse doInBackground(RetrieveUserRequest... retrieveUserRequests) {
        RetrieveUserResponse retrieveUserResponse = null;

        try {
            retrieveUserResponse = presenter.retrieveUser(retrieveUserRequests[0]);

            if(retrieveUserResponse.isSuccess()) {
                loadImage(retrieveUserResponse.getUser());
            }
        } catch (IOException ex) {
            exception = ex;
        }

        return retrieveUserResponse;
    }

    /**
     * Loads the profile image for the user.
     *
     * @param user the user whose profile image is to be loaded.
     */
    private void loadImage(User user) {
        try {
            byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
            user.setImageBytes(bytes);
        } catch (IOException e) {
            Log.e(this.getClass().getName(), e.toString(), e);
        }
    }

    /**
     * Notifies the observer (on the thread of the invoker of the
     * {@link #execute(RetrieveUserRequest...)} method) when the task completes.
     *
     * @param retrieveUserResponse the response that was received by the task.
     */
    @Override
    protected void onPostExecute(RetrieveUserResponse retrieveUserResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else if(retrieveUserResponse.isSuccess()) {
            observer.retrieveUserSuccessful(retrieveUserResponse);
        } else {
            observer.retrieveUserUnsuccessful(retrieveUserResponse);
        }
    }
}
