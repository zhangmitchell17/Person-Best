package com.example.team31_personalbest_ms2v2;

import java.util.HashMap;

public class FriendsRelationship {
    // username -> <friendsName, friendsObject>
    HashMap<String, HashMap<String, User>> friends;

    public FriendsRelationship() {
        this.friends = new HashMap<>();
    }

    public static void main(String[] args) {

    }

}
