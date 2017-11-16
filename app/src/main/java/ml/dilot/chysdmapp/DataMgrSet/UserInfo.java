package ml.dilot.chysdmapp.DataMgrSet;

import java.util.HashMap;

/**
 * Created by mlyg2 on 2017-11-05.
 */

public class UserInfo {
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
    public UserInfo(HashMap <String,String> hashMap){
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
}
