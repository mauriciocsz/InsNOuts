package com.yrmew.insandouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;


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

        btnBill.setOnClickListener(v -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            //Checks if viewOnly is activated
            if(!prefs.getBoolean("viewOnly", false)){
                editBillActivity eBA = new editBillActivity();
                eBA.deleteBill(getItem(position).getIdBill());
            }else
                Toast.makeText(mContext, "Desative o modo View-Only para acessar essa função!", Toast.LENGTH_SHORT).show();

        });
        return convertView;
    }
}

