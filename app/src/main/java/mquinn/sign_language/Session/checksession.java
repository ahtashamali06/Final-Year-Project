package mquinn.sign_language.Session;

import android.app.Application;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mquinn.sign_language.Activities.Home_Activity;
import mquinn.sign_language.Servies.lunchAppServices;

public class checksession extends Application {

    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;

    @Override
    public void onCreate(){
        super.onCreate();

        // Check User already logged in or not

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
            startActivity(intent);
        }
        // Start Service
        startService(new Intent(this, lunchAppServices.class));
    }
}
