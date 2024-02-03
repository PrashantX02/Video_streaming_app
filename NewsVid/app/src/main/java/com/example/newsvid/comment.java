package com.example.newsvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;

import java.util.ArrayList;

public class comment extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth auth;

    EditText editText;

    ImageView post_comment;

    commentRecyclerViewAdapter commentRecyclerViewAdapter;

    RecyclerView recyclerView;

    ArrayList<commentDriver> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getCurrentUser().getUid());

        editText = findViewById(R.id.editTextText);
        post_comment = findViewById(R.id.post_com);
        recyclerView = findViewById(R.id.comment_recycler);


        arrayList = new ArrayList<>();

        Intent intent = getIntent();
        String Vid_uri = intent.getStringExtra("videoUrl");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DatabaseReference pref = FirebaseDatabase.getInstance().getReference().child("comments").child(Vid_uri);

        pref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String img = snapshot1.child("img").getValue(String.class);
                    String name = snapshot1.child("name").getValue(String.class);
                    String text = snapshot1.child("text").getValue(String.class);

                    commentDriver driver = new commentDriver(img,name,text);

                    arrayList.add(driver);
                }
                commentRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        commentRecyclerViewAdapter = new commentRecyclerViewAdapter(getApplicationContext(),arrayList);
        recyclerView.setAdapter(commentRecyclerViewAdapter);



        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comen = editText.getText().toString();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String img = snapshot.child("imageAddress").getValue(String.class);
                        commentDriver driver = new commentDriver(img,name,comen);
                        post(driver);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    public void post(commentDriver driver){
        Intent intent = getIntent();
        String Vid_uri = intent.getStringExtra("videoUrl");

        if(Vid_uri!=null) {
            DatabaseReference pref = FirebaseDatabase.getInstance().getReference().child("comments").child(Vid_uri);

            pref.push().setValue(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  //  Toast.makeText(getApplicationContext(), Vid_uri, Toast.LENGTH_SHORT).show();
                    editText.setText("");
                }
            });
        }else{
          //  Toast.makeText(getApplicationContext(), "missing post key", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}