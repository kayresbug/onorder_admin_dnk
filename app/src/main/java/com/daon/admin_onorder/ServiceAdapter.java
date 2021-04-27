package com.daon.admin_onorder;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daon.admin_onorder.model.OrderModel;
import com.daon.admin_onorder.model.ServiceModel;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import java.util.ArrayList;

import static android.content.DialogInterface.*;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.OrderViewHolder> {
    private ArrayList<ServiceModel> mArraylist;
    private ServiceDialog serviceDialog;
    Context context;

    public class OrderViewHolder extends RecyclerView.ViewHolder{
        protected TextView table;
        protected TextView service;
        protected TextView status;
        protected TextView time;
        protected LinearLayout layout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.table = itemView.findViewById(R.id.serviceitem_text_table);
            this.service = itemView.findViewById(R.id.serviceitem_text_body);
            this.status = itemView.findViewById(R.id.serviceitem_text_status);
            this.time = itemView.findViewById(R.id.serviceitem_text_time);
            this.layout = itemView.findViewById(R.id.serviceitem_layout_main);
        }
    }
    public ServiceAdapter(Context context, ArrayList<ServiceModel> list){
        this.context = context;
        this.mArraylist = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent, false);

        OrderViewHolder viewHolder = new OrderViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.table.setText(mArraylist.get(position).getTableNo());
        holder.service.setText(mArraylist.get(position).getService().replace("n", "\n"));
        holder.status.setText(mArraylist.get(position).getStatus());
        holder.time.setText(mArraylist.get(position).getInsertTime());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceDialog = new ServiceDialog(context, mArraylist.get(position).getService().replace("n","\n"), mArraylist.get(position).getNum());
                serviceDialog.show();
                serviceDialog.setOnDismissListener(new ServiceDialog.ServiceDialogListener() {
                    @Override
                    public void onDismissListener(String s) {
                        if (s.equals("ok_dismiss")){
                            mArraylist.get(position).setStatus("접수완료");
                            ArrayList<ServiceModel> tempList1 = new ArrayList<>();
                            ArrayList<ServiceModel> tempList2 = new ArrayList<>();

                            for (int i = 0; i < mArraylist.size(); i++){
                                if (mArraylist.get(i).getStatus().equals("접수대기")){
                                    tempList1.add(mArraylist.get(i));
                                }else if (mArraylist.get(i).getStatus().equals("접수완료")){
                                    tempList2.add(mArraylist.get(i));
                                }
                            }
                            Sam4sPrint sam4sPrint = new Sam4sPrint();
                            sam4sPrint = AdminApplication.getPrinter();
                            Sam4sBuilder builder = new Sam4sBuilder("EPSON", Sam4sBuilder.LANG_KO);
                            try {
                                builder.addPageBegin();
                                builder.addTextFont(Sam4sBuilder.FONT_B);
                                builder.addTextSize(2, 2);
                                builder.addText("다온시스템");
                                builder.addFeedLine(1);
                                builder.addText(mArraylist.get(position).getService());
                                builder.addPageEnd();
                                builder.addCut(Sam4sBuilder.CUT_FEED);
                                sam4sPrint.sendData(builder);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mArraylist.clear();
                            mArraylist.addAll(tempList1);
                            mArraylist.addAll(tempList2);
                            notifyDataSetChanged();
                            serviceDialog.dismiss();
                        }
                    }
                });
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
