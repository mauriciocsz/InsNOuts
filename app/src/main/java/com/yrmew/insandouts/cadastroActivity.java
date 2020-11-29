package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        EditText usuario = findViewById(R.id.txt_cadastro_email);
        EditText usuariosenha = findViewById(R.id.txt_cadastro_senha);
        EditText senhaC = findViewById(R.id.txt_cadastro_senha2);
        Button btnCadastrar = findViewById(R.id.btn_cadastro_cadastrar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        btnCadastrar.setOnClickListener(v -> {

            String email = usuario.getText().toString();
            String senha = usuariosenha.getText().toString();
            String confsenha = senhaC.getText().toString();

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha) & !TextUtils.isEmpty(confsenha)) { //caso todos os campos estejam preenchidos
                if (senha.equals(confsenha)) { //checa se as senha são iguais

                    mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("qnt").setValue(2);
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("token").setValue("R");
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("0").child("name").setValue("Renda");
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("0").child("type").setValue(1);
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("1").child("name").setValue("Dívida");
                            rootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("1").child("type").setValue(-1);


                            try{
                                this.deleteDatabase("db_insouts");}
                            catch (Exception e){
                            }
                            Intent intent = new Intent(cadastroActivity.this, criarDBActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(cadastroActivity.this, "ERRO! Houve um erro na conexão, cheque sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                        }

                    });

                }
            }

        });
    }
}