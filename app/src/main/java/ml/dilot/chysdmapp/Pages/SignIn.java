package ml.dilot.chysdmapp.Pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ml.dilot.chysdmapp.Base;
import ml.dilot.chysdmapp.R;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    EditText ipt_id;
    EditText ipt_pw;
    Button btn_sign_in;
    Button btn_sign_up;
    Button btn_find_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ipt_id = findViewById(R.id.ipt_id);
        ipt_pw = findViewById(R.id.ipt_pw);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_find_account = findViewById(R.id.btn_find_account);

        btn_sign_in.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        btn_find_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_sign_in:
                trySignin();
                break;
            case R.id.btn_sign_up:
                intent = new Intent(this,Signup.class);
                startActivity(intent);
                break;
            case R.id.btn_find_account:
                intent = new Intent(this,FindAccount.class);
                startActivity(intent);
                break;
        }
    }

    public void trySignin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("로그인 중입니다...");
        progressDialog.show();
        final String id = ipt_id.getText().toString();
        final String pw = ipt_pw.getText().toString();
        if(id.equals("") || pw.equals("") || id == null || pw == null){
            Toast.makeText(SignIn.this,"로그인 정보를 확인 해주세요",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(id,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    Toast.makeText(SignIn.this,"로그인하였습니다.",Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id",id);
                    editor.putString("pw",pw);
                    editor.commit();
                    startActivity(new Intent(SignIn.this,MainMenu.class));
                    finish();
                } else Toast.makeText(SignIn.this,"로그인 실패하였습니다..",Toast.LENGTH_LONG).show();
            }
        });
    }
}
