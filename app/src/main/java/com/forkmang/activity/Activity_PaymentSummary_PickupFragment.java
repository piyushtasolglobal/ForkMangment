package com.forkmang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.forkmang.R;
import com.forkmang.adapter.CartBookingAdapter;

public class Activity_PaymentSummary_PickupFragment extends AppCompatActivity {
    RecyclerView recyclerView;
    Button btn_payment_proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary_pickup);
        recyclerView = findViewById(R.id.recycleview);
        btn_payment_proceed = findViewById(R.id.btn_payment_proceed);
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_PaymentSummary_PickupFragment.this));



        btn_payment_proceed.setOnClickListener(v -> {
            final Intent mainIntent = new Intent(Activity_PaymentSummary_PickupFragment.this, Pickup_ConformationActivity.class);
            startActivity(mainIntent);
        });

        CartBookingAdapter cartBookingAdapter = new CartBookingAdapter(Activity_PaymentSummary_PickupFragment.this );
        recyclerView.setAdapter(cartBookingAdapter);

    }
}