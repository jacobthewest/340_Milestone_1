package edu.byu.cs.tweeter.client.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.CountServiceProxy;
import edu.byu.cs.tweeter.shared.model.service.request.CountRequest;
import edu.byu.cs.tweeter.shared.model.service.response.CountResponse;

public class CountPresenter {

    private final CountPresenter.View view;

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
    public CountPresenter(CountPresenter.View view) {
        this.view = view;
    }

    /**
     * Makes a count request.
     *
     * @param countRequest the request.
     */
    public CountResponse getCount(CountRequest countRequest) throws IOException {
        CountServiceProxy countServiceProxy = new CountServiceProxy();
        return countServiceProxy.getCount(countRequest);
    }

    public CountServiceProxy getCountService() {
        return new CountServiceProxy();
    }
}
