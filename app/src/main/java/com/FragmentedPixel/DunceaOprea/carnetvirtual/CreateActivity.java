package com.FragmentedPixel.DunceaOprea.carnetvirtual;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    String Class;
    String CID;
    String Code;

    final int PIC_CROP = 2;

    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap STimage;
    boolean writeAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);


        Intent intent = getIntent();

        EditText clasa_editText = (EditText) findViewById(R.id.clasa_editText);
        Class = intent.getStringExtra("CName");
        CID = intent.getStringExtra("CID");
        Code = intent.getStringExtra("Code");
        clasa_editText.setText(Class);
        clasa_editText.setInputType(0);

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CheckSubmit();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        Button selectImage = (Button) findViewById(R.id.imagine_button);
        selectImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SelectPoza();
            }
        });
    }

    private void SelectPoza()
    {
        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};

        int permsRequestCode = 200;
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(perms, permsRequestCode);
            SelectPoza();

        }
        else
            writeAccepted=true;
        if(writeAccepted)
        {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 4);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 384 );
            cropIntent.putExtra("outputY", 512);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        switch(permsRequestCode){

            case 200:

                writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Eroaremare",""+requestCode);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            performCrop(selectedImage);
            /*
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            STimage = (BitmapFactory.decodeFile(picturePath));

            ImageView imagetest = (ImageView) findViewById(R.id.testimg);

            */


        } else if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");
                ImageView imagetest = (ImageView) findViewById(R.id.testimg);
                imagetest.setImageBitmap(selectedBitmap);
                STimage=selectedBitmap;

            }
        }
        else
        {
            Toast.makeText(this,"Eroare poza",Toast.LENGTH_LONG).show();

        }

    }


    private void CheckSubmit() throws UnsupportedEncodingException {
        EditText tname = (EditText) findViewById(R.id.name_editText);
        EditText tforename = (EditText) findViewById(R.id.forname_editText);
        EditText temail = (EditText) findViewById(R.id.email_editText);
        EditText tcnp = (EditText) findViewById(R.id.cnp_editText);
        EditText tphone_number = (EditText) findViewById(R.id.phone_editText);
        EditText tpass1 = (EditText) findViewById(R.id.pass1_editText);
        EditText tpass2 = (EditText) findViewById(R.id.pass2_editText);
        EditText taddress = (EditText) findViewById(R.id.adress_editText);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        STimage.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String STImage=Base64.encodeToString(b, Base64.DEFAULT);

        String name        =tname.getText().toString();
        String forename    =tforename.getText().toString();
        String email       =temail.getText().toString();
        String cnp         =tcnp.getText().toString();
        String phone_number=tphone_number.getText().toString();
        String pass1       =tpass1.getText().toString();
        String pass2       =tpass2.getText().toString();
        String address     =taddress.getText().toString();

        ArrayList<String> fields = new ArrayList<>();
        fields.add(name);
        fields.add(forename);
        fields.add(email);
        fields.add(cnp);
        fields.add(phone_number);
        fields.add(pass1);
        fields.add(pass2);
        fields.add(address);

        for (String field: fields)
            if (field.equals("")) {
                Toast.makeText(this, "Toate campurile trebuie completate.", Toast.LENGTH_SHORT).show();
                return;
            }


        if(!email.contains("@"))
        {
            Toast.makeText(this, "E-mailul nu contine simbolul '@'.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(! pass1.equals(pass2))
        {
            Toast.makeText(this, "Parolele nu sunt identice.", Toast.LENGTH_SHORT).show();
            return;
        }




        Response.Listener<String> loginListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    boolean code_access = jsonResponse.getBoolean("code_access");
                    boolean success = jsonResponse.getBoolean("success");
                    boolean email_free= jsonResponse.getBoolean("email_free");
                    if(code_access)
                    {
                        if(email_free)
                        {
                            if (success)
                            {
                                AlertDialog.Builder alert = new AlertDialog.Builder(CreateActivity.this);
                                alert.setMessage("Cont creat cu succes").setNegativeButton("Inapoi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent I = new Intent(CreateActivity.this, StartUp.class);
                                        startActivity(I);
                                    }
                                }).create().show();

                            }
                            else
                            {
                                AlertDialog.Builder alert = new AlertDialog.Builder(CreateActivity.this);
                                alert.setMessage("Eroare la creare cont").setNegativeButton("", null).create().show();

                            }
                        }
                        else
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(CreateActivity.this);
                            alert.setMessage("Acest email exista deja.").setNegativeButton("Inapoi", null).create().show();

                        }
                    }
                    else
                    {
                        AlertDialog.Builder alert = new AlertDialog.Builder(CreateActivity.this);
                        alert.setMessage("Cod gresit").setNegativeButton("Inapoi",null).create().show();
                        Intent I = new Intent(CreateActivity.this, StartUp.class);
                        startActivity(I);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        _Register_Request register_Request = new _Register_Request(Code,name,forename,email,cnp,phone_number,CID,pass1,address,STImage,loginListener);
        RequestQueue register_Queue = Volley.newRequestQueue(CreateActivity.this);
        register_Queue.add(register_Request);

    }
}
