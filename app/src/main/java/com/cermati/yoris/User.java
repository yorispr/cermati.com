package com.cermati.yoris;

/**
 * Created by YORIS on 5/29/17.
 */

public class User {
    private String login;
    private String id;
    private String avatar_url;
    private String gravatar_id;
    private String url;

    public User(){}

    public User(String login, String id, String avatar_url, String gravatar_id, String url){
        this.setLogin(login);
        this.setId(id);
        this.setAvatar_url(avatar_url);
        this.setGravatar_id(gravatar_id);
        this.setUrl(url);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getGravatar_id() {
        return gravatar_id;
    }

    public void setGravatar_id(String gravatar_id) {
        this.gravatar_id = gravatar_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
