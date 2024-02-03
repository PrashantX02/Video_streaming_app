package com.example.newsvid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<viewHolder>{

    ArrayList<Driver> arrayList;
    Context context;

    FirebaseAuth auth;

    public RecyclerViewAdapterUser(Context context,ArrayList<Driver> arrayList){
        this.arrayList = arrayList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.carduser,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Driver driver = arrayList.get(position);

        Picasso.get().load(driver.ImageAddress).into(holder.user);
        holder.name.setText(driver.name);

        if(driver.gender.toString().equals("Male")){
            holder.gender.setImageResource(R.drawable.baseline_male_24_blue);
        }else holder.gender.setImageResource(R.drawable.baseline_female_24_blue);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile").child(auth.getCurrentUser().getUid()).child("following");
        DatabaseReference user_fellow_ref = reference.child(driver.uid);

        user_fellow_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.follow.setBackgroundResource(R.drawable.follow_off);
                }else{
                    holder.follow.setBackgroundResource(R.drawable.gradi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference follower_ref = FirebaseDatabase.getInstance().getReference().child("profile").child(driver.uid).child("follower");

        DatabaseReference check_fr = follower_ref.child(auth.getCurrentUser().getUid());

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_fellow_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            reference.child(driver.uid).removeValue();
                            holder.follow.setBackgroundResource(R.drawable.gradi);
                        }else{
                            reference.child(driver.uid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.follow.setBackgroundResource(R.drawable.follow_off);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                check_fr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            follower_ref.child(auth.getCurrentUser().getUid()).removeValue();
                        }else{
                            follower_ref.child(auth.getCurrentUser().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

class viewHolder extends RecyclerView.ViewHolder{

    ImageView gender;
    CircleImageView user;
    TextView name,message,follow;
    public viewHolder(@NonNull View itemView) {
        super(itemView);

        gender = itemView.findViewById(R.id.genda);
        follow = itemView.findViewById(R.id.textView28);
        user = itemView.findViewById(R.id.usercard);
        name = itemView.findViewById(R.id.name_card);

    }
}