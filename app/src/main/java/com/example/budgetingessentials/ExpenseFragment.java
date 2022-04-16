package com.example.budgetingessentials;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ExpenseFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;


    // RV
    private RecyclerView recyclerView;
    ExpenseAdapter expenseAdapter;
    ArrayList<Data> list;

    // Total Expense
    public static int expenseTotalSum;
    private TextView expenseTotal;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        recyclerView = view.findViewById(R.id.rv_expense);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotal = view.findViewById(R.id.expense_txt_total);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(getContext(), list);
        recyclerView.setAdapter(expenseAdapter);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Reset total
                expenseTotalSum = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);

                    // Increment the total expense amount for each existing expense
                    expenseTotalSum += data.getAmount();
                    String stTotal = String.valueOf(expenseTotalSum);
                    expenseTotal.setText("$" + stTotal);

                    list.add(data);
                }
                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}