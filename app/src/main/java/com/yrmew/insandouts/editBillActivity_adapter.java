package com.yrmew.insandouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class editBillActivity_adapter extends ArrayAdapter<editBillActivity_bill> {

    private Context mContext;
    private int mResource;

    public editBillActivity_adapter(@NonNull Context context, int resource, @NonNull ArrayList<editBillActivity_bill> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView nameBill = convertView.findViewById(R.id.bill_name);
        TextView typeBill = convertView.findViewById(R.id.bill_type);
        Button btnBill = convertView.findViewById(R.id.bill_btn);

        nameBill.setText(getItem(position).getNameBill());
        typeBill.setText(getItem(position).getTypeBill());

        btnBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBillActivity eBA = new editBillActivity();

                //Toast.makeText(getContext(), ""+getItem(position).getIdBill(), Toast.LENGTH_SHORT).show();

                eBA.deleteBill(getItem(position).getIdBill());
            }
        });



        return convertView;
    }
}

