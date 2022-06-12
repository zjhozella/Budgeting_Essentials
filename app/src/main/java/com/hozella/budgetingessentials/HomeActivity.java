package com.hozella.budgetingessentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    // Global Total Expense and Income
    public static double expenseTotalSum = 0.0;
    public static double incomeTotalSum = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        frameLayout = findViewById(R.id.main_frame);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        setFragment(dashboardFragment);

        // Controls where each navigation button takes you
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashboard:
                        setFragment(dashboardFragment);


                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);


                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);


                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed(){

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        switch (itemId){
            case R.id.nav_income:
                incomeDataInsert();
                break;

            case R.id.nav_expense:
                expenseDataInsert();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }

        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());

        return true;
    }

    public void incomeDataInsert(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.layout_insert_data, null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        EditText edtAmount = myView.findViewById(R.id.amount_edt);
        EditText edtType = myView.findViewById(R.id.type_edt);
        EditText edtNote = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                Double dAmount = Double.parseDouble(amount);
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field...");
                    return;
                }else{
                    try {
                        Double num = Double.parseDouble(amount);
                    }catch (NumberFormatException e){
                        edtAmount.setError("Not a valid amount!");
                    }
                }



                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(dAmount, type, note, id, mDate);
                mIncomeDatabase.child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeActivity.this, "Data Upload Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "Data Upload Failed", Toast.LENGTH_SHORT).show();
                                Log.e("DATA UPLOAD FIREBASE", "FAILED. " + e);
                            }
                        });;


                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.layout_insert_data, null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        EditText edtAmount = myView.findViewById(R.id.amount_edt);
        EditText edtType = myView.findViewById(R.id.type_edt);
        EditText edtNote = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = edtType.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                Double dAmount = Double.parseDouble(amount);
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field...");
                    return;
                }else{
                    try {
                        Double num = Double.parseDouble(amount);
                    }catch (NumberFormatException e){
                        edtAmount.setError("Not a valid amount!");
                    }
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(dAmount, type, note, id, mDate);
                mExpenseDatabase.child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(HomeActivity.this, "Data Upload Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "Data Upload Failed", Toast.LENGTH_SHORT).show();
                                Log.e("DATA UPLOAD FIREBASE", "FAILED. " + e);
                            }
                        });;


                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}