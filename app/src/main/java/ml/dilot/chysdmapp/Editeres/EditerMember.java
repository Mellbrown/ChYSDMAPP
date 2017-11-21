package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

public class EditerMember extends AppCompatActivity implements View.OnClickListener {

    SearchView searchView;
    RecyclerView recyclerView;
    EditMemberAdpater editMemberAdpater;
    ActionBar actionBar;

    ImageButton fabConfirm;
    ImageButton fabCancle;

    CardView tip;
    TextView tipText;

    ArrayList<String> cate;
    HashMap<String,ArrayList<String>> subCate;
    HashMap<String,UserInfo> member;

    String selectedTitle;
    String selectedSubTitle;
    ArrayList<UserInfo> filteredMember = new ArrayList<>();
    ArrayList<Integer> lstSelected = new ArrayList<>();

    EditerMemberMode editerMemberMode = EditerMemberMode.Cate;

    @Override
    public void onClick(View view) {
        switch (editMode){
            case Move:
            switch (view.getId()){
                case R.id.fabConfirm:
                    SelectCateDialog selectCateDialog = new SelectCateDialog(this, lstSelected.size(),cate, subCate, new vvoidEvent() {
                        @Override
                        public void vvoidEvent(Map parmam) {
                            boolean result = (boolean)parmam.get("result");
                            if(result){
                                final String toCate = (String)parmam.get("selectedcate");
                                String toSubCate = (String)parmam.get("selectedsubcate");
                                Log.d("toCate",toCate);
                                Log.d("toSubCate",toSubCate);
                                ArrayList<String> tarUids = new ArrayList<>();
                                for(int pos : lstSelected) tarUids.add(filteredMember.get(pos).getUid());
                                final ProgressDialog progressDialog = new ProgressDialog(EditerMember.this);
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
                                            LoadData(new vvoidEvent() {
                                                @Override
                                                public void vvoidEvent(Map parmam) {
                                                    fabCancle.setVisibility(View.GONE);
                                                    fabConfirm.setVisibility(View.GONE);
                                                    tip.setVisibility(View.GONE);
                                                    lstSelected.clear();
                                                    filteredMember.clear();
                                                    for(String key : member.keySet()){
                                                        UserInfo mem = member.get(key);
                                                        if(selectedTitle.equals(mem.category) && selectedSubTitle.equals(mem.subCategory))
                                                            filteredMember.add(mem);
                                                    }
                                                    Collections.sort(filteredMember);
                                                    editMode = EditMode.View;
                                                    editMemberAdpater.notifyDataSetChanged();
                                                    Toast.makeText(EditerMember.this,"총 " +total +"명의 회원 이동, 성공:"+success+", 실패:"+fail,Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }else {
                                            String message = (String)parmam.get("message");
                                            Toast.makeText(EditerMember.this,message,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    selectCateDialog.show();
                    break;
                case R.id.fabCancle:
                    fabCancle.setVisibility(View.GONE);
                    fabConfirm.setVisibility(View.GONE);
                    tip.setVisibility(View.GONE);
                    lstSelected.clear();
                    editMode = EditMode.View;
                    editMemberAdpater.notifyDataSetChanged();
                    break;
            }
            break;
        default:
            fabCancle.setVisibility(View.GONE);
            fabConfirm.setVisibility(View.GONE);
            tip.setVisibility(View.GONE);
            break;
        }
    }

    enum  EditerMemberMode{
        Cate,
        SubCate,
        Member,
        Search
    }
    EditMode editMode = EditMode.View;
    enum EditMode{
        View,
        Move,
        ProfileEdit,
        Deactivate,
        AddManager,
        AddBoarder
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_member);

        searchView = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.edit_member_rec);
        actionBar = getSupportActionBar();
        fabConfirm = findViewById(R.id.fabConfirm);
        fabCancle = findViewById(R.id.fabCancle);
        tip = findViewById(R.id.tip);
        tipText = findViewById(R.id.tip_text);
        tip.setVisibility(View.GONE);
        fabCancle.setVisibility(View.GONE);
        fabConfirm.setVisibility(View.GONE);
        fabCancle.setOnClickListener(this);
        fabConfirm.setOnClickListener(this);

        LoadData(new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                boolean result = !(boolean)parmam.get("result");
                if(result){
                    editMemberAdpater = new EditMemberAdpater();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(EditerMember.this));
                    recyclerView.setAdapter(editMemberAdpater);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            actionBar.setTitle("'"+s+"'검색결과");
                            filteredMember.clear();
                            menuItemEnable(true);
                            for(String key : member.keySet()){
                                UserInfo mem = member.get(key);
                                if(
                                        mem.name.matches(".*"+s+".*")||
                                                mem.category.matches(".*"+s+".*")||
                                                mem.subCategory.matches(".*"+s+".*")||
                                                mem.phoneNumber.matches(".*"+s+".*")||
                                                mem.homeNumber.matches(".*"+s+".*")||
                                                mem.major.matches(".*"+s+".*")||
                                                mem.group.matches(".*"+s+".*")||
                                                mem.position.matches(".*"+s+".*")
                                        )
                                    filteredMember.add(mem);
                            }
                            Collections.sort(filteredMember);
                            editerMemberMode = EditerMemberMode.Search;
                            editMemberAdpater.notifyDataSetChanged();
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            return false;
                        }
                    });
                } else finish();
            }
        });
    }

    //액션 메뉴 전개
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_member, menu);
        for(int i = 1; menu.size() > i;  i++)
            menuItems.add(menu.getItem(i));
        menuItemEnable(false);
        return true;
    }
    public void menuItemEnable(boolean enable){
        for(MenuItem menuItem : menuItems)
            menuItem.setEnabled(enable);
    }

    //액션 메뉴 클릭
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.act_add_member:
                editMode = EditMode.View;
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                switch (editerMemberMode){
                    case Cate:
                    case Search:
                        Util.StartAddMemeber(this,null,null);
                        break;
                    case SubCate:
                        Util.StartAddMemeber(this,selectedTitle,null);
                        break;
                    case Member:
                        Util.StartAddMemeber(this,selectedTitle,selectedSubTitle);
                        break;
                }
                break;
            case R.id.act_move_member:
                editMode = EditMode.Move;
                lstSelected.clear();
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.VISIBLE);
                fabCancle.setVisibility(View.VISIBLE);
                tip.setVisibility(View.VISIBLE);
                tipText.setText("이동 : 0개 선택됨");
                break;
            case R.id.act_edit_member:
                editMode = EditMode.ProfileEdit;
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
            case R.id.act_deact_member:
                editMode = EditMode.Deactivate;
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
            case R.id.act_add_manager:
                editMode = EditMode.AddManager;
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
            case R.id.act_add_boarder:
                editMode = EditMode.AddBoarder;
                editMemberAdpater.notifyDataSetChanged();
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
                switch (editerMemberMode) {
                    case Cate:
                        finish();
                        break;
                    case SubCate:
                        selectedTitle = null;
                        actionBar.setTitle("회원 명단 수정");
                        editerMemberMode = EditerMemberMode.Cate;
                        editMemberAdpater.notifyDataSetChanged();
                        break;
                    case Member:
                        menuItemEnable(false);
                        actionBar.setTitle(selectedTitle+">");
                        editerMemberMode = EditerMemberMode.SubCate;
                        filteredMember.clear();
                        editMemberAdpater.notifyDataSetChanged();
                        break;
                    case Search:
                        menuItemEnable(false);
                        actionBar.setTitle("회원명단 수정");
                        editerMemberMode = EditerMemberMode.Cate;
                        filteredMember.clear();
                        editMemberAdpater.notifyDataSetChanged();
                        break;
                }break;
            case Move:
            case AddBoarder:
            case AddManager:
            case Deactivate:
            case ProfileEdit:
                editMode = EditMode.View;
                editMemberAdpater.notifyDataSetChanged();
                fabConfirm.setVisibility(View.GONE);
                fabCancle.setVisibility(View.GONE);
                tip.setVisibility(View.GONE);
                break;
        }

    }
    class EditMemberAdpater extends RecyclerView.Adapter{

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
            switch (editerMemberMode){
                case Cate:
                    TitleViewHolder cateView = (TitleViewHolder)holder;
                    String strCate = cate.get(position);
                    Integer cntCate = titleCounter.get(strCate);
                    cntCate = cntCate == null ? 0 : cntCate;
                    cateView.txtCate.setText( strCate + "(" + cntCate +")");
                    break;
                case SubCate:
                    TitleViewHolder subCateView = (TitleViewHolder)holder;
                    String strSubCate = subCate.get(selectedTitle).get(position);
                    Integer cntSubCate = titleCounter.get(selectedTitle+"-"+strSubCate);
                    cntSubCate = cntSubCate == null ? 0 : cntSubCate;
                    subCateView.txtCate.setText(strSubCate + "(" + cntSubCate +")");
                    break;
                case Member:
                case Search:
                    MemberViewHolder memberView = (MemberViewHolder)holder;
                    UserInfo userInfo = filteredMember.get(position);
                    memberView.title.setText(userInfo.name);
                    memberView.subtitle.setText("-" +userInfo.major+"("+userInfo.year.substring(2,4)+")");
                    memberView.desc.setText(userInfo.position + "-" +userInfo.group);
                    switch (editMode){
                        case View: memberView.imgSate.setImageBitmap(null); break;
                        case Move:
                            if(lstSelected.contains(position)) memberView.imgSate.setImageResource(R.drawable.ic_selected);
                            else memberView.imgSate.setImageResource(R.drawable.ic_unselected); break;
                        case ProfileEdit: memberView.imgSate.setImageResource(R.drawable.ic_edit); break;
                        case Deactivate: memberView.imgSate.setImageResource(R.drawable.ic_unselected); break;
                        case AddManager: memberView.imgSate.setImageResource(R.drawable.ic_unselected); break;
                        case AddBoarder:memberView.imgSate.setImageResource(R.drawable.ic_unselected); break;
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int i = 0;
            switch (editerMemberMode){
                case Cate:
                case SubCate:
                    i = 0;
                    break;
                case Member:
                case Search:
                    i = 1;
                    break;
            }

            return i;
        }

        @Override
        public int getItemCount() {
            switch (editerMemberMode){
                case Cate: return cate.size();
                case SubCate: return subCate.get(selectedTitle).size();
                case Member:
                case Search:return filteredMember.size();
            }
            return 0;
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
                        Intent intent = new Intent(EditerMember.this, ShowProfile.class);
                        intent.putExtra("user",filteredMember.get(position));
                        startActivity(intent);
                        break;
                    case Move:
                        if(lstSelected.contains(position)) lstSelected.remove((Object)position);
                        else lstSelected.add(position);
                        notifyItemChanged(position);
                        tipText.setText("이동 : " + lstSelected.size() + "개 선택됨");
                        break;
                    case AddBoarder:
                        ////////////////////////////////////////////////////////////////////////////////
                        break;
                    case AddManager:
                        ///////////////////////////////////////////////////////////////////////////////
                        break;
                    case Deactivate:
                        UserInfo user = filteredMember.get(position);
                        Util.ConfirmCancleDialog(EditerMember.this, "사용자 비활성화", "'" + user.name + "'님을 정말로 비활성화 하시겠습니까?", new ConfirmCancleDialog.ConfirmCancleListener() {
                            @Override
                            public void onConfrim() {
                                final ProgressDialog progressDialog = new ProgressDialog(EditerMember.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setTitle("해당 사용자 비활성화 중...");
                                progressDialog.show();
                                ArrayList<String> tarUids = new ArrayList<>();
                                tarUids.add(filteredMember.get(position).getUid());
                                MemeberListMgr.DeactivateMember(tarUids, new vvoidEvent() {
                                    @Override
                                    public void vvoidEvent(Map parmam) {
                                        progressDialog.dismiss();
                                        boolean result = (boolean)parmam.get("result");
                                        if(result){
                                            int total = (int)parmam.get("total");
                                            int success = (int)parmam.get("success");
                                            if(success == 1){
                                                LoadData(new vvoidEvent() {
                                                    @Override
                                                    public void vvoidEvent(Map parmam) {
                                                        filteredMember.clear();
                                                        for(String key : member.keySet()){
                                                            UserInfo mem = member.get(key);
                                                            if(mem.category.equals(selectedTitle) && mem.subCategory.equals(selectedSubTitle))
                                                                filteredMember.add(mem);
                                                        }
                                                        Collections.sort(filteredMember);
                                                        editMemberAdpater.notifyDataSetChanged();
                                                        Toast.makeText(EditerMember.this,"1명의 사용자 비활성화",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(EditerMember.this,"비활성화 실패",Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            String message = (String)parmam.get("message");
                                            Toast.makeText(EditerMember.this,message,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            @Override
                            public void onCancle() {
                                Toast.makeText(EditerMember.this,"?",Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case ProfileEdit:
                        intent = new Intent(EditerMember.this, EditerProfile.class);
                        intent.putExtra("user",filteredMember.get(position));
                        startActivity(intent);
                        break;
                }
            }
        }
        public class TitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txtCate;
            ImageView imgState;

            public TitleViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                txtCate = itemView.findViewById(R.id.edit_cate_item_title);
                imgState = itemView.findViewById(R.id.edit_cate_item_state);
                imgState.setImageBitmap(null);
            }

            @Override
            public void onClick(View view) {
                int position = getLayoutPosition();
                switch (editerMemberMode){
                    case Cate:
                        selectedTitle = cate.get(position);
                        editerMemberMode = editerMemberMode.SubCate;
                        menuItemEnable(false);
                        actionBar.setTitle(selectedTitle + ">");
                        notifyDataSetChanged();
                        break;
                    case SubCate:
                        menuItemEnable(true);
                        selectedSubTitle = subCate.get(selectedTitle).get(position);
                        actionBar.setTitle(selectedTitle + ">" + selectedSubTitle + ">");
                        filteredMember.clear();
                        for(String key : member.keySet()){
                            UserInfo mem = member.get(key);
                            if(mem.category.equals(selectedTitle) && mem.subCategory.equals(selectedSubTitle))
                                filteredMember.add(mem);
                        }
                        Collections.sort(filteredMember);
                        editerMemberMode = editerMemberMode.Member;
                        notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    public void LoadData(final vvoidEvent onload){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오는 중입니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final int load[] = new int[1];
        final boolean fail[] = new boolean[1];
        load[0] = 2;
        fail[0] = true;
        FirebaseDatabase.getInstance().getReference("회원명단/명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                member = UserInfo.HashMapUserInfoCaster((HashMap<String, HashMap<String,String>>) dataSnapshot.getValue());
                HashMap<String,Object> param = new HashMap();
                refreshTitleCounter();
                param.put("result",false);
                if(--load[0] <= 0){
                    progressDialog.dismiss();
                    if(!fail[0]) Util.confirmDialog(EditerMember.this, "오류", "목록을 불러올 수 없습니다.", onload);

                    else onload.vvoidEvent(param);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                HashMap<String,Object> param = new HashMap();
                param.put("result",false);
                fail[0] = false;
                if(load[0] <= 0){
                    progressDialog.dismiss();
                    if(!fail[0])Util.confirmDialog(EditerMember.this, "오류", "목록을 불러올 수 없습니다.", onload);
                    else onload.vvoidEvent(param);
                }
            }
        });
        FirebaseDatabase.getInstance().getReference("회원명단/분류").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> param = new HashMap();
                param.put("result",false);
                cate = (ArrayList<String>)dataSnapshot.child("대분류").getValue();
                subCate = (HashMap<String,ArrayList<String>>)dataSnapshot.child("소분류").getValue();
                if(--load[0] <= 0){
                    progressDialog.dismiss();
                    if(!fail[0])Util.confirmDialog(EditerMember.this, "오류", "목록을 불러올 수 없습니다.", onload);
                    else onload.vvoidEvent(param);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                HashMap<String,Object> param = new HashMap();
                param.put("result",false);
                fail[0] = false;
                if(--load[0] <= 0){
                    progressDialog.dismiss();
                    if(!fail[0])Util.confirmDialog(EditerMember.this, "오류", "목록을 불러올 수 없습니다.", onload);
                    else onload.vvoidEvent(param);
                }
            }
        });
    }

    HashMap<String,Integer> titleCounter = new HashMap<>();
    public void refreshTitleCounter(){
        titleCounter.clear();
        for(UserInfo userInfo : member.values()){
            String cate = userInfo.category;
            String cate_subcate = userInfo.category + "-" + userInfo.subCategory;
            Integer cnt_cate = titleCounter.get(cate);
            Integer cnt_cate_subcate = titleCounter.get(cate_subcate);
            cnt_cate = cnt_cate == null ? 1 : cnt_cate + 1;
            cnt_cate_subcate = cnt_cate_subcate == null ? 1 : cnt_cate_subcate + 1;
            titleCounter.put(cate,cnt_cate);
            titleCounter.put(cate_subcate, cnt_cate_subcate);
        }
    }
}
