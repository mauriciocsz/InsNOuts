package com.yrmew.insandouts;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
public class bdCreatorTest {
    //Generates a Token for Synchronization between the client and server values
    public String tokenGenerate(){
        String token="";
        for(int x=0;x<5;x++){
            Random rand = new Random();
            int numRandom= rand.nextInt(35);
            if(numRandom>9){
                token+=(char)(numRandom+55);
            }else{
                token+=numRandom;
            }
        }

        return token;

    }

    //Checks the user's connection
    public boolean connectionCheck(){
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    //Get today's date and return an Array
    public String[] getCurrentDate(){

        DateFormat df = new SimpleDateFormat("dd MM yyyy");
        String dateToday = df.format(Calendar.getInstance().getTime());
        String[] dateValues = dateToday.split(" "); // contains Day[0], Month[1], Year[2]

        return dateValues;
    }




}


