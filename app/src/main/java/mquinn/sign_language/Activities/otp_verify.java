package mquinn.sign_language.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import mquinn.sign_language.R;

public class otp_verify extends AppCompatActivity {

    String verificationCodeBySystem;
    PinView pinView;
    TextView textView;
    Button signin;
    ProgressBar progressBar;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        pinView = findViewById(R.id.pinView);
        textView = findViewById(R.id.show_phone_Number);
        signin = findViewById(R.id.get_code);
        progressBar = findViewById(R.id.progressBar);



         phoneNo = getIntent().getStringExtra("phoneNo");
        String text = textView.getText().toString().trim();
        textView.setText(text + phoneNo);

        sendVerrificationCodeToUser(phoneNo);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserCode = pinView.getText().toString();
                if (UserCode.isEmpty() || UserCode.length()<6){
                    pinView.setError("Wrong OTP...");
                    pinView.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifycode(UserCode);
            }
        });

    }

    private void sendVerrificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinView.setText(code);
                progressBar.setVisibility(View.VISIBLE);
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(otp_verify.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    private void verifycode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
        SignIn(credential);
    }

    private void SignIn(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(otp_verify.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedpreference",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Phone_Number",phoneNo);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), Register_User.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("number",phoneNo);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(otp_verify.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}