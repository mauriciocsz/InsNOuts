package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dailyUpdateActivity extends AppCompatActivity {

    TextView nameBill;
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    int quantityOfBills=99, currentBill=0, billIndex=0;
    Button finishBills;

    int[] billsValue = new int[quantityOfBills];
    int[] billsIndex=  new int[quantityOfBills];

    int dayCurrent=16, monthCurrent = 11;

    EditText valueBill;

    String user = "";

    Boolean oldDate=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_update);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        bdCreatorTest bdCreatorTest = new bdCreatorTest();

        String[] dayValues= bdCreatorTest.getCurrentDate();
        dayCurrent = Integer.parseInt(dayValues[0]);
        monthCurrent = Integer.parseInt(dayValues[1]);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oldDate = true;
            dayCurrent = extras.getInt("day");
            monthCurrent = extras.getInt("month");
        }

        getQuantity();

        SQLiteDatabase myDB;
        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);
        int Column1 = c.getColumnIndex("token");
        c.moveToFirst();
        String tokenLocal = c.getString(Column1);


        for (int x=0;x<quantityOfBills;x++){
            billsValue[x]=0;
        } // Seta todas bills como 0



        nameBill = findViewById(R.id.txt_daily_nameBill);
        TextView nextBill = findViewById(R.id.btn_nextBill);
        TextView prevBill = findViewById(R.id.btn_prevBill);
        valueBill = findViewById(R.id.txt_valueBill);
        finishBills = findViewById(R.id.btn_finishBill);

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

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String token = snapshot.child("token").getValue().toString();

                    if(token.equals(tokenLocal)){
                        bdCreatorTest bdc = new bdCreatorTest();
                        rootRef.child("Users").child(user).child("token").setValue(""+bdc.tokenGenerate());
                        for (int x=0;x<quantityOfBills;x++){
                            if(billsValue[x]>=0)
                                rootRef.child("Users").child(user).child(billsIndex[x]+"").child(monthCurrent+"").child(dayCurrent+"").setValue(billsValue[x]);
                        }
                    }else
                        Toast.makeText(dailyUpdateActivity.this, "Erro de Sincronização! Atualizando valores...", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);

            Intent intent = new Intent (dailyUpdateActivity.this, criarDBActivity.class);
            startActivity(intent);

        });// finish Daily Updating

        dailyUpdate(nextBill,prevBill,0);
    }

    private void getQuantity(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                quantityOfBills = snapshot.getValue(Integer.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        rootRef.child("Users").child(user).child("qnt").addListenerForSingleValueEvent(valueEventListener);

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

                for(int x=1;currentBill<quantityOfBills && !cond;x++){

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


        //Log.d("current",""+currentBill);


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(dailyUpdateActivity.this, criarDBActivity.class);
        startActivity(intent);

    }

}