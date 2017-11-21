package ml.dilot.chysdmapp.UtilPack;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;

/**
 * Created by mlyg2 on 2017-11-18.
 */

public class SelectCateDialog extends Dialog {
    Spinner dropCate;
    Spinner dropSubCate;
    Button btnCancle;
    Button btnConfirm;

    public SelectCateDialog(@NonNull final Context context, int count, final ArrayList<String> cate, final HashMap<String,ArrayList<String>> subcate, final vvoidEvent andthen) {
        super(context);
        this.setCancelable(false);
        this.setTitle("회원 "+count+"명 이동");
        this.setContentView(R.layout.diaglog_select_cate);
        dropCate = findViewById(R.id.spinner_cate);
        dropSubCate = findViewById(R.id.spinner_subcate);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancle = findViewById(R.id.btn_cancle);

        for(String key : subcate.keySet())
            if(subcate.get(key) != null)
                subcate.get(key).add(0,"소분류를 선택");
        final ArrayAdapter<String> subcateAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item);
        subcateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSubCate.setAdapter(subcateAdapter);

        cate.add(0,"대분류를 선택");
        final ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,cate);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropCate.setAdapter(cateAdapter);

        dropCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strCate = cateAdapter.getItem(i);
                subcateAdapter.clear();
                if(subcate.get(strCate) == null) subcateAdapter.addAll("대분류를 먼저 선택해주세요.");
                else subcateAdapter.addAll(subcate.get(strCate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dropCate.getSelectedItemPosition() == 0 || dropCate.getSelectedItemPosition() == 0){
                    Toast.makeText(context,"항목을 선택해주십시오.", Toast.LENGTH_LONG).show();
                    return;
                }
                SelectCateDialog.this.dismiss();
                HashMap<String,Object> param = new HashMap<>();
                param.put("result",true);
                param.put("selectedcate",cate.get(dropCate.getSelectedItemPosition()));
                param.put("selectedsubcate", dropSubCate.getSelectedItem());
                andthen.vvoidEvent(param);
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCateDialog.this.dismiss();
                HashMap<String,Object> param = new HashMap<>();
                param.put("result",false);
                andthen.vvoidEvent(param);
            }
        });
    }
}
