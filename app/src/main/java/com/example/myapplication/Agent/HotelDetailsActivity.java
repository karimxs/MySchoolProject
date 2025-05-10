package com.example.myapplication.Agent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Utils.DummyImageHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

public class HotelDetailsActivity extends AppCompatActivity {
    private EditText hotelfacility1ET, hotelfacility2ET, hotelfacility3ET, hoteldescriptionET;
    private ImageView hotelImage;
    private Button updatedetailsbtn;
    private FirebaseAuth mauth;
    private String currentuserid;
    private String saveCurrentDate, saveCurrentTime;
    private String HotelRandomKey, downloadimgurl;
    private DatabaseReference agentref;
    private StorageReference HotelImageRef;
    private ProgressDialog loadingBar;
    private Uri Imageuri;
    private int GALLARYPICK = 1;
    String hotelfacility1, hotelfacility2, hotelfacility3, hoteldescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        mauth = FirebaseAuth.getInstance();
        currentuserid = mauth.getCurrentUser().getUid();
        agentref = FirebaseDatabase.getInstance().getReference().child("Agents");
        HotelImageRef = FirebaseStorage.getInstance().getReference().child("Hotel Images");

        InitializaionFields();
        getavailableDetailsandDisplay();
        hotelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instead of opening gallery, use dummy image
                useDummyImage();
            }
        });
        updatedetailsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatehotelInfo();
            }
        });
    }

    // New method to use dummy image instead of gallery
    private void useDummyImage() {
        // Get dummy image URI
        Imageuri = DummyImageHelper.getDummyImageUri(this);
        // Set the dummy image to the ImageView
        DummyImageHelper.setDummyImage(hotelImage);
        Toast.makeText(this, "Dummy image selected", Toast.LENGTH_SHORT).show();
    }

    private void InitializaionFields() {
        hotelfacility1ET = findViewById(R.id.hotel_facility1);
        hotelfacility2ET = findViewById(R.id.hotel_facility2);
        hotelfacility3ET = findViewById(R.id.hotel_facility3);
        hoteldescriptionET = findViewById(R.id.hotel_description);
        hotelImage = findViewById(R.id.select_hotel_img);
        updatedetailsbtn = findViewById(R.id.update_hotel_details_btn);
        loadingBar = new ProgressDialog(this,R.style.MydialogTheme);
    }

    private void getavailableDetailsandDisplay() {
        agentref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("hotelimage")) {
                    hotelfacility1ET.setText(dataSnapshot.child("hotelfacility1").getValue().toString());
                    hotelfacility2ET.setText(dataSnapshot.child("hotelfacility2").getValue().toString());
                    hotelfacility3ET.setText(dataSnapshot.child("hotelfacility3").getValue().toString());
                    hoteldescriptionET.setText(dataSnapshot.child("hoteldescription").getValue().toString());
                    Picasso.get().load(dataSnapshot.child("hotelimage").getValue().toString()).into(hotelImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Keep this method for compatibility, but it won't be used
    private void OpenGallery() {
        Intent gallaryintent = new Intent();
        gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryintent.setType("image/*");
        startActivityForResult(gallaryintent, GALLARYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARYPICK && resultCode == RESULT_OK && data != null) {
            Imageuri = data.getData();
            hotelImage.setImageURI(Imageuri);
        }
    }

    private void validatehotelInfo() {
        hotelfacility1 = hotelfacility1ET.getText().toString();
        hotelfacility2 = hotelfacility2ET.getText().toString();
        hotelfacility3 = hotelfacility3ET.getText().toString();
        hoteldescription = hoteldescriptionET.getText().toString();

        // If no image is selected, use dummy image automatically
        if (Imageuri == null) {
            useDummyImage();
        }

        if (TextUtils.isEmpty(hotelfacility1)) {
            Toast.makeText(this, "please enter hotel facility1", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(hotelfacility2)) {
            Toast.makeText(this, "please enter hotel facility2", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(hotelfacility3)) {
            Toast.makeText(this, "please enter hotel facility3", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(hoteldescription)) {
            Toast.makeText(this, "please enter hotel description", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Adding Hotel Info");
            loadingBar.setMessage("Please wait while we are updating Hotel information");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            storehotelimage();
        }
    }

    private void storehotelimage() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM  dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH;mm;ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        HotelRandomKey = saveCurrentDate + saveCurrentTime;

        // Skip Firebase Storage upload and use dummy URL directly
        downloadimgurl = DummyImageHelper.getDummyDownloadUrl();
        Toast.makeText(HotelDetailsActivity.this, "Using dummy image URL", Toast.LENGTH_SHORT).show();

        // Proceed directly to saving hotel info to database
        saveHotelinfotodatabase();
    }

    private void saveHotelinfotodatabase() {
        final HashMap<String, Object> agentMap = new HashMap<>();
        agentMap.put("hotelfacility1", hotelfacility1);
        agentMap.put("hotelfacility2", hotelfacility2);
        agentMap.put("hotelfacility3", hotelfacility3);
        agentMap.put("hoteldescription", hoteldescription);
        agentMap.put("hotelimage", downloadimgurl);

        agentref.child(currentuserid).updateChildren(agentMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(HotelDetailsActivity.this, AgentHomeActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(HotelDetailsActivity.this, "Hotel details added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(HotelDetailsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
