package com.example.myapplication;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{

    private ArrayList<DictionaryCameraAnalyze> mList;
    private int textSize; //글씨 크기

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;
        protected TextView nut_name;
        protected TextView nut_size;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.id_listitem);
            this.nut_name = (TextView) view.findViewById(R.id.nut_listitem);
            this.nut_size = (TextView) view.findViewById(R.id.nutsize_listitem);
        }
    }

    public CustomAdapter(ArrayList<DictionaryCameraAnalyze> list) {
        this.mList = list;
    }


    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_camera_analyze, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        viewholder.nut_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        viewholder.nut_size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.id.setGravity(Gravity.CENTER);
        viewholder.nut_name.setGravity(Gravity.CENTER);
        viewholder.nut_size.setGravity(Gravity.CENTER);



        viewholder.id.setText(mList.get(position).getId());
        viewholder.nut_name.setText(mList.get(position).getNut_name());
        viewholder.nut_size.setText(mList.get(position).getNut_size());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public void setTextSizes(int textSize) {
        this.textSize = textSize;
        notifyDataSetChanged();
    }


}
