package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import mquinn.sign_language.R;
import mquinn.sign_language.User.RegisteredUserClass;

public class Register_User extends AppCompatActivity {

    Button skip,save;
    EditText name,email;
    TextView dateOfBirth,phoneNo_textview;
    ImageView profile;
    String PHONE_NUMBER;
    RadioButton Male,Female;
    DatePickerDialog.OnDateSetListener setListener;
    private static final String TAG = "Registration Page";

    private int REQUEST_IMAGE_CAPTURE = 100;
    RegisteredUserClass member;

    // Firebase DataBase Variable Intilization

    FirebaseDatabase database = FirebaseDatabase.getInstance();;
    DatabaseReference reference = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public Register_User() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__user);

        // Top Bar color
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Intilization

        skip = findViewById(R.id.skip);
        save = findViewById(R.id.savedata);
        name = findViewById(R.id.name);
        email = findViewById(R.id.User_Email);
        profile = findViewById(R.id.profile_image);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        phoneNo_textview = findViewById(R.id.phoneNo);
        Male = findViewById(R.id.radioMale);
        Female = findViewById(R.id.radioFemale);


        // Profile Image
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getpermission();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                }

            }
        });


        // Get Phone Number

        PHONE_NUMBER = getIntent().getStringExtra("number");
        phoneNo_textview.setText(PHONE_NUMBER);

        // RegisteredUserClass Class Object

        member = new RegisteredUserClass();

        //         Check User Exist
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String NameFromDB = snapshot.child("name").getValue(String.class);
                    name.setText(NameFromDB);
                    String EmailFromDB = snapshot.child("email").getValue(String.class);
                    email.setText(EmailFromDB);
                    String DateofBirthFromDB = snapshot.child("dateofbirth").getValue(String.class);
                    dateOfBirth.setText(DateofBirthFromDB);
                    String GenderFromDB = snapshot.child("gender").getValue(String.class);
                    if (GenderFromDB != null){
                        if (GenderFromDB.equals("Male")){
                            Male.setChecked(true);
                        }if (GenderFromDB.equals("Female")){
                            Female.setChecked(true); }
                    }else {
                        Male.setChecked(false);
                        Female.setChecked(false);
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null){
                        if (user.getPhotoUrl() != null){
                            Glide.with(Register_User.this).load(user.getPhotoUrl()).into(profile);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Date of Birth Picker

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int days = calendar.get(Calendar.DAY_OF_MONTH);

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Register_User.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,year,month,days);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });



        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                dateOfBirth.setText(date);
                member.setDateofbirth(dateOfBirth.getText().toString());
            }
        };


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                member.setName(null);
                member.setEmail(null);
                member.setGender(null);
                member.setDateofbirth("");
                member.setPhoneNo(PHONE_NUMBER);
                reference.setValue(member);


                Toast.makeText(Register_User.this, "Data Not saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Male_gender = Male.getText().toString();
                String Female_gender = Female.getText().toString();
                member.setName(name.getText().toString());
                member.setEmail(email.getText().toString());
                member.setPhoneNo(PHONE_NUMBER);

                if(Male.isChecked()){
                    member.setGender(Male_gender);
                }else{
                    member.setGender(Female_gender);
                }
                reference.setValue(member);


                Toast.makeText(Register_User.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Home_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profile.setImageBitmap(bitmap);
                    handleupload(bitmap);
            }
        }
    }
    private void handleupload(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImages").child(UID + ".jpeg");

        reference.putBytes(byteArrayOutputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDonwloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "onFailure: ", e.getCause());
            }
        });
    }

    private void getDonwloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e(TAG, "onSuccess: " + uri );
                setUserProfileUri(uri);
            }
        });
    }

    private void setUserProfileUri(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(Register_User.this, "Profile Image Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Register_User.this, "Profile Image Updateion Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},1);
        }
    }
}