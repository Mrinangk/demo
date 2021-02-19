package neb.com.asuspc.demoapplication;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class EnrollFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Matcher matcher;
    String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

    ImageView imageView;
    TextView selectProfileTextView;
    EditText firstName,lastName,dob,gender,country,state,hometown,phoneNumber,telephoneNumber;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    public EnrollFragment() {
    }

    public static EnrollFragment newInstance(String param1, String param2) {
        EnrollFragment fragment = new EnrollFragment();
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
        View view = inflater.inflate(R.layout.fragment_enroll, container, false);

        imageView = view.findViewById(R.id.profileImageView);
        selectProfileTextView = view.findViewById(R.id.selectProfileImageTextView);
        firstName = view.findViewById(R.id.firstNameEditText);
        lastName = view.findViewById(R.id.lastNameEditText);
        dob = view.findViewById(R.id.dateOfEditText);
        gender = view.findViewById(R.id.genderEditText);
        country = view.findViewById(R.id.countryEditText);
        state = view.findViewById(R.id.stateEditText);
        hometown = view.findViewById(R.id.hometownEditText);
        phoneNumber = view.findViewById(R.id.phoneNumberEditText);
        telephoneNumber = view.findViewById(R.id.telephoneEditText);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(Objects.requireNonNull(getActivity()), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String day = null,month = null;
                        if (dayOfMonth<10)
                           day = "0"+String.valueOf(dayOfMonth);

                        if (monthOfYear<10)
                            month = "0"+String.valueOf(monthOfYear+1);

                        
                        String date = String.valueOf(day) + "/" + String.valueOf(month)
                                + "/" + String.valueOf(year);
                        dob.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        storageReference = FirebaseStorage.getInstance().getReference();



        selectProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });



        view.findViewById(R.id.addUserButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri!=null)
                    uploadImageToFirebaseStorage(imageUri);
                else
                    Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }

    private void uploadImageToFirebaseStorage(final Uri imageUri) {
        final StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {

                        final Pattern p = Pattern.compile(pattern);

                        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    Toast.makeText(getContext(), "This Number already Exists in the database...", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                            if (TextUtils.isEmpty(firstName.getText().toString()) || !firstName(firstName.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter Proper First Name", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(lastName.getText().toString()) || !firstName(lastName.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter Proper Last Name", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(dob.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter date of birth", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(gender.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter gender", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(country.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter country", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(state.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter state", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(hometown.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter Hometown", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
                                            } else if (TextUtils.isEmpty(telephoneNumber.getText().toString())) {
                                                Toast.makeText(getContext(), "Enter Telephone Number", Toast.LENGTH_SHORT).show();
                                            } else {
                                                matcher = p.matcher(phoneNumber.getText().toString().trim());
                                                if (matcher.find()) {
                                                    UsersClass usersClass = new UsersClass(firstName.getText().toString(), lastName.getText().toString(),
                                                            dob.getText().toString(), gender.getText().toString(),
                                                            country.getText().toString(), state.getText().toString()
                                                            , hometown.getText().toString(), phoneNumber.getText().toString(),
                                                            telephoneNumber.getText().toString(), uri.toString());
                                                    databaseReference.push().setValue(usersClass);
                                                    Toast.makeText(getContext(), "User added", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(getContext(), "Check Phone no", Toast.LENGTH_SHORT).show();
                                            }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Image uploading failed....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = Objects.requireNonNull(getContext()).getContentResolver();
        MimeTypeMap  mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

                        assert selectedImage != null;
                        imageUri =data.getData();

                        imageView.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImageUri = data.getData();
                        imageUri = selectedImageUri;
                        try {
                            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), selectedImageUri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public static boolean firstName( String firstName ) {
        return firstName.matches( "[A-Z][a-z]*" );
    }
}