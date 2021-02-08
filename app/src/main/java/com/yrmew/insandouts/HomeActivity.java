package com.yrmew.insandouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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


    //TODO: Settings page
    // TODO View-Only mode
    // TODO Check which Year is it so I can delete old values

    String user = "";
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    int dayCurrent, monthCurrent;
    SQLiteDatabase myDB;
    Integer qntty;
    Boolean oldDate = false;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView saldoAtual = findViewById(R.id.txt_saldoAtual);
        TextView returnButton = findViewById(R.id.txt_home_return);
        saldoAtual.setText("Carregando...");
        Button btn_daily = findViewById(R.id.btn_addDaily);
        Button btn_settings = findViewById(R.id.btn_settings);
        ImageView imv_add = findViewById(R.id.imV_addConta);
        ImageView imv_edit = findViewById(R.id.imV_editarContas);
        ImageView btn_switch = findViewById(R.id.btn_home_switch);
        ImageView btn_logOut = findViewById(R.id.btn_home_logout);
        TextView txt_data = findViewById(R.id.txt_diaMes);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();

        bdCreatorTest bdCreatorTest = new bdCreatorTest();
        String[] datas = bdCreatorTest.getCurrentDate();


        /*bdCreatorTest.getTokenData(new com.yrmew.insandouts.bdCreatorTest.SimpleCallback<String>() {
            @Override
            public void callback(String data) {
                Toast.makeText(HomeActivity.this, ""+data, Toast.LENGTH_SHORT).show();
            }
        });

        bdCreatorTest.getTokenData(data -> Toast.makeText(HomeActivity.this, ""+data, Toast.LENGTH_SHORT).show());
    */

        dayCurrent=Integer.parseInt(datas[0]);
        monthCurrent=Integer.parseInt(datas[1]);


        txt_data.setText(dayCurrent+"/"+monthCurrent);

        //Date Listener for when the user inputs a Date at the calendar
        DatePickerDialog.OnDateSetListener mDateSetListener = (datePicker, year, month, day) -> {


            // If the date selected is in the same month, Load the data Using the Local Database
            if(day!=Integer.parseInt(datas[0]) && month+1==Integer.parseInt(datas[1])){
                dayCurrent=day;
                monthCurrent=month+1;
                oldDate=true;
                txt_data.setText(dayCurrent+"/"+monthCurrent);
                loadDailyLocal();
                returnButton.setVisibility(View.VISIBLE);
            }
            // If the date selected is in a different Month, use the online database
            else if(month+1!=Integer.parseInt(datas[1])){

                dayCurrent=day;
                monthCurrent=month+1;
                oldDate=true;
                txt_data.setText(dayCurrent+"/"+monthCurrent);
                loadDaily();
                returnButton.setVisibility(View.VISIBLE);

            }
            // Else if the date selected is the current date, remove the "OldDate" state
            else{
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

            //Checks if viewOnly is activated
            if(!prefs.getBoolean("viewOnly", false)) {
                Intent intent = new Intent(HomeActivity.this, dailyUpdateActivity.class);
                if (oldDate) {
                    intent.putExtra("day", dayCurrent);
                    intent.putExtra("month", monthCurrent);
                }

                startActivity(intent);
            }else
                Toast.makeText(this, "Desative o modo View-Only para acessar essa função!", Toast.LENGTH_SHORT).show();
        });
        // Go to "Add Bill" fragment
        imv_add.setOnClickListener(v -> {

            //Checks if viewOnly is activated
            if(!prefs.getBoolean("viewOnly", false)){
                Intent intent = new Intent(HomeActivity.this, addBillActivity.class);
                startActivity(intent);
            }else
                Toast.makeText(this, "Desative o modo View-Only para acessar essa função!", Toast.LENGTH_SHORT).show();

        });
        // Go to "Edit Bill" fragment
        imv_edit.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, editBillActivity.class);
                startActivity(intent);
        });
        // Switch Between Monthly/Daily view
        btn_switch.setOnClickListener(v ->{


            if (txt_data.getText().toString().equals(dayCurrent+"/"+monthCurrent)) {
                if (!oldDate || monthCurrent==Integer.parseInt(datas[1]))
                    loadMonthlyLocal();
                else
                    loadMonth();
                txt_data.setText(getMonthName(monthCurrent)+"");
            }
            else{
                if(!oldDate || monthCurrent==Integer.parseInt(datas[1]))
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

        //Go to "Settings" activity
        btn_settings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, settingsActivity.class);
            startActivity(intent);
        });



        checkTokens();


    }

    // Check the client's Token and compares it to the Current one
    private void checkTokens(){

        myDB = this.openOrCreateDatabase("db_insnouts", MODE_PRIVATE, null);

        Cursor c = myDB.rawQuery("SELECT * FROM tb_token" , null);

        //Gets local database's Token and Columns quantity
        int Column1 = c.getColumnIndex("token");
        int Column2 = c.getColumnIndex("qnt");
        c.moveToFirst();
        String tokenLocal = c.getString(Column1);
        Integer qntLocal = c.getInt(Column2);
        qntty= qntLocal;


        //Compares both local and online tokens
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String token = snapshot.child("token").getValue().toString();


                //If both tokens are equal, the user is up to date so load all bills
                if(tokenLocal.equals(token)){
                    loadDailyLocal();

                }

                //If they differ somehow, the user's data is outdated and we need to alter his local database
                else{


                    //Gets the quantity of bills from the online database
                    Integer qnt = snapshot.child("qnt").getValue(Integer.class);

                    //Re-Creates the entire database
                    myDB.execSQL("DROP TABLE tb_contas");
                    myDB.execSQL("CREATE TABLE IF NOT EXISTS tb_contas" + "(dia INTEGER)");

                    //Adds columns for each existing bill
                    for(int x=0;x<qnt;x++){
                       myDB.execSQL("ALTER TABLE tb_contas ADD conta_"+(x+1)+" INTEGER");
                    }

                    //Clears the database(?)
                    myDB.execSQL("DELETE FROM tb_contas");


                    //TODO: Clean this part

                    //Gets every bill value and inserts them all in the local database
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

                    //Checks both the local and online databases again
                    checkTokens();

                    // Load FB
                }

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
            //Log.d("Dia "+y,"conta "+x+ " = "+c.getInt(Column1));
            soma += c.getInt(Column1);

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

                int[] billsID = new int[qnt];
                int billsFound =0;

                //Retrieves all bill's IDs
                for(int x=0;billsFound<qnt;x++){

                    try{
                        int test = snapshot.child(x+"").child("type").getValue(Integer.class);
                        billsID[billsFound]=x;
                        billsFound++;
                    }catch(Exception e){
                    }

                }

                //Sum of all bill's values
                final int[] billsSum = {0};

                //Retrieves all values from the Online Database in the current month
                for(int i=0;i<qnt;i++){
                    for(int j=1;j<=31;j++){

                        try{
                            billsSum[0]+= snapshot.child(billsID[i]+"").child(monthCurrent+"").child(j+"").getValue(Integer.class) * snapshot.child(billsID[i]+"").child("type").getValue(Integer.class);

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
                int[] billsID = new int[qnt];
                int billsFound =0;

                //Retrieves all bill's IDs
                for(int x=0;billsFound<qnt;x++){

                    try{
                        int test = snapshot.child(x+"").child("type").getValue(Integer.class);
                        billsID[billsFound]=x;
                        billsFound++;
                    }catch(Exception e){
                    }

                }

                //Retrieves all values from the Online Database in the current day
                for(int i=0;i<qnt;i++){
                    try{
                        billsSum[0]+= snapshot.child(billsID[i]+"").child(monthCurrent+"").child(dayCurrent+"").getValue(Integer.class) * snapshot.child(billsID[i]+"").child("type").getValue(Integer.class);

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