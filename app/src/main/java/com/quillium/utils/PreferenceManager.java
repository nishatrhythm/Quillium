package com.quillium.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public void  putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


//    private String username;
//    private String email;
//    private int age;
//
//    // Constructor
//    public PreferenceManager(String username, String email, int age) {
//        this.username = username;
//        this.email = email;
//        this.age = age;
//    }
//
//    // Getters and setters
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    // Additional methods if needed
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", age=" + age +
//                '}';
//    }
}
