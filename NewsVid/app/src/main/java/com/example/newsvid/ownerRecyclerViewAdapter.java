package com.example.newsvid;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ownerRecyclerViewAdapter extends RecyclerView.Adapter<vholder> {

    ArrayList<VideoDriver> videoDriverArrayList;
    Context context;

    FirebaseAuth auth;




    public ownerRecyclerViewAdapter( Context context,ArrayList<VideoDriver> videoDriverArrayList){
        this.videoDriverArrayList = videoDriverArrayList;
        auth  = FirebaseAuth.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public vholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        vholder holder = new vholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull vholder holder, int position) {
        VideoDriver videoDriver = videoDriverArrayList.get(position);

        holder.name.setText(videoDriver.name);
        holder.add.setText(videoDriver.add);
        holder.description.setText(videoDriver.description);

        String sanitizedUrl = videoDriver.videoUrl.replaceAll("[^a-zA-Z0-9]", "_");
        String postKey = sanitizedUrl.replaceAll("[^a-zA-Z0-9]", "_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(postKey);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference userLikeReference = reference.child(auth.getCurrentUser().getUid());

                userLikeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            reference.child(auth.getCurrentUser().getUid()).removeValue();
                            holder.likes.setImageResource(R.drawable.baseline_thumb_not_like);
                        } else {
                            reference.child(auth.getCurrentUser().getUid()).setValue(true);
                            holder.likes.setImageResource(R.drawable.baseline_thumb_up_24);
                        }

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.likes_count.setText(String.valueOf(snapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.likes_count.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.videoView.setVideoURI(Uri.parse(videoDriver.videoUrl));
        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.videoView.start();
                holder.seekBar.setMax(holder.videoView.getDuration());

                final int[] play = {1};

                holder.videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (play[0] == 1) {
                            holder.videoView.pause();
                            play[0] = 0;
                        } else {
                            holder.videoView.start();
                            play[0] = 1;

                        }
                    }
                });

                holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            holder.videoView.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.videoView != null) {
                            int currentPosition = holder.videoView.getCurrentPosition();
                            holder.seekBar.setProgress(currentPosition);
                            handler.postDelayed(this, 1000);
                        }
                    }
                }, 0);
            }
        });


        DatabaseReference image_likeRefrence = reference.child(auth.getCurrentUser().getUid());

        image_likeRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.likes.setImageResource(R.drawable.baseline_thumb_up_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        int seekBarColor = ContextCompat.getColor(context, R.color.orange);
        holder.seekBar.getProgressDrawable().setColorFilter(seekBarColor, PorterDuff.Mode.SRC_IN);
        int thumbColor = ContextCompat.getColor(context, R.color.orange);
        holder.seekBar.getThumb().setColorFilter(thumbColor, PorterDuff.Mode.SRC_IN);

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.seekBar.setProgress(0);
            }
        });



        Picasso.get().load(videoDriver.imageUrl).into(holder.user_img);


        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postKey = videoDriver.videoUrl.replaceAll("[^a-zA-Z0-9]", "_");
                Intent intent = new Intent(context, comment.class);
                intent.putExtra("uid",auth.getCurrentUser().getUid());
                intent.putExtra("videoUrl",postKey);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoDriverArrayList.size();
    }

    @Override
    public void onViewRecycled(@NonNull vholder holder) {
        super.onViewRecycled(holder);
        holder.videoView.stopPlayback();
    }

}

class vholder extends RecyclerView.ViewHolder{

    TextView name,add,likes_count,description;

    ImageView user_img,likes,comments;
    VideoView videoView;

    SeekBar seekBar;


    public vholder(@NonNull View itemView) {
        super(itemView);

        description = itemView.findViewById(R.id.textView27);
        name = itemView.findViewById(R.id.textView14);
        comments = itemView.findViewById(R.id.imageView9);
        likes = itemView.findViewById(R.id.imageView8);
        likes_count = itemView.findViewById(R.id.like_count);
        seekBar = itemView.findViewById(R.id.seekBar);
        add = itemView.findViewById(R.id.textView117);
        user_img = itemView.findViewById(R.id.circleImageView);
        videoView = itemView.findViewById(R.id.videoView2);
    }

}
