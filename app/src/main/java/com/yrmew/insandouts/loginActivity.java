package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btn_login_entrar);
        EditText userName = findViewById(R.id.txt_login_user);
        EditText userPwd = findViewById(R.id.txt_login_senha);
        TextView cadastro = findViewById(R.id.txt_login_cadastro);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {

            String user = userName.getText().toString();
            String password = userPwd.getText().toString();

            if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){

                mAuth.signInWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            deleteDB();
                            Intent intent = new Intent(loginActivity.this, criarDBActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(loginActivity.this, "Erro! Cheque o usuário e senha e tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Toast.makeText(loginActivity.this, "Erro! Preencha todos campos antes de continuar.", Toast.LENGTH_SHORT).show();
            }
        });

        cadastro.setOnClickListener(v ->{

            Intent intent = new Intent(loginActivity.this, cadastroActivity.class);
            startActivity(intent);

        });


    }

    private void deleteDB(){
        try{
            this.deleteDatabase("db_insouts");}
        catch (Exception e){
        }
    }
}