package com.example.myapplication.Agent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.DummyImageHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AgentEditRoomActivity extends AppCompatActivity {
    private String roomid;
    private ImageView roomimage;
    private EditText acornonacET,roomdiscriptionET,roompriceET,noofroomsET;
    private Button updateRoomBtn;
    private ProgressDialog loadingBar;
    private String acornonac,roomdiscription,roomprice,noofrooms,roomimagestring;
    private DatabaseReference roomref;
    private Uri Imageuri;
    private int GALLARYPICK=1;
    private String checker="";
    private ProgressDialog progressDialog;
    private StorageReference RoomImageRef;
    private String saveCurrentDate,saveCurrentTime;
    private String RoomRandomKey,downloadimgurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_edit_room);

        roomid=getIntent().getExtras().get("roomid").toString();
        roomref= FirebaseDatabase.getInstance().getReference().child("Rooms");
        // Initialize RoomImageRef for compatibility
        RoomImageRef = FirebaseStorage.getInstance().getReference().child("Room Images");
        Initilizefields();
        getandDisplayDetails();

        roomimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instead of opening gallery, use dummy image
                useDummyImage();
                checker="checked";
            }
        });
        updateRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("checked")){
                    userInfoSave();
                }
                else {
                    updateOnlyUserInfo();
                }
            }
        });
    }

    // New method to use dummy image instead of gallery
    private void useDummyImage() {
        // Get dummy image URI
        Imageuri = DummyImageHelper.getDummyImageUri(this);
        // Set the dummy image to the ImageView
        DummyImageHelper.setDummyImage(roomimage);
        Toast.makeText(this, "Dummy image selected", Toast.LENGTH_SHORT).show();
    }

    private void Initilizefields() {
        roomimage=findViewById(R.id.agent_edit_room_img);
        acornonacET=findViewById(R.id.agent_edit_ac_or_nonac);
        roomdiscriptionET=findViewById(R.id.agent_edit_room_description);
        roompriceET=findViewById(R.id.agent_edit_room_price);
        noofroomsET=findViewById(R.id.agent_edit_no_of_rooms);
        updateRoomBtn=findViewById(R.id.agent_edit_update_room);
        progressDialog = new ProgressDialog(this,R.style.MydialogTheme);
    }

    private void getandDisplayDetails() {
        roomref.child(roomid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    acornonac=dataSnapshot.child("acornonac").getValue().toString();
                    roomdiscription=dataSnapshot.child("roomdiscription").getValue().toString();
                    roomprice=dataSnapshot.child("roomprice").getValue().toString();
                    noofrooms=dataSnapshot.child("noofrooms").getValue().toString();
                    roomimagestring=dataSnapshot.child("roomimage").getValue().toString();

                    Picasso.get().load(roomimagestring).into(roomimage);
                    acornonacET.setText(acornonac);
                    roomdiscriptionET.setText(roomdiscription);
                    roompriceET.setText(roomprice);
                    noofroomsET.setText(noofrooms);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Keep this method for compatibility, but it won't be used
    private void OpenGallery(){
        Intent gallaryintent=new Intent();
        gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryintent.setType("image/*");
        startActivityForResult(gallaryintent,GALLARYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLARYPICK && resultCode==RESULT_OK && data!=null){
            Imageuri=data.getData();
            roomimage.setImageURI(Imageuri);
        }
    }

    private void updateOnlyUserInfo() {
        acornonac=acornonacET.getText().toString();
        roomdiscription=roomdiscriptionET.getText().toString();
        roomprice=roompriceET.getText().toString();
        noofrooms=noofroomsET.getText().toString();
        if(TextUtils.isEmpty(acornonac)){
            Toast.makeText(this,"enter ac or non ac",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(roomdiscription)){
            Toast.makeText(this,"enter room description",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(roomprice)){
            Toast.makeText(this,"enter room price",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(noofrooms)){
            Toast.makeText(this,"enter no of rooms",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object> roomMap=new HashMap<>();
            roomMap.put("acornonac",acornonac);
            roomMap.put("roomdiscription",roomdiscription);
            roomMap.put("roomprice",roomprice);
            roomMap.put("noofrooms",noofrooms);
            roomref.child(roomid).updateChildren(roomMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AgentEditRoomActivity.this,"Update successfull",Toast.LENGTH_SHORT).show();
                        sendUserToAgentHomeActivity();
                    }
                }
            });
        }
    }

    private void userInfoSave(){
        acornonac=acornonacET.getText().toString();
        roomdiscription=roomdiscriptionET.getText().toString();
        roomprice=roompriceET.getText().toString();
        noofrooms=noofroomsET.getText().toString();

        // If no image is selected, use dummy image automatically
        if(Imageuri==null){
            useDummyImage();
        }

        if(TextUtils.isEmpty(acornonac)){
            Toast.makeText(this,"enter ac or non ac",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(roomdiscription)){
            Toast.makeText(this,"enter room description",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(roomprice)){
            Toast.makeText(this,"enter room price",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(noofrooms)){
            Toast.makeText(this,"enter no of rooms",Toast.LENGTH_SHORT).show();
        }
        else {
            uploadImage();
        }
    }

    private void uploadImage() {
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Skip Firebase Storage upload and use dummy URL directly
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM  dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH;mm;ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        RoomRandomKey=saveCurrentDate+saveCurrentTime;

        // Use dummy URL instead of uploading to Firebase
        downloadimgurl = DummyImageHelper.getDummyDownloadUrl();
        Toast.makeText(AgentEditRoomActivity.this, "Using dummy image URL", Toast.LENGTH_SHORT).show();

        // Update room info with dummy image URL
        HashMap<String,Object> roomMap=new HashMap<>();
        roomMap.put("acornonac",acornonac);
        roomMap.put("roomdiscription",roomdiscription);
        roomMap.put("roomprice",roomprice);
        roomMap.put("noofrooms",noofrooms);
        roomMap.put("roomimage",downloadimgurl);

        roomref.child(roomid).updateChildren(roomMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(AgentEditRoomActivity.this,"Update successfull",Toast.LENGTH_SHORT).show();
                    sendUserToAgentHomeActivity();
                }
            }
        });
    }

    private void sendUserToAgentHomeActivity() {
        Intent intent=new Intent(this,AgentHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
