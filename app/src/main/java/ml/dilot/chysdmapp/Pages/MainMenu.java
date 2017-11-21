package ml.dilot.chysdmapp.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import ml.dilot.chysdmapp.Base;
import ml.dilot.chysdmapp.Editeres.AddMember;
import ml.dilot.chysdmapp.Editeres.EditerBoarder;
import ml.dilot.chysdmapp.Editeres.EditerCategory;
import ml.dilot.chysdmapp.Editeres.EditerDeactiveMember;
import ml.dilot.chysdmapp.Editeres.EditerMember;
import ml.dilot.chysdmapp.Editeres.EditerPosition;
import ml.dilot.chysdmapp.Editeres.EditerSubject;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.UtilPack.Util;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //로그인 체크
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            String id = getIntent().getStringExtra("id");
            String pw = getIntent().getStringExtra("pw");
            FirebaseAuth.getInstance().signInWithEmailAndPassword(id,pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainMenu.this,"로그아웃되었습니다.",Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("id");
                        editor.remove("pw");
                        editor.commit();
                        startActivity(new Intent(MainMenu.this, Base.class));
                        finish();
                    }
                }
            });
        }//로그인 체크 끝
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    //액션 메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit_boader: startActivity(new Intent(this, EditerBoarder.class)); break;
            case R.id.edit_cate: startActivity(new Intent(this, EditerCategory.class)); break;
            case R.id.edit_deactive: startActivity(new Intent(this, EditerDeactiveMember.class)); break;
            case R.id.edit_boarder: startActivity(new Intent(this, EditerBoarder.class)); break;
            case R.id.edit_member: startActivity(new Intent(this, EditerMember.class)); break;
            case R.id.edit_position: startActivity(new Intent(this, EditerPosition.class)); break;
            case R.id.edit_subject: startActivity(new Intent(this, EditerSubject.class)); break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainMenu.this,"로그아웃되었습니다.",Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("id");
                editor.remove("pw");
                editor.commit();
                startActivity(new Intent(MainMenu.this, Base.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
