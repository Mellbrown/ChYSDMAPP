package ml.dilot.chysdmapp.Editeres;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.dilot.chysdmapp.DataMgrSet.MemeberListMgr;
import ml.dilot.chysdmapp.DataMgrSet.UserInfo;
import ml.dilot.chysdmapp.DataMgrSet.vvoidEvent;
import ml.dilot.chysdmapp.R;

public class AddMember extends AppCompatActivity {

    CircleImageView imgProfile;
    FloatingActionButton btn_change;
    EditText editName;
    EditText editYear;
    Spinner dropSubject;
    EditText editphone;
    EditText editZipNum;
    Spinner dropCate;
    Spinner dropSubCate;
    EditText editGroup;
    EditText editPosition;

    ArrayList<String> subject;
    ArrayList<String> cate;
    HashMap<String, ArrayList<String>> subcate;

    Uri imgCaptureUri;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Bundle bundle = getIntent().getExtras();
        subject = (ArrayList<String>) bundle.get("subject");
        cate  = (ArrayList<String>)bundle.get("cate");
        subcate = (HashMap<String,ArrayList<String>>)bundle.get("subcate");

        imgProfile = findViewById(R.id.profile_image);
        btn_change = findViewById(R.id.btn_change_profile_image);
        editName = findViewById(R.id.edit_name);
        editYear = findViewById(R.id.edit_year);
        dropSubject = findViewById(R.id.drop_subject);
        editphone = findViewById(R.id.edit_phone_num);
        editZipNum = findViewById(R.id.edit_zip_num);
        dropCate = findViewById(R.id.drop_category);
        dropSubCate = findViewById(R.id.drop_sub_category);
        editGroup = findViewById(R.id.edit_group);
        editPosition = findViewById(R.id.edit_position);

        subject.add(0,"학과를 선택");
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,subject);
        subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSubject.setAdapter(subAdapter);

        for(String key : subcate.keySet())
            if(subcate.get(key) != null)
                subcate.get(key).add(0,"소분류를 선택");
        final ArrayAdapter<String> subcateAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item);
        subcateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropSubCate.setAdapter(subcateAdapter);

        cate.add(0,"대분류를 선택");
        final ArrayAdapter<String> cateAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cate);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropCate.setAdapter(cateAdapter);
        if(getIntent().getAction() != null && (getIntent().getAction().equals("selectcate") || getIntent().getAction().equals("selectsubcate"))){
            String selectedcate = getIntent().getStringExtra("selectedcate");
            Log.d("AddMember.getStringEtra", "selectsubcate : " + selectedcate);
            Log.d("AddMember.subcate", "subcate : " + subcate);
            dropCate.setSelection(cateAdapter.getPosition(selectedcate));
            if(getIntent().getAction().equals("selectsubcate")){
                String selectedsubcate = getIntent().getStringExtra("selectedsubcate");
                subcateAdapter.clear();
                subcateAdapter.addAll(subcate.get(selectedcate));
                dropSubCate.setSelection(subcateAdapter.getPosition(selectedsubcate));
            }
        }
        dropCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strCate = cateAdapter.getItem(i);
                subcateAdapter.clear();
                if(subcate.get(strCate) == null)
                    subcateAdapter.addAll("대분류를 먼저 선택해주세요.");
                else
                    subcateAdapter.addAll(subcate.get(strCate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alterDialog = new AlertDialog.Builder(AddMember.this);
                alterDialog.setTitle("업로드할 이미지 선택");
                alterDialog.setPositiveButton("사진 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정합니다.
                        File photoFile = null;

                        String imgFileName = "tmp_" + System.currentTimeMillis() + "_";
                        String dirPath = Environment.getExternalStorageDirectory() + "/ChYSDMAPP/";
                        File dir_ChYSDMAPP = new File(dirPath);
                        if(!dir_ChYSDMAPP.exists())dir_ChYSDMAPP.mkdir();

                        try {
                            photoFile = File.createTempFile(imgFileName,".jpg",dir_ChYSDMAPP);
                        } catch (IOException e) {
                            Toast.makeText(AddMember.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();              finish();
                        }

                        if (photoFile != null) {
                            imgCaptureUri = FileProvider.getUriForFile(AddMember.this,
                                    "ml.dilot.chysdmapp.provider", photoFile); //FileProvider의 경우 이전 포스트를 참고하세요.
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgCaptureUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        }
                    }
                });
                alterDialog.setNegativeButton("앨범 선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent,PICK_FROM_ALBUM);
                    }
                });
                alterDialog.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alterDialog.show();
            }
        });

        int result;
        List<String> permissionList = new ArrayList<>();
        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(AddMember.this,pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(AddMember.this,permissionList.toArray(new String[permissionList.size()]),MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)  {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK) {
            Toast.makeText(this, "사진을 받아 오는 데 실패 하였습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM :
                Log.d("onActivityResult", "PICK_FROM_ALBUM");
                if(data == null) return;
                imgCaptureUri = data.getData();
            case PICK_FROM_CAMERA :
                Log.d("onActivityResult", "PICK_FROM_CAMERA");
                this.grantUriPermission("com.android.camera", imgCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imgCaptureUri, "image/*");

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                grantUriPermission(list.get(0).activityInfo.packageName, imgCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                int size = list.size();
                if (size == 0) {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);

                    File photoFile = null;
                    String imgFileName = "tmp_" + System.currentTimeMillis() + "_";
                    String dirPath = Environment.getExternalStorageDirectory() + "/ChYSDMAPP/";
                    File dir_ChYSDMAPP = new File(dirPath);
                    if (!dir_ChYSDMAPP.exists()) dir_ChYSDMAPP.mkdir();
                    try {
                        photoFile = File.createTempFile(imgFileName, ".jpg", dir_ChYSDMAPP);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
                    File tempFile = new File(folder.toString(), photoFile.getName());

                    imgCaptureUri = FileProvider.getUriForFile(AddMember.this, "ml.dilot.chysdmapp.provider", tempFile);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgCaptureUri);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(res.activityInfo.packageName, imgCaptureUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(i, CROP_FROM_IMAGE);
                }
                break;
            case CROP_FROM_IMAGE :
                Log.d("onActivityResult", "CROP_FROM_IMAGE");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgCaptureUri);
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 128, 128);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs);

                    imgProfile.setImageBitmap(thumbImage);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage().toString());
                }
