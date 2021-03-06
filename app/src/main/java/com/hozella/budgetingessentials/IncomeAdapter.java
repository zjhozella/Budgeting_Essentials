package com.hozella.budgetingessentials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.MyViewHolder> {

    Context context;

    IIncomeRecycler incomeListener;

    public IncomeAdapter(Context context, IIncomeRecycler incomeListener) {
        this.context = context;

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
        Data data = IncomeFragment.incomeList.get(position);
        holder.amount.setText(String.valueOf(data.getAmount()));
        holder.type.setText(data.getTitle());
        holder.note.setText(data.getNote());
        holder.date.setText(data.getDate());

        // Called when an item is tapped
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ON BIND CLICK LISTENER AT: " + holder.getAdapterPosition() + " " + data.getTitle());
                incomeListener.UpdateIncomeDataItem(data.getTitle(), data.getNote(), data.getAmount(), data.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return IncomeFragment.incomeList.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView amount, type, note, date;


        public MyViewHolder(@NonNull View itemView, IIncomeRecycler incomeListener) {
            super(itemView);

            amount = itemView.findViewById(R.id.amount_txt_income);
            type = itemView.findViewById(R.id.title_txt_income);
            note = itemView.findViewById(R.id.note_txt_income);
            date = itemView.findViewById(R.id.date_txt_income);

        }
    }

    interface IIncomeRecycler{
        void UpdateIncomeDataItem(String type, String note, Double amount, String id);
    }

}
