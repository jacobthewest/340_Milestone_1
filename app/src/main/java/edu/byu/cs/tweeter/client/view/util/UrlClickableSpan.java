package edu.byu.cs.tweeter.client.view.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.NonNull;

public class UrlClickableSpan extends ClickableSpan {

    String url;
    Activity activity;

    public UrlClickableSpan(String url, Activity activity) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        this.url = url;
        this.activity = activity;
    }

    @Override
    public void onClick(@NonNull View widget) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.url));
        activity.startActivity(browserIntent);
    }
}
