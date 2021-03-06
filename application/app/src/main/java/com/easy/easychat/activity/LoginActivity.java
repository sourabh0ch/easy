package com.easy.easychat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easy.easychat.Utills.CommonConstants;
import com.easy.easychat.R;
import com.easy.easychat.Utills.SharedPrefrenceUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private EditText etMobileNo;
    private Button btnLogin,btnGenerateOtp;
    private FirebaseAuth auth;
    private  String mobNo;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        proceed();

    }

    private void proceed(){
        if(SharedPrefrenceUtil.isLogIn(this))
        {
            SharedPreferences pref=getSharedPreferences("pendingnoti", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("pendingnoti", "yes");
            editor.commit();

            Intent contentIntent = new Intent(LoginActivity.this,
                    HomeActivity.class);
            contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(contentIntent);
            finish();
        }else {
            setContentView(R.layout.activity_update_user_profile);
            initView();
            initOnClckListener();
        }
    }

    private void  initView(){
        etMobileNo = (EditText)findViewById(R.id.et_update_mobileNo);
        btnLogin = (Button)findViewById(R.id.btn_login);
        //btnGenerateOtp = (Button)findViewById(R.id.btn_generate_otp);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
    }

    private void initOnClckListener(){

        btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mobNo  = etMobileNo.getText().toString().trim();
                if(mobNo.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please enter mobile no",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setTitle("Loging in");
                    progressDialog.setMessage("Please wait while loging in");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    // verificationCode = "123456";
                    loginUser("+91"+mobNo);

                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendOtpActivity("+91"+mobNo);
            }
        });
    }

    private void loginUser(final String mobileNo){
        try{
            mCallback  = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                     String auth = phoneAuthCredential.getSmsCode();
                     Log.d("authCredential",auth);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    progressDialog.setMessage(e.getMessage());
                    progressDialog.dismiss();
                    String errorMsg = e.getMessage();
                    Log.d("FirebaseException",e.getMessage());
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    verificationCode = s;
                    token = forceResendingToken;

                    Toast.makeText(LoginActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
                }
            };

            PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNo,60, TimeUnit.SECONDS,LoginActivity.this,mCallback);
            progressDialog.dismiss();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendOtpActivity(String mobileNo){
        Intent intent = new Intent(LoginActivity.this,OtpActivity.class);
        intent.putExtra(CommonConstants.MOBILE_NO,mobileNo);
        intent.putExtra(CommonConstants.OTP,verificationCode);
        SharedPrefrenceUtil.setIsLogIn(LoginActivity.this, true);
        startActivity(intent);
    }


}
