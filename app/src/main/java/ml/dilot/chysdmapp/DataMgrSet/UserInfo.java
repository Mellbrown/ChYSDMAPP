package ml.dilot.chysdmapp.DataMgrSet;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mlyg2 on 2017-11-05.
 */

public class UserInfo implements Serializable, Comparable<UserInfo>, Comparator<UserInfo>{
    private String uid;
    public String category;
    public String subCategory;
    public String major;
    public String year;

    public String name;
    public String homeNumber;
    public String phoneNumber;

    public String group;
    public String position;

    public String zip;
    public String memo;

    public UserInfo(){}
    public UserInfo(HashMap <String,String> hashMap,String uid){
        this.uid = uid;
        category = hashMap.get("category");
        subCategory = hashMap.get("subCategory");
        major = hashMap.get("major");
        year = hashMap.get("year");
        name = hashMap.get("name");
        homeNumber = hashMap.get("homeNumber");
        phoneNumber = hashMap.get("phoneNumber");
        group = hashMap.get("group");
        position = hashMap.get("position");
        zip = hashMap.get("zip");
        memo = hashMap.get("memo");
    }

    public String getUid(){return uid;}

    @Override
    public int compareTo(@NonNull UserInfo userInfo) {
        return this.name.compareTo(userInfo.name);
    }

    @Override
    public int compare(UserInfo userInfo, UserInfo t1) {
        return userInfo.compareTo(t1);
    }

    public static HashMap<String,UserInfo> HashMapUserInfoCaster(HashMap<String, HashMap<String,String>> origin){
        HashMap<String,UserInfo> casted = new HashMap<>();
        if(origin == null)origin = new HashMap<>();
        for(String uid : origin.keySet()){
            UserInfo userInfo = new UserInfo(origin.get(uid),uid);
            casted.put(uid,userInfo);
        }
        return casted;
    }
    public static ArrayList<UserInfo> ArrayListUserinfoCaster(HashMap<String, HashMap<String,String>> origin){
        ArrayList<UserInfo> casted = new ArrayList<>();
        if(origin == null)origin = new HashMap<>();
        for (String uid : origin.keySet()){
            UserInfo userInfo = new UserInfo(origin.get(uid),uid);
            casted.add(userInfo);
        }
        return casted;
    }
}
