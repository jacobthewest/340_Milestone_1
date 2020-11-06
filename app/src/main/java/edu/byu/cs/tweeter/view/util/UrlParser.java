package edu.byu.cs.tweeter.view.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlParser {
    private String tweetText;
    private String urls;
    private String words;

    public UrlParser(String tweetText) {
        this.tweetText = tweetText;
        this.urls = "";
        this.words = "";
    }

    public String parse() {
        // separate input by spaces ( URLs don't have spaces )
        String [] parts = tweetText.split("\\s+");

        // Attempt to convert each item into an URL.
        for( String item : parts ) {
            try {
                URL url = new URL(item);
                if(this.urls.equals("")) {
                    this.urls += url.toString();
                } else {
                    this.urls += " ";
                    this.urls += url.toString();
                }
            } catch (MalformedURLException e) {
                // We will come here for each time the word chunk in
                // tweet text isn't a url.
                if(this.words.equals("")) {
                    this.words += item;
                } else {
                    this.words += " ";
                    this.words += item;
                }
            }
        }
        return this.urls;
    }

}
