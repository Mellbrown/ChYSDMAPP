package ml.dilot.chysdmapp.Editeres;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ml.dilot.chysdmapp.R;

public class EditerPosition extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    PositionAdpater positionAdpater;

    ImageButton fabConfirm;
    ImageButton fabCancle;

    ArrayList<String> snapPosition;
    HashMap<String,Long> snapYearFee;

    //정렬 ItemTouchHelper
    ItemTouchHelper itemTouchHelper;
    ArrayList<String> reordBackup;

    EditMode editMode;

    enum EditMode{
        View,
        Edit,
        Reorde,
        Delete
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_position);

        recyclerView = findViewById(R.id.rec_view);
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);

        fabConfirm.setOnClickListener(this);
        fabCancle.setOnClickListener(this);
        fabConfirm.setVisibility(View.GONE);
        fabCancle.setVisibility(View.GONE);

        snapPosition = new ArrayList<>();
        snapYearFee = new HashMap<>();
        reordBackup = new ArrayList<>();

        editMode = EditMode.View;

        positionAdpater = new PositionAdpater();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(positionAdpater);

        //재 정렬 드래그 앤 드롭
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int posFrom = viewHolder.getAdapterPosition();
                int posTo = target.getAdapterPosition();
                Collections.swap(snapPosition,posFrom,posTo);
                positionAdpater.notifyItemMoved(posFrom,posTo);
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {   }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        load(null);
    }

    interface LoadListener{ void onload(boolean result); }
    public void load(final LoadListener loadListener){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("로딩중");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("임원명단/분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                snapPosition.clear();
                snapYearFee.clear();
                if(!dataSnapshot.child("직위").getValue().equals(""))
                    snapPosition = (ArrayList<String>) dataSnapshot.child("직위").getValue();
                if(!dataSnapshot.child("연회비용").getValue().equals(""))
                    snapYearFee = (HashMap<String,Long>) dataSnapshot.child("연회비용").getValue();
                positionAdpater.notifyDataSetChanged();
                if(loadListener != null)loadListener.onload(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(EditerPosition.this,"데이터를 로드하는데 실패하였습니다.",Toast.LENGTH_LONG).show();
                if(loadListener != null) loadListener.onload(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_position, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_position:
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                new PositionDialog(this, new ConfirmListener() {
                    @Override
                    public void onConfirm(String title, Long fee) {
                        ProgressDialog progressDialog = new ProgressDialog(EditerPosition.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("추가중...");
                        progressDialog.show();
                        if(snapPosition.contains(title)){
                            Toast.makeText(EditerPosition.this,"이미 해당 항목이 존재합니다.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        snapPosition.add(title);
                        snapYearFee.put(title,fee);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("임원명단/분류");
                        ref.child("직위").setValue(snapPosition);
                        ref.child("연회비용").setValue(snapYearFee);
                        positionAdpater.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.edit_position:
                editMode = EditMode.Edit;
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                positionAdpater.notifyDataSetChanged();
                break;
            case R.id.reorder_position:
                reordBackup.clear();
                reordBackup.addAll(snapPosition);
                editMode = EditMode.Reorde;
                positionAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                break;
            case R.id.delete_position:
                editMode = EditMode.Delete;
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                positionAdpater.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(editMode != EditMode.Reorde){
            fabConfirm.setVisibility(View.GONE);
            fabCancle.setVisibility(View.GONE);
            return;
        }

        // EditMode.Reorde
        switch (view.getId()){
            case R.id.fabConfirm:
                ProgressDialog progressDialog = new ProgressDialog(EditerPosition.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("적용중...");
                progressDialog.show();
                reordBackup.clear();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("임원명단/분류");
                ref.child("직위").setValue(snapPosition);
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                progressDialog.dismiss();
                break;
            case R.id.fabCancle:
                snapPosition.clear();
                snapPosition.addAll(reordBackup);
                editMode = EditMode.View;
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                positionAdpater.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (editMode){
            case View:
                finish();
                break;
            case Reorde:
                snapPosition.clear();
                snapPosition.addAll(reordBackup);
            case Edit:
            case Delete:
                editMode = EditMode.View;
                positionAdpater.notifyDataSetChanged();
        }
    }

    class PositionAdpater extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PositionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inline_with_desc,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PositionViewHolder positionViewHolder = (PositionViewHolder)holder;
            String bPosition = snapPosition.get(position);
            Long yearFee = snapYearFee.get(bPosition);
            positionViewHolder.txtTitle.setText(bPosition);
            positionViewHolder.txtDesc.setText("- 연 " + yearFee + " 원");
            switch (editMode){
                case View: positionViewHolder.imgState.setImageBitmap(null); break;
                case Edit: positionViewHolder.imgState.setImageResource(R.drawable.ic_edit_black_24dp); break;
                case Reorde: positionViewHolder.imgState.setImageResource(R.drawable.ic_reorder_black_24dp); break;
                case Delete: positionViewHolder.imgState.setImageResource(R.drawable.ic_delete_black_24dp); break;
            }
        }

        @Override
        public int getItemCount() {
            return snapPosition.size();
        }

        class PositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

            TextView txtTitle;
            TextView txtDesc;
            ImageView imgState;

            public PositionViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnTouchListener(this);
                txtTitle = itemView.findViewById(R.id.txt_title);
                txtDesc = itemView.findViewById(R.id.txt_desc);
                imgState = itemView.findViewById(R.id.img_state);
            }

            @Override
            public void onClick(View view) {
                int position = getLayoutPosition();
                final String title = snapPosition.get(position);
                Long fee = snapYearFee.get(title);
                switch (editMode){
                    case View: break;
                    case Edit:
                        new PositionDialog(EditerPosition.this, new ConfirmListener() {
                            @Override
                            public void onConfirm(String ntitle, Long nfee) {
                                ProgressDialog progressDialog = new ProgressDialog(EditerPosition.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("변경중");
                                progressDialog.show();
                                if(snapPosition.contains(ntitle)){
                                    Toast.makeText(EditerPosition.this,"이미 해당 항목이 존재합니다.",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                snapPosition.set(snapPosition.indexOf(title),ntitle);
                                snapYearFee.put(ntitle,snapYearFee.get(title));
                                snapYearFee.remove(title);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("임원명단/분류");
                                ref.child("직위").setValue(snapPosition);
                                ref.child("연회비용").setValue(snapYearFee);
                                positionAdpater.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        }).setPosition(title).setFee(fee).show();
                    case Delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditerPosition.this);
                        alertDialog.setTitle("직위 삭제");
                        alertDialog.setMessage("정말로 '" + title + "' 직위를 삭제하시겠습니까?");
                        alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressDialog = new ProgressDialog(EditerPosition.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("삭제중");
                                progressDialog.show();
                                snapPosition.remove(title);
                                snapYearFee.remove(title);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("임원명단/분류");
                                if(snapPosition.size() != 0 ) ref.child("직위").setValue(snapPosition);
                                else ref.child("직위").setValue("");
                                if(snapYearFee.size() != 0)ref.child("연회비용").setValue(snapYearFee);
                                else ref.child("연회비용").setValue("");
                                positionAdpater.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog.show();
                        break;
                }

            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && editMode == EditMode.Reorde){
                    itemTouchHelper.startDrag(this);
                    return true;
                }
                return false;
            }
        }
    }

    interface ConfirmListener{ void onConfirm(String title, Long fee); }
    class PositionDialog extends Dialog implements View.OnClickListener {

        EditText editPosition;
        EditText editFee;
        Button btnConfirm;
        Button btnCancle;
        ConfirmListener confirmListener;

        protected PositionDialog(Context context,ConfirmListener confirmListener) {
            super(context);
            setTitle("새로운 직위 추가");
            setContentView(R.layout.dialog_title_with_number);
            editPosition = findViewById(R.id.edit_position);
            editFee = findViewById(R.id.edit_fee);
            btnConfirm = findViewById(R.id.btn_confirm);
            btnCancle = findViewById(R.id.btn_cancle);
            btnCancle.setOnClickListener(this);
            btnConfirm.setOnClickListener(this);
            this.confirmListener = confirmListener;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_confirm:
                    String title = editPosition.getText().toString();
                    Long fee = Long.valueOf(editFee.getText().toString());
                    confirmListener.onConfirm(title,fee);
                case R.id.btn_cancle:
                    dismiss();
            }
        }


        public PositionDialog setPosition(String title){
            editPosition.setText(title);
            return this;
        }

        public PositionDialog setFee(Long fee){
            editFee.setText(fee+"");
            return this;
        }
    }
}
