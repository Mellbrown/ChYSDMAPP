package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.List;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.UtilPack.Util;

public class EditerSubCategory extends AppCompatActivity implements View.OnClickListener {

    String CATE;

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

        CATE = getIntent().getAction();
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

        //당기면 리로딩
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MemeberListMgr.GetSubCategory(CATE,new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        swipeRefreshLayout.setRefreshing(false);
                        boolean result = (boolean)parmam.get("result");
                        if(result){
                            List<String> subCate = (List<String>) parmam.get("subCate");
                            editSubCateAdpater.snapLstSubCate = subCate;
                            editSubCateAdpater.notifyDataSetChanged();
                        }else{
                            String message = (String)parmam.get("message");
                            Toast.makeText(EditerSubCategory.this,message,Toast.LENGTH_LONG).show();
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
                Collections.swap(editSubCateAdpater.snapLstSubCate,posFrom,posTo);
                editSubCateAdpater.notifyItemMoved(posFrom,posTo);
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {   }
        });
        itemTouchHelper.attachToRecyclerView(editSubCateRec);

        //최초 로딩
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오는 중입니다.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MemeberListMgr.GetSubCategory(CATE,new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                progressDialog.dismiss();
                boolean result = (boolean)parmam.get("result");
                if(result){
                    List<String> subCate = (List<String>) parmam.get("subCate");
                    editSubCateAdpater.snapLstSubCate = subCate;
                    editSubCateAdpater.notifyDataSetChanged();
                }else{
                    String message = (String)parmam.get("message");
                    Toast.makeText(EditerSubCategory.this,message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // 확인 취소 클릭
    public void onClick(View view){
        switch (editSubCateAdpater.editSubCateState){
            case Delete:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        final ProgressDialog progressDialog = new ProgressDialog(EditerSubCategory.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle( "삭제중...");
                        progressDialog.show();
                        String subCates[] = new String[editSubCateAdpater.lstSelected.size()];
                        for(int i = 0; editSubCateAdpater.lstSelected.size() > i; i++)
                            subCates[i] = editSubCateAdpater.snapLstSubCate.get(editSubCateAdpater.lstSelected.get(i));
                        MemeberListMgr.DeleteSubCategory(CATE, subCates, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    StringBuilder message = new StringBuilder();
                                    int cnt_success = (int)parmam.get("cnt_success");
                                    List<String> lst_success = (List<String>)parmam.get("lst_success");
                                    for(String cate : lst_success){
                                        int tar = editSubCateAdpater.snapLstSubCate.indexOf(cate);
                                        editSubCateAdpater.snapLstSubCate.remove(tar);
                                        editSubCateAdpater.notifyItemRemoved(tar);
                                    }
                                    int cnt_fail = (int)parmam.get("cnt_fail");
                                    message.append("성공 : "  + cnt_success + ", 실패 : " + cnt_fail);
                                    if(cnt_fail != 0){
                                        Map <String,String> lst_fail = (Map<String,String>)parmam.get("lst_fail");
                                        for(String title : lst_fail.keySet())
                                            message.append("\n " + title + " : " + lst_fail.get(title));
                                    }
                                    editSubCateAdpater.editSubCateState = EditSubCateState.View;
                                    editSubCateAdpater.notifyDataSetChanged();
                                    tip.setVisibility(View.GONE);
                                    fabCancle.setVisibility(View.GONE);
                                    fabConfirm.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    Toast.makeText(EditerSubCategory.this,message,Toast.LENGTH_LONG).show();
                                } else Toast.makeText(EditerSubCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    case R.id.fabCancle:
                        editSubCateAdpater.editSubCateState = EditSubCateState.View;
                        editSubCateAdpater.notifyDataSetChanged();
                        tip.setVisibility(View.GONE);
                        fabCancle.setVisibility(View.GONE);
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
            case Rename:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        editSubCateAdpater.editSubCateState = EditSubCateState.View;
                        editSubCateAdpater.notifyDataSetChanged();
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
            case Reorde:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        final ProgressDialog progressDialog = new ProgressDialog(EditerSubCategory.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle( "적용중...");
                        progressDialog.show();
                        MemeberListMgr.ReordeSubCategory(editSubCateAdpater.snapLstSubCate,CATE, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                progressDialog.dismiss();
                                if(!(boolean)parmam.get("result")){
                                    editSubCateAdpater.snapLstSubCate = reordBackup.subList(0, reordBackup.size());
                                    Toast.makeText(EditerSubCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                                }
                                editSubCateAdpater.editSubCateState = EditSubCateState.View;
                                editSubCateAdpater.notifyDataSetChanged();
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
                if(editSubCateAdpater.editSubCateState != EditSubCateState.View){
                    editSubCateAdpater.editSubCateState = EditSubCateState.View;
                    fabCancle.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    fabConfirm.setVisibility(View.GONE);
                    editSubCateAdpater.notifyDataSetChanged();
                }
                Util.TextEditDialog(this, "새 소분류", "새 소분류의 이름을 입력해주십시오.", new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        boolean request = (boolean)parmam.get("result");
                        final String text = (String)parmam.get("text");
                        if(request){
                            final ProgressDialog progressDialog = new ProgressDialog(EditerSubCategory.this);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            MemeberListMgr.AddSubCategory(text,CATE, new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    progressDialog.dismiss();
                                    boolean success = (Boolean) parmam.get("result");
                                    if(success) {
                                        editSubCateAdpater.snapLstSubCate.add(text);
                                        editSubCateAdpater.notifyItemInserted(editSubCateAdpater.snapLstSubCate.size()-1);
                                        Toast.makeText(EditerSubCategory.this, "'" + text + "' 항목이 추가되었습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        String message = (String) parmam.get("message");
                                        Toast.makeText(EditerSubCategory.this, message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.act_edit_cate_remove:
                if(editSubCateAdpater.editSubCateState != EditSubCateState.Delete){
                    editSubCateAdpater.editSubCateState = EditSubCateState.Delete;
                    editSubCateAdpater.lstSelected.clear();
                    editSubCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tipText.setText("삭제 : 0개 선택됨");
                break;
            case R.id.act_edit_cate_rename:
                if(editSubCateAdpater.editSubCateState != EditSubCateState.Rename){
                    editSubCateAdpater.editSubCateState = EditSubCateState.Rename;
                    editSubCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                break;
            case R.id.act_edit_cate_reorde:
                if(editSubCateAdpater.editSubCateState != EditSubCateState.Reorde){
                    editSubCateAdpater.editSubCateState = EditSubCateState.Reorde;
                    editSubCateAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                reordBackup = editSubCateAdpater.snapLstSubCate.subList(0,editSubCateAdpater.snapLstSubCate.size());
                break;
        }

        return super.onOptionsItemSelected(item);
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
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_cate,parent,false);
            EditSubCateViewHolder holder = new EditSubCateViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EditSubCateViewHolder vh = (EditSubCateViewHolder) holder;
            vh.txtCate.setText(snapLstSubCate.get(position));
            switch (editSubCateState){
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
            return snapLstSubCate.size();
        }

        public class EditSubCateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
            TextView txtCate;
            ImageView imgState;

            public EditSubCateViewHolder(View itemView) {
                super(itemView);
                itemView.setOnTouchListener(this);
                itemView.setOnClickListener(this);
                txtCate = itemView.findViewById(R.id.edit_cate_item_title);
                imgState = itemView.findViewById(R.id.edit_cate_item_state);
            }

            @Override
            public void onClick(View view) {
                final int position = getLayoutPosition();
                switch (editSubCateState){
                    case View:
//                        Intent intent = new Intent(EditerSubCategory.this,EditerSubCategory.class);
//                        intent.setAction(snapLstSubCate.get(position));
//                        startActivity(intent);
//                        좀 고민하다가 이걸 누르면 멀 표시할까? 아직은 아무 기능도 넣지 않는다
                        break;
                    case Delete:
                        if(lstSelected.contains(position)) lstSelected.remove((Object)position);
                        else lstSelected.add(position);
                        notifyItemChanged(position);
                        tipText.setText("삭제 : " + lstSelected.size() + "개 선택됨");
                        break;
                    case Rename:
                        final String oldTitle = snapLstSubCate.get(position);
                        Util.TextEditDialog(EditerSubCategory.this, "새 이름", "'" +  oldTitle + "'에 대한 새로운 이름을 입력해주세요", new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    final ProgressDialog progressDialog = new ProgressDialog(EditerSubCategory.this);
                                    progressDialog.setCancelable(false);
                                    final String newTitle = (String)parmam.get("text");
                                    MemeberListMgr.RenameSubCategory(snapLstSubCate.get(position),newTitle, CATE, new vvoidEvent() {
                                        @Override
                                        public void vvoidEvent(Map parmam) {
                                            progressDialog.dismiss();
                                            if((boolean)parmam.get("result")){
                                                int changed = (int)parmam.get("changed");
                                                snapLstSubCate.set(position,newTitle);
                                                notifyItemChanged(position);
                                                Toast.makeText(EditerSubCategory.this,"'"+oldTitle+"'->'"+newTitle+"', 총"+changed+"명의 사용자 데이터가 수정되었습니다.",Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(EditerSubCategory.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
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
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && editSubCateState == EditSubCateState.Reorde){
                    itemTouchHelper.startDrag(this);
                    swipeRefreshLayout.setEnabled(true);
                    return true;
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP && editSubCateState == EditSubCateState.Reorde) {
                    swipeRefreshLayout.setEnabled(false);
                    return true;
                }
                return false;
            }
        }
    }
}
