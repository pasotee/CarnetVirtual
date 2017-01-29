package com.example.oalex.carnetvirtual;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LoginActivity extends Activity
{
    private  EditText mEmailView;
    private EditText mPasswordView;
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar();
            }
        });
    }

    private void ProgressBar()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // Test for connection
        if (netInfo!= null && netInfo.isConnectedOrConnecting()) {}
        else {

            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setMessage("Conexiune la internet inexistenta.").setNegativeButton("Inapoi",null).create().show();
            // No conection
            return;
        }

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Va rugam asteptati.");
        progressDialog.setTitle("Incarcare date");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    LogIn();
                    Thread.sleep(5000);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void LogIn() {

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
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
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setMessage("Email-ul e gresit.").setNegativeButton("Inapoi",null).create().show();
                        }
                        else if (!is_password_right)
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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
                            Integer Chat_nr = jsonResponse.getInt("");

                            String STPicture = jsonResponse.getString("STPicture");
                            byte[] byteArray = STPicture.getBytes("UTF-16");  //Transforma poza in binar
                            byte[] data = Base64.decode(byteArray, Base64.DEFAULT); // decodeaza poza cryptata in base 64
                            Bitmap STPicture_bm = BitmapFactory.decodeByteArray(data, 0 ,data.length); //transforma in bitmap

                            new Student(SName,SAddress,SPhone,CName,STName,STFirstName,STPicture_bm,mEmail,mPassword,STSerialNr,null,STAddress,STPhone);
                            Serialization.saveSerializable(getApplicationContext());
                            startActivity(new Intent(LoginActivity.this, Main.class));

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
                                JSONObject grade = jsonResponse.getJSONObject("Grade"+i);
                                String GDate = grade.getString("GDate");
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                Date date = format.parse(GDate);
                                Integer GValue = grade.getInt("GValue");
                                String SBName = grade.getString("SBName");
                                new Grades(date,GValue,SBName);
                            }

                            for(int i=0;i<Chat_nr;i++)
                            {
                                JSONObject chat = jsonResponse.getJSONObject("Chat"+i);

                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                                String CHDate = chat.getString("CHDate");
                                String CHEDate = chat.getString("CHDate");
                                Date chdate = format.parse(CHDate);
                                Date chedate = format.parse(CHEDate);
                                Integer CHType = chat.getInt("CHType");
                                String CHMessage = chat.getString("CHMessage");
                                new ChatMessage(chdate,chedate,CHMessage,"Pomohaci",CHType);
                            }
                        }
                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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
        _Login_Request login_Request = new _Login_Request(mEmail,mPassword,loginListener);
        RequestQueue login_Queue = Volley.newRequestQueue(LoginActivity.this);
        login_Queue.add(login_Request);


    }
}

