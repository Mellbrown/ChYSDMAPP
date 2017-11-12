package ml.dilot.chysdmapp.Test;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.Util;

public class TestCategoryMgr extends AppCompatActivity {

    TestCateAdpater cateAdpater;
    RecyclerView recCate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_category_mgr);

        recCate = findViewById(R.id.recCate);
        recCate.setHasFixedSize(true);
        recCate.setLayoutManager(new LinearLayoutManager(this));

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshSwipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Reload();
                
            }
        });

        MemeberListMgr.GetCategory(new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                if((boolean) parmam.get("result")){
                    List<String> snapLasCate = (List<String>)parmam.get("cate");
                    cateAdpater = new TestCateAdpater(snapLasCate,TestCategoryMgr.this);
                    recCate.setAdapter(cateAdpater);
                }else Toast.makeText(TestCategoryMgr.this,(String) parmam.get("message"), Toast.LENGTH_LONG).show();

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member_list_mgr, menu);
        return true;
    }

    public void Reload(){
        MemeberListMgr.GetCategory(new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                if((boolean) parmam.get("result")){
                    List<String> snapLasCate = (List<String>)parmam.get("cate");
                    cateAdpater.snapLstCate = snapLasCate;
                    cateAdpater.notifyDataSetChanged();
                }else Toast.makeText(TestCategoryMgr.this,(String) parmam.get("message"), Toast.LENGTH_LONG).show();
            }
        });
    }
    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.menu_add:
                Util.TextEditDialog(this, "새 카테고리", "새카테고리의 이름을 입력해주십시오.", new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        Toast.makeText(TestCategoryMgr.this,"응답은 " + parmam.get("result") + ", 값은 " + parmam.get("text"),Toast.LENGTH_LONG).show();
                        if((boolean)parmam.get("result")){
                            final ProgressDialog pd = ProgressDialog.show(TestCategoryMgr.this, "로딩중", "페이지 로딩 중입니다...");
                            MemeberListMgr.AddCategory((String) parmam.get("text"), new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    Reload();
                                    pd.dismiss();
                                }
                            });
                        }

                    }
                });
            case R.id.menu_remove:
            case R.id.menu_rename:
            case R.id.menu_reorder:
        }

        return super.onOptionsItemSelected(item);
    }
}

class TestCateAdpater extends RecyclerView.Adapter{

    List<String> snapLstCate;
    Context context;

    public TestCateAdpater(List<String> snapLstCate, Context context){
        this.snapLstCate = snapLstCate;
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rec_cate,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.txtCate.setText(snapLstCate.get(position));
    }

    @Override
    public int getItemCount() {
        return snapLstCate.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtCate;
        ImageButton btnDrag;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCate = itemView.findViewById(R.id.itemTxtCate);
            btnDrag = itemView.findViewById(R.id.btnReord);
        }
    }

}
