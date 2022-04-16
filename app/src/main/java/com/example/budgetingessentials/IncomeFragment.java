package com.example.budgetingessentials;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;


public class IncomeFragment extends Fragment {

   // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;


    // RV
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;



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

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);

        recyclerView = view.findViewById(R.id.rv_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                    .setQuery(query, new SnapshotParser<Data>() {
                        @NonNull //amount, type, note, id, date
                        @Override
                        public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                            return new Data((int) snapshot.child("amount").getValue(),
                                    snapshot.child("type").getValue().toString(),
                                    snapshot.child("note").getValue().toString(),
                                    snapshot.child("id").getValue().toString(),
                                    snapshot.child("date").getValue().toString());
                        }
                    }).build();

        /*
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();*/

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.income_rv_data, parent, false));
            }

            protected void onBindViewHolder(MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmountString());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}

    class MyViewHolder extends RecyclerView.ViewHolder{

    View mView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    void setType(String type){
        TextView mType = mView.findViewById(R.id.type_txt_income);
        mType.setText(type);
    }

    void setNote(String note){
        TextView mNote = mView.findViewById(R.id.note_txt_income);
        mNote.setText(note);
    }

    void setDate(String date){
        TextView mDate = mView.findViewById(R.id.date_txt_income);
        mDate.setText(date);
    }

    void setAmount(String amount){
        TextView mType = mView.findViewById(R.id.type_txt_income);
        String stAmount = String.valueOf(amount);
        mType.setText(stAmount);
    }
}