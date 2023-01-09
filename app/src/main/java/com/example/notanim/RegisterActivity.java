package com.example.notanim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notanim.Network.NetworkChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextView backlogin;
    private Button register;
    private EditText nameTv,passwordTv,confpasswordTv,emailTv;

    //progress dialog
    private ProgressDialog progressDialog;

    //firebase auth
    private FirebaseAuth mAuth;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameTv = findViewById(R.id.nameEt);
        passwordTv = findViewById(R.id.passwordEt);
        confpasswordTv = findViewById(R.id.confEt);
        register = findViewById(R.id.submitBtn);
        emailTv = findViewById(R.id.emailid);
        backlogin = findViewById(R.id.backBtn);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click, go back
        backlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
        //handle click, begin register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatedate();
            }
        });
    }

    private String Name,Email,Password,CPassword;

    private void validatedate() {
        /*Before creating account, lets do some data validation*/

        //get data
        Name = nameTv.getText().toString().trim();
        Email = emailTv.getText().toString().trim();
        Password = passwordTv.getText().toString().trim();
        CPassword = confpasswordTv.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(Name)){
            //name edit text is empty, must enter name
            Toast.makeText(this, "Enter your name...!", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            //email is either no entered or email pattern is invalid, don't allow to continue in that case
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Password)){
            //password edit text is empty, must enter password
            Toast.makeText(this, "Enter your password...!", Toast.LENGTH_SHORT).show();
        }
        else if (Password.length()<8){
            //passwor is minimum 8 character or password pattern is invalid, don't allow to continue in that case
            Toast.makeText(this, "Password minimum 8 Character..!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(CPassword)){
            //confirm password edit text is empty,must enter confirm password
            Toast.makeText(this, "Confirm Password...!", Toast.LENGTH_SHORT).show();
        }
        else if (!Password.equals(CPassword)){
            //password and confirm password doesn't match, don't allow to continue in that case,both password must match
            Toast.makeText(this, "Password doesn't match...!", Toast.LENGTH_SHORT).show();
        }
        else {
            //all data is validated, begin creating account
            createaccount();
        }

    }

    private void createaccount() {
        //show progress
        progressDialog.setMessage("Creating account");
        progressDialog.show();

        //create user in firebase auth
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account creation success, now add in firebase realtime database
                inputUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //account creating failed
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inputUserInfo() {
        progressDialog.setMessage("Saving user info...!");
        progressDialog.show();

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get now
        String uid = mAuth.getUid();

        //setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("name",""+Name);
        hashMap.put("email",""+Email);
        hashMap.put("online","true");
        hashMap.put("usertype","user");//possible values are user, admin: will make manually admin in firebase realtime database by changing this values
        hashMap.put("timestamp",""+timestamp);

        //set data db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account Created...", Toast.LENGTH_SHORT).show();
                        //since user account is created so start dashboard of user
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //data failed adding to db
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}
