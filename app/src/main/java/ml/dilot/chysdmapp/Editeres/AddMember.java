package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.UserInfo;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.Util;

public class AddMember extends AppCompatActivity {

    CircleImageView imgProfile;
    EditText editName;
    EditText editYear;
    Spinner dropSubject;
    EditText editphone;
    EditText editZipNum;
    Spinner dropCate;
    Spinner dropSubCate;
    EditText editGroup;
    EditText editPosition;

    ArrayList<String> subject;
    ArrayList<String> cate;
    HashMap<String, ArrayList<String>> subcate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Bundle bundle = getIntent().getExtras();
        subject = (ArrayList<String>) bundle.get("subject");
        cate  = (ArrayList<String>)bundle.get("cate");
        subcate = (HashMap<String,ArrayList<String>>)bundle.get("subcate");

        imgProfile = findViewById(R.id.profile_image);
        editName = findViewById(R.id.edit_name);
        editYear = findViewById(R.id.edit_year);
        dropSubject = findViewById(R.id.drop_subject);
        editphone = findViewById(R.id.edit_phone_num);
        editZipNum = findViewById(R.id.edit_zip_num);
        dropCate = findViewById(R.id.drop_category);
        dropSubCate = findViewById(R.id.drop_sub_category);
        editGroup = findViewById(R.id.edit_group);
        editPosition = findViewById(R.id.edit_position);

        subject.set(0,"학과를 선택");
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,subject);
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSubject.setAdapter(subAdapter);

        for(String key : subcate.keySet())
            if(subcate.get(key) != null)
                subcate.get(key).set(0,"소분류를 선택");
        final ArrayAdapter<String> subcateAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item);
        subcateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSubCate.setAdapter(subcateAdapter);

        cate.set(0,"대분류를 선택");
        final ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cate);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropCate.setAdapter(cateAdapter);
        dropCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strCate = cateAdapter.getItem(i);
                subcateAdapter.clear();
                if(subcate.get(strCate) == null)
                    subcateAdapter.addAll("대분류를 먼저 선택해주세요.");
                else
                    subcateAdapter.addAll(subcate.get(strCate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.btn_change_profile_image:
                break;
            case R.id.btn_confirm:
                AddMem(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        if((boolean)parmam.get("result"))
                            finish();
                    }
                });
                break;
            case R.id.btn_confirm_next:
                AddMem(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        if((boolean)parmam.get("result")){
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    public void AddMem(final vvoidEvent andthen){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("추가중...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserInfo userInfo = new UserInfo();
        userInfo.category = (String)dropCate.getSelectedItem();
        userInfo.subCategory = (String)dropSubCate.getSelectedItem();
        userInfo.major = (String)dropSubject.getSelectedItem();
        userInfo.name = editName.getText().toString();
        userInfo.homeNumber = editZipNum.getText().toString();
        userInfo.phoneNumber = editphone.getText().toString();
        userInfo.group = editGroup.getText().toString();
        userInfo.position = editPosition.getText().toString();
        MemeberListMgr.AddMember(userInfo, new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                progressDialog.dismiss();
                boolean result = (boolean)parmam.get("result");
                if(result)
                    Toast.makeText(AddMember.this,"추가가 완료되었습니다.",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AddMember.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                andthen.vvoidEvent(parmam);
            }
        });
    }
}
