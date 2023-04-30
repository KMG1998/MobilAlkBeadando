package com.example.mobilalk_vizora.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.formatters.DateFormatters;
import com.example.mobilalk_vizora.model.Statement;

import java.util.ArrayList;
import java.util.Date;

public class StatementListAdapter extends RecyclerView.Adapter<StatementListAdapter.ViewHolder>{

    private ArrayList<Statement> statements;
    private Context context;

    FireBaseProvider fBaseProvider = new FireBaseProvider();

    public StatementListAdapter(Context ctx, ArrayList<Statement> statements){
        this.context = ctx;
        this.statements = statements;
    }
    @NonNull
    @Override
    public StatementListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout
        View statementListView
                = inflater.inflate(R.layout.statement_list_item,
                        parent, false);

        ViewHolder viewHolder = new ViewHolder(statementListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StatementListAdapter.ViewHolder holder, int position) {
        // Get current sport.
        Statement currentItem = statements.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentItem);
    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAmount;
        TextView textViewDate;
        ImageView listImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            listImage = itemView.findViewById(R.id.listImage);
        }

        public void bindTo(Statement currentItem){
            fBaseProvider.getImageForStatement(currentItem).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        textViewAmount.setText(currentItem.getWaterAmount());
                        Date time = currentItem.getTimestamp().toDate();
                        textViewDate.setText(DateFormatters.getDateFormat().format(time));
                    }else {
                        Toast.makeText(context, R.string.listing_failure, Toast.LENGTH_SHORT).show();
                    }
            });
        }
    }


}
