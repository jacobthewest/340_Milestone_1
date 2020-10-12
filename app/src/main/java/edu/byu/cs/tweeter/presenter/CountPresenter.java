package edu.byu.cs.tweeter.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.CountService;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;

public class CountPresenter {

    private final CountPresenter.View view;

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
    public CountPresenter(CountPresenter.View view) {
        this.view = view;
    }

    /**
     * Makes a count request.
     *
     * @param countRequest the request.
     */
    public CountResponse getCount(CountRequest countRequest) throws IOException {
        CountService countService = new CountService();
        return countService.getCount(countRequest);
    }
}
