package com.e.coursework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncomeActivity extends AppCompatActivity {

    private TextView BudgetAmount;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    private FirebaseUser users;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private String usersID;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    ImageView userImage;

    private String post_key = "";
    private String item = "";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);


        users = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.userImage);
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
                    Toast.makeText(IncomeActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AboutUs.class));
                }
                else if (id == R.id.home){
                    Toast.makeText(IncomeActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else if (id == R.id.myprofile){
                    Toast.makeText(IncomeActivity.this, "MyProfile",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
                else if (id == R.id.budget){
                    Toast.makeText(IncomeActivity.this, "Expenses",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), BudgetActivity.class));
                }
                else if (id == R.id.income){
                    Toast.makeText(IncomeActivity.this, "Income",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                }
                else if (id == R.id.settings){
                    Toast.makeText(IncomeActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), Settings.class));
                }
                else if (id == R.id.logout){
                    Toast.makeText(IncomeActivity.this, "Successfully Logged Out",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                return true;
            }
        });


        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("Income").child(mAuth.getCurrentUser().getUid());

        BudgetAmount = findViewById(R.id.BudgetAmount);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for(DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Month Income: £" + totalAmount);
                    BudgetAmount.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout2, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);
        final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button saver = myView.findViewById(R.id.saving);

        saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String budgetAmount = amount.getText().toString();
                String budgetItem =  itemSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is Required!");
                    return;
                }

                if(budgetItem.equals("Select item")){
                    Toast.makeText(IncomeActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                }

                else{

                    Log.d("TAG", "success");
                    String id = budgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date1 = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);

                    Data data = new Data(budgetItem, date1, id, null, Integer.parseInt(budgetAmount), months.getMonths());

                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(IncomeActivity.this, "Income item added successfully", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "no success");
                            }
                            else {
                                Toast.makeText(IncomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setItemAmount("Allocated Amount: £" + model.getAmount());
                holder.setItemDate("On: " + model.getData());
                holder.setItemName("Income Item: " + model.getItem());

                holder.notes.setVisibility(View.GONE);

                switch (model.getItem()){
                    case "Transport":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_directions_car_24);
                        break;
                    case "Food":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_fastfood_24);
                        break;
                    case "House":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_house_24);
                        break;
                    case "Entertainment":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_local_activity_24);
                        break;
                    case "Education":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_work_24);
                        break;
                    case "Charity":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_attach_money_24);
                        break;
                    case "Personal":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_person_24);
                        break;
                    case "Other":
                        holder.imageV.setImageResource(R.drawable.ic_baseline_devices_other_24);
                        break;
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ImageView imageV;
        public TextView notes,date, note;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageV = itemView.findViewById(R.id.imageV);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
        }
        public void setItemName(String itemName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(itemName);
        }
        public void setItemAmount(String Amount){
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(Amount);
        }
        public void setItemDate(String Date){
            TextView date = mView.findViewById(R.id.date);
            date.setText(Date);
        }
    }
    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button delBut = mView.findViewById(R.id.delete);
        Button upBut = mView.findViewById(R.id.update);

        upBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(mAmount.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date1 = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);

                Data data = new Data(item, date1, post_key, null, amount, months.getMonths());

                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "no success");
                        }
                        else {
                            Toast.makeText(IncomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });

        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(IncomeActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "no success");
                        }
                        else {
                            Toast.makeText(IncomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}