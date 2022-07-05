package com.almusand.aaber.ui.balance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almusand.aaber.R;
import com.almusand.aaber.model.Payment;
import com.almusand.aaber.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class MyPaymentsAdapter extends RecyclerView.Adapter<MyPaymentsAdapter.StoresViewHolder> {

    private List<Payment> paymentList = new ArrayList<>();

    public MyPaymentsAdapter() {
    }

    public void setList(List<Payment> paymentList) {
        this.paymentList = paymentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment, parent, false);

        return new StoresViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {

        Payment payment = paymentList.get(position);

        holder.bind(payment);

    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class StoresViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout lyContainer;
        private TextView tvPaymentStatus;
        private TextView tvPaymentTime;
        private TextView tvPaymentTotal;


        StoresViewHolder(@NonNull View itemView) {
            super(itemView);

            lyContainer = itemView.findViewById(R.id.ly_container);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            tvPaymentTime = itemView.findViewById(R.id.tv_payment_time);
            tvPaymentTotal = itemView.findViewById(R.id.tv_payment_total);


        }

        void bind(final Payment payment) {

            tvPaymentStatus.setText(payment.getStatus());
            tvPaymentTotal.setText(String.format("%s %s", Double.parseDouble(payment.getDeliveryCost())
                    + Double.parseDouble(payment.getExtraCost()), itemView.getContext().getResources().getString(R.string.sar)));

            tvPaymentTime.setText(Utilities.convertFromTimeStamp(payment.getCreatedAt()));
        }


    }


}
