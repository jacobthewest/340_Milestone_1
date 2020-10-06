package edu.byu.cs.tweeter.model.domain;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Status in the system
 */
public class Status implements Comparable<Status>, Serializable {

    private final User user;
    private final String tweetText;
    private final List<String> urls;
    private final Calendar timePosted;
    private final List<String> mentions;
    private byte [] imageBytes;


    public Status(@NotNull User user, String postText, List<String> urls, Calendar timePosted, List<String> mentions) {
        this.user = user;
        this.tweetText = postText;
        this.urls = urls;
        this.timePosted = timePosted;
        this.mentions = mentions;
    }

    public User getUser() {
        return user;
    }

    public String getTweetText() {
        return tweetText;
    }

    public List<String> getUrls() {
        return urls;
    }

    public Calendar getTimePosted() {
        return timePosted;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public byte [] getImageBytes() {
        return imageBytes;
    }


    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return user.getAlias().equals(status.user.getAlias()) &&
                tweetText.equals(status.getTweetText()) &&
                urls.equals(status.getUrls()) &&
                timePosted.equals(status.getTimePosted()) &&
                mentions.equals(status.getMentions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, tweetText, urls, timePosted, mentions);
    }

    @Override
    public String toString() {
        return "Status{" +
                "firstName='" + user.getFirstName() + '\'' +
                ", lastName='" + user.getLastName() + '\'' +
                ", alias='" + user.getAlias() + '\'' +
                ", postText='" + tweetText + '\'' +
                ", urls='" + urls.toString() + '\'' +
                ", timePosted='" + timePosted.toString() + '\'' +
                ", mentions='" + mentions.toString() + '\'' +
                '}';
    }

    @Override
    public int compareTo(Status status) {
        return this.toString().compareTo(status.toString());
    }
}
