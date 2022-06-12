package com.hozella.budgetingessentials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    Context context;

    IExpenseRecycler expenseListener;

    public ExpenseAdapter(Context context, IExpenseRecycler expenseListener){
        this.context = context;
        this.expenseListener = expenseListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.expense_rv_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = ExpenseFragment.expenseList.get(position);
        holder.amount.setText(data.getAmount());
        holder.type.setText(data.getType());
        holder.note.setText(data.getNote());
        holder.date.setText(data.getDate());

        // Called when an item is tapped
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ON BIND CLICK LISTENER AT: " + holder.getAdapterPosition() + " " + data.getType());
                expenseListener.UpdateExpenseDataItem(data.getType(), data.getNote(), data.getAmount(), data.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ExpenseFragment.expenseList.size();
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
        void UpdateExpenseDataItem(String type, String note, String amount, String id);
    }

}
