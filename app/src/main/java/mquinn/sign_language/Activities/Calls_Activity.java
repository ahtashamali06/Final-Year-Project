package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import mquinn.sign_language.R;

public class Calls_Activity extends AppCompatActivity {

    CircleImageView profile_image,call_button;

    // Firebase DataBase Variable Intilization

    FirebaseDatabase database ;
    DatabaseReference reference ;


    public Calls_Activity() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorChat));
        }


        profile_image = findViewById(R.id.profile_image);
        call_button = findViewById(R.id.call_button);


        //         Check User Exist
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null){
                        if (user.getPhotoUrl() != null){
                            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(profile_image);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), All_Users_Activity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.BOTTOM_END);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats:
                        Intent intent_chats = new Intent(Calls_Activity.this, Home_Activity.class);
                        startActivity(intent_chats);
                        break;
                    case R.id.template:
                        Intent intent_template = new Intent(Calls_Activity.this, Template_Activity.class);
                        startActivity(intent_template);
                        break;
                    case R.id.calls:
                        break;
                    case R.id.setting:
                        Intent intent_setting = new Intent(Calls_Activity.this, Setting_Activity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.camera:
                        Intent intent_camera = new Intent(Calls_Activity.this, MainActivity.class);
                        startActivity(intent_camera);
                        break;
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Intent intent_chats = new Intent(Calls_Activity.this,Home_Activity.class);
        intent_chats.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent_chats);
        finish();
    }

    public void startChatActivity(View view) {
        startActivity(new Intent(Calls_Activity.this, chatRoomActivity.class));
    }
}