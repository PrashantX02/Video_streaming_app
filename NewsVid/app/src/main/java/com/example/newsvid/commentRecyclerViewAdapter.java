package com.example.newsvid;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class commentRecyclerViewAdapter extends RecyclerView.Adapter<commentViewHolder> {

    Context context;
    ArrayList<commentDriver> arrayList;
    public commentRecyclerViewAdapter(Context context, ArrayList<commentDriver> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new commentViewHolder(LayoutInflater.from(context).inflate(R.layout.card_comment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {
        commentDriver driver = arrayList.get(position);
        Picasso.get().load(driver.img).into(holder.img);
        holder.comment.setText(driver.text);
        holder.name.setText(driver.name);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

class commentViewHolder extends RecyclerView.ViewHolder{

    TextView comment,name;
    ImageView img;

    public commentViewHolder(@NonNull View itemView) {
        super(itemView);

        img = itemView.findViewById(R.id.usercard_comments);

        name = itemView.findViewById(R.id.textView24);
        comment = itemView.findViewById(R.id.textView25);

    }
}
