package com.example.myapplication.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Admin.AdminProfileActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Login.SignInORSignUpActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AgentHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentuserid;
    private DrawerLayout drawer;
    private DatabaseReference agentref;
    private String currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home);


        agentref= FirebaseDatabase.getInstance().getReference().child("Agents");


        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser().getUid();

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        drawer=findViewById(R.id.agent_draw_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.Open_navigation_drawer,R.string.Close_navigation_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.agent_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                if (menuItem.getItemId() == R.id.agent_nav_profile) {
                    intent = new Intent(AgentHomeActivity.this, AgentProfileActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.agent_nav_hotel_facilities) {
                    intent = new Intent(AgentHomeActivity.this, HotelDetailsActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.agent_nav_settings) {
                    intent = new Intent(AgentHomeActivity.this, AgentProfileActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.agent_nav_logout) {
                    FirebaseAuth mauth = FirebaseAuth.getInstance();
                    mauth.signOut();
                    intent = new Intent(AgentHomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (menuItem.getItemId() == R.id.agent_nav_contact_admin) {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "7901048098"));
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.agent_nav_send) {
                    intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "charan@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Complaint");
                    intent.putExtra(Intent.EXTRA_TEXT, "Enter text here:");
                    startActivity(Intent.createChooser(intent, "Chooser title"));
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

        });

        getSupportFragmentManager().beginTransaction().replace(R.id.agent_fragment_container,new AgentHomeFragment()).commit();
        BottomNavigationView bottomNavigationView=findViewById(R.id.agent_bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                if (menuItem.getItemId() == R.id.agent_bottom_nav_home) {
                    selectedFragment = new AgentHomeFragment();
                } else if (menuItem.getItemId() == R.id.agent_nav_add) {
                    selectedFragment = new AddNewRoomCategoryFragment();
                } else if (menuItem.getItemId() == R.id.agent_nav_manage) {
                    selectedFragment = new AgentManageFragment();
                } else if (menuItem.getItemId() == R.id.agent_nav_bookings) {
                    selectedFragment = new AgentBookingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.agent_fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }

        });
        displayUserNameandImage();


    }
    private void displayUserNameandImage() {
        NavigationView navigationView = findViewById(R.id.agent_nav_view);
        View HeaderView=navigationView.getHeaderView(0);
        final TextView agentprofilename=HeaderView.findViewById(R.id.agent_profile_name_nav);
        final ImageView agentprofileimage=HeaderView.findViewById(R.id.agent_profile_image_nav);
        agentref.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.hasChild("name")){
                    agentprofilename.setText(dataSnapshot.child("name").getValue().toString());
                }
                if(dataSnapshot.exists()&&dataSnapshot.hasChild("agentimage")){
                    Picasso.get().load(dataSnapshot.child("agentimage").getValue().toString()).into(agentprofileimage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
