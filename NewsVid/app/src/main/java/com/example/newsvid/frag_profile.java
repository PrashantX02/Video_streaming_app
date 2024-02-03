package com.example.newsvid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class frag_profile extends Fragment {

    RecyclerView recyclerViewUser;
    ArrayList<Driver> arrayList;
    TextView log_out;

    FirebaseAuth auth;
    RecyclerViewAdapterUser recyclerViewAdapterUser;

    CircleImageView circleImageView;

    String logeduser;

    ImageView genIcon;

    TextView gend,nam,marit,addT,email,follower,following,follower_shift,following_shift;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_frag_profile, container, false);

        recyclerViewUser = view.findViewById(R.id.recycler_user);

        circleImageView = view.findViewById(R.id.circleImageView);
        nam = view.findViewById(R.id.textView6);
        marit = view.findViewById(R.id.textView13);
        genIcon = view.findViewById(R.id.imageView4);
        email = view.findViewById(R.id.textView8);

        addT = view.findViewById(R.id.textView9);

        following = view.findViewById(R.id.textView12);

        follower = view.findViewById(R.id.textView11);

        follower_shift = view.findViewById(R.id.imageView5);

        following_shift = view.findViewById(R.id.imageView6);

        following_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), show_pop.class);
                intent.putExtra("keyToPop","following");
                startActivity(intent);
            }
        });

        follower_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), show_pop.class);
                intent.putExtra("keyToPop","follower");
                startActivity(intent);
            }
        });



        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            String user = auth.getCurrentUser().getUid();
            logeduser = user;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile").child(user);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        //ImageAddress,uid,name,passcode,gender,address,married_status;
                        String name = snapshot.child("name").getValue(String.class);
                        String uri = snapshot.child("imageAddress").getValue(String.class);
                        String gen = snapshot.child("gender").getValue(String.class);
                        String add = snapshot.child("address").getValue(String.class);
                        //String id = snapshot.child("uid").getValue(String.class);
                        String mail = snapshot.child("mail").getValue(String.class);
                        String marital  = snapshot.child("married_status").getValue(String.class);

                        email.setText(mail);

                        nam.setText(name);
                        if(marital.equals("No")) marit.setText("Unmarried");
                        else marit.setText("married");

                        if(gen.equals("Female"))  genIcon.setImageResource(R.drawable.baseline_female_24_blue);
                        else genIcon.setImageResource(R.drawable.baseline_male_24_blue);

                        addT.setText(add);

                        Picasso.get().load(uri).into(circleImageView);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        log_out = view.findViewById(R.id.log_out);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(),LogIn.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        DatabaseReference follower_ref = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getCurrentUser().getUid()).child("follower");

        follower_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                follower.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        follower_ref = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getCurrentUser().getUid()).child("following");

        follower_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                following.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        arrayList = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile");

        recyclerViewAdapterUser = new RecyclerViewAdapterUser(getContext(), arrayList);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUser.setAdapter(recyclerViewAdapterUser);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Driver user =snapshot1.getValue(Driver.class);
                    if(!logeduser.equals(user.uid))arrayList.add(user);
                }
                Log.d("Firebase", "Data getting complete");
                recyclerViewAdapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}