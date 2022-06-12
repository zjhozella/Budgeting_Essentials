package com.hozella.budgetingessentials;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.DoubleToLongFunction;


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
    Button btnCancel;


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


                    // Increment the total income amount for each existing income
                    HomeActivity.incomeTotalSum += data.getAmount();
                    String stTotal = String.valueOf(HomeActivity.incomeTotalSum);
                    incomeTotal.setText("$" + stTotal);

                    for (int i = 0; i < incomeList.size(); ++i){

                        if (incomeList.get(i).getId().equals(data.getId())){

                            HomeActivity.incomeTotalSum = 0;
                            for (int j = 0; j < incomeList.size(); ++j){
                                HomeActivity.incomeTotalSum += incomeList.get(j).getAmount();
                            }

                            String stTotalx = String.valueOf(HomeActivity.incomeTotalSum);
                            incomeTotal.setText("$" + stTotalx);
                            return;
                        }
                    }
                    incomeList.add(data);
                }
                incomeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    // Part of the IIncomeRecycler interface. Called when a user taps on an item
    @Override
    public void UpdateIncomeDataItem(String type, String note, Double amount, String id) {
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
        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        // Connect the buttons
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Get data from fields
                String fType = edtType.getText().toString().trim();
                String fNote = edtNote.getText().toString().trim();
                String sAmount = edtAmount.getText().toString().trim();
                Double dAmount = Double.parseDouble(sAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(dAmount, fType, fNote, id, mDate);

                if(TextUtils.isEmpty(fType)){
                    edtType.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(sAmount)){
                    edtAmount.setError("Required Field...");
                    return;
                }else{
                    try {
                        Double num = Double.parseDouble(sAmount);
                    }catch (NumberFormatException e){
                        edtAmount.setError("Not a valid amount!");
                        return;
                    }
                }

                // Update selected data item to be equal to what is in the edit fields
                mIncomeDatabase.child(id).setValue(data);

                incomeList.clear();

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // Delete the selected item by its id
                mIncomeDatabase.child(id).removeValue();

                incomeList.clear();

                dialog.dismiss();


            }
        });

        dialog.show();
    }
}