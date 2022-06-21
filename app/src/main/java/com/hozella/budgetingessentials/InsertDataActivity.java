package com.hozella.budgetingessentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsertDataActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    // Insert Date Dialog
    private DatePickerDialog datePickerDialog;
    Button edtDate;
    EditText edtAmount;
    EditText edtType;
    EditText edtNote;
    String type;
    String[] types = {"Income", "Expense"};

    Button btnSave;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

        // Setup Firebase connection and get User ID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        // Setup Firebase database connections
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(uid);
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child(uid).child("IncomeData");
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child(uid).child("ExpenseData");

        // Setup toolbar
        setCustomInsertDataToolbar("New Item Creation");

        // Add back button onto toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Setup form fields
        fieldSetup();


    }

    public void dataInsert(){








        if (true == true){ // Spinner == Income

        }else{ // Spinner == Expense

        }






    }

    private void fieldSetup(){
        Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Log.w("SPINNER:", type + "");
        edtDate = findViewById(R.id.date_edt);
        edtDate.setText(getTodaysDate());
        edtAmount = findViewById(R.id.amount_edt);
        edtType = findViewById(R.id.type_edt);
        edtNote = findViewById(R.id.note_edt);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(saveListener);
        btnCancel.setOnClickListener(cancelListener);

        initDatePicker();
        //datePickerDialog.show();
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String type = edtType.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
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
                            Toast.makeText(InsertDataActivity.this, "Data Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InsertDataActivity.this, "Data Upload Failed", Toast.LENGTH_SHORT).show();
                            Log.e("DATA UPLOAD FIREBASE", "FAILED. " + e);
                        }
                    });
            HomeActivity.incomeTotalSum += dAmount;
            mUserDatabase.child("IncomeTotal").setValue(HomeActivity.incomeTotalSum);

            // TODO RETURN TO HOME ACTIVITY
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO RETURN TO HOME ACTIVITY
        }
    };


    // Create the overflow menu in the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.insert_data_overflow_menu, menu);
        return true;
    }

    // Listen for items to be selected in the overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.insert_info:
                AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
                myDialog.setTitle("New Item Creation Help");
                myDialog.setMessage("Helpful information goes here");
                myDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = myDialog.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // Creates the toolbar, sets the title, and sets it as the supportActionBar
    private void setCustomInsertDataToolbar(String title){
        Toolbar toolbar = findViewById(R.id.app_insert_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = types[position];
        Toast.makeText(getApplicationContext(), type, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}