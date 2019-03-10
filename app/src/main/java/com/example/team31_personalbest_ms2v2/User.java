package com.example.team31_personalbest_ms2v2;

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

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

}
