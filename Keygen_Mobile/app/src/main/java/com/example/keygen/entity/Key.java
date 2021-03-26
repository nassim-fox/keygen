package com.example.keygen.entity;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Key {


    private String key ;
    private Date create_date ;
    private Date expire_date ;
    private boolean used ;

    public Key() {
    }

    public Key(String key, Date create_date, Date expire_date, boolean used) {
        this.key = key;
        this.create_date = create_date;
        this.expire_date = expire_date;
        this.used = used;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public ArrayList<Key> convertJsonToKeys(JSONArray jsonArray) throws JSONException, ParseException {
        // pour gérer les dates
        DateFormat format = new SimpleDateFormat("dd/mm/yyyy") ;

        ArrayList<Key> keys = new ArrayList<>() ;

        for(int i = 0 ; i < jsonArray.length() ; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i) ;

            Key k = new Key() ;
            k.setKey(jsonObject.getString("key")) ;
            //convertir les dates
            k.setCreate_date(format.parse(jsonObject.getString("create_date"))) ;
            k.setExpire_date(format.parse(jsonObject.getString("expire_date"))) ;
            k.setUsed(jsonObject.getBoolean("used")) ;
            keys.add(k) ;

        }

        return keys ;
    }

    @NonNull
    @Override
    public String toString() {
        return " key : "+this.key+" create_date : "+this.create_date ;
    }
}
