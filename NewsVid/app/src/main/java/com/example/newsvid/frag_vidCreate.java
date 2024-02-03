package com.example.newsvid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class frag_vidCreate extends Fragment {

    VideoView videoView;
    MediaController mediaController;


    EditText descrip;
    private int request_code = 1001;

    private static final String KEY_VIDEO_URI = "video_uri";


    Uri videoUri;
    FirebaseAuth auth;

    ArrayList<VideoDriver> videoDriverArrayList;

    ownerRecyclerViewAdapter recyclerViewAdapterUser;

    TextView post,date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_frag_vid_create, container, false);

        videoView = view.findViewById(R.id.videoView);
        post = view.findViewById(R.id.post);
        descrip = view.findViewById(R.id.description);

        mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.start();

        auth = FirebaseAuth.getInstance();

        date =view.findViewById(R.id.date);

        post = view.findViewById(R.id.post);

        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(d);
        date.setText(formattedDate);


        videoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,request_code);
                return true;
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                StorageReference reference = FirebaseStorage.getInstance().getReference().child(auth.getCurrentUser().getUid().toString()+"/"+System.currentTimeMillis()+".mp4");

                reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String user = auth.getCurrentUser().getUid();
                                DatabaseReference socialRef = FirebaseDatabase.getInstance().getReference().child("video");

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("profile").child(user);


                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("name").getValue(String.class);
                                        String imguri = snapshot.child("imageAddress").getValue(String.class);
                                        String add = snapshot.child("address").getValue(String.class);


                                        VideoDriver videoDriver = new VideoDriver(user,uri.toString(),"0",descrip.getText().toString(),name,imguri,add);

                                        socialRef.push().setValue(videoDriver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused){
                                              //  videoUrl,description,name,imageUrl,add;
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyc_owner);

        videoDriverArrayList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("video");
        Query current_user_only = databaseReference.orderByChild("uid").equalTo(auth.getCurrentUser().getUid().toString());

        current_user_only.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoDriverArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String add = dataSnapshot.child("add").getValue(String.class);
                    String likes = dataSnapshot.child("likes").getValue(String.class);
                    String uid = dataSnapshot.child("uid").getValue(String.class);

                    VideoDriver videoDriver = new VideoDriver(uid,videoUrl,likes,description, name, imageUrl, add);
                    videoDriverArrayList.add(videoDriver);

                }
                Log.d("Firebase", "Data getting complete");

                recyclerViewAdapterUser.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerViewAdapterUser = new ownerRecyclerViewAdapter(getContext(),videoDriverArrayList);
        recyclerView.setAdapter(recyclerViewAdapterUser);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == request_code && resultCode == Activity.RESULT_OK){
            videoUri = data.getData();
            videoView.stopPlayback();
            videoView.setVideoURI(null);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(videoUri!=null){
            outState.putString(KEY_VIDEO_URI,videoUri.toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_VIDEO_URI)){
            videoUri = Uri.parse(savedInstanceState.getString(KEY_VIDEO_URI));
            if(videoUri != null) {
                videoView.setVideoURI(videoUri);
                videoView.start();
            }
        }
    }
}