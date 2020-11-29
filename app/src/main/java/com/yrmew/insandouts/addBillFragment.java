package com.yrmew.insandouts;

import android.graphics.RadialGradient;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class addBillFragment extends Fragment {

    String user = "";

    public addBillFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bill, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        RadioButton dividaBtn = getView().findViewById(R.id.rb_dívida);
        RadioButton rendaBtn = getView().findViewById(R.id.rB_renda);
        RadioGroup radioGroup = getView().findViewById(R.id.rB_group);
        Button btnCriar = getView().findViewById(R.id.btn_criar);
        EditText txt_nomeConta = getView().findViewById(R.id.txt_nomeContaCriada);

        rendaBtn.toggle();


        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeConta = txt_nomeConta.getText().toString();

                if(!nomeConta.equals("")) {


                    int checkedID = radioGroup.getCheckedRadioButtonId();
                    int current;
                    if (checkedID == 2131230940) {
                        current = 1;
                    } else {
                        current = -1;
                    }

                    insertValues(nomeConta,current);



                }else{
                    Toast.makeText(getContext(), "Erro! Preencha todos campos e tente novamente!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    public void insertValues(String nomeConta, int typeConta){


        DatabaseReference path = FirebaseDatabase.getInstance().getReference().child("Users").child(user);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Integer qnt = snapshot.child("qnt").getValue(Integer.class);
                boolean cond=false;
                for(int x=qnt;!cond;x++){

                    try{

                        int ab = snapshot.child(x+"").child("type").getValue(Integer.class);

                    }catch(Exception e){

                        cond=true;
                        path.child(x+"").child("type").setValue(typeConta);
                        path.child(x+"").child("name").setValue(nomeConta);
                        path.child("qnt").setValue(qnt+1);

                        bdCreatorTest bcdt = new bdCreatorTest();
                        path.child("token").setValue(bcdt.tokenGenerate());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        path.addListenerForSingleValueEvent(valueEventListener);


    }

    public void delValues(){
        for (int x=0;x<12;x++){
            for(int y=0;y<31;y++){
                ArrayList<Integer> valores = new ArrayList<Integer>();
                valores.add(x,y);
            }
        }
    }
}