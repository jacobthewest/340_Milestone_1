package edu.byu.cs.tweeter.view.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;
import edu.byu.cs.tweeter.presenter.MainPresenter;
import edu.byu.cs.tweeter.view.HomeActivity;
import edu.byu.cs.tweeter.view.asyncTasks.LogoutTask;
import edu.byu.cs.tweeter.view.main.make_tweet.MakeTweet;
import edu.byu.cs.tweeter.view.util.ImageUtils;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements MainPresenter.View, LogoutTask.Observer {

    public static final String CURRENT_USER_KEY = "CurrentUser";
    public static final String AUTH_TOKEN_KEY = "AuthTokenKey";
    private static final String LOG_TAG = "MainActivity";
    private User user;
    private AuthToken authToken;
    private MainPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.user = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if(user == null) {
            throw new RuntimeException("User not passed to activity");
        }

        authToken = (AuthToken) getIntent().getSerializableExtra(AUTH_TOKEN_KEY);
        MainSectionsPagerAdapter mainSectionsPagerAdapter = new MainSectionsPagerAdapter(this, getSupportFragmentManager(), user, authToken);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(mainSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Button logoutButton = findViewById(R.id.logoutButton);
        FloatingActionButton fab = findViewById(R.id.fab);
        presenter = new MainPresenter(this);


        // We should use a Java 8 lambda function for the listener (and all other listeners), but
        // they would be unfamiliar to many students who use this code.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                MakeTweet dialog = new MakeTweet(MainActivity.this);
                dialog.show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutRequest logoutRequest = getLogoutRequest();
                LogoutTask logoutTask = new LogoutTask(presenter, getObserver()); // TODO: Make this work too
                logoutTask.execute(logoutRequest);
            }
        });

        TextView userName = findViewById(R.id.userName);
        userName.setText(user.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(user.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(user.getImageBytes()));

        TextView followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText("Following: " + "-42");

        TextView followerCount = findViewById(R.id.followerCount);
        followerCount.setText("Followers: " + "-42");
    }

    /**
     * Used to get the observer implementation object of this class
     * @return the class's observer object
     */
    private LogoutTask.Observer getObserver() {
        return this;
    }

    private MainPresenter.View getPresenterView() {
        return this;
    }

    /**
     * Used to generate a logoutRequest from our class's member variables
     * @return A new logoutRequest
     */
    private LogoutRequest getLogoutRequest() {
        return new LogoutRequest(user, authToken);
    }

    @Override
    public void logoutSuccessful(LogoutResponse logoutResponse) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Makes it so this Activity ends after we get the new HomeActivity going.
    }

    @Override
    public void logoutUnsuccessful(LogoutResponse logoutResponse) {
        Toast.makeText(this, "Failed to logout: " + logoutResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        Toast.makeText(this, "Failed to logout because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}