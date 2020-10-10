package edu.byu.cs.tweeter.view.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;
import edu.byu.cs.tweeter.presenter.CountPresenter;
import edu.byu.cs.tweeter.util.ByteArrayUtils;
import edu.byu.cs.tweeter.view.main.MainActivity;

public class CountTask extends AsyncTask<CountRequest, Void, CountResponse> {

    private final CountPresenter presenter;
    private final CountTask.Observer observer;
    private Exception exception;

    /**
     * An observer interface to be implemented by observers who want to be notified when this task
     * completes.
     */
    public interface Observer {
        void countSuccessful(CountResponse countResponse);
        void countUnsuccessful(CountResponse countResponse);
        void handleException(Exception ex);
    }
    
    public CountTask(CountPresenter presenter, CountTask.Observer observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }
    
    @Override
    protected CountResponse doInBackground(CountRequest... countRequests) {
        CountResponse countResponse = null;

        try {
            countResponse = presenter.getCount(countRequests[0]);
        } catch (IOException ex) {
            exception = ex;
        }

        return countResponse;
    }

    @Override
    protected void onPostExecute(CountResponse countResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else if(countResponse.isSuccess()) {
            observer.countSuccessful(countResponse);
        } else {
            observer.countUnsuccessful(countResponse);
        }
    }
}
