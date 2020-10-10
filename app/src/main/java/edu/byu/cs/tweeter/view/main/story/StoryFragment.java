package edu.byu.cs.tweeter.view.main.story;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.StoryRequest;
import edu.byu.cs.tweeter.model.service.response.StoryResponse;
import edu.byu.cs.tweeter.presenter.StoryPresenter;
import edu.byu.cs.tweeter.view.util.AliasClickableSpan;
import edu.byu.cs.tweeter.view.util.DatePrinter;
import edu.byu.cs.tweeter.view.asyncTasks.GetStoryTask;
import edu.byu.cs.tweeter.view.util.ImageUtils;
import edu.byu.cs.tweeter.view.util.UrlClickableSpan;

/**
 * The fragment that displays on the 'Story' tab.
 */
public class StoryFragment extends Fragment implements StoryPresenter.View {

    private static final String LOG_TAG = "FollowingFragment";
    private static final String USER_KEY = "UserKey";
    private static final String FOLLOW_KEY = "FollowKey";
    private static final String AUTH_TOKEN_KEY = "AuthTokenKey";

    private static final int LOADING_DATA_VIEW = 0;
    private static final int ITEM_VIEW = 1;

    private static final int PAGE_SIZE = 10;

    private User user;
    private User followUser;
    private AuthToken authToken;
    private StoryPresenter presenter;

