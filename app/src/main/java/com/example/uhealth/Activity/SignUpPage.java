package com.example.uhealth.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uhealth.utils.CachedThreadPool;
import com.example.uhealth.utils.FireBaseInfo;
import com.example.uhealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = SignUpPage.class.getSimpleName();
    
    private EditText mUsername, mEmail, mPw, mConfirmPw;
    private Button mSubmit;
    private TextView mPrivacyPolicy;
    private CheckBox mcheckbox;

    private FireBaseInfo mFireBaseInfo;
    private CachedThreadPool threadManager;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Handler mhandler;
    private Boolean dialogopen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mUsername =findViewById(R.id.SignUp_username);
        mEmail = findViewById(R.id.SignUp_email);
        mPw = findViewById(R.id.SignUp_pw);
        mConfirmPw = findViewById(R.id.SignUp_pwre);
        mSubmit = findViewById(R.id.SignUp_submit);
        mPrivacyPolicy = findViewById(R.id.SignUp_PrivacyPolicy);
        mcheckbox = findViewById(R.id.SignUp_agree_PrivacyPolicy);
        dialog = new Dialog(this);
        threadManager = CachedThreadPool.getInstance();
        mFireBaseInfo = new FireBaseInfo();

        getSupportActionBar().setTitle("Sign Up");

        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPrivacyDialog();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        if (savedInstanceState != null){
            dialogopen = savedInstanceState.getBoolean("DIALOGOPEN");
            if (dialogopen){
                openPrivacyDialog();
            }
        }else{
            dialogopen = false;
        }
    }

//    after onStop
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("DIALOGOPEN", dialogopen);
        super.onSaveInstanceState(outState);
    }

//    after on start
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        dialogopen = savedInstanceState.getBoolean("DIALOGOPEN");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void openPrivacyDialog() {
        TextView textView;
        Button btncancel;
        dialog.setContentView(R.layout.dialog_policy);
        btncancel = dialog.findViewById(R.id.policy_cancelbtn);
        textView = dialog.findViewById(R.id.policy_txtview);
        textView.setMovementMethod(new ScrollingMovementMethod());
        dialog.setCancelable(false);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpPage.this.dialogopen = false;
                dialog.dismiss();
            }
        });
        dialogopen = true;
        Log.d(TAG, "openPrivacyDialog: ");
        dialog.show();
    }

    private void submit() {
        mhandler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Runnable r = new Runnable() {
            String s_email, s_pw, s_username;
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
                            s_username = mUsername.getText().toString();
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
                        registeruser(s_email, s_pw, s_username);
                    }else{
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadManager.add_run(r);
    }

    private void registeruser(String s_email, String s_pw, String s_username) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        };
        mFireBaseInfo.mAuth.createUserWithEmailAndPassword(s_email, s_pw)
                .addOnCompleteListener(SignUpPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createUser(s_username, s_email, r);
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mPw.setError("PW require 6 char");
                                mhandler.post(r);
                                return;
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mEmail.setError("Invalid Credential");
                                mhandler.post(r);
                                return;
                            } catch (FirebaseAuthUserCollisionException e) {
                                mEmail.setError("Email already used");
                                mhandler.post(r);
                                return;
                            } catch (Exception e) {
                                mhandler.post(r);
                                return;
                            }
                        }
                    }
                });
    }

    private void createUser(String s_username, String s_email, Runnable r) {
        Map<String, Object> userinfo = new HashMap<>();
        userinfo.put("username", s_username);
        userinfo.put("email", s_email);
        mFireBaseInfo = new FireBaseInfo();
        mFireBaseInfo.mFirestore.collection("users").document(mFireBaseInfo.mUser.getUid())
                .set(userinfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: reached createuser");
                        Intent intent = new Intent(SignUpPage.this, ProfilePage.class);
                        mhandler.post(r);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpPage.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        mhandler.post(r);
                        return;
                    }
                });
    }

    private boolean textfieldcheck() {
        boolean pass = true;
        if (mUsername.getText().toString().equals("")){
            mUsername.setError("empty username");
            pass = false;
        }
        if (mPw.getText().toString().equals("")){
            mPw.setError("Empty Pw");
            pass = false;
        }
        if (mEmail.getText().toString().equals("")){
            mEmail.setError("Empty email");
            pass = false;
        }else if (!isValidEmail(mEmail.getText().toString())){
            mEmail.setError("Invalid email");
            pass = false;
        }
        if (!mPw.getText().toString().equals(mConfirmPw.getText().toString())){
            mConfirmPw.setError("PW does not match");
            pass = false;
        }
        if (!mcheckbox.isChecked()){
            mcheckbox.setError("Must agree to policy");
            pass = false;
        }
        return pass;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

