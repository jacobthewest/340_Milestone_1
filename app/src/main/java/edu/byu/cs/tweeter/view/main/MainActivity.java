package edu.byu.cs.tweeter.view.main;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.CountRequest;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.request.LogoutRequest;
import edu.byu.cs.tweeter.model.service.response.CountResponse;
import edu.byu.cs.tweeter.model.service.response.LogoutResponse;
import edu.byu.cs.tweeter.model.service.response.UpdateFollowResponse;
import edu.byu.cs.tweeter.presenter.CountPresenter;
import edu.byu.cs.tweeter.presenter.MainPresenter;
import edu.byu.cs.tweeter.presenter.UpdateFollowPresenter;
import edu.byu.cs.tweeter.view.HomeActivity;
import edu.byu.cs.tweeter.view.asyncTasks.CountTask;
import edu.byu.cs.tweeter.view.asyncTasks.LogoutTask;
import edu.byu.cs.tweeter.view.asyncTasks.UpdateFollowTask;
import edu.byu.cs.tweeter.view.main.tweet.TweetFragment;
import edu.byu.cs.tweeter.view.util.ImageUtils;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements MainPresenter.View, LogoutTask.Observer, CountPresenter.View, CountTask.Observer,
        UpdateFollowPresenter.View, UpdateFollowTask.Observer {

    public static final String CURRENT_USER_KEY = "CurrentUser";
    public static final String CURRENT_FOLLOW_KEY = "FollowUser";
    public static final String FOLLOWING_KEY = "Following";
    public static final String AUTH_TOKEN_KEY = "AuthTokenKey";
    private static final String LOG_TAG = "MainActivity";
    private User user;
    private User followUser;
    private List<User> following;
    private AuthToken authToken;
    private MainPresenter mainPresenter;
    private CountPresenter countPresenter;
    private UpdateFollowPresenter updateFollowPresenter;
    private FragmentTransaction fragmentTransaction;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.user = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        this.followUser = (User) getIntent().getSerializableExtra(CURRENT_FOLLOW_KEY);
        this.following = (List<User>) getIntent().getSerializableExtra(FOLLOWING_KEY);

        if(user == null) {
            throw new RuntimeException("User not passed to activity");
        }

        if(followUser == null) {
            throw new RuntimeException("Follow user not passed to activity");
        }

        authToken = (AuthToken) getIntent().getSerializableExtra(AUTH_TOKEN_KEY);
        MainSectionsPagerAdapter mainSectionsPagerAdapter = new MainSectionsPagerAdapter(this, getSupportFragmentManager(), user, followUser, authToken);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(mainSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Button logoutButton = findViewById(R.id.logoutButton);
        FloatingActionButton fab = findViewById(R.id.fab);
        mainPresenter = new MainPresenter(this);
        countPresenter = new CountPresenter(this);
        updateFollowPresenter = new UpdateFollowPresenter(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutRequest logoutRequest = getLogoutRequest();
                LogoutTask logoutTask = new LogoutTask(getMainPresenter(), getLogoutObserver());
                logoutTask.execute(logoutRequest);
            }
        });

        TextView userName = findViewById(R.id.userName);
        userName.setText(followUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(followUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(followUser.getImageBytes()));

        setCount();
    }

    public MainPresenter getMainPresenter() {
        return this.mainPresenter;
    }

    public CountPresenter getCountPresenter() {
        return this.countPresenter;
    }

    public UpdateFollowPresenter getUpdateFollowPresenter() {
        return this.updateFollowPresenter;
    }

    public void setCount() {
        CountRequest countRequest = new CountRequest(this.followUser);
        CountTask countTask = new CountTask(getCountPresenter(), getCountObserver());
        countTask.execute(countRequest);
    }

    private void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        this.fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog.
        dialogFragment = new TweetFragment(user);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    private LogoutTask.Observer getLogoutObserver() {
        return this;
    }

    private CountTask.Observer getCountObserver() {
        return this;
    }

    private UpdateFollowTask.Observer getUpdateFollowObserver() {
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
    public void countSuccessful(CountResponse countResponse) {
        int followingCount = countResponse.getFollowingCount();
        int followersCount = countResponse.getFollowersCount();

        TextView ingCount = findViewById(R.id.followeeCount);
        TextView erCount = findViewById(R.id.followerCount);
        ingCount.setText("Following: " + Integer.toString(followingCount));
        erCount.setText("Followers: " + Integer.toString(followersCount));
    }

    @Override
    public void countUnsuccessful(CountResponse countResponse) {
        Toast.makeText(this, "Failed to get the followers and following count: " + countResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateFollowSuccessful(UpdateFollowResponse updateFollowResponse) {

    }

    @Override
    public void updateFollowUnsuccessful(UpdateFollowResponse updateFollowResponse) {
        Toast.makeText(this, "Failed to follow/unfollow the user: " + updateFollowResponse.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleException(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        Toast.makeText(this, "Failed to logout because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}