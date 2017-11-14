package ml.dilot.chysdmapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ml.dilot.chysdmapp.Editeres.EditerCategory;
import ml.dilot.chysdmapp.Editeres.EditerSubject;
import ml.dilot.chysdmapp.Test.TestCategoryMgr;

public class Base extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.btn_edit_cateogry:
                startActivity(new Intent(this, EditerCategory.class));
                break;
            case R.id.btn_edit_subject:
                startActivity(new Intent(this, EditerSubject.class));
                break;
        }
    }
}
