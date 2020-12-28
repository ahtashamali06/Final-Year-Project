package mquinn.sign_language.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import mquinn.sign_language.R;
import mquinn.sign_language.User.UserListAdapter;
import mquinn.sign_language.User.UserObject;


public class All_Users_Activity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;

    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__users);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorChat));
        }

        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contact List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactList= new ArrayList<>();
        userList= new ArrayList<>();
        Button mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChat();
                startActivity(new Intent(All_Users_Activity.this,Home_Activity.class));
            }
        });

        intializeRecyclerView();
        getContactList();


    }

    private void createChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(key).child("info");
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users");


        HashMap newChatMap = new HashMap();
        newChatMap.put("Id",key);
        newChatMap.put("Users/"+ FirebaseAuth.getInstance().getUid(),true);

        Boolean ValidChat = false;
        for (UserObject mUser : userList){
            if (mUser.getSelected()){
                ValidChat= true;
                newChatMap.put("Users/"+mUser.getUid(),true);
                userDB.child(mUser.getUid()).child("Chat").child(key).setValue(true);
            }
        }
        if (ValidChat){
            chatInfoDb.updateChildren(newChatMap);
            userDB.child(FirebaseAuth.getInstance().getUid()).child("Chat").child(key).setValue(true);
        }



    }
    private void getContactList(){

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


            UserObject mContact = new UserObject("", name, phone);
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }


    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserDB.orderByChild("phoneNo").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name ="", phone ="";
                    for (DataSnapshot childSnapshot : snapshot.getChildren()){
                        if (childSnapshot.child("phoneNo").getValue()!=null){
                            phone = childSnapshot.child("phoneNo").getValue().toString();
                        }
                        if (childSnapshot.child("name").getValue()!=null){
                            name = childSnapshot.child("name").getValue().toString();
                        }

                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);

                        if (name.equals(phone))
                            for (UserObject mContactIterator : contactList){
                                if (mContactIterator.getPhone().equals(mUser.getPhone())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }
                        userList.add(mUser);
                        mUserAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void intializeRecyclerView() {

        mUserList= findViewById(R.id.all_users);
        mUserList.setHasFixedSize(false);
        mUserList.setNestedScrollingEnabled(false);
        mUserLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mUserList.setLayoutManager(mUserLayoutManager);
        mUserAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserAdapter);
    }
}