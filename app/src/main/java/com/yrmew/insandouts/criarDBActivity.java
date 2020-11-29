package com.yrmew.insandouts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class criarDBActivity extends AppCompatActivity {

    SQLiteDatabase myDB;
    String TableName ;
    String Data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_d_b);


        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);
        createtable();
    }

    public void createtable(){

        TableName = "tb_contas";

        //myDB.execSQL("DROP TABLE tb_contas");

        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + TableName + "(dia INTEGER, conta_1 MONEY, conta_2 MONEY)");

        //myDB.execSQL("DROP TABLE tb_token");
        myDB.execSQL("CREATE TABLE IF NOT EXISTS tb_token (token VARCHAR,qnt INTEGER)");

        Cursor c = myDB.rawQuery("SELECT token FROM tb_token" , null);

        int qnt = c.getCount();

        if(qnt==0){
            myDB.execSQL("INSERT INTO tb_token VALUES ('A4EJR',2) ");
            Toast.makeText(this, "inserto", Toast.LENGTH_SHORT).show();
        }


        Intent intent = new Intent (criarDBActivity.this, HomeActivity.class );
        startActivity(intent);
        finish();

    }
}