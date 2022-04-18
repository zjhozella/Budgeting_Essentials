package com.example.budgetingessentials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    Context context;

    ArrayList<Data> list;

    IExpenseRecycler expenseRecycler;

    public ExpenseAdapter(Context context, ArrayList<Data> list, IExpenseRecycler expenseRecycler) {
        this.context = context;
        this.list = list;

        this.expenseRecycler = expenseRecycler;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.expense_rv_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = list.get(position);
        holder.amount.setText(data.getAmountString());
        holder.type.setText(data.getType());
        holder.note.setText(data.getNote());
        holder.date.setText(data.getDate());

        // Called when an item is tapped
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ON BIND CLICK LISTENER AT: " + holder.getBindingAdapterPosition() + " " + data.getType());
                expenseRecycler.UpdateExpenseDataItem(data.getType(), data.getNote(), data.getAmount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView amount, type, note, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.amount_txt_expense);
            type = itemView.findViewById(R.id.type_txt_expense);
            note = itemView.findViewById(R.id.note_txt_expense);
            date = itemView.findViewById(R.id.date_txt_expense);

        }
    }

    interface IExpenseRecycler{
        void UpdateExpenseDataItem(String type, String note, int amount);
    }

}
