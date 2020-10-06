package edu.byu.cs.tweeter.model.domain;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

/**
 * Represents a story in the system.
 */
public class Story {

    private final List<Status> story;

    public Story(@NotNull List<Status> story) {
        this.story = story;
    }

    public List<Status> getStory() {
        return story;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story that = (Story) o;
        return story.equals(that.story);
    }

    @Override
    public int hashCode() {
        return Objects.hash(story);
    }

    @NotNull
    @Override
    public String toString() {
        Writer out = new StringWriter();
        for(int i = 0; i < story.size(); i++) {
            try {
                out.write("Status text = " + story.get(i).getTweetText() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }
}
