package com.example.budgetingessentials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {

    Context context;

    ArrayList<Data> list;

    IIncomeRecycler incomeListener;

    public IncomeAdapter(Context context, ArrayList<Data> list, IIncomeRecycler incomeListener) {
        this.context = context;
        this.list = list;

        this.incomeListener = incomeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.income_rv_data, parent, false);
        return new MyViewHolder(v, incomeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = list.get(position);
        holder.amount.setText(data.getAmountString());
        holder.type.setText(data.getType());
        holder.note.setText(data.getNote());
        holder.date.setText(data.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ON BIND CLICK LISTENER AT: " + holder.getBindingAdapterPosition() + " " + data.getType());
                incomeListener.UpdateIncomeDataItem();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView amount, type, note, date;
        View rootView;
        IIncomeRecycler incomeListener;

        public MyViewHolder(@NonNull View itemView, IIncomeRecycler incomeListener) {
            super(itemView);
            rootView = itemView;
            this.incomeListener = incomeListener;

            amount = itemView.findViewById(R.id.amount_txt_income);
            type = itemView.findViewById(R.id.type_txt_income);
            note = itemView.findViewById(R.id.note_txt_income);
            date = itemView.findViewById(R.id.date_txt_income);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ON HOLDER CLICK LISTENER AT: " + getBindingAdapterPosition() + " " + type);

                }
            });



        }
    }

    interface IIncomeRecycler{
        void UpdateIncomeDataItem();
    }

}
