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
    private final String postText;
    private final String imageUrl;
    private final String videoUrl;
    private final Calendar timePosted;
    private final List<String> mentions;
    private byte [] imageBytes;


    public Status(@NotNull User user, String postText, String imageUrl, String videoUrl, Calendar timePosted, List<String> mentions) {
        this.user = user;
        this.postText = postText;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.timePosted = timePosted;
        this.mentions = mentions;
    }

    public User getUser() {
        return user;
    }

    public String getPostText() {
        return postText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
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
                postText.equals(status.getPostText()) &&
                imageUrl.equals(status.getImageUrl()) &&
                videoUrl.equals(status.getVideoUrl()) &&
                timePosted.equals(status.getTimePosted()) &&
                mentions.equals(status.getMentions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, postText, imageUrl, videoUrl, timePosted, mentions);
    }

    @Override
    public String toString() {
        return "Status{" +
                "firstName='" + user.getFirstName() + '\'' +
                ", lastName='" + user.getLastName() + '\'' +
                ", alias='" + user.getAlias() + '\'' +
                ", postText='" + postText + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", timePosted='" + timePosted.toString() + '\'' +
                ", mentions='" + mentions.toString() + '\'' +
                '}';
    }

    @Override
    public int compareTo(Status status) {
        return this.toString().compareTo(status.toString());
    }
}
