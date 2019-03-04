package com.example.team31_personalbest;

import java.util.HashMap;

public class friendsRelationship {
    // username -> Map<friendsName, friendsObject>
    HashMap<String, HashMap<String, User>> friends;

    public friendsRelationship() {
        this.friends = new HashMap<>();
    }

    /**
     * Add a user to the relationship
     * @param user User to be added to the relationship
     */
    public void addUser(User user) {
        this.friends.put(user.name, new HashMap<String, User>());
    }

    /**
     * Add a friend to existing user
     * @param user user to be add friend
     * @param friendObj friend to be added
     */
    public void addFriend(String user, User friendObj) {
        this.friends.get(user).put(user, friendObj);
    }
}
