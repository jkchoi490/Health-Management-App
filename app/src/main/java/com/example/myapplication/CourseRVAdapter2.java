package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseRVAdapter2 extends RecyclerView.Adapter<CourseRVAdapter2.ViewHolder> { //error

    // variable for our array list and context
    private ArrayList<CourseModal2> courseModalArrayList;
    private Context context;

    // constructor
    public CourseRVAdapter2(ArrayList<CourseModal2> courseModalArrayList, Context context) {
        this.courseModalArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference해결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_rv_item2, parent, false); //R.layout.course_rv_item2로 변경
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        CourseModal2 modal = courseModalArrayList.get(position);
        holder.NameTV.setText(modal.getdate());

        holder.DescTV.setText(modal.getsize());
        holder.DurationTV.setText(modal.getdiet());
        holder.TracksTV.setText(modal.getmenu());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return courseModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView NameTV, DescTV, DurationTV, TracksTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            NameTV = itemView.findViewById(R.id.idName);
            DescTV = itemView.findViewById(R.id.idDescription);
            DurationTV = itemView.findViewById(R.id.idDuration);
            TracksTV = itemView.findViewById(R.id.idTracks);
        }
    }
}
