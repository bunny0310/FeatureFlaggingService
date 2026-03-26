package com.ishaan.featureflagservice.data.repository;

import java.util.HashMap;
import java.util.Map;

import com.ishaan.featureflagservice.data.model.User;

public class UserRepository {
    
    private Map<String, User> users = new HashMap<String, User>();

    public UserRepository() {
        users.put("1", new User("1", "ikhurana"));
        users.put("2", new User("2", "rriel"));
        users.put("3", new User("3", "hli"));
        users.put("4", new User("4", "vturnier"));
        users.put("5", new User("5", "gbhat"));
        users.put("6", new User("6", "dfu"));
    }

    public User getUser(String userId) {
        return this.users.get(userId);
    }

}
