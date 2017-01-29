package com.example.oalex.carnetvirtual;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vlad_ on 29.01.2017.
 */

public class Refresh  {
    public void LogIn(final Context context) {

        Response.Listener<String> loginListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    boolean is_email_right = jsonResponse.getBoolean("is_email_right");
                    boolean is_password_right = jsonResponse.getBoolean("is_password_right");
                    if(success){

                        if (!is_email_right)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setMessage("Email-ul e gresit.").setNegativeButton("Inapoi",null).create().show();
                        }
                        else if (!is_password_right)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setMessage("Parola e necorespunzatoare.").setNegativeButton("Inapoi",null).create().show();
                        }
                        else
                        {

                            String SName = jsonResponse.getString("SName");
                            String SAddress = jsonResponse.getString("SAddress");
                            String SPhone = jsonResponse.getString("SPhone");
                            String CName = jsonResponse.getString("CName");
                            String STName = jsonResponse.getString("STName");
                            String STFirstName = jsonResponse.getString("STFirstName");
                            //String STEmail = jsonResponse.getString("STEmail");
                            String STSerialNr = jsonResponse.getString("STSerialNr");
                            //String STCnp = jsonResponse.getString("STCnp");
                            String STAddress = jsonResponse.getString("STAddress");
                            String STPhone = jsonResponse.getString("STPhone");
                            Integer Grade_nr = jsonResponse.getInt("Grade_nr");
                            Integer Presence_nr = jsonResponse.getInt("Presence_nr");

                            String STPicture = jsonResponse.getString("STPicture");
                            byte[] byteArray = STPicture.getBytes("UTF-16");  //Transforma poza in binar
                            byte[] data = Base64.decode(byteArray, Base64.DEFAULT); // decodeaza poza cryptata in base 64
                            Bitmap STPicture_bm = BitmapFactory.decodeByteArray(data, 0 ,data.length); //transforma in bitmap

                            new Student(SName,SAddress,SPhone,CName,STName,STFirstName,STPicture_bm,Serialization.serialization.email,Serialization.serialization.password,STSerialNr,null,STAddress,STPhone);
                            Serialization.saveSerializable(context.getApplicationContext());
                            context.startActivity(new Intent(context, Main.class));

                            for(int i=0;i<Presence_nr;i++)
                            {
                                JSONObject presence = jsonResponse.getJSONObject("Presence"+i);
                                String PDate = presence.getString("PDate");
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                Date date = format.parse(PDate);
                                Boolean PValue = presence.getBoolean("PValue");
                                String SBName = presence.getString("SBName");

                                new Presences(date,PValue,SBName);
                            }

                            for(int i=0;i<Grade_nr;i++)
                            {
                                JSONObject presence = jsonResponse.getJSONObject("Grade"+i);
                                String GDate = presence.getString("GDate");
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                Date date = format.parse(GDate);
                                Integer GValue = presence.getInt("GValue");
                                String SBName = presence.getString("SBName");
                                new Grades(date,GValue,SBName);
                            }




                        }

                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setMessage("Ups.. S-a intamplat ceva neprevazut").setNegativeButton("Inapoi",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        };
        _Login_Request login_Request = new _Login_Request(Serialization.serialization.email,Serialization.serialization.password,loginListener);
        RequestQueue login_Queue = Volley.newRequestQueue(context);
        login_Queue.add(login_Request);


    }


}
