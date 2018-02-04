package com.example.developer.myapplication;

/**
 * Created by Developer on 2/3/18.
 */

public class Request {

    public int id;

    public String user_id;

    public String lat;

    public String lon;

    private String state;

    public Request(int id, String user_id, String lat , String lon, String state){


        this.id = id;
        this.user_id = user_id;
        this.lat = lat;
        this.lon = lon;
        this.state = state;
    }

}
