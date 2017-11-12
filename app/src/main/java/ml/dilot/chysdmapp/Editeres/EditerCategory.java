package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.Space;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.Test.TestCategoryMgr;
import ml.dilot.chysdmapp.Util;

public class EditerCategory extends AppCompatActivity implements View.OnClickListener {

    //리사이클러뷰와 그의 어뎁터
    RecyclerView editCateRec;
    EditCateAdpater editCateAdpater;

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
        setContentView(R.layout.activity_editer_category);

        //각종 UI 로드
        swipeRefreshLayout = findViewById(R.id.edit_cate_refresh);
        editCateRec = findViewById(R.id.edit_cate_recview);
        editCateAdpater = new EditCateAdpater(this);
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);
        tip = findViewById(R.id.edit_cate_tip);
        tipText = findViewById(R.id.edit_cate_tip_text);

        //숨길 UI 숨기기
        tip.setVisibility(View.GONE);
        fabCancle.setVisibility(View.GONE);
        fabConfirm.setVisibility(View.GONE);

        editCateRec.setHasFixedSize(true);
        editCateRec.setLayoutManager(new LinearLayoutManager(this));
        editCateRec.setAdapter(editCateAdpater);
        fabConfirm.setOnClickListener(this);
        fabCancle.setOnClickListener(this);

        //당기면 리로딩
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MemeberListMgr.GetCategory(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        swipeRefreshLayout.setRefreshing(false);
                        boolean result = (boolean)parmam.get("result");
                        if(result){
                            List<String> cate = (List<String>) parmam.get("cate");
                            Log.d("hello", cate.toString());
                            editCateAdpater.snapLstCate = cate;
                            editCateAdpater.notifyDataSetChanged();
                        }else{
                            String message = (String)parmam.get("message");
                            Toast.makeText(EditerCategory.this,message,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

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
                Collections.swap(editCateAdpater.snapLstCate,posFrom,posTo);
                editCateAdpater.notifyItemMoved(posFrom,posTo);
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {   }
        });
        itemTouchHelper.attachToRecyclerView(editCateRec);

        //최초 로딩
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오는 중입니다.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MemeberListMgr.GetCategory(new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                progressDialog.dismiss();
                boolean result = (boolean)parmam.get("result");
                if(result){
                    List<String> cate = (List<String>) parmam.get("cate");
                    editCateAdpater.snapLstCate = cate;
                    Log.d("hello", editCateAdpater.snapLstCate.toString());
                    editCateAdpater.notifyDataSetChanged();
                }else{
                    String message = (String)parmam.get("message");
                    Toast.makeText(EditerCategory.this,message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // 확인 취소 클릭
    public void onClick(View view){
        switch (editCateAdpater.editCateState){
            case Delete:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        final ProgressDialog progressDialog = new ProgressDialog(EditerCategory.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle( "삭제중...");
                        progressDialog.show();
                        String cates[] = new String[editCateAdpater.lstSelected.size()];
                        for(int i = 0; editCateAdpater.lstSelected.size() > i; i++)
                            cates[i] = editCateAdpater.snapLstCate.get(editCateAdpater.lstSelected.get(i));
                        MemeberListMgr.DeleteCategory(cates, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    StringBuilder message = new StringBuilder();
                                    int cnt_success = (int)parmam.get("cnt_success");
                                    List<String> lst_success = (List<String>)parmam.get("lst_success");
                                    for(String cate : lst_success){
                                        int tar = editCateAdpater.snapLstCate.indexOf(cate);
                                        editCateAdpater.snapLstCate.remove(tar);
                                        editCateAdpater.notifyItemRemoved(tar);
                                    }
                                    int cnt_fail = (int)parmam.get("cnt_fail");
                                    message.append("성공 : "  + cnt_success + ", 실패 : " + cnt_fail);
                                    if(cnt_fail != 0){
                                        Map <String,String> lst_fail = (Map<String,String>)parmam.get("lst_fail");
                                        for(String title : lst_fail.keySet())
                                            message.append("\n " + title + " : " + lst_fail.get(title));
                                    }
                                    editCateAdpater.editCateState = EditCateState.View;
                                    editCateAdpater.notifyDataSetChanged();
                                    tip.setVisibility(View.GONE);
                                    fabCancle.setVisibility(View.GONE);
                                    fabConfirm.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    Toast.makeText(EditerCategory.this,message,Toast.LENGTH_LONG).show();
                                } else Toast.makeText(EditerCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    case R.id.fabCancle:
                        editCateAdpater.editCateState = EditCateState.View;
                        editCateAdpater.notifyDataSetChanged();
                        tip.setVisibility(View.GONE);
                        fabCancle.setVisibility(View.GONE);
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
            case Rename:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        editCateAdpater.editCateState = EditCateState.View;
                        editCateAdpater.notifyDataSetChanged();
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
            case Reorde:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        final ProgressDialog progressDialog = new ProgressDialog(EditerCategory.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle( "적용중...");
                        progressDialog.show();
                        MemeberListMgr.ReordeCategory(editCateAdpater.snapLstCate, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                progressDialog.dismiss();
                                if(!(boolean)parmam.get("result")){
                                    editCateAdpater.snapLstCate = reordBackup.subList(0, reordBackup.size());
                                    Toast.makeText(EditerCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                                }
                                editCateAdpater.editCateState = EditCateState.View;
                                editCateAdpater.notifyDataSetChanged();
                                fabConfirm.setVisibility(View.GONE);
                            }
                        });
                }break;
        }
    }

    //액션 메뉴 전개
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_cate, menu);
        return true;
    }

    //액션 메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.act_edit_cate_add:
                if(editCateAdpater.editCateState != EditCateState.View){
                    editCateAdpater.editCateState = EditCateState.View;
                    fabCancle.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    fabConfirm.setVisibility(View.GONE);
                    editCateAdpater.notifyDataSetChanged();
                }
                Util.TextEditDialog(this, "새 카테고리", "새카테고리의 이름을 입력해주십시오.", new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        boolean request = (boolean)parmam.get("result");
                        final String text = (String)parmam.get("text");
                        if(request){
                            final ProgressDialog progressDialog = new ProgressDialog(EditerCategory.this);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            MemeberListMgr.AddCategory(text, new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    progressDialog.dismiss();
                                    boolean success = (Boolean) parmam.get("result");
                                    if(success) {
                                        editCateAdpater.snapLstCate.add(text);
                                        editCateAdpater.notifyItemInserted(editCateAdpater.snapLstCate.size()-1);
                                        Toast.makeText(EditerCategory.this, "'" + text + "' 항목이 추가되었습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        String message = (String) parmam.get("message");
                                        Toast.makeText(EditerCategory.this, message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.act_edit_cate_remove:
                if(editCateAdpater.editCateState != EditCateState.Delete){
                    editCateAdpater.editCateState = EditCateState.Delete;
                    editCateAdpater.lstSelected.clear();
                    editCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tipText.setText("삭제 : 0개 선택됨");
                break;
            case R.id.act_edit_cate_rename:
                if(editCateAdpater.editCateState != EditCateState.Rename){
                    editCateAdpater.editCateState = EditCateState.Rename;
                    editCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                break;
            case R.id.act_edit_cate_reorde:
                if(editCateAdpater.editCateState != EditCateState.Reorde){
                    editCateAdpater.editCateState = EditCateState.Reorde;
                    editCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                reordBackup = editCateAdpater.snapLstCate.subList(0,editCateAdpater.snapLstCate.size());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //뷰 편집 상태
    enum EditCateState{
        View,
        Delete,
        Reorde,
        Rename
    }

    //어뎁터 뷰
    class EditCateAdpater extends RecyclerView.Adapter{
        List<String> snapLstCate;
        List<Integer> lstSelected;
        EditCateState editCateState = EditCateState.View;

        public EditCateAdpater(Context context){
            this.snapLstCate = new ArrayList<>();lstSelected = new ArrayList<>();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_cate,parent,false);
            EditCateViewHolder holder = new EditCateViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EditCateViewHolder vh = (EditCateViewHolder) holder;
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
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && editCateState == EditCateState.Reorde){
                    itemTouchHelper.startDrag(this);
                    swipeRefreshLayout.setEnabled(true);
                    return true;
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP && editCateState == EditCateState.Reorde) {
                    swipeRefreshLayout.setEnabled(false);
                    return true;
                }
                return false;
            }
        }
    }
}


