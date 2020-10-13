package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a user in the system.
 */
public class User implements Comparable<User>, Serializable {

    private final String firstName;
    private final String lastName;
    private final String alias;
    private final String imageUrl;
    private final String password;
    private byte [] imageBytes;

    public User(String firstName, String lastName, String imageURL, String password) {
        this(firstName, lastName, String.format("@%s%s", firstName, lastName), imageURL, password);
    }

    public User(String firstName, String lastName, String imageURL, byte[] imageBytes, String password) {
        this(firstName, lastName, String.format("@%s%s", firstName, lastName), imageURL, imageBytes, password);
    }

    public User(String firstName, String lastName, String alias, String imageURL, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.imageUrl = imageURL;
        this.password = password;
    }

    public User(String firstName, String lastName, String alias, String imageURL, byte[] imageBytes, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.alias = alias;
        this.imageUrl = imageURL;
        this.imageBytes = imageBytes;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getAlias() {
        return alias;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPassword() {
        return password;
    }

    public byte [] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public int compareTo(User user) {
        return this.getAlias().compareTo(user.getAlias());
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", alias='" + alias + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", password='" + password +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(alias, user.alias) &&
                Objects.equals(imageUrl, user.imageUrl) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(firstName, lastName, alias, imageUrl, password);
        result = 31 * result;
        return result;
    }
}
