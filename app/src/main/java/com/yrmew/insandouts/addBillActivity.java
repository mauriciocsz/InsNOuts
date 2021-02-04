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
import android.widget.ListView;
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

public class addBillActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String user = mAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        RadioButton dividaBtn = findViewById(R.id.rb_dívida);
        RadioButton rendaBtn = findViewById(R.id.rB_renda);
        RadioGroup radioGroup = findViewById(R.id.rB_group);

        Button btnCriar = findViewById(R.id.btn_criar);

        EditText txt_nomeConta = findViewById(R.id.txt_nomeContaCriada);

        //Toggles profit button and get its ID
        rendaBtn.toggle();
        int rendaID = radioGroup.getCheckedRadioButtonId();

        //Retrieves the token from the Local Database
        SQLiteDatabase myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);
        int Column1 = c.getColumnIndex("token");
        c.moveToFirst();
        String tokenLocal = c.getString(Column1);

        btnCriar.setOnClickListener(V -> {

                //Retrieves the bill's name
                String nomeConta = txt_nomeConta.getText().toString();

                //Checks if the bill has a name
                if(!nomeConta.equals("")) {

                    //Gets selected button ID
                    int checkedID = radioGroup.getCheckedRadioButtonId();

                    //Tells if the current bill is either debt or profit
                    int typeConta;

                    // Uses the ID of the radio button to check which one is selected
                    if (checkedID == rendaID) {
                        typeConta = 1;
                    } else {
                        typeConta = -1;
                    }


                    insertValues(nomeConta,typeConta, tokenLocal);

                }else{
                    Toast.makeText(this, "Erro! Preencha todos campos e tente novamente!", Toast.LENGTH_SHORT).show();
                }

        });
    }

    public void insertValues(String nomeConta, int typeConta, String tokenLocal){

        // Sets a path for the Online Database
        DatabaseReference path = FirebaseDatabase.getInstance().getReference().child("Users").child(user);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //Gets "token" fom the Database
                String token = snapshot.child("token").getValue().toString();
                //Gets bills value from the Database
                Integer qnt = snapshot.child("qnt").getValue(Integer.class);

                //Condition that'll be used to confirm that both DBs are synchronized
                boolean cond=false;


                //Checks if both "tokens" match
                if(token.equals(tokenLocal)){
                    //Searches for an empty spot to fit the new bill in
                    for(int x=qnt;!cond;x++){
                        // If we can retrieve the type from the Database, skip and go to the next spot
                        try{
                            int retrievedTest = snapshot.child(x+"").child("type").getValue(Integer.class);
                        }
                        // If no value is retrieved that means this spot is vacant and we can fill it
                        catch(Exception e){

                            cond=true;
                            path.child(x+"").child("type").setValue(typeConta);
                            path.child(x+"").child("name").setValue(nomeConta);
                            path.child("qnt").setValue(qnt+1);

                            bdCreatorTest bcdt = new bdCreatorTest();
                            path.child("token").setValue(bcdt.tokenGenerate());

                        }
                    }

                }else{
                    Toast.makeText(addBillActivity.this, "Erro de Sincronização! Atualizando valores...", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        path.addListenerForSingleValueEvent(valueEventListener);

        Intent intent = new Intent(addBillActivity.this,criarDBActivity.class);
        startActivity(intent);


    } // Method responsible for Inserting the created bill

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(addBillActivity.this, HomeActivity.class);
        startActivity(intent);

    } // Returns to the previous page (Home)

}