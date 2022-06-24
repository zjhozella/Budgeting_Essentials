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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private Button btnLogout;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    // Fragments
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;


    // Global Total Expense and Income
    public static double expenseTotalSum = 0.0;
    public static double incomeTotalSum = 0.0;

    // Insert Date Dialog
    private DatePickerDialog datePickerDialog;
    EditText edtDate;
    EditText edtAmount;
    EditText edtTitle;
    EditText edtNote;

    Button btnSave;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setup Firebase connection and get User ID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        // Setup Firebase database connections
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(uid);
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child(uid).child("IncomeData");
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child(uid).child("ExpenseData");

        // Sets up bottom navigation view
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        frameLayout = findViewById(R.id.main_frame);

        // Initializes all fragments
        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        // Set toolbar title and fragment (Dashboard opens by default)
        setCustomDrawerToolbar("Dashboard");
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

        btnLogout = this.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void setCustomDrawerToolbar(String title){
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                HomeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setFragment(Fragment fragment) {

        // Update the title of the toolbar depending on the fragment
        if (fragment == dashboardFragment)
            setCustomDrawerToolbar("Dashboard");
        else if (fragment == incomeFragment)
            setCustomDrawerToolbar("Your Income");
         else if (fragment == expenseFragment)
            setCustomDrawerToolbar("Your Expenses");
        else
            setCustomDrawerToolbar("Budgeting Essentials");

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
                //incomeDataInsert();
                startActivity(new Intent(getApplicationContext(), InsertDataActivity.class));
                break;

            case R.id.nav_expense:
                expenseDataInsert();
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

        View myView = inflater.inflate(R.layout.layout_insert_data_help_dialog, null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        edtDate = myView.findViewById(R.id.date_edt);
        edtDate.setText(getTodaysDate());
        edtAmount = myView.findViewById(R.id.amount_edt);
        edtTitle = myView.findViewById(R.id.title_edt);
        edtNote = myView.findViewById(R.id.note_edt);

        btnSave = myView.findViewById(R.id.btnSave);
        btnCancel = myView.findViewById(R.id.btnCancel);

        initDatePicker();
        datePickerDialog.show();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = edtTitle.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();


                if(TextUtils.isEmpty(type)){
                    edtTitle.setError("Required Field...");
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
                        return;
                    }
                }

                Double dAmount = Double.parseDouble(amount);

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
                        });
                incomeTotalSum += dAmount;
                mUserDatabase.child("IncomeTotal").setValue(incomeTotalSum);

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

        View myView = inflater.inflate(R.layout.layout_insert_data_help_dialog, null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        edtAmount = myView.findViewById(R.id.amount_edt);
        edtTitle = myView.findViewById(R.id.title_edt);
        edtNote = myView.findViewById(R.id.note_edt);

        btnSave = myView.findViewById(R.id.btnSave);
        btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = edtTitle.getText().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtTitle.setError("Required Field...");
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
                        return;
                    }
                }

                Double dAmount = Double.parseDouble(amount);

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
                expenseTotalSum += dAmount;
                mUserDatabase.child("ExpenseTotal").setValue(expenseTotalSum);

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

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                edtDate.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return month + "/" + day + "/" + year;
    }

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }
}