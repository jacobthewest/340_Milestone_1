package edu.byu.cs.tweeter.view.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.view.main.story.StoryFragment;
import edu.byu.cs.tweeter.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.view.main.following.FollowingFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages
 * of the Main Activity.
 */
class MainSectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] FOUR_TAB_TITLES = new int[]{R.string.feedTabTitle, R.string.storyTabTitle, R.string.followingTabTitle, R.string.followersTabTitle};
    private static final int[] THREE_TAB_TITLES = new int[]{R.string.storyTabTitle, R.string.followingTabTitle, R.string.followersTabTitle};
    private final Context mContext;
    private final User user;
    private final User followUser;
    private final AuthToken authToken;
    private boolean displayFourTabs;

    public MainSectionsPagerAdapter(Context context, FragmentManager fm, User user, User followUser, AuthToken authToken) {
        super(fm);
        mContext = context;
        this.user = user;
        this.followUser = followUser;
        this.authToken = authToken;

        if(user.equals(followUser)) {
            displayFourTabs = true;
        } else {displayFourTabs = false;}
    }

    @Override
    public Fragment getItem(int position) {
        if (displayFourTabs) {
            if (position == 0) {
                return FeedFragment.newInstance(user, followUser, authToken);
            } else if (position == 1) {
                return StoryFragment.newInstance(user, followUser, authToken);
            } else if (position == 2) {
                return FollowingFragment.newInstance(user, followUser, authToken);
            } else if (position == 3) {
                return FollowersFragment.newInstance(user, followUser, authToken);
            } else {
                return MainPlaceholderFragment.newInstance(position + 1);
            }
        } else {
            if (position == 0) {
                return StoryFragment.newInstance(user, followUser, authToken);
            } else if (position == 1) {
                return FollowingFragment.newInstance(user, followUser, authToken);
            } else if (position == 2) {
                return FollowersFragment.newInstance(user, followUser, authToken);
            } else {
                return MainPlaceholderFragment.newInstance(position + 1);
            }
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(this.displayFourTabs) {
            return mContext.getResources().getString(FOUR_TAB_TITLES[position]);
        }
        return mContext.getResources().getString(THREE_TAB_TITLES[position]);

    }

    @Override
    public int getCount() {
        if(this.displayFourTabs) {
            return 4; // Show 4 total pages
        }
        return 3; // Show 3 total pages
    }
}