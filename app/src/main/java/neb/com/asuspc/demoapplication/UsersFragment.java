package neb.com.asuspc.demoapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class UsersFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    FirebaseRecyclerOptions<UsersClass> options;
    FirebaseRecyclerAdapter<UsersClass, ViewHolder> adapter;
    DatabaseReference databaseReference;

//    private RecyclerView recyclerView;
//    Adapter adapter;
//    DatabaseReference databaseReference;

    public UsersFragment() {
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<UsersClass>().setQuery(databaseReference, UsersClass.class)
                .build();
        loadData();

//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
//        FirebaseRecyclerOptions<UsersClass> options = new FirebaseRecyclerOptions.Builder<UsersClass>()
//                .setQuery(databaseReference, UsersClass.class)
//                .build();
//        adapter = new Adapter(options);
//        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadData() {

        adapter = new FirebaseRecyclerAdapter<UsersClass, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final UsersClass model) {
                String dateOfBirth = model.getDob();

                String year = dateOfBirth.substring(6);
                String month = dateOfBirth.substring(3, 4);
                String day = dateOfBirth.substring(1, 2);

                int age = getAge(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
                if (age<0)
                {
                    age = 0;
                }

                String name = model.getFirstName() + " " + model.getLastName();
                holder.name.setText(name);
                holder.country.setText(model.getCountry());
                holder.gender.setText(model.getGender());
                holder.age.setText(String.valueOf(age));
                Glide.with(Objects.requireNonNull(getContext())).load(model.imageUrl).into(holder.imageV);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.getRef(position).removeValue();
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false));
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

        };
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}