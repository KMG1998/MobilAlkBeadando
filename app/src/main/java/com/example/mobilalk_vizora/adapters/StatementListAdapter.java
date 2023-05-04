package com.example.mobilalk_vizora.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.formatters.DateFormatters;
import com.example.mobilalk_vizora.model.Statement;
import com.google.android.gms.tasks.OnFailureListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class StatementListAdapter extends RecyclerView.Adapter<StatementListAdapter.ViewHolder> {

    private ArrayList<Statement> statements;
    private Context context;
    FireBaseProvider fBaseProvider = new FireBaseProvider();
    private int lastPosition = -1;

    String LOG_TAG = StatementListAdapter.class.getName();

    public StatementListAdapter(Context ctx, ArrayList<Statement> statements) {
        this.context = ctx;
        this.statements = statements;
    }

    @Override
    public StatementListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.statement_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(StatementListAdapter.ViewHolder holder, int position) {
        // Get current sport.
        Statement currentItem = statements.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

   public void update(ArrayList<Statement> data) {
        statements.clear();
        Log.d(LOG_TAG,"data size is "+data.size());
        Log.d(LOG_TAG,"adapter dataset size after add is "+statements.size());
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewAmount;
        private TextView textViewDate;
        private TextView textViewApproved;
        private ImageView listImage;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            listImage = itemView.findViewById(R.id.listImage);
            textViewApproved = itemView.findViewById(R.id.textViewApproved);
        }

        void bindTo(Statement currentItem) {
            fBaseProvider.getImageForStatement(currentItem).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    textViewAmount.setText(currentItem.getWaterAmount());
                    textViewDate.setText(DateFormatters.getTimestampFormat().format(currentItem.getTimestamp().toDate()));
                    textViewApproved.setText(currentItem.getApproved() ?
                            R.string.approved : R.string.waiting_for_approval);
                    Picasso.get().load(task.getResult().toString()).into(listImage);
                } else {
                    Toast.makeText(context, R.string.listing_failure, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