    private StoryFragment.StoryRecyclerViewAdapter storyRecyclerViewAdapter;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @param user the logged in user.
     * @param followUser the user in the view.
     * @param authToken the auth token for this user's session.
     * @return the fragment.
     */
    public static StoryFragment newInstance(User user, User followUser, AuthToken authToken) {
        StoryFragment fragment = new StoryFragment();

        Bundle args = new Bundle(2);
        args.putSerializable(USER_KEY, user);
        args.putSerializable(FOLLOW_KEY, followUser);
        args.putSerializable(AUTH_TOKEN_KEY, authToken);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        //noinspection ConstantConditions
        user = (User) getArguments().getSerializable(USER_KEY);
        followUser = (User) getArguments().getSerializable(FOLLOW_KEY);
        authToken = (AuthToken) getArguments().getSerializable(AUTH_TOKEN_KEY);

        presenter = new StoryPresenter(this);

        RecyclerView storyRecyclerView = view.findViewById(R.id.storyRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        storyRecyclerView.setLayoutManager(layoutManager);

        storyRecyclerViewAdapter = new StoryFragment.StoryRecyclerViewAdapter();
        storyRecyclerView.setAdapter(storyRecyclerViewAdapter);

        storyRecyclerView.addOnScrollListener(new StoryFragment.StoryRecyclerViewPaginationScrollListener(layoutManager));

        return view;
    }

    /**
     * The ViewHolder for the RecyclerView that displays the Following data.
     */
    private class StoryHolder extends RecyclerView.ViewHolder {

        private final ImageView userImage;
        private final TextView userAlias;
        private final TextView userName;
        private TextView postText;
        private TextView timePosted;
        private TextView mentions;


        /**
         * Creates an instance and sets an OnClickListener for the user's row.
         *
         * @param itemView the view on which the user will be displayed.
         */
        StoryHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userAlias = itemView.findViewById(R.id.userAlias);
            userName = itemView.findViewById(R.id.userName);
            postText = itemView.findViewById(R.id.postText);
            timePosted = itemView.findViewById(R.id.timePosted);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AliasClickableSpan(getActivity(), user, userAlias.getText().toString(), authToken).onClick(view);
                }
            });
        }

        /**
         * Binds the status's data to the view.
         *
         * @param status the status.
         */
        void bindStatus(Status status) {
            SpannableString tempPostText = formulatePostText(status); // TODO: Update this thing right here.
            String tempTimePosted = formulateTimePosted(status.getTimePosted());
            User statusUser = status.getUser();

            userImage.setImageDrawable(ImageUtils.drawableFromByteArray(followUser.getImageBytes()));
            userAlias.setText(statusUser.getAlias());
            userName.setText(statusUser.getName());
            postText.setText(tempPostText);
            postText.setMovementMethod(LinkMovementMethod.getInstance());
            timePosted.setText(tempTimePosted);
        }

        /**
         * Adds links to the mentions, and video/image URLs.
         * @param status
         * @return A string with clickable links
         */
        private SpannableString formulatePostText(Status status) {
            String tweetText = status.getTweetText();
            Spannable spannable = new SpannableString(tweetText);

            if(status.getUrls() != null) {
                for(String url : status.getUrls()) {
                    int startIndex = tweetText.indexOf(url);
                    int endIndex = startIndex + url.length();
                    spannable.setSpan(new UrlClickableSpan(url, getActivity()), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if(status.getMentions() != null) {
                for(String mention : status.getMentions()) {
                    int startIndex = tweetText.indexOf(mention);
                    int endIndex = startIndex + mention.length();
                    spannable.setSpan(new AliasClickableSpan(getActivity(), user, mention, authToken ), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            return (SpannableString) spannable;
        }

        /**
         * Makes the date into the correct string format
         * @param cal
         * @return A readable date as a string
         */
        private String formulateTimePosted(Calendar cal) {
            DatePrinter datePrinter = new DatePrinter(cal);
            return datePrinter.toString();
        }
    }

    /**
     * The adapter for the RecyclerView that displays the Following data.
     */
    private class StoryRecyclerViewAdapter extends RecyclerView.Adapter<StoryFragment.StoryHolder> implements GetStoryTask.Observer {

        private final List<Status> story = new ArrayList<>();

        private edu.byu.cs.tweeter.model.domain.Status lastStatus;

        private boolean hasMorePages;
        private boolean isLoading = false;

        /**
         * Creates an instance and loads the first page of following data.
         */
        StoryRecyclerViewAdapter() {
            loadMoreItems();
        }

        /**
         * Adds new statuses to the list from which the RecyclerView retrieves the statuses it displays
         * and notifies the RecyclerView that items have been added.
         *
         * @param newStatuses the statuses to add.
         */
        void addItems(List<Status> newStatuses) {
            int startInsertPosition = story.size();
            story.addAll(newStatuses);
            this.notifyItemRangeInserted(startInsertPosition, newStatuses.size());
        }

        /**
         * Adds a single status to the list from which the RecyclerView retrieves the statuses it
         * displays and notifies the RecyclerView that an item has been added.
         *
         * @param status the status to add.
         */
        void addItem(Status status) {
            story.add(status);
            this.notifyItemInserted(story.size() - 1);
        }

        /**
         * Removes a user from the list from which the RecyclerView retrieves the users it displays
         * and notifies the RecyclerView that an item has been removed.
         *
         * @param status the status to remove.
         */
        void removeItem(Status status) {
            int position = story.indexOf(status);
            story.remove(position);
            this.notifyItemRemoved(position);
        }

        /**
         *  Creates a view holder for a status to be displayed in the RecyclerView or for a message
         *  indicating that new rows are being loaded if we are waiting for rows to load.
         *
         * @param parent the parent view.
         * @param viewType the type of the view (ignored in the current implementation).
         * @return the view holder.
         */
        @NonNull
        @Override
        public StoryFragment.StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(StoryFragment.this.getContext());
            View view;

            if(viewType == LOADING_DATA_VIEW) {
                view = layoutInflater.inflate(R.layout.loading_row, parent, false);

            } else {
                view = layoutInflater.inflate(R.layout.status_row, parent, false);
            }

            return new StoryFragment.StoryHolder(view);
        }

        /**
         * Binds the status at the specified position unless we are currently loading new data. If
         * we are loading new data, the display at that position will be the data loading footer.
         *
         * @param storyHolder the ViewHolder to which the status should be bound.
         * @param position the position (in the list of statuses) that contains the status to be
         *                 bound.
         */
        @Override
        public void onBindViewHolder(@NonNull StoryHolder storyHolder, int position) {
            if(!isLoading) {
                storyHolder.bindStatus(story.get(position));
            }
        }

        /**
         * Returns the current number of statuses available for display.
         * @return the number of statuses available for display.
         */
        @Override
        public int getItemCount() {
            return story.size();
        }

        /**
         * Returns the type of the view that should be displayed for the item currently at the
         * specified position.
         *
         * @param position the position of the items whose view type is to be returned.
         * @return the view type.
         */
        @Override
        public int getItemViewType(int position) {
            return (position == story.size() - 1 && isLoading) ? LOADING_DATA_VIEW : ITEM_VIEW;
        }

        /**
         * Causes the Adapter to display a loading footer and make a request to get more following
         * data.
         */
        void loadMoreItems() {
            isLoading = true;
            addLoadingFooter();

            GetStoryTask getStoryTask = new GetStoryTask(presenter, this);
            StoryRequest request = new StoryRequest(followUser, PAGE_SIZE, lastStatus);
            getStoryTask.execute(request);
        }

        /**
         * A callback indicating more following data has been received. Loads the new statuses
         * and removes the loading footer.
         *
         * @param storyResponse the asynchronous response to the request to load more items.
         */
        @Override
        public void statusesRetrieved(StoryResponse storyResponse) {
            List<Status> statuses = storyResponse.getStory();

            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() -1) : null;
            hasMorePages = storyResponse.getHasMorePages();

            isLoading = false;
            removeLoadingFooter();
            storyRecyclerViewAdapter.addItems(statuses);
        }

        /**
         * A callback indicating that an exception was thrown by the presenter.
         *
         * @param exception the exception.
         */
        @Override
        public void handleException(Exception exception) {
            Log.e(LOG_TAG, exception.getMessage(), exception);
            removeLoadingFooter();
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * Adds a dummy user to the list of users so the RecyclerView will display a view (the
         * loading footer view) at the bottom of the list.
         */
        private void addLoadingFooter() {
            List<String> mentions = getMentions();
            Calendar timePosted = getTimePosted();
            String postUrl = "Statuses are loading";
            addItem(new Status(new User("Dummy", "User", "", "password"), postUrl, null, timePosted, mentions));
        }

        /**
         * Removes the dummy status from the list of statuses so the RecyclerView will stop displaying
         * the loading footer at the bottom of the list.
         */
        private void removeLoadingFooter() {
            removeItem(story.get(story.size() - 1));
        }

        /**
         * Generates a mention for the addLoadingFooter function
         * @return A list of mentions
         */
        private List<String> getMentions() {
            List<String> mentions = new ArrayList<>();
            mentions.add("@TestMention");
            mentions.add("@TheRealSlimShady");
            return mentions;
        }

        /**
         * Generates a Date for the addLoadingFooter function
         * @return A date object
         */
        private Calendar getTimePosted() {
            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.MONTH, 6);
            c1.set(Calendar.DATE, 11);
            c1.set(Calendar.YEAR, 2020);
            c1.set(Calendar.HOUR_OF_DAY, 10); // 24 hours
            return c1;
        }
    }

    /**
     * A scroll listener that detects when the user has scrolled to the bottom of the currently
     * available data.
     */
    private class StoryRecyclerViewPaginationScrollListener extends RecyclerView.OnScrollListener {

        private final LinearLayoutManager layoutManager;

        /**
         * Creates a new instance.
         *
         * @param layoutManager the layout manager being used by the RecyclerView.
         */
        StoryRecyclerViewPaginationScrollListener(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        /**
         * Determines whether the user has scrolled to the bottom of the currently available data
         * in the RecyclerView and asks the adapter to load more data if the last load request
         * indicated that there was more data to load.
         *
         * @param recyclerView the RecyclerView.
         * @param dx the amount of horizontal scroll.
         * @param dy the amount of vertical scroll.
         */
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!storyRecyclerViewAdapter.isLoading && storyRecyclerViewAdapter.hasMorePages) {
                if ((visibleItemCount + firstVisibleItemPosition) >=
                        totalItemCount && firstVisibleItemPosition >= 0) {
                    storyRecyclerViewAdapter.loadMoreItems();
                }
            }
        }
    }
}


