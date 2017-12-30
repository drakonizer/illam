package com.namboodiri.illam;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public ArrayList<String> myValues;
    public static int caller = 0;
    public static int toSend = 0;
    public RecyclerViewAdapter (ArrayList<String> myValues){
        this.myValues= myValues;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.myTextView.setText(myValues.get(position));
    }

    @Override
    public int getItemCount() {
        return myValues.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView myTextView;
        public MyViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.text_cardview);
            CardView myCard = itemView.findViewById(R.id.card);
            myCard.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    String selected = (String)myTextView.getText();
                    Intent intent;
                    if(caller == 0) {
                        intent = new Intent(v.getContext(), ResultsActivity.class);
                        intent.putExtra("KEY", selected);
                    }
                    else
                    {
                        intent = new Intent(v.getContext(), RelationshipSearch.class);
                        intent.putExtra("NAME", selected);
                        intent.putExtra("ACTION", toSend);
                        caller = 0;
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}