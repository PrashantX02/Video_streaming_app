package com.example.newsvid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class popAdapter extends RecyclerView.Adapter<popViewHolder>{

    Context context;
    ArrayList<popDrive> arrayList;

    public popAdapter(Context context,ArrayList<popDrive> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public popViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new popViewHolder(LayoutInflater.from(context).inflate(R.layout.pop_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull popViewHolder holder, int position) {
        popDrive popDrive = arrayList.get(position);
        Picasso.get().load(popDrive.imageURL).into(holder.pop_user);
        holder.name.setText(popDrive.name);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
class popViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    CircleImageView pop_user;
    public popViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.textView29);
        pop_user = itemView.findViewById(R.id.pop_user);
    }
}