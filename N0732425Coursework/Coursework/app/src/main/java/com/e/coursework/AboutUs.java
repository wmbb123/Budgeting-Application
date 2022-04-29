package com.e.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AboutUs extends AppCompatActivity {

    private FirebaseUser users;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private String usersID;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    ImageView userImage;
    TextView Monthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        users = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.userImage);
        Monthly = findViewById(R.id.Monthly);
        usersID = users.getUid();

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView FirstNameView = findViewById(R.id.FullName);
        final NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);

        ref. child(usersID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserInfo userProf = dataSnapshot.getValue(UserInfo.class);
                if(userProf != null){
                    String firstName = userProf.firstName;
                    String lastName = userProf.lastName;
                    FirstNameView.setText(firstName + " " + lastName);
                    userImage = findViewById(R.id.userImage);

                    storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(userImage);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(UserData.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.aboutUs){
                    Toast.makeText(AboutUs.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs.class));
                }
                else if (id == R.id.home){
                    Toast.makeText(AboutUs.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else if (id == R.id.myprofile){
                    Toast.makeText(AboutUs.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
                else if (id == R.id.budget){
                    Toast.makeText(AboutUs.this, "Budget",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
                }
                else if (id == R.id.income){
                    Toast.makeText(AboutUs.this, "Income",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                }
                else if (id == R.id.settings){
                    Toast.makeText(AboutUs.this, "Settings",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), Settings.class));
                }
                else if (id == R.id.logout){
                    Toast.makeText(AboutUs.this, "Successfully Logged Out",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
