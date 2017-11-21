package ml.dilot.chysdmapp.Pages;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import ml.dilot.chysdmapp.R;

public class ShowBoader extends AppCompatActivity {

    TextView txtYear;
    FloatingActionButton btnPrevYear;
    FloatingActionButton btnNextYear;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_boader);
    }
}
