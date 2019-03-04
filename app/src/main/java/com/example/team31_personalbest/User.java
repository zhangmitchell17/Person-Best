package com.example.team31_personalbest;

public class User {
    public String name;
    public String email;

    /**
     * Constructor
     */
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /**
     * Constructor
     * @param username Name of the user
     * @param email email of the user
     */
    public User(String username, String email) {
        this.name = username;
        this.email = email;
    }
}
