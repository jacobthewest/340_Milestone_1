package edu.byu.cs.tweeter.presenter;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.StoryService;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;

public class StoryPresenter {

    private final StoryPresenter.View view;

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
    public StoryPresenter(StoryPresenter.View view) {
        this.view = view;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of statuses returned and to return the next set of
     * statuses after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @return the statuses.
     */
    public StoryResponse getStory(StoryRequest request) throws IOException {
        StoryService storyService = getStoryService();
        return storyService.getStory(request);
    }

    /**
     * Returns an instance of {@link StoryService}. Allows mocking of the StoryService class
     * for testing purposes. All usages of StoryService should get their StoryService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    StoryService getStoryService() {
        return new StoryService();
    }
}