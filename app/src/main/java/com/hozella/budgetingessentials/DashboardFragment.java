package com.hozella.budgetingessentials;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashboardFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mIncomeTotalData;
    private DatabaseReference mExpenseTotalData;

    private TextView expense_total;
    private TextView income_total;

    private Double iTotal = 0.0;
    private Double eTotal = 0.0;

    public DashboardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(uid);

        // Update Income and Expense total amounts
        expense_total = myView.findViewById(R.id.expense_dashboard_total);
        income_total = myView.findViewById(R.id.income_dashboard_total);

         if (mUserDatabase.child("IncomeTotal") != null){
             mIncomeTotalData = mUserDatabase.child("IncomeTotal");

             mIncomeTotalData.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     iTotal = snapshot.getValue(Double.class);
                     if (iTotal != null)
                     {
                         income_total.setText("$" + iTotal);
                     }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });
         }

        if (mUserDatabase.child("ExpenseTotal") != null){
            mExpenseTotalData = mUserDatabase.child("ExpenseTotal");

            mExpenseTotalData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eTotal = snapshot.getValue(Double.class);
                    if (eTotal != null)
                    {
                        expense_total.setText("$" + eTotal);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        return myView;
    }






}