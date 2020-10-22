package edu.byu.cs.tweeter.client.view.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlParser {
    private String tweetText;
    private List<String> urls;
    private List<String> words;

    public UrlParser(String tweetText) {
        this.tweetText = tweetText;
        this.urls = new ArrayList<>();
        this.words = new ArrayList<>();
    }

    public List<String> parse() {
        // separate input by spaces ( URLs don't have spaces )
        String [] parts = tweetText.split("\\s+");

        // Attempt to convert each item into an URL.
        for( String item : parts ) {
            try {
                URL url = new URL(item);
                urls.add(url.toString());
            } catch (MalformedURLException e) {
                // We will come here for each time the word chunk in
                // tweet text isn't a url.
                words.add(item);
            }
        }
        return this.urls;
    }

}
