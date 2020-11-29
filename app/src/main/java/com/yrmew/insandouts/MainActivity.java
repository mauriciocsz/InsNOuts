package com.yrmew.insandouts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference  reference;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Users");


        TextView  txto = findViewById(R.id.textViewTest);

        //int day = LocalDate.now().getDayOfMonth(); // Get Today's Date
        //int month = LocalDate.now().getMonthValue(); // Get Today's Month


        //criador.createBill(0,16,"Kaio", "Renda");
        //criador.createBill(1,12,"Kaio","Dívidas");

        Intent intent = new Intent (MainActivity.this, criarDBActivity.class );
        startActivity(intent);
        finish();


    }


}