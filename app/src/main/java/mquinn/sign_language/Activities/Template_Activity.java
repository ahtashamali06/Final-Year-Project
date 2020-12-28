package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mquinn.sign_language.R;
import mquinn.sign_language.Template.Template_Adapter;

public class Template_Activity extends AppCompatActivity {

    CircleImageView profile_image;
    // Firebase DataBase Variable Intilization

    FirebaseDatabase database ;
    DatabaseReference reference ;

    private RecyclerView recyclerView;
    List<String> titles;
    List<Integer>images;
    Template_Adapter Template_Adapter;

    public Template_Activity() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorChat));
        }

        recyclerView = findViewById(R.id.template_recyclerview);
        titles = new ArrayList<>();
        images = new ArrayList<>();

        titles.add("First Title");
        titles.add("Second Title");
        images.add(R.drawable.smartphone);
        images.add(R.drawable.verify);

        Template_Adapter = new Template_Adapter(this,titles,images);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(Template_Adapter);

        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

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


        BottomNavigationView bottomNavigationView = findViewById(R.id.BOTTOM_END);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats:
                        Intent intent_chats = new Intent(Template_Activity.this, Home_Activity.class);
                        startActivity(intent_chats);
                        break;
                    case R.id.template:

                        break;
                    case R.id.calls:
                        Intent intent_calls = new Intent(Template_Activity.this, Calls_Activity.class);
                        startActivity(intent_calls);
                        break;
                    case R.id.setting:
                        Intent intent_setting = new Intent(Template_Activity.this, Setting_Activity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.camera:
                        Intent intent_camera = new Intent(Template_Activity.this, MainActivity.class);
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
        Intent intent_chats = new Intent(Template_Activity.this,Home_Activity.class);
        intent_chats.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent_chats);
        finish();
    }
}