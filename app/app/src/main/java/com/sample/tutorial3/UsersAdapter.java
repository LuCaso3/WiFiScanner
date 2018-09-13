package com.sample.tutorial3;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {

    private ArrayList<Locatedata> mList = null;
    private Activity context = null;


    public UsersAdapter(Activity context, ArrayList<Locatedata> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView locate_id;


        public CustomViewHolder(View view) {
            super(view);
            this.locate_id = (TextView) view.findViewById(R.id.textView_locate_id);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.locate_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.locate_id.setText(mList.get(position).getLocate_values());

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}