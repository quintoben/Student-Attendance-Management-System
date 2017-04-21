package com.example.quintoben.sams;

import android.app.Application;

import java.net.CookieStore;

/**
 * Created by quintoben on 2016/2/24.
 */
public class SamsApplication extends Application {
    private CookieStore cookieStore;
    public CookieStore getCookieStore(){
        return cookieStore;
    }
    public void setCookieStore( CookieStore cookieStore){
        this.cookieStore = cookieStore;
    }
}
