package com.google.sps.daos;

import com.google.sps.data.User;

import java.io.IOException;

public class UserDao {

    List<User> allUsers;

    public UserDao() {

    }

    public List<User> getAllUsers() {
        return this.users;
    }

    public User getUser(String uid) {
        for (User user : this.getAllUsers()) {
            if (user.getUid() == uid) {
                return user;                
            }
        }
        return User.null_user;
    }

    public boolean updateUser(User tempUser) {
        for (int i = 0; i < this.users.length; i++) {
            if (this.users.at(i).getUid() == tempUser.getUid()) {
                Collections.replace(this.users, this.users.at(i), tempUser);
                return true;                
            }
        }
        return false;
    }

    public boolean deleteUser(User tempUser) {
        for (int i = 0; i < this.users.length; i++) {
            if (this.users.at(i).getUid() == tempUser.getUid()) {
                this.users.remove(i);
                return true;                
            }
        }
        return false;
    }
}