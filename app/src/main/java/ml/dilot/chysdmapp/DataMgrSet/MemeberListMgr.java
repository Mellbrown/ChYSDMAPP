package ml.dilot.chysdmapp.DataMgrSet;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mlyg2 on 2017-11-05.
 */

public class MemeberListMgr {
    public static void GetCategory(final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("대분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> cate = (List<String>)dataSnapshot.getValue();
                if(cate == null) cate = new ArrayList<>();
                param.put("result",true);
                param.put("cate",cate);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String,Object> param = new HashMap<>();
                param.put("result",false);
                param.put("message","데이터를 받아오는데 오류가 발생하였습니다.");
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void AddCategory(final String title, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();

                List<String> cate = (List<String>)dataSnapshot.child("대분류").getValue();
                if(cate == null) cate = new ArrayList<>();
                if(cate.contains(title)){ //이미 존재하는 타이틀
                    param.put("result", false);
                    param.put("message", "'" + title + "'는 이미 존재하는 분류입니다." );
                } else { // 존재하지 않는 타이틀
                    param.put("result", true);
                    cate.add(title);
                    FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("대분류").setValue(cate);
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void GetSubCategory(final String at, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();

                List<String> cate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(cate == null) cate = new ArrayList<>();
                if(!cate.contains(at)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류");
                } else {
                    List<String> subCate = (List<String>)dataSnapshot.child("소분류").child(at).getValue();
                    if(subCate == null) subCate = new ArrayList<>();
                    param.put("result",true);
                    param.put("subCate",subCate);
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void AddSubCategory(final String title,final String at, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<String,Object>();

                List<String> cate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(cate == null) cate = new ArrayList<>();
                if(!cate.contains(at)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류");
                } else {
                    List<String> subCate = (List<String>)dataSnapshot.child("소분류").child(at).getValue();
                    if(subCate == null) subCate = new ArrayList<>();
                    if (subCate.contains(title)) {
                        param.put("result", false);
                        param.put("message", "이미 존재하는 타이틀");
                    } else {
                        param.put("result", true);
                        subCate.add(title);
                        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(at).setValue(subCate);
                    }
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<String,Object>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
   }
    public static void ReordeCategory(final List<String> cate, final vvoidEvent andthen) {
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                List<String> orgCate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(orgCate == null) orgCate = new ArrayList<>();
                boolean check = true;
                for(String t : cate) if(!orgCate.contains(t)) {check = false; break;}
                if(check){
                    param.put("result", true);
                    FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("대분류").setValue(cate);
                }else {
                    param.put("result", false);
                    param.put("message", "목록 갱신이 필요합니다.");
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void ReordeSubCategory(final List<String> subCate, final String at, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<String,Object>();

                List<String> cate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(cate == null) cate = new ArrayList<>();
                if(!cate.contains(at)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류");
                } else {
                    List<String> orgSubCate = (List<String>) dataSnapshot.child("소분류").child(at).getValue();
                    if(orgSubCate == null) orgSubCate = new ArrayList<>();
                    boolean check = true;
                    for(String t : subCate) if(!orgSubCate.contains(t)) {check = false; break;}
                    if(check){
                        param.put("result", true);
                        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(at).setValue(subCate);
                    } else {
                        param.put("result", false);
                        param.put("message", "목록 갱신이 필요합니다.");
                    }
                    param.put("result",true);
                    param.put("subCate",subCate);
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<String,Object>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void MoveToMember(final List<String> uids, final String cateAt, final String subCateAt, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                List<String> lstCate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                if(!lstCate.contains(cateAt)){
                    param.put("result", false);
                    param.put("message", "대상 대분류가 존재하지 않습니다.");
                    andthen.vvoidEvent(param);
                    return;
                } else {
                    List<String> lstSubcate = (List<String>) dataSnapshot.child("소분류").child(cateAt).getValue();
                    if(lstSubcate == null) lstSubcate = new ArrayList<>();
                    if(!lstSubcate.contains(subCateAt)){
                        param.put("result",false);
                        param.put("message", "대상 소분류가 존재하지 않습니다.");
                        andthen.vvoidEvent(param);
                        return;
                    }
                }

                Map<String,UserInfo> memberDates = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(memberDates == null) memberDates = new HashMap<>();
                List<String> notExistUides = new ArrayList<>();
                DatabaseReference userinfoesref = FirebaseDatabase.getInstance().getReference().child("회원데이터");
                for(String uid : uids){
                    UserInfo userInfo = memberDates.get(uid);
                    if(userInfo != null){
                        userInfo.category = cateAt;
                        userInfo.subCategory = subCateAt;
                        userinfoesref.child(uid).setValue(userInfo);
                    } else notExistUides.add(uid);
                }

                param.put("result", true);
                param.put("total", uids.size());
                param.put("success", uids.size() - notExistUides.size());
                param.put("fail", notExistUides.size());
                param.put("failes", notExistUides);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<String,Object>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void DeactivateMember(final List<String> uids, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> param = new HashMap<>();
                Map<String,UserInfo> memberDates = (Map<String, UserInfo>) dataSnapshot.getValue();
                if(memberDates == null) memberDates = new HashMap<>();
                List<String> notExistUides = new ArrayList<>();
                DatabaseReference userinfoesref = FirebaseDatabase.getInstance().getReference().child("회원데이터");
                for(String uid : uids){
                    UserInfo userInfo = memberDates.get(uid);
                    if(userInfo != null){
                        userInfo.category = null;
                        userInfo.subCategory = null;
                        userinfoesref.child(uid).setValue(userInfo);
                    } else notExistUides.add(uid);
                }

                param.put("result", true);
                param.put("total", uids.size());
                param.put("success", uids.size() - notExistUides.size());
                param.put("fail", notExistUides.size());
                param.put("failes", notExistUides);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<String,Object>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void IsExistCategory(final String cate, final String subCate, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();

                List<String> lstCate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                if(!lstCate.contains(cate)){
                    param.put("result", true);
                    param.put("isExist", false);
                    param.put("message","존재하지 않는 대분류");
                } else {
                    List<String> lstSubCate = (List<String>) dataSnapshot.child("소분류").child(cate).getValue();
                    if(lstSubCate == null) lstSubCate = new ArrayList<>();
                    if(!lstSubCate.contains(subCate)){
                        param.put("result", true);
                        param.put("isExist", false);
                        param.put("message","존재하지 않는 소분류");
                    } else {
                        param.put("result", true);
                        param.put("isExist", true);
                    }
                }
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<String,Object>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void RenameCategory(final String oldCate, final String newCate, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> lstCate = (List<String>) dataSnapshot.child("분류").child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                if(!lstCate.contains(oldCate)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                if(lstCate.contains(newCate)){
                    param.put("result", false);
                    param.put("message","이미 존재하는 대분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                lstCate.set(lstCate.indexOf(oldCate),newCate);
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("대분류").setValue(lstCate);
                List<String> backupSubCate = (List<String>) dataSnapshot.child("분류").child("소분류").child(oldCate).getValue();
                if(backupSubCate == null) backupSubCate = new ArrayList<>();
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(oldCate).removeValue();
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(newCate).setValue(backupSubCate);
                Map<String,UserInfo> backupUserInfoes = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(backupUserInfoes == null) backupUserInfoes = new HashMap<>();
                int changed = 0;
                for(String uid : backupUserInfoes.keySet()){
                    UserInfo user = backupUserInfoes.get(uid);
                    if(user.category.equals(oldCate)){
                        user.category = newCate;
                        FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child(uid).setValue(user);
                        changed++;
                    }
                }
                param.put("result", true);
                param.put("changed", changed);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void RenameSubCategory(final String oldSubCate, final String newSubCate, final String at, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> lstCate = (List<String>) dataSnapshot.child("분류").child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                if(!lstCate.contains(at)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                List<String> lstSubCate = (List<String>) dataSnapshot.child("분류").child("소분류").child(at).getValue();
                if(lstSubCate == null) lstSubCate = new ArrayList<>();
                if(!lstSubCate.contains(oldSubCate)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 소분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                if(lstSubCate.contains(newSubCate)){
                    param.put("result", false);
                    param.put("message","이미 존재하는 소분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                lstSubCate.set(lstCate.indexOf(oldSubCate),newSubCate);
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(at).setValue(lstSubCate);
                Map<String,UserInfo> backupUserInfoes = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(backupUserInfoes == null) backupUserInfoes = new HashMap<>();
                int changed = 0;
                for(String uid : backupUserInfoes.keySet()){
                    UserInfo user = backupUserInfoes.get(uid);
                    if(user.subCategory.equals(oldSubCate)){
                        user.subCategory = newSubCate;
                        FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child(uid).setValue(user);
                        changed++;
                    }
                }
                param.put("result", true);
                param.put("changed", changed);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void DeleteCategory(final String cates[], final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> lstCate = (List<String>) dataSnapshot.child("분류").child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                Map<String,UserInfo> backupUserInfoes = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(backupUserInfoes == null) backupUserInfoes = new HashMap<>();
                List<String> successList = new ArrayList<>();
                Map<String,String> failList = new HashMap<>();
                for(String cate : cates){
                    if(!lstCate.contains(cate)){
                        failList.put(cate, "존재하지 않는 대분류입니다.");
                        continue;
                    }
                    boolean serach = false;
                    for(String uid : backupUserInfoes.keySet()) if(backupUserInfoes.get(uid).category.equals(cate)){ serach = true; break;}
                    if(serach){
                        failList.put(cate, "해당 분류에 남은 인원이 있습니다.");
                        continue;
                    }
                    lstCate.remove(cate);
                    FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(cate).removeValue();
                    successList.add(cate);
                }
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("대분류").setValue(lstCate);
                Map<String,Object> params = new HashMap<>();
                params.put("result", true);
                params.put("cnt_success", successList.size());
                params.put("lst_success", successList);
                params.put("cnt_fail",failList.size());
                params.put("lst_fail",failList);
                andthen.vvoidEvent(params);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void DeleteSubCategory(final String cate, final String subCates[], final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> lstCate = (List<String>) dataSnapshot.child("분류").child("대분류").getValue();
                if(lstCate == null) lstCate = new ArrayList<>();
                if(!lstCate.contains(cate)){
                    param.put("result", false);
                    param.put("message","존재하지 않는 대분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }

                List<String> lstSubCate = (List<String>) dataSnapshot.child("분류").child("소분류").child(cate).getValue();
                if(lstSubCate == null) lstSubCate = new ArrayList<>();
                Map<String,UserInfo> backupUserInfoes = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(backupUserInfoes == null) backupUserInfoes = new HashMap<>();
                List<String> successList = new ArrayList<>();
                Map<String,String> failList = new HashMap<>();
                for(String subCate : subCates){
                    if(!lstSubCate.contains(subCate)){
                        failList.put(subCate,"존재하지 않는 소분류입니다.");
                        continue;
                    }
                    boolean serach = false;
                    for(String uid : backupUserInfoes.keySet()) {
                        UserInfo user = backupUserInfoes.get(uid);
                        if (user.category.equals(cate) && user.subCategory.equals(subCate)) {
                            serach = true;
                            break;
                        }
                    }
                    if(serach){
                        failList.put(subCate,"해당 분류에 남은 인원이 있습니다.");
                        continue;
                    }

                    lstSubCate.remove(subCate);
                    successList.add(subCate);
                }
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("소분류").child(cate).setValue(lstSubCate);
                param.put("result", true);
                param.put("cnt_success", successList.size());
                param.put("lst_success", successList);
                param.put("cnt_fail",failList.size());
                param.put("lst_fail",failList);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void AddMember(final UserInfo newUser, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                List<String> snapCate = (List<String>) dataSnapshot.child("대분류").getValue();
                if(!snapCate.contains(newUser.category)){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 대분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                List<String> snapSubCate = (List<String>) dataSnapshot.child("소분류").child(newUser.category).getValue();
                if(!snapSubCate.contains(newUser.subCategory)){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 소분류입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                List<String> snapMajor = (List<String>) dataSnapshot.child("학과").getValue();
                if(!snapMajor.contains(newUser.major)){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 학과입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").push();
                newRef.setValue(newUser);
                param.put("result",true);
                param.put("uid",newRef.getKey());
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void RemoveMember(final String uid, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                Map<String,UserInfo> snapUseres = (Map<String, UserInfo>) dataSnapshot.getValue();
                if(snapUseres == null) snapUseres = new HashMap<>();
                UserInfo user = snapUseres.get(uid);
                if(user == null){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 사용자입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                if(user.category != null || user.subCategory != null){
                    param.put("result", false);
                    param.put("message", "비활성화가 먼저 선행되어야합니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child(uid).removeValue();
                param.put("result",true);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void UpdateMember(final String uid, final UserInfo newUserInfo, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                Map<String,UserInfo> snapUseres = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(snapUseres == null) snapUseres = new HashMap<>();
                UserInfo user = snapUseres.get(uid);
                if(user == null){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 사용자입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                List<String> snapMajor = (List<String>) dataSnapshot.child("분류").child("학과").getValue();
                if(snapMajor == null) snapMajor = new ArrayList<>();
                if(!snapMajor.contains(newUserInfo.major)){
                    param.put("result", false);
                    param.put("message", "존재하지 않는 학과입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                newUserInfo.category = user.category;
                newUserInfo.subCategory = user.subCategory;
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child(uid).setValue(newUserInfo);
                param.put("result",true);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void GetMember(final String[] uides, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                Map<String,UserInfo> snapUseres = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(snapUseres == null) snapUseres = new HashMap<>();
                if(uides == null){
                    param.put("result", true);
                    param.put("uesres", snapUseres);
                    andthen.vvoidEvent(param);
                    return;
                }
                Map<String,UserInfo> found = new HashMap<>();
                List<String> notfound = new ArrayList<>();
                for(String uid : uides){
                    UserInfo user = snapUseres.get(uid);
                    if(user == null) notfound.add(uid);
                     else found.put(uid,user);
                }
                param.put("result",true);
                param.put("list",found);
                param.put("NotFound",notfound);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", false);
                param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
                andthen.vvoidEvent(param);
            }
        });
    }
    public static void GetMajor(final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("학과").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                List<String> snapMajor = (List<String>) dataSnapshot.getValue();
                if(snapMajor == null) snapMajor = new ArrayList<>();
                Collections.sort(snapMajor);
                param.put("result", true);
                param.put("snapMajor", snapMajor);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ReturnError(andthen);
            }
        });
    }
    public static void AddMajor(final String major, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("학과").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> snapMajor = (List<String>) dataSnapshot.getValue();
                if(snapMajor == null) snapMajor = new ArrayList<>();
                if(snapMajor.contains(major)){
                   param.put("result",false);
                   param.put("message", "이미 해당 과목이 존재합니다.");
                   andthen.vvoidEvent(param);
                   return;
                }
                snapMajor.add(major);
                Collections.sort(snapMajor);
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("학과").setValue(snapMajor);
                param.put("result",true);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ReturnError(andthen);
            }
        });
    }

    public static void RemoveMajor(final String majores[], final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Object> param = new HashMap<>();
                List<String> snapMajor = (List<String>) dataSnapshot.child("분류").child("학과").getValue();
                Map<String, UserInfo> snapMemberInfo = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(snapMemberInfo == null) snapMemberInfo = new HashMap<>();
                if(snapMajor == null) snapMajor = new ArrayList<>();
                List<String> successList = new ArrayList<>();
                Map<String,String> failList = new HashMap<>();
                for(String major : majores){
                    if(!snapMajor.contains(major)){
                        failList.put(major,"해당 과목이 존재하지 않습니다.");
                        continue;
                    }
                    boolean find = false;
                    for(String key : snapMemberInfo.keySet())
                        if(snapMemberInfo.get(key).major == major){
                            find = true;
                            break;
                        }
                    if(find){
                        failList.put(major,"해당 과목을 가진 사용자가 존재합니다.");;
                        continue;
                    }
                    snapMajor.remove(major);
                    successList.add(major);
                }
                Collections.sort(snapMajor);
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("분류").child("학과").setValue(snapMajor);
                param.put("result", true);
                param.put("cnt_success", successList.size());
                param.put("lst_success", successList);
                param.put("cnt_fail",failList.size());
                param.put("lst_fail",failList);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ReturnError(andthen);
            }
        });
    }
    public static void RenameMajor(final String oldMajor, final String newMajor, final vvoidEvent andthen){
        FirebaseDatabase.getInstance().getReference().child("회원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> param = new HashMap<>();
                List<String> snapMajor = (List<String>) dataSnapshot.child("분류").child("학과").getValue();
                if(snapMajor == null) snapMajor = new ArrayList<>();
                if(!snapMajor.contains(oldMajor)){
                    param.put("result",false);
                    param.put("message", "해당 과목이 존재하지 않습니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                if(snapMajor.contains(newMajor)){
                    param.put("result",false);
                    param.put("message", "이미 존재하는 과목입니다.");
                    andthen.vvoidEvent(param);
                    return;
                }
                Map<String, UserInfo> snapUserInfo = (Map<String, UserInfo>) dataSnapshot.child("명단").getValue();
                if(snapUserInfo == null) snapUserInfo = new HashMap<>();
                int changed = 0;
                for(String key : snapUserInfo.keySet())
                    if(snapUserInfo.get(key).major.equals(oldMajor)){
                        snapUserInfo.get(key).major = newMajor;
                        changed++;
                        FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child(key).setValue(snapUserInfo.get(key).major );
                    }
                snapMajor.set(snapMajor.indexOf(oldMajor),newMajor);
                FirebaseDatabase.getInstance().getReference().child("회원명단").child("명단").child("학과").setValue(snapMajor);
                param.put("result", true);
                param.put("changed", changed);
                andthen.vvoidEvent(param);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ReturnError(andthen);
            }
        });
    }
    private static void ReturnError(final vvoidEvent andthen){
        Map<String, Object> param = new HashMap<>();
        param.put("result", false);
        param.put("message", "데이터를 받아오는데 오류가 발생하였습니다." );
        andthen.vvoidEvent(param);
    }
}
