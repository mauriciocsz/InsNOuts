package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Arrays;

public class dailyUpdateActivity extends AppCompatActivity {


    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    int quantityOfBills=99, currentBill=0;

    bill[] bills = new bill[quantityOfBills];

    int dayCurrent, monthCurrent;

    EditText valueBill;
    TextView nextBill ;
    TextView prevBill ;
    Button finishBills;
    TextView nameBill;

    String user = "";
    Context context = this;

    Boolean oldDate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_update);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        bdCreatorTest bdCreatorTest = new bdCreatorTest();

        //Get Today's date
        String[] dayValues= bdCreatorTest.getCurrentDate();
        dayCurrent = Integer.parseInt(dayValues[0]);
        monthCurrent = Integer.parseInt(dayValues[1]);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oldDate = true;
            dayCurrent = extras.getInt("day");
            monthCurrent = extras.getInt("month");
        }

        //Gets Local Token
        SQLiteDatabase myDB;
        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);
        int Column1 = c.getColumnIndex("token");
        c.moveToFirst();
        String tokenLocal = c.getString(Column1);

        nameBill = findViewById(R.id.txt_daily_nameBill);
        nextBill = findViewById(R.id.btn_nextBill);
        prevBill = findViewById(R.id.btn_prevBill);
        valueBill = findViewById(R.id.txt_valueBill);
        finishBills = findViewById(R.id.btn_finishBill);

        nameBill.setText("Carregando...");

        //Moves onto the next bill
        nextBill.setOnClickListener(v -> {
            //Saves current bill inside of an array and increases the counter
            bills[currentBill].value = Integer.parseInt(valueBill.getText().toString());
            currentBill++;
            loadBill();
        });

        //Moves onto the previous bill
        prevBill.setOnClickListener(v -> {
            //Saves current bill inside of an array and decreases the counter
            bills[currentBill].value = Integer.parseInt(valueBill.getText().toString());
            currentBill--;
            loadBill();
        });

        //Gets all bill's values and insert them into the Online DataBase
        finishBills.setOnClickListener(v -> {
            //Saves the last bill's value
            bills[currentBill].value=Integer.parseInt(valueBill.getText().toString());

            //Gets the current local token
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            //Using the tokenClass method, compares both Tokens (Firebase and Local) and if they match, proceed.
            bdCreatorTest.callTokenClass(new dailyToken(),prefs.getString("token","A"));
        });

        loadData();
    }

    //Loads all bills based on the Online DataBase
    private void loadData(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int billsFound=0;

                //Sets the quantity of Bills based on the online database
                quantityOfBills = snapshot.child("qnt").getValue(Integer.class);

                for(int index=0;billsFound<quantityOfBills;index++){
                    try{

                        //If you can retrieve the bills type from the online database then this bill exists, therefore save it
                        int test = snapshot.child(index+"").child("type").getValue(Integer.class);

                        String name = snapshot.child(index+"").child("name").getValue().toString();

                        //Creates a Bill object with its info
                        bills[billsFound] = new bill(index,name,0);
                        billsFound++;


                    }catch (Exception exception){

                    }

                }

                //Now that all bills are loaded, load the first one for the user
                loadBill();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);


    }

    //Loads the current bill
    private void loadBill(){

        nextBill.setVisibility(View.VISIBLE);
        prevBill.setVisibility(View.VISIBLE);

        int color = R.color.common_google_signin_btn_text_light_disabled;
        finishBills.setActivated(false);


        //Sets the text onscreen to the bill's info
        nameBill.setText(bills[currentBill].name);
        valueBill.setText(""+bills[currentBill].value);


        //Controls all button's attributes
        if(currentBill==quantityOfBills-1){
            nextBill.setVisibility(View.INVISIBLE);
            color = R.color.purple_500;
            finishBills.setActivated(true);
        }
        else if(currentBill==0)
            prevBill.setVisibility(View.INVISIBLE);

        finishBills.setBackgroundColor(getResources().getColor(color));


    }

    //Bill object that contains its ID, Name and Current Value
    private class bill{

        int ID;
        String name;
        int value;

        public bill(int ID, String name, int value){
            this.ID = ID;
            this.name = name;
            this.value = value;
        }

    }

    //Sets all tokenClasses values so we can use it
    public class dailyToken extends bdCreatorTest.tokenClass {

        public void proceed(Boolean result){
            if(result)
                finishProcess();
            else{
                Toast.makeText(dailyUpdateActivity.this, "Erro de Sincronização! Atualizando valores...", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent (dailyUpdateActivity.this, criarDBActivity.class);
            startActivity(intent);
        }

    }

    //Finishes inserting the values on the online database
    private void finishProcess(){
        bdCreatorTest bdc = new bdCreatorTest();
        rootRef.child("Users").child(user).child("token").setValue(bdc.tokenGenerate());

        //Inserts all values into the Online Database
        for(int x=0;x<quantityOfBills;x++){
            if(bills[x].value>=0)
                rootRef.child("Users").child(user).child(bills[x].ID+"").child(monthCurrent+"").child(dayCurrent+"").setValue(bills[x].value);
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(dailyUpdateActivity.this, criarDBActivity.class);
        startActivity(intent);

    }

}