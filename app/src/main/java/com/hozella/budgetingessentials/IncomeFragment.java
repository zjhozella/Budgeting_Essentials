package com.hozella.budgetingessentials;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class IncomeFragment extends Fragment implements IncomeAdapter.IIncomeRecycler {

   // Firebase
    FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;


    // RV
    RecyclerView recyclerView;
    IncomeAdapter incomeAdapter;
    public static ArrayList<Data> incomeList = new ArrayList<>();

    // Total Income
    private TextView incomeTotal;

    // Update Income Data
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    Button btnUpdate;
    Button btnDelete;


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

        // Define recycler view
        recyclerView = view.findViewById(R.id.rv_income);

        // Setup database path //TODO Add email as part of path above UID
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

        //list = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(getContext(), this);
        recyclerView.setAdapter(incomeAdapter);

        Log.w("onCreateView", "onCreateView");



        // Called every time an item is added to the database
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Reset total
                HomeActivity.incomeTotalSum = 0;

                // Clear the list to update with fresh data
                incomeList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);

                    Log.w("SNAPSHOT", "getChildren:" + dataSnapshot.getValue());

                    // Increment the total income amount for each existing income
                    HomeActivity.incomeTotalSum += Integer.parseInt(data.getAmount());
                    String stTotal = String.valueOf(HomeActivity.incomeTotalSum);
                    incomeTotal.setText("$" + stTotal);

                    for (int i = 0; i < incomeList.size(); ++i){
                        Log.w("1onDataChange-ArrayList", incomeList.get(i).getAmount());
                    }

                    for (int i = 0; i < incomeList.size(); ++i){
                        //Log.w("FOR", "IF:" + list.get(i).getId().toString() + " : " + data.getId().toString());
                        if (incomeList.get(i).getId().equals(data.getId())){
                            Log.w("1FOR", "IF:" + incomeList.get(i).getId() + " : " + data.getId());
                            HomeActivity.incomeTotalSum = 0;
                            for (int j = 0; j < incomeList.size(); ++j){
                                HomeActivity.incomeTotalSum += Integer.parseInt(incomeList.get(j).getAmount());
                                Log.w("INCOME TOTAL:", "+:" + incomeList.get(j).getAmount() + " : " + HomeActivity.incomeTotalSum);
                            }
                            String stTotalx = String.valueOf(HomeActivity.incomeTotalSum);
                            incomeTotal.setText("$" + stTotalx);
                            return;
                        }else
                        {
                            Log.w("2FOR", "IF:" + incomeList.get(i).getId() + " : " + data.getId());

                        }
                    }

                    incomeList.add(data);

                    Log.w("onDataChange", data.getAmount() + " ADDED TO LIST!");

                    for (int i = 0; i < incomeList.size(); ++i){
                        Log.w("2onDataChange-ArrayList", incomeList.get(i).getAmount());
                    }
                    Log.w("onDataChange", "onDataChange");
                }



                incomeAdapter.notifyDataSetChanged();
                Log.w("incomeAdapter", "Notify data set changed.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    // Part of the IIncomeRecycler interface. Called when a user taps on an item
    @Override
    public void UpdateIncomeDataItem(String type, String note, String amount, String id) {
        // Build the dialog
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.layout_update_data, null);
        myDialog.setView(view);

        // Connect the edit fields
        edtAmount = view.findViewById(R.id.amount_edt_u);
        edtType = view.findViewById(R.id.type_edt_u);
        edtNote = view.findViewById(R.id.note_edt_u);

        // Show the selected item's data in the edit fields
        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmount.setText(amount);
        edtAmount.setSelection(amount.length());

        // Connect the buttons
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Get data from fields
                String fType = edtType.getText().toString().trim();
                String fNote = edtNote.getText().toString().trim();
                String sAmount = edtAmount.getText().toString().trim();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(sAmount, fType, fNote, id, mDate);

                // Update selected data item to be equal to what is in the edit fields
                mIncomeDatabase.child(id).setValue(data);
                for (int i = 0; i < incomeList.size(); ++i){
                    Log.w("onClick1-ArrayList", incomeList.get(i).getAmount());
                }
                incomeList.clear();
                Log.w("List", "CLEARED");
                for (int i = 0; i < incomeList.size(); ++i){
                    Log.w("onClick2-ArrayList", incomeList.get(i).getAmount());
                }
                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}