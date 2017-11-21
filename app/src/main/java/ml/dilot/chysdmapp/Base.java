package ml.dilot.chysdmapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ml.dilot.chysdmapp.Editeres.EditerCategory;
import ml.dilot.chysdmapp.Editeres.EditerDeactiveMember;
import ml.dilot.chysdmapp.Editeres.EditerMember;
import ml.dilot.chysdmapp.Editeres.EditerPosition;
import ml.dilot.chysdmapp.Editeres.EditerSubject;
import ml.dilot.chysdmapp.Pages.MainMenu;
import ml.dilot.chysdmapp.Pages.ShowMember;
import ml.dilot.chysdmapp.Pages.SignIn;
import ml.dilot.chysdmapp.Pages.Signup;
import ml.dilot.chysdmapp.UtilPack.Util;

public class Base extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);
        String id = sharedPreferences.getString("id",null);
        String pw = sharedPreferences.getString("pw",null);

        if(id == null || pw == null){
            startActivity(new Intent(this, SignIn.class));
        } else {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra("id",id);
            intent.putExtra("pw",pw);
            startActivity(intent);
        }
        finish();
    }
}
