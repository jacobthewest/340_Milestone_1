package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {
    private final String id;
    private final String username;
    private boolean isActive;

    public AuthToken(String username) {
        this.id = UUID.randomUUID().toString(); // Generates a random string.
        this.username = username;
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean IsActive() {
        return isActive;
    }

    public void deactivate() {
        isActive = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return isActive == authToken.isActive &&
                Objects.equals(username, authToken.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, isActive);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
