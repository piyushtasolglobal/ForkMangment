package com.forkmang.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.forkmang.R;
import com.forkmang.activity.BookingTableScreen2;
import com.forkmang.models.BookTable;

import java.util.ArrayList;


public class BookTableAdapter extends RecyclerView.Adapter<BookTableAdapter.BookTableItemHolder> {
    Activity activity;
    ArrayList<BookTable> bookTable_dataArrayList;

    public BookTableAdapter(Activity activity, ArrayList<BookTable> bookTable_dataArrayList) {
        this.activity = activity;
        this.bookTable_dataArrayList = bookTable_dataArrayList;
    }

    public BookTableAdapter(Activity activity) {
        this.activity = activity;

    }

    /*book_table_cell*/
    @NonNull
    @Override
    public BookTableItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_table_cell, null);
        BookTableItemHolder bookTableItemHolder = new BookTableItemHolder(v);
        return bookTableItemHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookTableItemHolder holder, int position) {
        BookTable bookTable = bookTable_dataArrayList.get(position);
        holder.txtrestroname.setText(bookTable.getRest_name());
        holder.txt_endtime.setText(bookTable.getEndtime());
        holder.txttotalkm.setText(bookTable.getDistance()+" Km");

    }

    @Override
    public int getItemCount(){
       return bookTable_dataArrayList.size();

    }

    public class BookTableItemHolder extends RecyclerView.ViewHolder {
        ImageView imgproduct;
        TextView txtrestroname, txt_endtime, txt_distance,txttotalkm,txt_ratingno;
        RatingBar rating_bar;
        RelativeLayout relative_view;
        public BookTableItemHolder(@NonNull View itemView) {
            super(itemView);
            relative_view = itemView.findViewById(R.id.relative_view);
            txtrestroname=itemView.findViewById(R.id.txtrestroname);
            txt_endtime=itemView.findViewById(R.id.txt_endtime);
            txt_distance=itemView.findViewById(R.id.txt_distance);
            txttotalkm=itemView.findViewById(R.id.txttotalkm);
            txt_ratingno=itemView.findViewById(R.id.txt_ratingno);
            imgproduct=itemView.findViewById(R.id.imgproduct);
            rating_bar=itemView.findViewById(R.id.rating_bar);

            relative_view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Intent intent = new Intent(activity, BookingTableScreen2.class);
                activity.startActivity(intent);
                //activity.finish();
            });

        }
    }

}
