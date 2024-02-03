package com.example.newsvid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.service.autofill.Field;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class frag_video extends Fragment {

    globalVideoRecyclerVideoAdapter globalVideoRecyclerVideoAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view =  inflater.inflate(R.layout.fragment_frag_video, container, false);





        ViewPager2 recyclerView = view.findViewById(R.id.glob_recycler);

        ArrayList<VideoDriver> arrayList = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("video");

        globalVideoRecyclerVideoAdapter = new globalVideoRecyclerVideoAdapter(getContext(),arrayList);
        recyclerView.setAdapter(globalVideoRecyclerVideoAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String add = dataSnapshot.child("add").getValue(String.class);
                    String likes = dataSnapshot.child("likes").getValue(String.class);
                    String uid = dataSnapshot.child("uid").getValue(String.class);


                    VideoDriver videoDriver = new VideoDriver(uid,videoUrl,likes,description, name, imageUrl, add);

                    arrayList.add(videoDriver);
                }

                Collections.shuffle(arrayList);

                globalVideoRecyclerVideoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        globalVideoRecyclerVideoAdapter = new globalVideoRecyclerVideoAdapter(getContext(),arrayList);
        recyclerView.setAdapter(globalVideoRecyclerVideoAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}