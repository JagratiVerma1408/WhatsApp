package com.example.whatsapp.loginsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private Button LogInBtn,PhoneLogInBtn;
    EditText email,password;
    TextView forgotPassword,needNewAccount;
    RelativeLayout relativeLayout;
    FirebaseAuth mauth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        InitialiseFiels();
        mauth=FirebaseAuth.getInstance();
        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendtoRegActivity();
            }
        });
        LogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogIn();
            }
        });
        PhoneLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendPhoneLogInActivity();
            }
        });
    }

    private void SendPhoneLogInActivity() {
        Intent mainIntent=new Intent(LogInActivity.this,PhoneLogInActivity.class);
        startActivity(mainIntent);
    }

    private void AllowUserToLogIn()
    {
        String userEmail=email.getText().toString();
        String userPassword=password.getText().toString();
        if(TextUtils.isEmpty(userEmail))
        {
            Snackbar.make(relativeLayout, "PLease Enter Your Email ...", Snackbar.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(userPassword))
        {
            Snackbar.make(relativeLayout,"Please Enter Password ...",Snackbar.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPassword))
        {
            Snackbar.make(relativeLayout,"Please Enter Email and Password ...",Snackbar.LENGTH_SHORT).show();

        }
        if(!TextUtils.isEmpty(userEmail) &&!TextUtils.isEmpty(userPassword)) {
            progressDialog.setTitle("Signing In")        ;
            progressDialog.setMessage("Please Wait ...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mauth.signInWithEmailAndPassword(userEmail,userPassword)
           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful())
                   {
                       SendUserMainActivity();
                       Toast.makeText(getApplicationContext(), "Logged In Successfully !!", Toast.LENGTH_SHORT).show();

                   }
                   else
                   {
                       String errorMessage=task.getException().getLocalizedMessage();
                       Snackbar.make(relativeLayout,"Error :"+errorMessage,Snackbar.LENGTH_SHORT).show();

                   }
                   progressDialog.cancel();
                   progressDialog.dismiss();
               }
           });
        }

    }

    private void InitialiseFiels() {
        LogInBtn=findViewById(R.id.login_button);
        PhoneLogInBtn=findViewById(R.id.phone_login_button);
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        forgotPassword=findViewById(R.id.forgot_password);
        needNewAccount=findViewById(R.id.newAccount);
        relativeLayout=findViewById(R.id.logIn_relative_layout);
        progressDialog=new ProgressDialog(this);
    }



    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void SendUserMainActivity() {
        Intent mainIntent=new Intent(LogInActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
   public void SendtoRegActivity()
    {
        Intent regIntent=new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(regIntent);

    }
}