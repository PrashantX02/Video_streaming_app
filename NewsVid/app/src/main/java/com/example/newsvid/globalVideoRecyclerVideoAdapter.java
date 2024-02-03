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
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class globalVideoRecyclerVideoAdapter extends RecyclerView.Adapter<globalViewHolder> {

    ArrayList<VideoDriver> arrayList;
    Context context;

    ViewPager2 viewPager2;

    FirebaseAuth auth;
    public globalVideoRecyclerVideoAdapter(Context context, ArrayList<VideoDriver> arrayList){
        auth = FirebaseAuth.getInstance();
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public globalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new globalViewHolder(LayoutInflater.from(context).inflate(R.layout.global_video,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull globalViewHolder holder, int position) {
        VideoDriver videoDriver = arrayList.get(position);


        String postKey = videoDriver.videoUrl.replaceAll("[^a-zA-Z0-9]", "_");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(postKey);

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

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userLikReference = reference.child(auth.getCurrentUser().getUid());

                userLikReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.likes.setImageResource(R.drawable.baseline_thumb_up_alt_24);
                            reference.child(auth.getCurrentUser().getUid()).removeValue();
                        }else{
                            holder.likes.setImageResource(R.drawable.baseline_thumb_up_24);
                            reference.child(auth.getCurrentUser().getUid()).setValue(true);
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

        DatabaseReference global_like_img = reference.child(auth.getCurrentUser().getUid());

        global_like_img.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) holder.likes.setImageResource(R.drawable.baseline_thumb_up_24);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        holder.progressBar.setVisibility(View.VISIBLE);

        holder.name.setText(videoDriver.name);
        holder.description.setText(videoDriver.description);

        Picasso.get().load(videoDriver.imageUrl).into(holder.userpic);

        holder.videoView.setVideoURI(Uri.parse(videoDriver.videoUrl));
//        MediaController mediaController = new MediaController(context);
//        holder.videoView.setMediaController(mediaController);
//        holder.videoView.start();

        final Handler handler = new Handler();

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {


                holder.progressBar.setVisibility(View.GONE);
                mp.start();
                holder.seekBar.setMax(holder.videoView.getDuration());

                int seekBarColor = ContextCompat.getColor(context, R.color.orange);
                holder.seekBar.getProgressDrawable().setColorFilter(seekBarColor, PorterDuff.Mode.SRC_IN);
                int thumbColor = ContextCompat.getColor(context, R.color.orange);
                holder.seekBar.getThumb().setColorFilter(thumbColor, PorterDuff.Mode.SRC_IN);

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

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });





        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, comment.class);
                intent.putExtra("uid",auth.getCurrentUser().getUid());
                intent.putExtra("videoUrl",postKey);
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void updateSeekBar(final Handler handler, final globalViewHolder holder) {
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
}

class globalViewHolder extends RecyclerView.ViewHolder{

    ImageView userpic,likes,comments;

    TextView name,description,likes_count;

    ProgressBar progressBar;
    VideoView videoView;

    SeekBar seekBar;
    public globalViewHolder(@NonNull View itemView) {
        super(itemView);
        userpic = itemView.findViewById(R.id.glob_user_vid_pic);
        name = itemView.findViewById(R.id.textView19);
        description = itemView.findViewById(R.id.textView21);
        videoView = itemView.findViewById(R.id.videoView3);

        likes = itemView.findViewById(R.id.imageView10);

        seekBar = itemView.findViewById(R.id.seekBar3);
        likes_count = itemView.findViewById(R.id.textView23);

        comments = itemView.findViewById(R.id.imageView14);

        progressBar = itemView.findViewById(R.id.progressBar);
    }

}
