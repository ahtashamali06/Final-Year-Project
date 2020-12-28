package mquinn.sign_language.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import mquinn.sign_language.R;

public class chatRoomActivity extends AppCompatActivity {

    EditText secretCodeBox;
    Button join,share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);


        secretCodeBox = findViewById(R.id.secretCodeBox);
        join = findViewById(R.id.joinButton);
        share = findViewById(R.id.shareButton);

        URL serverUrl;
        try {
            serverUrl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defultOption =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverUrl)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defultOption);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JitsiMeetConferenceOptions options =
                        new JitsiMeetConferenceOptions.Builder()
                                .setRoom(secretCodeBox.getText().toString())
                                .setWelcomePageEnabled(false)
                                .build();
                JitsiMeetActivity.launch(chatRoomActivity.this,options);
            }
        });
    }
}