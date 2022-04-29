package com.e.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.util.ArrayList;

public class PercActivity extends AppCompatActivity {
    private FirebaseUser users;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private String usersID;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    PieChart pieChart;
    ImageView userImage;
    TextView Monthly;
    private Button switchPound;
    private FirebaseAuth mAuth;
    private DatabaseReference budgetRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perc);

        users = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.userImage);
        Monthly = findViewById(R.id.Monthly);

        usersID = users.getUid();

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);

        setPieChart();

        ArrayList<PieEntry> yValues = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("Budget").child(mAuth.getCurrentUser().getUid());

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Monthly Expenses: £" + totalAmount);
                    Monthly.setText(sTotal);
                    yValues.add(new PieEntry(data.getAmount(), data.getItem()));
                }

                pieChart.animateY (1000);
                PieDataSet dataSet = new PieDataSet(yValues, "");
                dataSet.setSliceSpace(4f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData data = new PieData((dataSet));
                data.setValueTextSize(10f);
                data.setValueTextColor(Color.BLACK);
                pieChart.setData(data);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switchPound = findViewById(R.id.Pound);
        switchPound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });

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
                    Toast.makeText(PercActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs.class));
                }
                else if (id == R.id.home){
                    Toast.makeText(PercActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else if (id == R.id.myprofile){
                    Toast.makeText(PercActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
                else if (id == R.id.budget){
                    Toast.makeText(PercActivity.this, "Budget",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
                }
                else if (id == R.id.income){
                    Toast.makeText(PercActivity.this, "Income",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                }
                else if (id == R.id.settings){
                    Toast.makeText(PercActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), Settings.class));
                }
                else if (id == R.id.logout){
                    Toast.makeText(PercActivity.this, "Successfully Logged Out",Toast.LENGTH_SHORT).show();
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

    private void setPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);

        pieChart.setEntryLabelTextSize(12);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Spending by Category %");
        pieChart.setCenterTextSize(24);
        pieChart.animateY (1000);
        pieChart.getDescription().setEnabled(false);

        Legend two = pieChart.getLegend();
        two.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        two.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        two.setOrientation(Legend.LegendOrientation.VERTICAL);
        two.setTextColor(Color.WHITE);
        two.setDrawInside(false);
        two.setEnabled(true);
    }


}
