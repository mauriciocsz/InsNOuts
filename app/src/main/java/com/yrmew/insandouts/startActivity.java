package com.yrmew.insandouts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class startActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        bdCreatorTest bdCreatorTest = new bdCreatorTest();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentUser!=null){
                    String uid = mAuth.getCurrentUser().getUid();
                    Intent intent = new Intent(startActivity.this, criarDBActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(startActivity.this, loginActivity.class);
                    startActivity(intent);
                }
            }
        }, 500);
    }
}