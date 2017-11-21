package ml.dilot.chysdmapp.UtilPack;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.Editeres.AddMember;
import ml.dilot.chysdmapp.R;

/**
 * Created by mlyg2 on 2017-11-07.
 */

public class Util {
    public static FirebaseUser firebaseUser;
    public static void TextEditDialog(Context context, String title, String message, final vvoidEvent andthen){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);

        final EditText editText = new EditText(context);
        ad.setView(editText);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", true);
                param.put("text", editText.getText().toString());
                andthen.vvoidEvent(param);
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Object> param = new HashMap<>();
                param.put("result", false);
                param.put("text", editText.getText().toString());
            }
        });
        ad.show();
    }

    public static void ConfirmCancleDialog(Context context, String title, String message, final ConfirmCancleDialog.ConfirmCancleListener callback) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(false);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onConfrim();
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onCancle();
            }
        });
        ad.show();
    }

    public static void confirmDialog(Context context, String title, String message, final vvoidEvent andthen){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setCancelable(false);
        ad.setTitle(title);
        ad.setMessage(message);

        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", true);
                andthen.vvoidEvent(param);
            }
        });
        ad.show();
    }

    public static void StartAddMemeber(final Context context, final String strCate, final String strSubcate){
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("회원명단/분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                ArrayList<String> subject = (ArrayList<String>)dataSnapshot.child("학과").getValue();
                ArrayList<String> cate = (ArrayList<String>)dataSnapshot.child("대분류").getValue();
                HashMap<String, ArrayList<String>> subcate = (HashMap<String,ArrayList<String>>)dataSnapshot.child("소분류").getValue();

                Intent intent = new Intent(context, AddMember.class);
                intent.putExtra("cate",cate);
                intent.putExtra("subject",subject);
                intent.putExtra("subcate",subcate);

                if(strCate != null && strSubcate == null){
                    intent.setAction("selectcate");
                    intent.putExtra("selectedcate",strCate);
                } else if(strCate != null && strSubcate != null){
                    intent.setAction("selectsubcate");
                    intent.putExtra("selectedcate",strCate);
                    intent.putExtra("selectedsubcate",strCate);
                }
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(context,"서버와 통신을 할 수 없습니다.",Toast.LENGTH_LONG).show();
            }
        });
    }
}
