package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class EditerSubject extends AppCompatActivity implements View.OnClickListener{

    //리사이클러뷰와 그의 어뎁터
    RecyclerView editSubjRec;
    EditSubjAdpater editSubjAdpater;

    //레이아웃의 각종 UI들
    SwipeRefreshLayout swipeRefreshLayout; //당기면 리로딩

    ImageButton fabConfirm;
    ImageButton fabCancle;

    CardView tip;
    TextView tipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_subject);

        //각종 UI 로드
        swipeRefreshLayout = findViewById(R.id.edit_subj_refresh);
        editSubjRec = findViewById(R.id.edit_subj_recview);
        editSubjAdpater = new EditSubjAdpater(this);
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);
        tip = findViewById(R.id.edit_subj_tip);
        tipText = findViewById(R.id.edit_subj_tip_text);

        //숨길 UI 숨기기
        tip.setVisibility(View.GONE);
        fabCancle.setVisibility(View.GONE);
        fabConfirm.setVisibility(View.GONE);

        editSubjRec.setHasFixedSize(true);
        editSubjRec.setLayoutManager(new LinearLayoutManager(this));
        editSubjRec.setAdapter(editSubjAdpater);
        fabConfirm.setOnClickListener(this);
        fabCancle.setOnClickListener(this);

        //당기면 리로딩
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MemeberListMgr.GetMajor(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        swipeRefreshLayout.setRefreshing(false);
                        boolean result = (boolean)parmam.get("result");
                        if(result){
                            List<String> subj = (List<String>) parmam.get("snapMajor");
                            Collections.sort(subj);
                            editSubjAdpater.snapLstSubj = subj;
                            editSubjAdpater.notifyDataSetChanged();
                        }else{
                            String message = (String)parmam.get("message");
                            Toast.makeText(EditerSubject.this,message,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        //최초 로딩
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오는 중입니다.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MemeberListMgr.GetMajor(new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                progressDialog.dismiss();
                boolean result = (boolean)parmam.get("result");
                if(result){
                    List<String> subj = (List<String>) parmam.get("snapMajor");
                    editSubjAdpater.snapLstSubj = subj;
                    editSubjAdpater.notifyDataSetChanged();
                }else{
                    String message = (String)parmam.get("message");
                    Toast.makeText(EditerSubject.this,message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // 확인 취소 클릭
    public void onClick(View view){
        switch (editSubjAdpater.editSubjState){
            case Delete:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle( "삭제중...");
                        progressDialog.show();
                        String subjs[] = new String[editSubjAdpater.lstSelected.size()];
                        for(int i = 0; editSubjAdpater.lstSelected.size() > i; i++)
                            subjs[i] = editSubjAdpater.snapLstSubj.get(editSubjAdpater.lstSelected.get(i));
                        MemeberListMgr.RemoveMajor(subjs, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    StringBuilder message = new StringBuilder();
                                    int cnt_success = (int)parmam.get("cnt_success");
                                    List<String> lst_success = (List<String>)parmam.get("lst_success");
                                    for(String subj : lst_success){
                                        int tar = editSubjAdpater.snapLstSubj.indexOf(subj);
                                        editSubjAdpater.snapLstSubj.remove(tar);
                                        editSubjAdpater.notifyItemRemoved(tar);
                                    }
                                    int cnt_fail = (int)parmam.get("cnt_fail");
                                    message.append("성공 : "  + cnt_success + ", 실패 : " + cnt_fail);
                                    if(cnt_fail != 0){
                                        Map <String,String> lst_fail = (Map<String,String>)parmam.get("lst_fail");
                                        for(String title : lst_fail.keySet())
                                            message.append("\n " + title + " : " + lst_fail.get(title));
                                    }
                                    editSubjAdpater.editSubjState = EditSubjState.View;
                                    editSubjAdpater.notifyDataSetChanged();
                                    tip.setVisibility(View.GONE);
                                    fabCancle.setVisibility(View.GONE);
                                    fabConfirm.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    Toast.makeText(EditerSubject.this,message,Toast.LENGTH_LONG).show();
                                } else Toast.makeText(EditerSubject.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    case R.id.fabCancle:
                        editSubjAdpater.editSubjState = EditSubjState.View;
                        editSubjAdpater.notifyDataSetChanged();
                        tip.setVisibility(View.GONE);
                        fabCancle.setVisibility(View.GONE);
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
            case Rename:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        editSubjAdpater.editSubjState = EditSubjState.View;
                        editSubjAdpater.notifyDataSetChanged();
                        fabConfirm.setVisibility(View.GONE);
                        break;
                }break;
        }
    }

    //액션 메뉴 전개
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_subject, menu);
        return true;
    }

    //액션 메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.act_edit_cate_add:
                if(editSubjAdpater.editSubjState != EditSubjState.View){
                    editSubjAdpater.editSubjState = EditSubjState.View;
                    fabCancle.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    fabConfirm.setVisibility(View.GONE);
                    editSubjAdpater.notifyDataSetChanged();
                }
                Util.TextEditDialog(this, "새 학과", "새 학과의 이름을 입력해주십시오.", new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        boolean request = (boolean)parmam.get("result");
                        final String text = (String)parmam.get("text");
                        if(request){
                            final ProgressDialog progressDialog = new ProgressDialog(EditerSubject.this);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            MemeberListMgr.AddMajor(text, new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    progressDialog.dismiss();
                                    boolean success = (Boolean) parmam.get("result");
                                    if(success) {
                                        editSubjAdpater.snapLstSubj.add(text);
                                        Collections.sort(editSubjAdpater.snapLstSubj);
                                        editSubjAdpater.notifyDataSetChanged();
                                        Toast.makeText(EditerSubject.this, "'" + text + "' 항목이 추가되었습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        String message = (String) parmam.get("message");
                                        Toast.makeText(EditerSubject.this, message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.act_edit_cate_remove:
                if(editSubjAdpater.editSubjState != EditSubjState.Delete){
                    editSubjAdpater.editSubjState = EditSubjState.Delete;
                    editSubjAdpater.lstSelected.clear();
                    editSubjAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tipText.setText("삭제 : 0개 선택됨");
                break;
            case R.id.act_edit_cate_rename:
                if(editSubjAdpater.editSubjState != EditSubjState.Rename){
                    editSubjAdpater.editSubjState = EditSubjState.Rename;
                    editSubjAdpater.notifyDataSetChanged();
                }
                fabConfirm.setVisibility(View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //뷰 편집 상태
    enum EditSubjState{
        View,
        Delete,
        Rename
    }

    //어뎁터 뷰
    class EditSubjAdpater extends RecyclerView.Adapter{
        List<String> snapLstSubj;
        List<Integer> lstSelected;
        EditSubjState editSubjState = EditSubjState.View;

        public EditSubjAdpater(Context context){
            this.snapLstSubj = new ArrayList<>();lstSelected = new ArrayList<>();
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_cate,parent,false);
            EditSubjViewHolder holder = new EditSubjViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EditSubjViewHolder vh = (EditSubjViewHolder) holder;
            vh.txtCate.setText(snapLstSubj.get(position));
            switch (editSubjState){
                case View: vh.imgState.setImageBitmap(null);break;
                case Delete:
                    if(lstSelected.contains(position)) vh.imgState.setImageResource(R.drawable.ic_selected);
                    else vh.imgState.setImageResource(R.drawable.ic_unselected); break;
                case Rename: vh.imgState.setImageResource(R.drawable.ic_edit);
            }
        }

        @Override
        public int getItemCount() {
            return snapLstSubj.size();
        }

        public class EditSubjViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView txtCate;
            ImageView imgState;

            public EditSubjViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                txtCate = itemView.findViewById(R.id.edit_cate_item_title);
                imgState = itemView.findViewById(R.id.edit_cate_item_state);
            }

            @Override
            public void onClick(View view) {
                final int position = getLayoutPosition();
                switch (editSubjState){
                    case View:
//                        Intent intent = new Intent(this,EditerSubCategory.class);
//                        intent.setAction(snapLstSubj.get(position));
//                        startActivity(intent);
                        break;
                    case Delete:
                        if(lstSelected.contains(position)) lstSelected.remove((Object)position);
                        else lstSelected.add(position);
                        notifyItemChanged(position);
                        tipText.setText("삭제 : " + lstSelected.size() + "개 선택됨");
                        break;
                    case Rename:
                        final String oldTitle = snapLstSubj.get(position);
                        Util.TextEditDialog(EditerSubject.this, "새 이름", "'" +  oldTitle + "'에 대한 새로운 이름을 입력해주세요", new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                if((boolean)parmam.get("result")){
                                    final ProgressDialog progressDialog = new ProgressDialog(EditerSubject.this);
                                    progressDialog.setCancelable(false);
                                    final String newTitle = (String)parmam.get("text");
                                    MemeberListMgr.RenameMajor(snapLstSubj.get(position), newTitle , new vvoidEvent() {
                                        @Override
                                        public void vvoidEvent(Map parmam) {
                                            progressDialog.dismiss();
                                            if((boolean)parmam.get("result")){
                                                int changed = (int)parmam.get("changed");
                                                snapLstSubj.set(position,newTitle);
                                                notifyItemChanged(position);
                                                Toast.makeText(EditerSubject.this,"'"+oldTitle+"'->'"+newTitle+"', 총"+changed+"명의 사용자 데이터가 수정되었습니다.",Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(EditerSubject.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            }
                        });
                        break;
                }
            }
        }
    }
}
