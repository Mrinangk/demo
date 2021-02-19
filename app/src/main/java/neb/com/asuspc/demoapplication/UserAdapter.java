package neb.com.asuspc.demoapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<
        UsersClass, UserAdapter.UserViewHolder> {

    public UserAdapter(
            @NonNull FirebaseRecyclerOptions<UsersClass> options)
    {
        super(options);
    }
    @Override
    protected void
    onBindViewHolder(@NonNull UserViewHolder holder,
                     int position, @NonNull UsersClass model)
    {

        String name = model.getFirstName() + " " + model.getLastName();
        holder.name.setText(name);
        holder.country.setText(model.getCountry());
        holder.gender.setText(model.getGender());
        Picasso.get().load(model.getImageUrl())
                .into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
    @NonNull
    @Override
    public UserViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name,age,gender,country;
        ImageView image,delete;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name_textView);
            age = itemView.findViewById(R.id.age_textView);
            gender = itemView.findViewById(R.id.gender_textView);
            country = itemView.findViewById(R.id.country_textView);

            delete = itemView.findViewById(R.id.deleteButton);
            image = itemView.findViewById(R.id.profile_pic);
        }
    } }