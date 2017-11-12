package ml.dilot.chysdmapp.Test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;

public class AddUser extends AppCompatActivity {

    Button btnAddCate;
    Button btnDelCate;
    EditText editCate;
    TextView txtError;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        btnAddCate = findViewById(R.id.btnAddCate);
        btnDelCate = findViewById(R.id.btnDelCate);
        editCate = findViewById(R.id.EditTitle);
        txtError = findViewById(R.id.txtError);
        txtResult = findViewById(R.id.txtResult);


    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.btnAddCate:
                MemeberListMgr.AddCategory(editCate.getText().toString(), new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        if((boolean)parmam.get("result")){
                            MemeberListMgr.GetCategory(new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    if((boolean)parmam.get("result")){

                                    }else {
                                        txtError.setText((String)parmam.get("message"));
                                        txtResult.setText("");
                                    }
                                }
                            });
                        } else {
                            txtError.setText((String)parmam.get("message"));
                            txtResult.setText("");
                        }
                    }
                });
                break;
            case R.id.btnDelCate:
                break;
        }
    }
}
