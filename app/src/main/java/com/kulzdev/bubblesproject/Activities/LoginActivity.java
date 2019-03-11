package com.kulzdev.bubblesproject.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kulzdev.bubblesproject.Models.User;
import com.kulzdev.bubblesproject.R;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText userEmail, userPassword;
    private Button btnLogin;
    private TextView signUp;
    private FirebaseAuth mAuth;
    private User findUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        signUp = (TextView)findViewById(R.id.txtSignUp);
        btnLogin = (Button)findViewById(R.id.btnSignUp);
        userEmail = (EditText)findViewById(R.id.Email);
        userPassword = (EditText)findViewById(R.id.Password);

        mAuth = FirebaseAuth.getInstance();

        //Log.d("TAG","mAuth.getCurrentUser().getID() : " + mAuth.getCurrentUser().getUid());



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mail = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){

                    showMessage("Enter your email and password");

                }else{


                    signIn(mail, password);
                }


            }
        });




        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    Log.d("Log In","Login Act " + mAuth.getCurrentUser().getUid());
                    Log.d("Log In","User Email " + mAuth.getCurrentUser().getEmail());

                    //find out who locked in

                    Query findUser = FirebaseDatabase.getInstance().getReference("users").child("Client")
                            .orderByChild("id")
                            .equalTo(mAuth.getCurrentUser().getUid());

                    findUser.addValueEventListener(findUsersByID);
                }
            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUI(){

        if(findUser.getClientType().toString().equals("Client")){
            Intent i = new Intent(LoginActivity.this, ClientHomeActivity.class);
            startActivity(i);
        }else{

            //Intent i = new Intent(LoginActivity.this, StylistActivity.class);
           // startActivity(i);
        }

    }


    ValueEventListener findUsersByID = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){

                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    findUser = dataSnap.getValue(User.class);
                }

                updateUI();
            }else{
                showMessage("User doesn't exits, please register");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
