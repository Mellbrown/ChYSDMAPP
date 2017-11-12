package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.Util;

public class EditerSubCategory extends AppCompatActivity {

    final String CATE = getIntent().getAction();

    //리사이클러뷰와 그의 어뎁터
    RecyclerView editSubCateRec;
    EditSubCateAdpater editSubCateAdpater;

    //레이아웃의 각종 UI들
    SwipeRefreshLayout swipeRefreshLayout; //당기면 리로딩

    ImageButton fabConfirm;
    ImageButton fabCancle;

    CardView tip;
    TextView tipText;

    //정렬 ItemTouchHelper
    ItemTouchHelper itemTouchHelper;
    List<String> reordBackup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_sub_category);

        getSupportActionBar().setTitle(getSupportActionBar().getTitle() + CATE);

        //각종 UI 로드
        swipeRefreshLayout = findViewById(R.id.edit_sub_cate_refresh);
        editSubCateRec = findViewById(R.id.edit_sub_cate_recview);
        editSubCateAdpater = new EditSubCateAdpater(this);
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);
        tip = findViewById(R.id.edit_sub_cate_tip);
        tipText = findViewById(R.id.edit_sub_cate_tip_text);

        //숨길 UI 숨기기
        tip.setVisibility(View.GONE);
        fabCancle.setVisibility(View.GONE);
        fabConfirm.setVisibility(View.GONE);

        editSubCateRec.setHasFixedSize(true);
        editSubCateRec.setLayoutManager(new LinearLayoutManager(this));
        editSubCateRec.setAdapter(editSubCateAdpater);
        fabConfirm.setOnClickListener(this);
        fabCancle.setOnClickListener(this);

    }

    //뷰 편집 상태
    enum EditSubCateState{
        View,
        Delete,
        Reorde,
        Rename
    }

    //어뎁터 뷰
    class EditSubCateAdpater extends RecyclerView.Adapter{
        List<String> snapLstSubCate;
        List<Integer> lstSelected;
        EditSubCateState editSubCateState = EditSubCateState.View;

        public EditSubCateAdpater(Context context){
            snapLstSubCate = new ArrayList<>();
            lstSelected = new ArrayList<>();
        }/////////////////////////////////////////////////////////////////////////////////////////
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_cate,parent,false);
            EditerCategory.EditCateAdpater.EditCateViewHolder holder = new EditerCategory.EditCateAdpater.EditCateViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EditerCategory.EditCateAdpater.EditCateViewHolder vh = (EditerCategory.EditCateAdpater.EditCateViewHolder) holder;
            vh.txtCate.setText(snapLstCate.get(position));
            switch (editCateState){
                case View: vh.imgState.setImageBitmap(null);break;
                case Delete:
                    if(lstSelected.contains(position)) vh.imgState.setImageResource(R.drawable.ic_selected);
                    else vh.imgState.setImageResource(R.drawable.ic_unselected); break;
                case Reorde: vh.imgState.setImageResource(R.drawable.ic_handle);break;
                case Rename: vh.imgState.setImageResource(R.drawable.ic_edit);
            }
        }

        @Override
        public int getItemCount() {
            return snapLstCate.size();
        }

        public class EditCateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
            TextView txtCate;
            ImageView imgState;

            public EditCateViewHolder(View itemView) {
                super(itemView);
                itemView.setOnTouchListener(this);
                itemView.setOnClickListener(this);
                txtCate = itemView.findViewById(R.id.edit_cate_item_title);
                imgState = itemView.findViewById(R.id.edit_cate_item_state);
            }

            @Override
            public void onClick(View view) {
                final int position = getLayoutPosition();
                switch (editCateState){
                    case View:
                        Intent intent = new Intent(EditerCategory.this,EditerSubCategory.class);
                        intent.setAction(snapLstCate.get(position));
                        startActivity(intent);
                        break;
                    case Delete:
                        if(lstSelected.contains(position)) lstSelected.remove((Object)position);
                        else lstSelected.add(position);
                        notifyItemChanged(position);
                        tipText.setText("삭제 : " + lstSelected.size() + "개 선택됨");
                        break;
                    case Rename:
                        final String oldTitle = snapLstCate.get(position);
                        Util.TextEditDialog(EditerCategory.this, "새 이름", "'" +  oldTitle + "'에 대한 새로운 이름을 입력해주세요", new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    final ProgressDialog progressDialog = new ProgressDialog(EditerCategory.this);
                                    progressDialog.setCancelable(false);
                                    final String newTitle = (String)parmam.get("text");
                                    MemeberListMgr.RenameCategory(snapLstCate.get(position), newTitle , new vvoidEvent() {
                                        @Override
                                        public void vvoidEvent(Map parmam) {
                                            progressDialog.dismiss();
                                            if((boolean)parmam.get("result")){
                                                int changed = (int)parmam.get("changed");
                                                snapLstCate.set(position,newTitle);
                                                notifyItemChanged(position);
                                                Toast.makeText(EditerCategory.this,"'"+oldTitle+"'->'"+newTitle+"', 총"+changed+"명의 사용자 데이터가 수정되었습니다.",Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(EditerCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }
                        });
                        break;
                }
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && editCateState == EditerCategory.EditCateState.Reorde){
                    itemTouchHelper.startDrag(this);
                    swipeRefreshLayout.setEnabled(true);
                    return true;
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP && editCateState == EditerCategory.EditCateState.Reorde) {
                    swipeRefreshLayout.setEnabled(false);
                    return true;
                }
                return false;
            }
        }
    }
}
