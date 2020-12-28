package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import mquinn.sign_language.R;
import mquinn.sign_language.Servies.lunchAppServices;

public class Setting_Activity extends AppCompatActivity {

    Button Logout;
    ImageView Profile_Image;
    TextView Name,Email;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Logout = findViewById(R.id.Logout);
        Profile_Image = findViewById(R.id.User_Profile);
        Name = findViewById(R.id.User_Name);
        Email = findViewById(R.id.User_Email);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null){
                        if (user.getPhotoUrl() != null){
                            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(Profile_Image);
                        }
                    }

                    String NameFromDB = snapshot.child("name").getValue(String.class);
                    Name.setText(NameFromDB);
                    String EmailFromDB = snapshot.child("email").getValue(String.class);
                    Email.setText(EmailFromDB);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.BOTTOM_END);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats:
                        Intent intent_chats = new Intent(Setting_Activity.this, Home_Activity.class);
                        startActivity(intent_chats);
                        break;
                    case R.id.template:
                        Intent intent_template = new Intent(Setting_Activity.this, Template_Activity.class);
                        startActivity(intent_template);
                        break;
                    case R.id.calls:
                        Intent intent_calls = new Intent(Setting_Activity.this, Calls_Activity.class);
                        startActivity(intent_calls);
                        break;
                    case R.id.setting:
                        break;
                    case R.id.camera:
                        Intent intent_camera = new Intent(Setting_Activity.this, MainActivity.class);
                        startActivity(intent_camera);
                        break;
                }
                return false;
            }
        });


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OneSignal.setSubscription(false);
                stopService(new Intent(Setting_Activity.this, lunchAppServices.class));
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), SignIn_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        Intent intent_chats = new Intent(Setting_Activity.this,Home_Activity.class);
        startActivity(intent_chats);
        finish();
    }
}