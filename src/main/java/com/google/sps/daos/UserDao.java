package com.google.sps.daos;

import com.google.sps.data.User;

import java.io.IOException;

public class UserDao {

    // A list of all users
    List<User> users;

    public UserDao() {

    }

    /**
     * Returns all users
     * @return  all users
     */
    public List<User> getAllUsers() {
        return this.users;
    }

    /**
     * Returns a user by it's user id
     * @param   uid  any user id
     * @return  the user as class
     */
    public User getUser(String uid) {
        for (User user : this.getAllUsers()) {
            if (user.getUid() == uid) {
                return user;                
            }
        }
        return User.null_user;
    }

    /**
     * Update the user with new data
     * @param   tempUser    a user class
     * @return  If the event was succesfull 
     */
    public boolean updateUser(User tempUser) {
        for (int i = 0; i < this.users.length; i++) {
            if (this.users.at(i).getUid() == tempUser.getUid()) {
                Collections.replace(this.users, this.users.at(i), tempUser);
                return true;                
            }
        }
        return false;
    }

    /**
     * Delete the user that is receved
     * @param   tempUser    a user class
     * @return  If the event was succesfull 
     */
    public boolean deleteUser(User tempUser) {
        for (int i = 0; i < this.users.length; i++) {
            if (this.users.at(i).getUid() == tempUser.getUid()) {
                this.users.remove(i);
                return true;                
            }
        }
        return false;
    }

    /**
     * Delete the user that is receved
     * @param   user    a user class
     * @return  If the event was succesfull 
     */
    public void addUser(User user) {
        this.users.add(user);
    }
}