package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mquinn.sign_language.Chat.ChatObject;
import mquinn.sign_language.Chat.MediaAdapter;
import mquinn.sign_language.Chat.MessageAdapter;
import mquinn.sign_language.Chat.MessageObject;
import mquinn.sign_language.R;
import mquinn.sign_language.User.UserObject;
import mquinn.sign_language.Utils.SendNotification;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;

    ArrayList<MessageObject> messageList;

    ChatObject mChatObject;

    DatabaseReference mChatMessagesDb;
    Button mSend,mAddMedia,mAudio;

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;
    private String Converted_Message = "";

    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private String keeper = "";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorChat));
        }

        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");
        mChatMessagesDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(mChatObject.getChatId()).child("Messages");

        mToolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         mSend = findViewById(R.id.send);
         mAddMedia = findViewById(R.id.addMedia);
         mAudio = findViewById(R.id.audio);
         mMessage = findViewById(R.id.message);

         // Assign Sign Converted Message to EditText
        Converted_Message = getIntent().getStringExtra("Message_Text");
        mMessage.setText(Converted_Message);
        mMessage.setSelection(mMessage.length());

         mMessage.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 if (charSequence.length() != 0){
                     mSend.setVisibility(View.VISIBLE);
                     mAudio.setVisibility(View.GONE);
                     mAddMedia.setVisibility(View.GONE);
                 }
                 else {
                     mSend.setVisibility(View.GONE);
                     mAudio.setVisibility(View.VISIBLE);
                     mAddMedia.setVisibility(View.VISIBLE);
                 }

             }

             @Override
             public void afterTextChanged(Editable editable) {

             }
         });

         mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
         mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

         speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ChatActivity.this);
         intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
         intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());

         speechRecognizer.setRecognitionListener(new RecognitionListener() {
             @Override
             public void onReadyForSpeech(Bundle bundle) {

             }

             @Override
             public void onBeginningOfSpeech() {

             }

             @Override
             public void onRmsChanged(float v) {

             }

             @Override
             public void onBufferReceived(byte[] bytes) {

             }

             @Override
             public void onEndOfSpeech() {

             }

             @Override
             public void onError(int i) {

             }

             @Override
             public void onResults(Bundle bundle) {

                 ArrayList<String> Results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                 if (Results != null){
                     keeper = Results.get(0);
                     mMessage.setText(keeper);
                     mMessage.setSelection(mMessage.length());

                 }

             }

             @Override
             public void onPartialResults(Bundle bundle) {

             }

             @Override
             public void onEvent(int i, Bundle bundle) {

             }
         });


         mAudio.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View view, MotionEvent motionEvent) {
                 switch (motionEvent.getAction())

                 {
                     case MotionEvent.ACTION_DOWN:
                         speechRecognizer.startListening(intentRecognizer);
                         keeper = "";
                        break;
                     case MotionEvent.ACTION_UP:
                         speechRecognizer.stopListening();
                         break;
                 }
                 return false;
             }
         });

        intializeMessage();
        intializeMedia();
        getChatMessages();
    }


    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    String text = "", creatorID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if (snapshot.child("text").getValue() != null)
                        text = snapshot.child("text").getValue().toString();
                    if (snapshot.child("creator").getValue() != null)
                        creatorID = snapshot.child("creator").getValue().toString();
                    if (snapshot.child("media").getChildrenCount() > 0)
                        for (DataSnapshot mediaSnapshot : snapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapshot.toString());

                    MessageObject mMessage = new MessageObject(snapshot.getKey(),creatorID,text,mediaUrlList);
                    messageList.add(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {


            String messageId = mChatMessagesDb.push().getKey();
            final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);

            final Map newMessageMap = new HashMap<>();

            if (mMessage.getText().toString().isEmpty() && mediaUriList.isEmpty()){

                Toast.makeText(this, "Empty Message", Toast.LENGTH_SHORT).show();
            }

            if (!mMessage.getText().toString().isEmpty()) {

                newMessageMap.put("creator", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                newMessageMap.put("text", mMessage.getText().toString());
                newMessageDb.updateChildren(newMessageMap);
                mMessage.setText(null);
            }


            if (!mediaUriList.isEmpty()){

                for (String mediaUri : mediaUriList){

                    String mediaId = newMessageDb.child("Media").push().getKey();
                    mediaIdList.add(mediaId);

                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);
                    UploadTask uploadTask = filepath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("creator", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/" , uri.toString());
                                    totalMediaUploaded++;
                                    if (totalMediaUploaded == mediaUriList.size()){
                                        updateDatabasWithNewMessage(newMessageDb,newMessageMap);
                                    }
                                }
                            });

                        }
                    });
                }
            }

            if (!mMessage.getText().toString().isEmpty() && !mediaUriList.isEmpty()){

                for (String mediaUri : mediaUriList){

                    String mediaId = newMessageDb.child("Media").push().getKey();
                    mediaIdList.add(mediaId);

                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);
                    UploadTask uploadTask = filepath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("creator", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                    newMessageMap.put("text", mMessage.getText().toString());
                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/" , uri.toString());
                                    totalMediaUploaded++;
                                    if (totalMediaUploaded == mediaUriList.size()){
                                        updateDatabasWithNewMessage(newMessageDb,newMessageMap);
                                    }
                                }
                            });

                        }
                    });
                }
            }
    }

    private void updateDatabasWithNewMessage(DatabaseReference newMessageDb,Map newMessageMap){

        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUploaded=0;
        mMediaAdapter.notifyDataSetChanged();

        String message;
        if (newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();
        else
            message = "Send Media";
        for (UserObject mUser : mChatObject.getUserObjectArrayList()){
            if (!mUser.getUid().equals(FirebaseAuth.getInstance().getUid()))
                new SendNotification(message,"New Message",mUser.getNotificationKey());
        }

    }

    private void intializeMessage() {
        messageList = new ArrayList<>();
        mChat= findViewById(R.id.messageList);
        mChat.setHasFixedSize(false);
        mChat.setNestedScrollingEnabled(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }


    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    private void intializeMedia() {
        mediaUriList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setHasFixedSize(false);
        mMedia.setNestedScrollingEnabled(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL,false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(),mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_INTENT){
            if (resultCode == RESULT_OK){
                if (data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());
                }else {
                    for (int i = 0; i< data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).toString());
                    }
                }
                mMediaAdapter.notifyDataSetChanged();
                if (!mediaUriList.toString().isEmpty()){
                    mSend.setVisibility(View.VISIBLE);
                    mAudio.setVisibility(View.GONE);
                }

            }

        }
    }


}