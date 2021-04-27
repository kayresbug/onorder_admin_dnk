package com.daon.admin_onorder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daon.admin_onorder.model.OrderModel;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private ArrayList<OrderModel> mArraylist;
    private OrderDialog orderDialog;
    Context context;

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        private TextView table;
        protected TextView menu;
        protected TextView price;
        protected TextView payment;
        protected TextView status;
        protected TextView count;
        protected TextView time;
        protected LinearLayout layout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.table = itemView.findViewById(R.id.orderitem_text_table);
            this.menu = itemView.findViewById(R.id.orderitem_text_menu);
            this.count = itemView.findViewById(R.id.orderitem_text_count);
            this.price = itemView.findViewById(R.id.orderitem_text_price);
            this.payment = itemView.findViewById(R.id.orderitem_text_payment);
            this.status = itemView.findViewById(R.id.orderitem_text_status);
            this.time = itemView.findViewById(R.id.orderitem_text_time);
            this.layout = itemView.findViewById(R.id.orderitem_layout_main);
        }
    }
    public OrderAdapter(Context context, ArrayList<OrderModel> list){
        this.context = context;
        this.mArraylist = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);

        OrderViewHolder viewHolder = new OrderViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.table.setText(mArraylist.get(position).getTable());
        holder.menu.setText(mArraylist.get(position).getMenu());
        holder.price.setText(mArraylist.get(position).getPrice());
        holder.payment.setText(mArraylist.get(position).getPayment());
        holder.status.setText(mArraylist.get(position).getStatus());
        holder.time.setText(mArraylist.get(position).getTime());
        holder.count.setText(mArraylist.get(position).getCount() + " ea");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                orderDialog = new OrderDialog(context, mArraylist.get(position).getNum());
//                orderDialog.show();
                Intent intent_ = new Intent(context, PopupOrderActivity.class);
                intent_.putExtra("title", "주문내용1");
                intent_.putExtra("body", mArraylist.get(position).getMenu());
                intent_.putExtra("price", mArraylist.get(position).getPrice());
                intent_.putExtra("auth_num", mArraylist.get(position).getAuth_num());
                intent_.putExtra("auth_date", mArraylist.get(position).getAuth_date());
                intent_.putExtra("vantr", mArraylist.get(position).getVantr());
                intent_.putExtra("cardbin", mArraylist.get(position).getCardbin());
                intent_.putExtra("menu", mArraylist.get(position).getMenu());
                intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent_);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (mArraylist == null){
            return 0;
        }else {
            return mArraylist.size();
        }
    }
}
