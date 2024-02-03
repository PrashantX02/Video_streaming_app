package com.example.newsvid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RegisTration extends AppCompatActivity {

    ImageView userImage;
    EditText userName,email,passcode,address;

    TextView createUser;
    Spinner gender,married;

    String gen ;
    String mar ;

    Uri imageUri;

    private int REQUEST_CODE = 101;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
        ,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regis_tration);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        userImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        passcode = findViewById(R.id.passocde);
        createUser = findViewById(R.id.createUser);
        address = findViewById(R.id.address);
        gender = findViewById(R.id.gender_spinner);
        married = findViewById(R.id.married_spinner);


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ArrayList<String> mf = new ArrayList<>();
        mf.add("Male");
        mf.add("Female");

        ArrayList<String> mare = new ArrayList<>();
        mare.add("Yes");
        mare.add("No");


        // Spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item ,mare);
        married.setAdapter(adapter1);

        // Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisTration.this, android.R.layout.simple_spinner_dropdown_item, mf);

        gender.setAdapter(adapter);

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gen = parent.getItemAtPosition(position).toString();
                ImageView mficon = findViewById(R.id.mfIcon);
                if (gen.equals("Male")) {
                    mficon.setImageResource(R.drawable.baseline_male_24);
                } else {
                    mficon.setImageResource(R.drawable.baseline_female_24);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle situation where no item is selected
            }
        });

        married.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mar = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle situation where no item is selected
            }
        });




        createUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               auth = FirebaseAuth.getInstance();
               String name = userName.getText().toString();
               String mail = email.getText().toString();
               String pass = passcode.getText().toString();
               String add = address.getText().toString();




               if(gen != null && mar != null && !name.isEmpty() && !mail.isEmpty() && !pass.isEmpty() && !add.isEmpty()) {

                   ProgressDialog progressDialog = new ProgressDialog(RegisTration.this);
                   progressDialog.setMessage("Sign up...");
                   progressDialog.setCancelable(false);
                   progressDialog.show();

                   auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           String id = task.getResult().getUser().getUid();

                           DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile").child(id);
                           StorageReference storage = FirebaseStorage.getInstance().getReference().child("profile_pic").child(id);

                           storage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                   if (task.isSuccessful()) {
                                       storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {
                                               String imageUrl = uri.toString();

                                               Driver driver = new Driver(id, name, imageUrl, pass,gen,add,mar,mail);

                                               reference.setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if (task.isSuccessful()) {
                                                           Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                                           startActivity(intent);
                                                           progressDialog.dismiss();
                                                           finish();
                                                       }
                                                   }
                                               });
                                           }
                                       });
                                   }
                               }
                           });
                       }
                   });
               }else Toast.makeText(getApplicationContext(),"Enter all details",Toast.LENGTH_SHORT).show();
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE && data!= null){
            imageUri = data.getData();
            Picasso.get().load(imageUri.toString()).into(userImage);
        }
    }

    public void onBackPressed() {
        Intent intent  = new Intent(getApplicationContext(),LogIn.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}