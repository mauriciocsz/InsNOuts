package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HomeActivity extends AppCompatActivity {


    // TODO ADICIONAR modo View-Only
    // TODO Checar mês e atualizar
    // TODO Checar ano e atualizar

    String user = "";
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    int dayCurrent, monthCurrent;
    SQLiteDatabase myDB;
    Integer qntty;
    Boolean oldDate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
        TextView returnButton = findViewById(R.id.txt_home_return);
        saldoAtual.setText("Carregando...");
        Button btn_daily = findViewById(R.id.btn_addDaily);
        ImageView imv_add = findViewById(R.id.imV_addConta);
        ImageView imv_edit = findViewById(R.id.imV_editarContas);
        ImageView btn_switch = findViewById(R.id.btn_home_switch);
        ImageView btn_logOut = findViewById(R.id.btn_home_logout);
        TextView txt_data = findViewById(R.id.txt_diaMes);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        bdCreatorTest bdCreatorTest = new bdCreatorTest();
        String[] datas = bdCreatorTest.getCurrentDate();

        dayCurrent=Integer.parseInt(datas[0]);
        monthCurrent=Integer.parseInt(datas[1]);


        txt_data.setText(dayCurrent+"/"+monthCurrent);

        //Date Listener for when the user inputs a Date at the calendar
        DatePickerDialog.OnDateSetListener mDateSetListener = (datePicker, year, month, day) -> {

            if(day!=Integer.parseInt(datas[0]) || month+1!=Integer.parseInt(datas[1])){

                dayCurrent=day;
                monthCurrent=month+1;
                oldDate=true;
                txt_data.setText(dayCurrent+"/"+monthCurrent);
                loadDaily();
                returnButton.setVisibility(View.VISIBLE);

            }else{
                dayCurrent=Integer.parseInt(datas[0]);
                monthCurrent=Integer.parseInt(datas[1]);
                oldDate=false;
                txt_data.setText(dayCurrent+"/"+monthCurrent);
                loadDailyLocal();
                returnButton.setVisibility(View.GONE);
            }



        };
        // Button to Return to Today's date
        returnButton.setOnClickListener(v ->{

            dayCurrent=Integer.parseInt(datas[0]);
            monthCurrent=Integer.parseInt(datas[1]);
            oldDate=false;
            txt_data.setText(dayCurrent+"/"+monthCurrent);
            loadDailyLocal();
            returnButton.setVisibility(View.GONE);

        });
        // Go to "Insert Daily Bills" fragment
        btn_daily.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, dailyUpdateActivity.class);
                if(oldDate){
                    intent.putExtra("day",dayCurrent);
                    intent.putExtra("month",monthCurrent);
                }

                startActivity(intent);
        });
        // Go to "Add Bill" fragment
        imv_add.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, addBillActivity.class);
                startActivity(intent);

        });
        // Go to "Edit Bill" fragment
        imv_edit.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, editBillActivity.class);
                startActivity(intent);
        });
        // Switch Between Monthly/Daily view
        btn_switch.setOnClickListener(v ->{
            if (txt_data.getText().toString().equals(dayCurrent+"/"+monthCurrent)) {
                if (!oldDate)
                    loadMonthlyLocal();
                else
                    loadMonth();
                txt_data.setText(getMonthName(monthCurrent)+"");
            }
            else{
                if(!oldDate)
                    loadDailyLocal();
                else
                    loadDaily();
                txt_data.setText(dayCurrent+"/"+monthCurrent);
            }
        });
        // Choose another date to check values
        txt_data.setOnClickListener(v ->{

            DatePickerDialog dialog = new DatePickerDialog(HomeActivity.this, mDateSetListener, Integer.parseInt(datas[2]),Integer.parseInt(datas[1])-1,Integer.parseInt(datas[0]));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });
        // Logs Out
        btn_logOut.setOnClickListener(v -> logOut());

        checkTokens();

    }

    // Check the client's Token and compares it to the Current one
    private void checkTokens(){

        //SQLiteDatabase myDB;
        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);

        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);

        int Column1 = c.getColumnIndex("token");
        int Column2 = c.getColumnIndex("qnt");

        c.moveToFirst();

        String tokenLocal = c.getString(Column1);
        Integer qntLocal = c.getInt(Column2);
        qntty= qntLocal;


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String token = snapshot.child("token").getValue().toString();

                if(tokenLocal.equals(token)){
                    loadDailyLocal();
                }
                else{

                    Integer qnt = snapshot.child("qnt").getValue(Integer.class);

                    myDB.execSQL("DROP TABLE tb_contas");
                    myDB.execSQL("CREATE TABLE IF NOT EXISTS tb_contas" + "(dia INTEGER)");

                    for(int x=0;x<qnt;x++){
                       myDB.execSQL("ALTER TABLE tb_contas ADD conta_"+(x+1)+" INTEGER");
                    }


                    myDB.execSQL("DELETE FROM tb_contas");


                    for(int x=0;x<31;x++){
                        int[] values = new int[99];
                        String sqlReq = "INSERT INTO tb_contas VALUES ("+x;
                        int z=0;
                        for(int y=0;y<qnt+z;y++){

                            try{

                                int ab = snapshot.child(y+"").child("type").getValue(Integer.class);

                                try {
                                    values[y] = snapshot.child("" + y).child(""+monthCurrent).child(""+x).getValue(Integer.class) * snapshot.child(y+"").child("type").getValue(Integer.class);
                                }catch (Exception e){
                                    values[y] = 0;
                                }

                                sqlReq+=", "+values[y];

                            }catch(Exception e){

                                z++;

                            }

                        }

                        sqlReq+=");";

                        myDB.execSQL(sqlReq);


                    }
                    myDB.execSQL("UPDATE tb_token SET token = '"+token+"', qnt="+qnt);

                    checkTokens();

                    // Load FB
                } //Rewrite LocalDB data

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);


    }

    //Loads Daily Value based on the Local Database
    private void loadDailyLocal(){

        int soma=0;

        Cursor c = myDB.rawQuery("SELECT * FROM tb_contas WHERE dia="+dayCurrent , null);

        for(int x=0;x<qntty;x++){

            int Column1 = c.getColumnIndex("conta_"+(x+1));
            c.moveToFirst();
            soma += c.getInt(Column1);
            Log.d("adg",""+soma);

        }

        TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
        saldoAtual.setText("R$"+soma+".00");
    }

    //Loads Monthly Value based on the Local Database
    private void loadMonthlyLocal(){

        int soma=0;

        for(int y=0;y<31;y++){

            Cursor c = myDB.rawQuery("SELECT * FROM tb_contas WHERE dia="+y , null);

        for(int x=0;x<qntty;x++){

            int Column1 = c.getColumnIndex("conta_"+(x+1));
            c.moveToFirst();
            soma += c.getInt(Column1);
            Log.d("adg",""+soma);

            }
        }

        TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
        saldoAtual.setText("R$"+soma+".00");
    }

    //Loads Monthly Value based on the Online Database
    public void loadMonth(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Integer qnt = snapshot.child("qnt").getValue(Integer.class);
                final int[] billsSum = {0};
                final int[] x = {0};
                final int[] y = {0};


                for ( x[0] = 0; x[0]<qnt; x[0]++){
                    for(y[0]=0;y[0]<30;y[0]++) {

                        try{
                            billsSum[0]+= snapshot.child(x[0]+"").child(monthCurrent+"").child(y[0]+"").getValue(Integer.class) * snapshot.child(x[0]+"").child("type").getValue(Integer.class);
                        }catch (Exception e){

                        }

                    }
                }

                TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
                saldoAtual.setText("R$"+billsSum[0]+".00");


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);


    }

    //Loads Daily Value based on the Online Database
    public void loadDaily(){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Integer qnt = snapshot.child("qnt").getValue(Integer.class);

                final int[] billsSum = {0};
                final int[] x = {0};

                for ( x[0] = 0; x[0]<qnt; x[0]++){
                    try{
                        billsSum[0]+= snapshot.child(x[0]+"").child(monthCurrent+"").child(dayCurrent+"").getValue(Integer.class) * snapshot.child(x[0]+"").child("type").getValue(Integer.class);
                    }catch (Exception e){

                    }

                }
                TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
                saldoAtual.setText("R$"+billsSum[0]+".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        rootRef.child("Users").child(user).addListenerForSingleValueEvent(valueEventListener);

    }

    //Converts a month's number to it's written counterpart
    private String getMonthName(int monthCurrent){

        String[] Months = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        return Months[monthCurrent-1];
    }

    // Returns to the previous page (Home)
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);

    }

    //Logs off the account
    private void logOut(){

        Intent intent = new Intent(HomeActivity.this, loginActivity.class);
        startActivity(intent);
        finish();

    }

}