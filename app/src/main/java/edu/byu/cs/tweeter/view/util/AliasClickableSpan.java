package edu.byu.cs.tweeter.view.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.RetrieveUserRequest;
import edu.byu.cs.tweeter.model.service.response.RetrieveUserResponse;
import edu.byu.cs.tweeter.presenter.RetrieveUserPresenter;
import edu.byu.cs.tweeter.view.asyncTasks.RetrieveUserTask;
import edu.byu.cs.tweeter.view.main.MainActivity;

public class AliasClickableSpan extends ClickableSpan implements RetrieveUserPresenter.View, RetrieveUserTask.Observer {
    private static final String LOG_TAG = "RegisterFragment";

    private Activity activity;
    private User user;
    private User followUser;
    private String mention;
    private AuthToken authToken;
    private RetrieveUserPresenter presenter;
    private Toast toast;

    public AliasClickableSpan(Activity activity, User user, String mention, AuthToken authToken) {
        this.activity = activity;
        this.user = user;
        this.mention = mention;
        this.authToken = authToken;
        presenter = new RetrieveUserPresenter(this);
    }

    private RetrieveUserTask.Observer getObserver() {
        return this;
    }

    @Override
    public void onClick(@NonNull View widget) {
        RetrieveUserRequest retrieveUserRequest = new RetrieveUserRequest(mention);
        RetrieveUserTask retrieveUserTask = new RetrieveUserTask(presenter, getObserver());
        retrieveUserTask.execute(retrieveUserRequest);
    }

    @Override
    public void retrieveUserSuccessful(RetrieveUserResponse retrieveUserResponse) {
        Intent intent = new Intent(this.activity, MainActivity.class);

        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        intent.putExtra(MainActivity.CURRENT_FOLLOW_KEY, retrieveUserResponse.getUser());
        intent.putExtra(MainActivity.AUTH_TOKEN_KEY, this.authToken);

        this.activity.startActivity(intent);
    }

    @Override
    public void retrieveUserUnsuccessful(RetrieveUserResponse retrieveUserResponse) {
        Toast.makeText(this.activity, "Failed to retrieve clicked user. " + retrieveUserResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        toast.setText("Failed to retrieve clicked user because of exception: " + ex.getMessage());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
