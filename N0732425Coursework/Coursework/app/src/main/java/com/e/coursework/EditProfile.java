package com.e.coursework;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private EditText enterFName, enterSName, enterAge, enterGender, enterNumber;
    private FirebaseAuth firebaseAuth;
    private Button saveInfo, uploadImage;
    ImageView imageUser;
    StorageReference storageReference;
    private DatabaseReference ref;
    private String usersID;
    private FirebaseUser users;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    ImageView userImage;
    TextView Monthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        users = FirebaseAuth.getInstance().getCurrentUser();
        imageUser = findViewById(R.id.imageUser);
        uploadImage = findViewById(R.id.uploadImage);
        firebaseAuth = FirebaseAuth.getInstance();
        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageUser);
            }
        });

        usersID = users.getUid();

        enterFName = (EditText) findViewById(R.id.editTextFirstName);
        enterSName = (EditText) findViewById(R.id.editTextLastName);
        enterAge = (EditText) findViewById(R.id.editTextAge);
        enterGender = (EditText) findViewById(R.id.editTextGender);
        enterNumber = (EditText) findViewById(R.id.editTextPhone);

        saveInfo = (Button) findViewById(R.id.Insert);
        ref = FirebaseDatabase.getInstance().getReference("Users");

        ref. child(usersID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserInfo userProf = dataSnapshot.getValue(UserInfo.class);
                if (userProf != null) {
                    String firstName = userProf.firstName;
                    String age = userProf.age;
                    String lastName = userProf.lastName;
                    String gender = userProf.gender;
                    String phoneNumber = userProf.phoneNumber;

                    enterFName.setText(firstName);
                    enterAge.setText(age);
                    enterSName.setText(lastName);
                    enterNumber.setText(phoneNumber);
                    enterGender.setText(gender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    Toast.makeText(EditProfile.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs.class));
                }
                else if (id == R.id.home){
                    Toast.makeText(EditProfile.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else if (id == R.id.myprofile){
                    Toast.makeText(EditProfile.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
                else if (id == R.id.budget){
                    Toast.makeText(EditProfile.this, "Budget",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
                }
                else if (id == R.id.income){
                    Toast.makeText(EditProfile.this, "Income",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                }
                else if (id == R.id.settings){
                    Toast.makeText(EditProfile.this, "Settings",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), Settings.class));
                }
                else if (id == R.id.logout){
                    Toast.makeText(EditProfile.this, "Successfully Logged Out",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                return true;
            }
        });

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, ExistingAccount.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //FirebaseUser user = firebaseAuth.getCurrentUser();

        saveInfo.setOnClickListener(this);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImage();
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });
    }

    private void saveUserInformation(){
        String firstName = enterFName.getText().toString().trim();
        String lastName = enterSName.getText().toString().trim();
        String age = enterAge.getText().toString().trim();
        String gender = enterGender.getText().toString().trim();
        String phoneNumber = enterNumber.getText().toString().trim();

        UserInfo userInformation = new UserInfo(firstName, lastName, gender, age, phoneNumber);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Users").child(user.getUid()).setValue(userInformation);

        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    /*public void selectImage() {
        ImagePicker.Companion.with(this)
                .cropSquare()
                .compress(1024)
                .start();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imageURI = data.getData();
                //imageUser.setImageURI(imageURI);
                uploadImageToFirebase(imageURI);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageURI) {
        Log.d("TAG", "success");

        StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"profile.jpg");

        fileRef.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageUser);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view == saveInfo){
            saveUserInformation();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}