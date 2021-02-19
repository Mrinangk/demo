package neb.com.asuspc.demoapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter extends FirebaseRecyclerAdapter<UsersClass,Adapter.ViewHolder>
{

    public Adapter(@NonNull FirebaseRecyclerOptions<UsersClass> options)
    {
        super(options);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final UsersClass model)
    {
        String name = model.getFirstName() + " " + model.getLastName();
        holder.name.setText(name);
        holder.country.setText(model.getCountry());
        holder.gender.setText(model.getGender());
        Picasso.get().load(model.getImageUrl())
                    .into(holder.imageV);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.orderByChild("phoneNumber").equalTo(model.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            Objects.requireNonNull(dataSnapshot.getRef().removeValue());
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Read Fail", "Error");
                    }
                });
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
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
        }
    }
}
