package com.yrmew.insandouts;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class editBillActivity extends AppCompatActivity {

    Context context = this;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String user = mAuth.getCurrentUser().getUid();
    int qnt;
    String tokenLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);


        ArrayList<editBillActivity_bill> arrayList = new ArrayList<>();
        ListView list = findViewById(R.id.listView_editBill);

        SQLiteDatabase myDB;
        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);
        int Column1 = c.getColumnIndex("token");
        c.moveToFirst();
         tokenLocal = c.getString(Column1);



        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                 qnt = snapshot.child("qnt").getValue(Integer.class);

                int y=0;

                for(int x=0;x<qnt+y;x++){

                    try{

                        String name = snapshot.child(""+x).child("name").getValue().toString();
                        String type;
                        if (snapshot.child(""+x).child("type").getValue(Integer.class)==1){
                            type="Renda";
                        }else
                            type="Dívida";

                        arrayList.add(new editBillActivity_bill(name,type,x));


                    }catch (Exception e){
                        y++;
                    }

                }

                editBillActivity_adapter adapter = new editBillActivity_adapter(context, R.layout.activity_edit_bill_row,arrayList);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);

        //arrayList.add(new editBillActivity_bill("Renda","Dívida",0));
        //arrayList.add(new editBillActivity_bill("Dívida","Renda",1));


        editBillActivity_adapter adapter = new editBillActivity_adapter(this, R.layout.activity_edit_bill_row,arrayList);
        list.setAdapter(adapter);

    }


    public void deleteBill(int billID){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    qnt = snapshot.child("qnt").getValue(Integer.class);
                    rootRef.child("Users").child(user).child(""+billID).removeValue();
                    int newQnt = qnt-1;
                    rootRef.child("Users").child(user).child("qnt").setValue(newQnt);
                    bdCreatorTest bdct = new bdCreatorTest();
                    rootRef.child("Users").child(user).child("token").setValue(bdct.tokenGenerate());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(editBillActivity.this, HomeActivity.class);
        startActivity(intent);

    }

}