package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.UserInfo;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.Pages.ShowProfile;
import ml.dilot.chysdmapp.R;
import ml.dilot.chysdmapp.UtilPack.ConfirmCancleDialog;
import ml.dilot.chysdmapp.UtilPack.SelectCateDialog;
import ml.dilot.chysdmapp.UtilPack.Util;

public class EditerDeactiveMember extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    ImageButton fabConfirm;
    ImageButton fabCancle;
    CardView tip;
    TextView tipText;

    DeactivateMemberAdpater deactivateMemberAdpater;

    ArrayList<UserInfo> deactiveMembers;
    ArrayList<Integer> selected;

    ArrayList<String> cate;
    HashMap<String,ArrayList<String>> subCate;

    EditMode editMode;
    enum EditMode{
        View,
        Recover,
        Edite,
        Delete,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_deactive_member);

        recyclerView = findViewById(R.id.recview);
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);
        tip = findViewById(R.id.tip);
        tipText = findViewById(R.id.tip_text);
        tip.setVisibility(View.GONE);

        fabCancle.setVisibility(View.GONE);
        fabConfirm.setVisibility(View.GONE);
        fabCancle.setOnClickListener(this);
        fabConfirm.setOnClickListener(this);

        deactivateMemberAdpater = new DeactivateMemberAdpater();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(deactivateMemberAdpater);

        deactiveMembers = new ArrayList<>();
        selected = new ArrayList<>();

        editMode = EditMode.View;

        load(null);

        FirebaseDatabase.getInstance().getReference("회원명단/분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cate = (ArrayList<String>)dataSnapshot.child("대분류").getValue();
                subCate = (HashMap<String,ArrayList<String>>)dataSnapshot.child("소분류").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditerDeactiveMember.this,"분류 데이터를 로드하는데 실패하였습니다.",Toast.LENGTH_LONG).show();
            }
        });
    }
    interface LoadListener{ void onLoad();}
    public void load(final LoadListener loadListener){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("로딩중...");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("회원명단/명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<UserInfo> org = UserInfo.ArrayListUserinfoCaster((HashMap<String, HashMap<String,String>>)dataSnapshot.getValue());
                deactiveMembers.clear();
                for(UserInfo userInfo : org)
                    if("".equals(userInfo.category) && "".equals(userInfo.subCategory))
                        deactiveMembers.add(userInfo);
                Collections.sort(deactiveMembers);
                deactivateMemberAdpater.notifyDataSetChanged();
                progressDialog.dismiss();
                if(loadListener != null)loadListener.onLoad();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.confirmDialog(EditerDeactiveMember.this, "오류", "데이터를 로드하는데 실패하였습니다.", new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        finish();
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_deactive_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.act_recover:
                editMode = EditMode.Recover;
                selected.clear();
                deactivateMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tipText.setText("복구 : 0개 선택됨");
                break;
            case R.id.act_edit:
                editMode = EditMode.Edite;
                deactivateMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
            case R.id.act_delete:
                editMode = EditMode.Delete;
                deactivateMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (editMode){
            case View:
                finish();
                break;
            case Delete:
            case Edite:
            case Recover:
                editMode = EditMode.View;
                selected.clear();
                deactivateMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (editMode){
            case Recover:
                switch (view.getId()){
                    case R.id.fabConfirm:
                        SelectCateDialog selectCateDialog = new SelectCateDialog(this, selected.size(),cate, subCate, new vvoidEvent() {
                            @Override
                            public void vvoidEvent(Map parmam) {
                                boolean result = (boolean)parmam.get("result");
                                if(result){
                                    final String toCate = (String)parmam.get("selectedcate");
                                    String toSubCate = (String)parmam.get("selectedsubcate");

                                    ArrayList<String> tarUids = new ArrayList<>();
                                    for(int pos : selected) tarUids.add(deactiveMembers.get(pos).getUid());
                                    final ProgressDialog progressDialog = new ProgressDialog(EditerDeactiveMember.this);
                                    progressDialog.setTitle("회원 이동중...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    MemeberListMgr.MoveToMember(tarUids, toCate, toSubCate, new vvoidEvent() {
                                        @Override
                                        public void vvoidEvent(Map parmam) {
                                            progressDialog.dismiss();
                                            boolean result = (boolean)parmam.get("result");
                                            if(result){
                                                final int total = (int)parmam.get("total");
                                                final int success = (int)parmam.get("success");
                                                final int fail = (int)parmam.get("fail");
                                                load(new LoadListener() {
                                                    @Override
                                                    public void onLoad() {
                                                        editMode = EditMode.View;
                                                        selected.clear();
                                                        deactivateMemberAdpater.notifyDataSetChanged();
                                                        fabConfirm.setVisibility(View.GONE);
                                                        fabCancle.setVisibility(View.GONE);
                                                        tip.setVisibility(View.GONE);
                                                        Toast.makeText(EditerDeactiveMember.this,"총 " +total +"명의 회원 이동, 성공:"+success+", 실패:"+fail,Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }else {
                                                String message = (String)parmam.get("message");
                                                Toast.makeText(EditerDeactiveMember.this,message,Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        selectCateDialog.show();
                        break;
                    case R.id.fabCancle:
                        editMode = EditMode.View;
                        selected.clear();
                        deactivateMemberAdpater.notifyDataSetChanged();
                        fabConfirm.setVisibility(View.GONE);
                        fabCancle.setVisibility(View.GONE);
                        tip.setVisibility(View.GONE);
                        break;
                }
        }
    }

    public class DeactivateMemberAdpater extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MemberViewHolder memberViewHolder = (MemberViewHolder) holder;
            UserInfo userInfo = deactiveMembers.get(position);
            memberViewHolder.title.setText(userInfo.name);
            memberViewHolder.subtitle.setText("-"+userInfo.major+"("+userInfo.year.substring(2,4)+")");
            memberViewHolder.desc.setText(userInfo.position+"-"+userInfo.group);
            switch (editMode){
                case View:memberViewHolder.imgSate.setImageBitmap(null); break;
                case Edite: memberViewHolder.imgSate.setImageResource(R.drawable.ic_edit); break;
                case Delete: memberViewHolder.imgSate.setImageResource(R.drawable.ic_unselected); break;
                case Recover:
                    if(selected.contains(position)) memberViewHolder.imgSate.setImageResource(R.drawable.ic_selected);
                    else memberViewHolder.imgSate.setImageResource(R.drawable.ic_unselected); break;


            }
        }

        @Override
        public int getItemCount() {
            return deactiveMembers.size();
        }

        public class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imgProfile;
            TextView title;
            TextView subtitle;
            TextView desc;
            ImageView imgSate;

            public MemberViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                imgProfile = itemView.findViewById(R.id.profile_image);
                title = itemView.findViewById(R.id.txt_name);
                subtitle = itemView.findViewById(R.id.txt_sub_title);
                desc = itemView.findViewById(R.id.txt_position);
                imgSate = itemView.findViewById(R.id.img_state);
                imgSate.setImageBitmap(null);
            }

            @Override
            public void onClick(View view) {
                final int position = getLayoutPosition();
                switch (editMode){
                    case View:
                        Intent intent = new Intent(EditerDeactiveMember.this, ShowProfile.class);
                        intent.putExtra("user",deactiveMembers.get(position));
                        startActivity(intent);
                        break;
                    case Edite:
                        intent = new Intent(EditerDeactiveMember.this, EditerProfile.class);
                        intent.putExtra("user",deactiveMembers.get(position));
                        startActivity(intent);
                        break;
                    case Delete:
                        final UserInfo userInfo = deactiveMembers.get(position);
                        Util.ConfirmCancleDialog(EditerDeactiveMember.this, "회원 데이터 삭제", "정말로 '" + userInfo.name + "'님의 모든 데이터를 삭제하겠습니까?\n(삭제된 데이터는 복구할 수 없습니다.)", new ConfirmCancleDialog.ConfirmCancleListener() {
                            @Override
                            public void onConfrim() {
                                final ProgressDialog progressDialog = new ProgressDialog(EditerDeactiveMember.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("삭제중...");
                                progressDialog.show();
                                MemeberListMgr.RemoveMember(userInfo.getUid(), new vvoidEvent() {
                                    @Override
                                    public void vvoidEvent(Map parmam) {
                                        progressDialog.dismiss();
                                        boolean result = (boolean) parmam.get("result");
                                        if(result){
                                            editMode = EditMode.View;
                                            load(null);
                                        } else {
                                            String message = (String) parmam.get("message");
                                            Toast.makeText(EditerDeactiveMember.this,message,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancle() {

                            }
                        });
                        break;
                    case Recover:
                        if(selected.contains(position)) selected.remove((Object)position);
                        else selected.add(position);
                        notifyItemChanged(position);
                        tipText.setText("복구 : " + selected.size() + "개 선택됨");
                        break;
                }
            }
        }
    }
}
