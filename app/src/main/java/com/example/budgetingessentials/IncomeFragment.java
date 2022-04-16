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


public class IncomeFragment extends Fragment {

   // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;


    // RV
    private RecyclerView recyclerView;
    IncomeAdapter incomeAdapter;
    ArrayList<Data> list;

    // Total Income
    public static int incomeTotalSum;
    private TextView incomeTotal;




    public IncomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        recyclerView = view.findViewById(R.id.rv_income);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        incomeTotal = view.findViewById(R.id.income_txt_total);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(getContext(), list);
        recyclerView.setAdapter(incomeAdapter);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Reset total
                incomeTotalSum = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);

                    // Increment the total income amount for each existing income
                    incomeTotalSum += data.getAmount();
                    String stTotal = String.valueOf(incomeTotalSum);
                    incomeTotal.setText("$" + stTotal);

                    list.add(data);
                }
                incomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}