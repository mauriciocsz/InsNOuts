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
    int qnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        RadioButton dividaBtn = findViewById(R.id.rb_dívida);
        RadioButton rendaBtn = findViewById(R.id.rB_renda);
        RadioGroup radioGroup = findViewById(R.id.rB_group);
        Button btnCriar = findViewById(R.id.btn_criar);
        EditText txt_nomeConta = findViewById(R.id.txt_nomeContaCriada);

        rendaBtn.toggle();

        SQLiteDatabase myDB;
        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);
        int Column1 = c.getColumnIndex("token");
        c.moveToFirst();
        String tokenLocal = c.getString(Column1);


        btnCriar.setOnClickListener(V -> {

                String nomeConta = txt_nomeConta.getText().toString();

                if(!nomeConta.equals("")) {


                    int checkedID = radioGroup.getCheckedRadioButtonId();
                    int current;

                    Log.d("","aaaaaa"+checkedID);

                    Toast.makeText(this, ""+checkedID, Toast.LENGTH_SHORT).show();

                    if (checkedID == 2131230952) {
                        current = 1;
                    } else {
                        current = -1;
                    }

                    insertValues(nomeConta,current, tokenLocal);



                }else{
                    Toast.makeText(this, "Erro! Preencha todos campos e tente novamente!", Toast.LENGTH_SHORT).show();
                }

        });


    }

    public void insertValues(String nomeConta, int typeConta, String tokenLocal){

        DatabaseReference path = FirebaseDatabase.getInstance().getReference().child("Users").child(user);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String token = snapshot.child("token").getValue().toString();

                Integer qnt = snapshot.child("qnt").getValue(Integer.class);
                boolean cond=false;


                if(token.equals(tokenLocal)){
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


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(addBillActivity.this, HomeActivity.class);
        startActivity(intent);

    }

}