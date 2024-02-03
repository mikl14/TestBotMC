package io.projectBot.TestBot.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MasterClass {
    public String name;
    private int max_users;

    private final int defaultMaxUsers = 10;

    public int getMax_users() {
        return max_users;
    }

    private List<String> users = new ArrayList<>();

    public MasterClass(String name, int max_users) {
        this.name = name;
        this.max_users = max_users;
    }

    public MasterClass(String name) {
        this.name = name;
        this.max_users = defaultMaxUsers;
    }
    public void setMax_users(int max_users) {
        this.max_users = max_users;
    }

    public void setUsers(String[] users) {
        this.users.addAll(List.of(users));
    }

    public void setUsers(JSONArray users){
        for(Object user:users)
        {
            this.users.add(user.toString());
        }
    }
    public int getFreeBooking()
    {
        return max_users-users.size();
    }

    public List<String> getUsers() {
        return users;
    }

    public int checkUser(String username)
    {
        if(!users.contains(username)){
            if(addUser(username)){
                 return 1;
            }
            else {
                return -1;
            }
        }
        else {
            deleteUser(username);
            return 0;
        }
    }

    public String userToString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("На Мастер класс: " + name + "\n записаны:");
        for(String name:users){
            sb.append('\n'+name);
        }
        return sb.toString();
    }
    private void deleteUser(String username)
    {
        users.remove(username);
    }

    private boolean addUser(String username)
    {
        if(users.size() <= max_users) {
            users.add(username);
            return true;
        }
        else {
            return false;
        }
    }
}
