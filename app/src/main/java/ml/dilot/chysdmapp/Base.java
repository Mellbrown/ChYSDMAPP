package ml.dilot.chysdmapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ml.dilot.chysdmapp.Editeres.EditerCategory;
import ml.dilot.chysdmapp.Test.TestCategoryMgr;

public class Base extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        startActivity(new Intent(this, EditerCategory.class));
    }
}
