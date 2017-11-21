package ml.dilot.chysdmapp.UtilPack;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ml.dilot.chysdmapp.R;

/**
 * Created by mlyg2 on 2017-11-18.
 */

public class ConfirmCancleDialog extends Dialog{
    public ConfirmCancleDialog(@NonNull Context context, String title, String message, final ConfirmCancleListener callback) {
        super(context);
        setContentView(R.layout.dialog_confirm_cancle);
        setCancelable(false);
        setTitle(title);
        TextView txtMsg = findViewById(R.id.txt_msg);
        Button btnCancle = findViewById(R.id.btn_cancle);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        txtMsg.setText(message);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmCancleDialog.this.dismiss();
                callback.onCancle();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmCancleDialog.this.dismiss();
                callback.onConfrim();
            }
        });
    }

    public interface ConfirmCancleListener{
        void onConfrim();
        void onCancle();
    }
}
