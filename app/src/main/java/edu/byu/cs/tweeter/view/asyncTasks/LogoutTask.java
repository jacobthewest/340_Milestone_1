package edu.byu.cs.tweeter.view.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;
import edu.byu.cs.tweeter.presenter.LogoutPresenter;

public class LogoutTask extends AsyncTask<LogoutRequest, Void, LogoutResponse> {

    private final LogoutPresenter presenter;
    private final LogoutTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void logoutSuccessful(LogoutResponse logoutResponse);
        void logoutUnsuccessful(LogoutResponse logoutResponse);
        void handleException(Exception ex);
    }

    /**
     * Creates an instance.
     *
     * @param presenter the presenter this task should use to logout.
     * @param observer the observer who wants to be notified when this task completes.
     */
    public LogoutTask(LogoutPresenter presenter, LogoutTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    /**
     * The method that is invoked on a background thread to log the user in. This method is
     * invoked indirectly by calling {@link #execute(LogoutRequest...)}.
     *
     * @param logoutRequests the request object (there will only be one).
     * @return the response.
     */
    @Override
    protected LogoutResponse doInBackground(LogoutRequest... logoutRequests) {
        LogoutResponse logoutResponse = null;

        try {
            logoutResponse = presenter.logout(logoutRequests[0]);
        } catch (IOException ex) {
            exception = ex;
        }

        return logoutResponse;
    }

    /**
     * Notifies the observer (on the thread of the invoker of the
     * {@link #execute(LogoutRequest...)} method) when the task completes.
     *
     * @param logoutResponse the response that was received by the task.
     */
    @Override
    protected void onPostExecute(LogoutResponse logoutResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else if(logoutResponse.isSuccess()) {
            observer.logoutSuccessful(logoutResponse);
        } else {
            observer.logoutUnsuccessful(logoutResponse);
        }
    }
}
