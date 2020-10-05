package edu.byu.cs.tweeter.view.main.make_tweet;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import edu.byu.cs.tweeter.R;

public class MakeTweet extends Dialog implements android.view.View.OnClickListener {
    public Activity activity;

    public MakeTweet() {
        super(null);
    }
    public MakeTweet(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.make_tweet);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }
}
