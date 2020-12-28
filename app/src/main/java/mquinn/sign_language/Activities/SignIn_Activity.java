package mquinn.sign_language.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

import mquinn.sign_language.Activities.otp_verify;
import mquinn.sign_language.R;

public class SignIn_Activity extends AppCompatActivity {

    Button msend;
    EditText mphoneNo;
    CountryCodePicker mcodePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        msend = findViewById(R.id.send);
        mphoneNo = findViewById(R.id.phoneNo);
        mcodePicker = findViewById(R.id.countrycode);


        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mphoneNo.length()==10) {

                    String country_code = "+" + mcodePicker.getSelectedCountryCode();
                    String UserEnterNumber = mphoneNo.getText().toString().trim();
                    String _phone = country_code + UserEnterNumber;
                    Intent intent = new Intent(getApplicationContext(), otp_verify.class);
                    intent.putExtra("phoneNo", _phone);
                    startActivity(intent);
                }
                if (mphoneNo.getText().toString().isEmpty()){
                    mphoneNo.setError("Please Enter Mobile Number First!");
                    mphoneNo.requestFocus();
                    return;
                }
                else if (mphoneNo.length()<10){
                    mphoneNo.setError("Mobile Number Not Correct!");
                    mphoneNo.requestFocus();
                    return;
                    }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        finish();
    }
}