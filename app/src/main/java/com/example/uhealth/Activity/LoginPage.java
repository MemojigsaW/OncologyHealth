package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhealth.utils.CachedThreadPool;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.concurrent.Semaphore;

public class LoginPage extends AppCompatActivity {
    private static final String TAG = LoginPage.class.getSimpleName();

    private EditText mEmail, mPw;
    private TextView mForgetPw;
    private Button mLogin, mSignUp;

    private FireBaseInfo mFireBaseInfo;
    private CachedThreadPool threadPool;
    private ProgressDialog progressDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mEmail = findViewById(R.id.Login_email);
        mPw = findViewById(R.id.Login_pw);
        mForgetPw = findViewById(R.id.Login_forgetpw);
        mLogin = findViewById(R.id.Login_login);
        mSignUp = findViewById(R.id.Login_Signup);

        mFireBaseInfo = new FireBaseInfo();
        threadPool = CachedThreadPool.getInstance();
        progressDialog = new ProgressDialog(this);

        mForgetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPw();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void forgetPw() {
        Toast.makeText(this, "Forget PW not implemented",Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        Intent intent = new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

    private void logIn() {
        handler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Runnable r = new Runnable() {
            String s_email, s_pw;
            boolean check;

            @Override
            public void run() {
                final Semaphore mutex = new Semaphore(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (textfieldcheck()) {
                            check = true;
                            s_email = mEmail.getText().toString();
                            s_pw = mPw.getText().toString();
                        } else {
                            check = false;
                            progressDialog.dismiss();
                        }
                        mutex.release();
                    }
                });

                try {
                    mutex.acquire();
                    if (check){
                        SignIn(s_email, s_pw);
                    }else{
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadPool.add_run(r);
    }

    private void SignIn(String s_email, String s_pw) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        };
        mFireBaseInfo.mAuth.signInWithEmailAndPassword(s_email, s_pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent profileintent = new Intent(LoginPage.this, ProfilePage.class);
                            handler.post(r);
                            startActivity(profileintent);
                            finish();
                        } else {
                            Toast.makeText(LoginPage.this, "Wrong Credential", Toast.LENGTH_SHORT).show();
                            handler.post(r);
                        }
                    }
                });
    }

    private boolean textfieldcheck() {
        boolean check = true;
        if (mEmail.getText().toString().equals("")){
            mEmail.setError("Empty Email");
            check = false;
        }
        if (mPw.getText().toString().equals("")){
            mPw.setError("Empty Pw");
            check = false;
        }
        return check;
    }

    public void forgetPw(View view) {
    }
}