package ml.dilot.chysdmapp.Pages;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.UserInfo;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.Util;

public class ShowMember extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;

    ArrayList<String> cate;
    HashMap<String,ArrayList<String>> subCate;
    HashMap<String,UserInfo> member;

    String selectedTitle;
    ArrayList<UserInfo> filteredMember = new ArrayList<>();

    ShowMemberMode showMemberMode = ShowMemberMode.Cate;
    enum  ShowMemberMode{
        Cate,
        SubCate,
        Member,
        Search
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_member);

        switch (getIntent().getAction()){
            case "selCate":
                break;
            case "selSubCate":
                break;
            case "search":
                break;
            default:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("목록을 불러오는 중입니다...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final int load[] = new int[1];
                final boolean fail[] = new boolean[1];
                load[0] = 3;
                fail[0] = true;
                FirebaseDatabase.getInstance().getReference("회원명단/명단").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        member = (HashMap<String, UserInfo>) dataSnapshot.getValue();
                        if(--load[0] <= 0){
                            progressDialog.dismiss();
                            if(!fail[0])Util.confirmDialog(ShowMember.this, "오류", "목록을 불러올 수 없습니다.", new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    finish();
                                }
                            });else OnLoaded();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        fail[0] = false;
                        if(--load[0] <= 0){
                            progressDialog.dismiss();
                            if(!fail[0])Util.confirmDialog(ShowMember.this, "오류", "목록을 불러올 수 없습니다.", new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    finish();
                                }
                            });else OnLoaded();
                        }
                    }
                });
                FirebaseDatabase.getInstance().getReference("회원명단/분류").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cate = (ArrayList<String>)dataSnapshot.child("대분류").getValue();
                        subCate = (HashMap<String,ArrayList<String>>)dataSnapshot.child("소분류").getValue();
                        if(--load[0] <= 0){
                            progressDialog.dismiss();
                            if(!fail[0])Util.confirmDialog(ShowMember.this, "오류", "목록을 불러올 수 없습니다.", new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    finish();
                                }
                            }); else OnLoaded();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        fail[0] = false;
                        if(--load[0] <= 0){
                            progressDialog.dismiss();
                            if(!fail[0])Util.confirmDialog(ShowMember.this, "오류", "목록을 불러올 수 없습니다.", new vvoidEvent() {
                                @Override
                                public void vvoidEvent(Map parmam) {
                                    finish();
                                }
                            });else OnLoaded();
                        }
                    }
                });
                break;

        }
        searchView = findViewById(R.id.searchbar);
        recyclerView = findViewById(R.id.show_member_rec);


    }

    public void OnLoaded(){
        searchView = findViewById(R.id.searchbar);
        recyclerView = findViewById(R.id.show_member_rec);
    }

    class ShowMemberAdpater extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case 0: return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_cate,parent,false)); 
                case 1: return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member,parent,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (showMemberMode){
                case Cate:
                    TitleViewHolder cateView = (TitleViewHolder)holder;
                    cateView.txtCate.setText(cate.get(position));
                    break;
                case SubCate:
                    TitleViewHolder subCateView = (TitleViewHolder)holder;
                    subCateView.txtCate.setText(subCate.get(selectedTitle).toString());
                    break;
                case Member:
                    MemberViewHolder memberView = (MemberViewHolder)holder;
                    memberView.title.setText(filteredMember.get(position).name);
                    break;
                case Search:
                    MemberViewHolder resultView = (MemberViewHolder)holder;
                    resultView.title.setText(filteredMember.get(position).name);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int i = 0;
            switch (showMemberMode){
                case Cate:
                case SubCate:
                    i = 0;
                    break;
                case Member:
                case Search:
                    i = 1;
                    break;
            }

            return super.getItemViewType(i);
        }

        @Override
        public int getItemCount() {
            switch (showMemberMode){
                case Cate: return cate.size();
                case SubCate:
            }
            return 0;
        }
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProfile;
        TextView title;
        TextView subtitle;
        TextView desc;
        ImageView imgSate;

        public MemberViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.profile_image);
            title = itemView.findViewById(R.id.txt_name);
            subtitle = itemView.findViewById(R.id.txt_sub_title);
            desc = itemView.findViewById(R.id.txt_position);
            imgSate = itemView.findViewById(R.id.img_state);
            imgSate.setImageBitmap(null);
        }
    }
    public class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView txtCate;
        ImageView imgState;

        public TitleViewHolder(View itemView) {
            super(itemView);
            txtCate = itemView.findViewById(R.id.edit_cate_item_title);
            imgState = itemView.findViewById(R.id.edit_cate_item_state);
            imgState.setImageBitmap(null);
        }
    }
}
