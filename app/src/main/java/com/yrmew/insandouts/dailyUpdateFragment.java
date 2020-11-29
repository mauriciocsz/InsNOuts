package com.yrmew.insandouts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dailyUpdateFragment extends Fragment {

    TextView nameBill;
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    // TODO: Rename and change types of parameters

    public dailyUpdateFragment() {
        // Required empty public constructor

    }

    int quantityOfBills=99, currentBill=0, billIndex=0;
    Button finishBills;

    int[] billsValue = new int[quantityOfBills];
    int[] billsIndex=  new int[quantityOfBills];

    int dayCurrent=16, monthCurrent = 11;

    EditText valueBill;

    String user = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        quantityOfBills = Integer.parseInt(getArguments().getString("qntBills"));
        return inflater.inflate(R.layout.fragment_daily_update, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        for (int x=0;x<quantityOfBills;x++){
            billsValue[x]=0;
        } // Seta todas bills como 0

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

         nameBill = getView().findViewById(R.id.txt_daily_nameBill);
         TextView nextBill = getView().findViewById(R.id.btn_nextBill);
         TextView prevBill = getView().findViewById(R.id.btn_prevBill);
          valueBill = getView().findViewById(R.id.txt_valueBill);
         finishBills = getView().findViewById(R.id.btn_finishBill);

        nameBill.setText("Carregando...");

        nextBill.setOnClickListener(v -> {
                billsValue[currentBill]=Integer.parseInt(valueBill.getText().toString());
                currentBill++;
                valueBill.setText("");
                dailyUpdate(nextBill,prevBill,1);

        }); // move to Next Bill
        prevBill.setOnClickListener(v -> {

                billsValue[currentBill]=Integer.parseInt(valueBill.getText().toString());
                currentBill--;
                valueBill.setText("");
                dailyUpdate(nextBill,prevBill,-1);


        });// move to Previous Bill

        finishBills.setOnClickListener(v -> {
                billsValue[currentBill]=Integer.parseInt(valueBill.getText().toString());

                bdCreatorTest bdc = new bdCreatorTest();

                rootRef.child("Users").child(user).child("token").setValue(""+bdc.tokenGenerate());

                for (int x=0;x<=quantityOfBills;x++){
                    if(billsValue[x]>=0)
                        rootRef.child("Users").child(user).child(billsIndex[x]+"").child(monthCurrent+"").child(dayCurrent+"").setValue(billsValue[x]);

                }

                bdCreatorTest bdct = new bdCreatorTest();

               rootRef.child("Users").child(user).child("token").setValue(bdct.tokenGenerate());


                Intent intent = new Intent (getActivity(), criarDBActivity.class);
                startActivity(intent);

        });

        dailyUpdate(nextBill,prevBill,0);
    }


    public void dailyUpdate(TextView nextBill, TextView prevBill, int counter){

        if(billsValue[currentBill]>=0)
            valueBill.setText(billsValue[currentBill]+"");

        DatabaseReference path  = rootRef.child("Users").child(user);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Boolean cond=false;

                //currentBill+=counter;

                for(int x=1;x<quantityOfBills && !cond;x++){

                    try{

                        int ab = snapshot.child(billIndex+(x*counter)+"").child("type").getValue(Integer.class);

                        cond=true;
                        nameBill.setText(snapshot.child(billIndex+(x*counter)+"").child("name").getValue().toString());

                        billIndex+=(x*counter);
                        billsIndex[currentBill]=billIndex;

                        //Log.d("teste",x+"= "+ab);
                    }catch (Exception e){


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        path.addListenerForSingleValueEvent(valueEventListener);

        if(quantityOfBills-currentBill>1)
            nextBill.setVisibility(View.VISIBLE);
        else
            nextBill.setVisibility(View.INVISIBLE);
        if(currentBill>0)
            prevBill.setVisibility(View.VISIBLE);
        else
            prevBill.setVisibility(View.INVISIBLE);



        int color;
        if(quantityOfBills-1==currentBill) {
            color = R.color.purple_500;
            finishBills.setActivated(true);

        }else{
            color = R.color.common_google_signin_btn_text_light_disabled;
            finishBills.setActivated(false);
        }

        finishBills.setBackgroundColor(getResources().getColor(color));


        Log.d("current",""+currentBill);


    }

}