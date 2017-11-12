package ml.dilot.chysdmapp;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;

/**
 * Created by mlyg2 on 2017-11-07.
 */

public class Util {
    public static void TextEditDialog(Context context, String title, String message, final vvoidEvent andthen){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);

        final EditText editText = new EditText(context);
        ad.setView(editText);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> param = new HashMap<>();
                param.put("result", true);
                param.put("text", editText.getText().toString());
                andthen.vvoidEvent(param);
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Object> param = new HashMap<>();
                param.put("result", false);
                param.put("text", editText.getText().toString());
            }
        });
        ad.show();
    }
}
