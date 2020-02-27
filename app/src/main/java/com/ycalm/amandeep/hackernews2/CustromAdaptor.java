package com.ycalm.amandeep.hackernews2;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by amandeep on 23-01-2018.
 */

class CustromAdaptor extends RecyclerView.Adapter<CustromAdaptor.ViewHolder> {
    private Context context;
    private ArrayList<String> strings;
    public CustromAdaptor(Context context, ArrayList<String> titlesArrayList) {
        this.context = context;
        this.strings = titlesArrayList;
    }


    @Override
    public CustromAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustromAdaptor.ViewHolder holder, int position) {
        String thisItemName = strings.get(position);
        holder.title.setText(thisItemName);
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
