package edu.byu.cs.tweeter.view.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MentionParser {
    private String tweetText;
    private String mentions;

    public MentionParser(String tweetText) {
        this.tweetText = tweetText;
        this.mentions = "";
    }

    public String parse() {
        // separate input by spaces ( URLs don't have spaces )
        String [] parts = tweetText.split("\\s+");

        // Check each item for being a mention
        for( String item : parts ) {
            if(hasAlias(item)) {
                String alias = extractAlias(item);
                if(mentions.equals("")) {
                    mentions += alias;
                } else {
                    mentions += " ";
                    mentions += alias;
                }
            }
        }
        return this.mentions;
    }

    private boolean hasAlias(String item) {
        if(item.contains("@")) {
            return true;
        }
        return false;
    }

    private String extractAlias(String item) {
        int atSymbolPos = item.indexOf("@");
        String alias = item.substring(atSymbolPos);
        return alias;
    }
}