//                final Bundle extras = data.getExtras();
//                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ChYSDMAPP/";
//                String filePath = dirPath + System.currentTimeMillis() + ".jpg";
//                if(extras != null){
//                    Bitmap photo = extras.getParcelable("data");
//                    imgProfile.setImageBitmap(photo);
//
//                    File dir_ChYSDMAPP = new File(dirPath);
//                    if(!dir_ChYSDMAPP.exists())dir_ChYSDMAPP.mkdir();
//
//                    File copyFile = new File(filePath);
//                    BufferedOutputStream out = null;
//
//                    try{
//                        copyFile.createNewFile();
//                        out = new BufferedOutputStream(new FileOutputStream(copyFile));
//                        photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//                        out.flush();
//                        out.close();
//                    } catch (Exception e){ e.printStackTrace(); }
//                }
        }

        if(requestCode == CROP_FROM_IMAGE){
            File f = new File(imgCaptureUri.getPath());
            if(f.exists()) f.delete();
        }
    }

    public void OnClick(View view){
        switch (view.getId()){
            case R.id.btn_change_profile_image:
                break;
            case R.id.btn_confirm:
                AddMem(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        if((boolean)parmam.get("result"))
                            finish();
                    }
                });
                break;
            case R.id.btn_confirm_next:
                AddMem(new vvoidEvent() {
                    @Override
                    public void vvoidEvent(Map parmam) {
                        if((boolean)parmam.get("result")){
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    public void AddMem(final vvoidEvent andthen){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("추가중...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserInfo userInfo = new UserInfo();
        userInfo.category = (String)dropCate.getSelectedItem();
        userInfo.subCategory = (String)dropSubCate.getSelectedItem();
        userInfo.major = (String)dropSubject.getSelectedItem();
        userInfo.name = editName.getText().toString();
        userInfo.homeNumber = editZipNum.getText().toString();
        userInfo.phoneNumber = editphone.getText().toString();
        userInfo.group = editGroup.getText().toString();
        userInfo.position = editPosition.getText().toString();
        MemeberListMgr.AddMember(userInfo, new vvoidEvent() {
            @Override
            public void vvoidEvent(Map parmam) {
                progressDialog.dismiss();
                boolean result = (boolean)parmam.get("result");
                if(result)
                    Toast.makeText(AddMember.this,"추가가 완료되었습니다.",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AddMember.this,(String)parmam.get("message"),Toast.LENGTH_LONG).show();
                andthen.vvoidEvent(parmam);
            }
        });
    }
}
