package edu.byu.cs.tweeter.view.main.tweet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.SubmitTweetRequest;
import edu.byu.cs.tweeter.model.service.response.SubmitTweetResponse;
import edu.byu.cs.tweeter.presenter.SubmitTweetPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.SubmitTweetTask;
import edu.byu.cs.tweeter.view.util.DatePrinter;
import edu.byu.cs.tweeter.view.util.MentionParser;
import edu.byu.cs.tweeter.view.util.UrlParser;

public class TweetFragment extends DialogFragment implements View.OnClickListener, SubmitTweetPresenter.View, SubmitTweetTask.Observer {
    private static final String LOG_TAG = "RegisterFragment";

    private SubmitTweetPresenter presenter;

    private User user;
    private Button cancel;
    private Button post;
    private EditText editText;
    private String[] urls;
    private String[] mentions;

    public TweetFragment(User user) {this.user = user;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.make_tweet, container, false);
        presenter = new SubmitTweetPresenter(this);
        editText = (EditText)v.findViewById(R.id.tweetText);
        cancel = (Button)v.findViewById(R.id.cancelTweetButton);
        post = (Button)v.findViewById(R.id.postTweetButton);

        cancel.setOnClickListener(this);
        post.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(cancel)) {
            cancelClickHelper();
        } else {
            postClickHelper();
        }
    }

    public void cancelClickHelper() {
        getFragmentManager().popBackStack();
    }

    public void postClickHelper() {
        if(hasErrors()) {
            Toast.makeText(getActivity(), "You can't submit an empty tweet.", Toast.LENGTH_LONG).show();
        } else {
            SubmitTweetRequest submitTweetRequest = getSubmitTweetRequest();
            SubmitTweetTask submitTweetTask = new SubmitTweetTask(presenter, getObserver());
            submitTweetTask.execute(submitTweetRequest);
        }
    }

    /**
     * This checks the tweet text to make sure that it is error free.
     * @return boolean if the tweet is error free.
     */
    private boolean hasErrors() {
        String tweetText = editText.getText().toString();
        if(tweetText.length() == 0) {
            return true;
        }
        return false;
    }

    private SubmitTweetRequest getSubmitTweetRequest() {
        String tweetText = editText.getText().toString();

        UrlParser urlParser = new UrlParser(tweetText);
        MentionParser mentionParser = new MentionParser(tweetText);

        String currentTime = getCurrentTime();
        String mentions = mentionParser.parse();
        String urls = urlParser.parse();

        Status status = new Status(user, tweetText, urls, currentTime, mentions);
        SubmitTweetRequest submitTweetRequest = new SubmitTweetRequest(user, status);
        return submitTweetRequest;
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

    /**
     * Get's the current time.
     * @return a Calendar object of the current time.
     */
    private String getCurrentTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    /**
     * Helper function to get this class's observer
     * @return This class's observer.
     */
    private SubmitTweetTask.Observer getObserver() {
        return this;
    }

    @Override
    public void submitTweetSuccessful(SubmitTweetResponse submitTweetResponse) {
        Toast.makeText(getActivity(), "Tweet published!", Toast.LENGTH_LONG).show();
        cancelClickHelper();
    }

    @Override
    public void submitTweetUnsuccessful(SubmitTweetResponse submitTweetResponse) {
        Toast.makeText(getActivity(), "Failed to save the tweet. " + submitTweetResponse.getMessage(), Toast.LENGTH_LONG).show();
        cancelClickHelper();
    }

    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        Toast.makeText(getActivity(), "Failed to register because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}
