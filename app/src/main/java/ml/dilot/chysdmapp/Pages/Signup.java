package ml.dilot.chysdmapp.Pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ml.dilot.chysdmapp.R;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    EditText ipt_new_id;
    EditText ipt_new_pw;
    EditText ipt_re_pw;
    EditText ipt_name;
    EditText ipt_year;
    Spinner spin_subject;
    Button btn_next;

    ArrayList<String> subject;
    ArrayAdapter<String> subAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ipt_new_id = findViewById(R.id.ipt_new_id);
        ipt_new_pw = findViewById(R.id.ipt_new_pw);
        ipt_re_pw = findViewById(R.id.ipt_re_pw);
        ipt_name = findViewById(R.id.ipt_name);
        ipt_year = findViewById(R.id.ipt_year);
        spin_subject = findViewById(R.id.spin_subject);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        subject = new ArrayList<>();

        //과목 로딩 시작
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("로딩중...");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("회원명단/분류/학과").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                subject = (ArrayList<String>)dataSnapshot.getValue();
                subject.add(0,"학과를 선택");
                ArrayAdapter<String> subAdapter = new ArrayAdapter<>(Signup.this,android.R.layout.simple_spinner_item,subject);
                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_subject.setAdapter(subAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(Signup.this,"로딩에 실패하였습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next:
                tryCreateAccount();;
                break;
        }
    }

    public void tryCreateAccount(){
        String new_id = ipt_new_id.getText().toString();
        String new_pw = ipt_new_pw.getText().toString();
        String re_pw = ipt_re_pw.getText().toString();
        if(!new_pw.equals(re_pw)){
            Toast.makeText(this,"비밀번호가 같지 않습니다.",Toast.LENGTH_LONG).show();
            return;
        }
        Long year = Long.valueOf(ipt_year.getText().toString());
        if(spin_subject.getSelectedItemPosition() == 0){
            Toast.makeText(this,"학과를 선택해주세요",Toast.LENGTH_LONG).show();
            return;
        }
        final String name = ipt_name.getText().toString();
        String subject = (String)spin_subject.getSelectedItem();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("계정 생성중...");
        progressDialog.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(new_id,new_pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(Signup.this,"계정 생성 완료",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Signup.this,ConnectUser.class));
                    finish();
                } else {
                    Toast.makeText(Signup.this,"계정 생성 실패",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
