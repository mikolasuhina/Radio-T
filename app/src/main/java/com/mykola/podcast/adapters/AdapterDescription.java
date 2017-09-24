package com.mykola.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mykola.podcast.R;
import com.mykola.podcast.models.Description;

import java.util.List;

public class AdapterDescription extends RecyclerView.Adapter<AdapterDescription.ViewHolder> {

    private List<Description> mDescriptions;
    private Context context;

    public AdapterDescription(List<Description> mDescriptions, Context mContext) {
        this.mDescriptions = mDescriptions;
        this.context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Description itemDescription = mDescriptions.get(position);

        if (itemDescription.getLinc() != null) {
            holder.description.setTextColor(Color.BLUE);
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        if (itemDescription.getTime() != null)
            holder.description.setText(itemDescription.getText() + " - " + itemDescription.getTime());
        else {
            holder.description.setText(itemDescription.getText());
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                if (itemDescription.getLinc() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemDescription.getLinc()));
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDescriptions.size();
    }


      class ViewHolder extends RecyclerView.ViewHolder {

        View layout;
        TextView description;

        ViewHolder(View view) {
            super(view);
            layout = view;
            description = (TextView) view.findViewById(R.id.text_description);
        }

    }

}