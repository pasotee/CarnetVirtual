package com.example.oalex.carnetvirtual;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class StartUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        LinkButtons();
        Serialization.readSerializable(getApplicationContext());

        if(Serialization.serialization!=null)
            LogIn();
    }



    private void LogIn() {
        final String mEmail= Serialization.serialization.email;
        final String mPassword = Serialization.serialization.password;
        Response.Listener<String> loginListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    boolean is_email_right = jsonResponse.getBoolean("is_email_right");
                    boolean is_password_right = jsonResponse.getBoolean("is_password_right");
                    if(success){

                        if (!is_email_right||!is_password_right)
                        {
                            startActivity(new Intent(StartUp.this, LoginActivity.class));
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

                            new Student(SName,SAddress,SPhone,CName,STName,STFirstName,null,mEmail,mPassword,STSerialNr,null,STAddress,STPhone);
                            startActivity(new Intent(StartUp.this, Main.class));
                        }

                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(StartUp.this);
                        alert.setMessage("Ups.. S-a intamplat ceva neprevazut").setNegativeButton("Inapoi",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        Login_Request login_Request = new Login_Request (mEmail,mPassword,loginListener);
        RequestQueue login_Queue = Volley.newRequestQueue(StartUp.this);
        login_Queue.add(login_Request);


    }

    private void LinkButtons()
    {
        Button submit_button = (Button) findViewById(R.id.submit_button);
        Button login_button = (Button) findViewById(R.id.login_button);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitCode();
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartUp.this, LoginActivity.class));
            }
        });
    }

    private void SubmitCode()
    {
        Toast.makeText(getApplicationContext(), Serialization.serialization.email, Toast.LENGTH_LONG).show();

        String input_code = ((EditText)findViewById(R.id.code_editText)).getText().toString();
        Response.Listener<String> loginListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        String FirstName = jsonResponse.getString("CID");
                        String LastName = jsonResponse.getString("SName");
                        String Email = jsonResponse.getString("CName");


                        Intent intent = new Intent(StartUp.this,CreateActivity.class);
                        intent.putExtra("CID",FirstName);
                        intent.putExtra("SName",LastName);
                        intent.putExtra("CName",Email);
                        StartUp.this.startActivity(intent);
                    }
                    else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(StartUp.this);
                        alert.setMessage("Cod inexistent").setNegativeButton("Inapoi",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        Code_Request code_Request = new Code_Request(input_code,loginListener);
        RequestQueue code_Queue = Volley.newRequestQueue(StartUp.this);
        code_Queue.add(code_Request);
    }
}
