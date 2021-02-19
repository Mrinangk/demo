package neb.com.asuspc.demoapplication;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolder extends RecyclerView.ViewHolder{
    TextView name,age,gender,country;
    ImageView delete;
    CircleImageView imageV;
    public ViewHolder(View view) {
        super(view);
        name = itemView.findViewById(R.id.name_textView);
        age = itemView.findViewById(R.id.age_textView);
        gender = itemView.findViewById(R.id.gender_textView);
        country = itemView.findViewById(R.id.country_textView);

        delete = itemView.findViewById(R.id.deleteButton);
        imageV = itemView.findViewById(R.id.profile_pic);
    }}