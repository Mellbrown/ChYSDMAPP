package ml.dilot.chysdmapp.Editeres;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import ml.dilot.chysdmapp.DataMgrSet.UserInfo;
import ml.dilot.chysdmapp.R;

public class EditerBoarder extends AppCompatActivity {

    RecyclerView recyclerView;
    EditBoarderAdapter editBoarderAdapter;

    TextView textYear;
    FloatingActionButton btnPrevYear;
    FloatingActionButton btnNextYear;

    HashMap<Long,HashMap<String,HashMap<String,HashMap<String,String>>>> rawSnapBoader;
    HashMap<Long,ArrayList<ItemBoarder>> snapBoader;
    ArrayList<String> snapPosition;
    Long currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editer_boarder);

    }

    interface LoadListener{ void onload(boolean result); }
    void load(final LoadListener loadListener){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("로딩중");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("임원명단").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                snapPosition.clear();
                rawSnapBoader.clear();
                snapBoader.clear();
                if(!"".equals(dataSnapshot.child("분류/직위").getValue())) {
                    snapPosition = (ArrayList<String>)dataSnapshot.child("분류/직위").getValue();
                }
                if(!"".equals(dataSnapshot.child("명단/년도별").getValue())){
                    rawSnapBoader = (HashMap<Long,HashMap<String,HashMap<String,HashMap<String,String>>>>)dataSnapshot.child("명단/년도별").getValue();
                    snapBoader = ItemBoarderesCaster(rawSnapBoader,snapPosition);
                }
                if(!"".equals(dataSnapshot.child("명단/기본값").getValue())) {
                    currentYear = (Long)dataSnapshot.child("명단/기본값").getValue();
                } else {
                    ArrayList<Long> longs = new ArrayList<>();
                    longs.addAll(snapBoader.keySet());
                    Collections.sort(longs);
                    if(longs.size() == 0) currentYear = null;
                    else currentYear = longs.get(longs.size()-1);
                }
                editBoarderAdapter.notifyDataSetChanged();
                loadListener.onload(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(EditerBoarder.this,"로드하는데 실패하였습니다.",Toast.LENGTH_LONG).show();
                loadListener.onload(false);
            }
        });
    }

    class EditBoarderAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case DIVIDER: return new DivideViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_divider,parent,false));
                case MEMBER: return new MemberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member,parent,false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (snapBoader.get(currentYear).get(position).type){
                case DIVIDER:
                    DivideViewHolder divideViewHolder = (DivideViewHolder)holder;
                    break;
                case MEMBER:

                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return snapBoader.get(currentYear).get(position).type;
        }

        @Override
        public int getItemCount() {
            return snapBoader.get(currentYear).size();
        }

        class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            }
        }

        class DivideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView txtDivider;
            public DivideViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                txtDivider = itemView.findViewById(R.id.txt_divider);
            }

            @Override
            public void onClick(View view) {

            }
        }
    }

    public static final int DIVIDER = 0;
    public static final int MEMBER = 1;
    class ItemBoarder{
        int type;
        String divider;
        UserInfo userInfo;

        public ItemBoarder(String divider){
            type = DIVIDER;
            this.divider = divider;
        }

        public ItemBoarder(UserInfo userInfo){
            type = MEMBER;
            this.userInfo = userInfo;
        }
    }
    public HashMap<Long,ArrayList<ItemBoarder>> ItemBoarderesCaster (HashMap<Long,HashMap<String,HashMap<String,HashMap<String,String>>>> rawSnapBoader,ArrayList<String> snapPosition){
        HashMap<Long,ArrayList<ItemBoarder>> casted = new HashMap<>();
        for(Long key : rawSnapBoader.keySet()){
            ArrayList<ItemBoarder> itemBoarders = new ArrayList<>();
            for(String position : snapPosition){
                itemBoarders.add(new ItemBoarder(position));
                ArrayList<UserInfo> userInfos = UserInfo.ArrayListUserinfoCaster(rawSnapBoader.get(key).get(position));
                for(UserInfo userInfo : userInfos) itemBoarders.add(new ItemBoarder(userInfo));
            }
        }
        return casted;
    }
}
