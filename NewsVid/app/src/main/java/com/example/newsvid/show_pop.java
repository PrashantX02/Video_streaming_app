package com.example.newsvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class show_pop extends AppCompatActivity {

    RecyclerView popRecycler;

    popAdapter popAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pop);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String id = auth.getCurrentUser().getUid();
        popRecycler = findViewById(R.id.recycler_view_pop);

        Intent intent = getIntent();
        String key = intent.getStringExtra("keyToPop");

        ArrayList<popDrive> arrayList = new ArrayList<>();

        popRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        popAdapter = new popAdapter(getApplicationContext(),arrayList);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile");
        if(key.equals("follower")) {


            DatabaseReference follower_ref = reference.child(id).child("follower");
            follower_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                       reference.child(snapshot1.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               String url = snapshot.child("imageAddress").getValue(String.class);
                               String name = snapshot.child("name").getValue(String.class);
                               arrayList.add(new popDrive(url,name));
                               popAdapter.notifyDataSetChanged();

                               //Toast.makeText(getApplicationContext(),arrayList.toString(),Toast.LENGTH_LONG).show();
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            DatabaseReference following_ref = reference.child(id).child("following");
            following_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        reference.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String url = snapshot.child("imageAddress").getValue(String.class);
                                String name = snapshot.child("name").getValue(String.class);
                                arrayList.add(new popDrive(url,name));
                                popAdapter.notifyDataSetChanged();

                               // Toast.makeText(getApplicationContext(),arrayList.toString(),Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        popRecycler.setAdapter(popAdapter);
    }
}