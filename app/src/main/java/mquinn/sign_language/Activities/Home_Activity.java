package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mquinn.sign_language.Chat.ChatListAdapter;
import mquinn.sign_language.Chat.ChatObject;
import mquinn.sign_language.R;
import mquinn.sign_language.User.UserObject;

public class Home_Activity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    CircleImageView Profile_Image,message_button;

    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    ArrayList<ChatObject> ChatList;

    // Firebase DataBase Variable Intilization
    FirebaseDatabase database ;
    DatabaseReference reference ;



    public Home_Activity() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorChat));
        }
        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Notification Key ").setValue(userId);
            }
        });

        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        Fresco.initialize(this);
        getpermission();
        intializeRecyclerView();
        getUserChatList();

        //         Check User Exist
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Profile_Image = findViewById(R.id.profile_image);
        message_button = findViewById(R.id.message_image);
        Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        message_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), All_Users_Activity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.BOTTOM_END);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats:
                        break;
                    case R.id.template:
                        Intent intent_template = new Intent(Home_Activity.this, Template_Activity.class);
                        startActivity(intent_template);
                        break;
                    case R.id.calls:
                        Intent intent_calls = new Intent(Home_Activity.this, Calls_Activity.class);
                        startActivity(intent_calls);
                        break;
                    case R.id.setting:
                        Intent intent_setting = new Intent(Home_Activity.this, Setting_Activity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.camera:
                        Intent intent_camera = new Intent(Home_Activity.this, MainActivity.class);
                        startActivity(intent_camera);
                        break;
                }
                return false;
            }
        });
    }





    private void getUserChatList(){
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Chat");
        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()){
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean exists = false;
                        for (ChatObject mChatIterator : ChatList){
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        ChatList.add(mChat);
                        getChatData(mChat.getChatId());
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatData(final String chatId) {
        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).child("info");
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String ChatID = "";
                    if (snapshot.child("id").getValue() != null){
                        ChatID = snapshot.child("id").getValue().toString();
                        for (DataSnapshot userSnapshot : snapshot.child("Users").getChildren()){
                            for (ChatObject mChat : ChatList){
                                if (mChat.getChatId().equals(ChatID)){
                                    UserObject mUser = new UserObject(userSnapshot.getKey());
                                    mChat.addUserToArrayList(mUser);
                                    getUserData(mUser);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(UserObject mUser) {
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserObject mUser = new UserObject(snapshot.getKey());
                if (snapshot.child("Notification Key").getValue()!= null)
                    mUser.setNotificationKey(snapshot.child("Notification Key").getValue().toString());
                for (ChatObject mChat : ChatList){
                    for (UserObject mUserIt : mChat.getUserObjectArrayList()){
                        if (mUserIt.getUid().equals(mUser.getUid())){
                            mUserIt.setNotificationKey(mUser.getNotificationKey());
                        }
                    }
                }
                mChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void intializeRecyclerView() {
        ChatList = new ArrayList<>();
        mChatList= findViewById(R.id.chatList);
        mChatList.setHasFixedSize(false);
        mChatList.setNestedScrollingEnabled(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mChatList.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatListAdapter(ChatList);
        mChatList.setAdapter(mChatAdapter);
    }

    private void getpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},4);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            backToast=Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}