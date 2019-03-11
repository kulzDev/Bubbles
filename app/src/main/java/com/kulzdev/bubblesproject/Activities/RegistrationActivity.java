package com.kulzdev.bubblesproject.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kulzdev.bubblesproject.Models.User;
import com.kulzdev.bubblesproject.R;

public class RegistrationActivity extends AppCompatActivity {

    private static int PReCode = 1;
    private static int REQUESTCODE = 1;

    private Uri pickedImg;

    private EditText userFullName, userEmail, userPassword, userConfirmPassword;
    private String email, password, confirmPassword, fullName;
    private ProgressBar loadingProgress;
    private Button registerBtn;
    private TextView userLogin;
    private ImageView imgUserPhoto;

    private String TAG = "TAG";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RadioGroup radioGroup;

    private RadioButton userTypeRbtn;
    private RadioButton clientRadioButton;
    private RadioButton stylistRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();


        imgUserPhoto = (ImageView) findViewById(R.id.imgProfile);

        //Text Fields
        userConfirmPassword = (EditText) findViewById(R.id.txtViewConfirmPassword);
        userFullName = (EditText) findViewById(R.id.txtViewfullName);
        userEmail = (EditText) findViewById(R.id.txtViewRegEmail);
        userPassword = (EditText) findViewById(R.id.txtViewRegPassword);


        //radio group
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupId);
        stylistRadioButton = (RadioButton) findViewById(R.id.stylist_radioBtn);
        clientRadioButton = (RadioButton) findViewById(R.id.client_radioBtn);

        userLogin = (TextView) findViewById(R.id.txtLogin);

        registerBtn = (Button) findViewById(R.id.btnSignUp);

       // loadingProgress = (ProgressBar) findViewById(R.id.progressBar);


        //OnClick Register Button
        registerBtn.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {

                if (userEmail.getText() != null) {
                    email = userEmail.getText().toString();
                }
                if (userPassword.getText() != null) {
                    password = userPassword.getText().toString();
                }
                if (userConfirmPassword.getText() != null) {
                    confirmPassword = userConfirmPassword.getText().toString();
                }
                if (userFullName.getText() != null) {
                    fullName = userFullName.getText().toString();
                }


                final int radioId = radioGroup.getCheckedRadioButtonId();
                userTypeRbtn = findViewById(radioId);
                final String userType = userTypeRbtn.getText().toString();
               // registerBtn.setVisibility(View.VISIBLE);
               // loadingProgress.setVisibility(View.INVISIBLE);



                User user = new User(fullName, email, userType);

                if (fullName.isEmpty()) {
                    userFullName.setError("Full Name requried");
                    userFullName.requestFocus();
                }
                if (email.isEmpty()) {
                    userEmail.setError("Email is required");
                    userEmail.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    userEmail.setError("Please enter a valid email address");
                    userEmail.requestFocus();
                }
                if (password.isEmpty()) {
                    userPassword.setError("Password is required");
                    userPassword.requestFocus();
                }
                if (!password.equals(confirmPassword)) {
                    userConfirmPassword.setError("Passwords do not matchs");
                    userConfirmPassword.requestFocus();
                }


                CreateUserAccount(password, user);


            }
        });

        //OnClick User Login Text
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        //OnClick Profile Image
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "image clicked", Toast.LENGTH_LONG).show();

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }


            }
        });
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegistrationActivity.this, "Please accept for permission", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(RegistrationActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReCode);
                Log.d("Img", "checkAndRequestPermission permission accepted");
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image

        Log.d("Img", "Gallery Image");

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);

    }

    private void CreateUserAccount(String password, final User user) {
        Toast.makeText(this, "Registration starts", Toast.LENGTH_LONG).show();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            showMessage("Account Created");
                            user.setId(mAuth.getCurrentUser().getUid()); //this will match the mAuth and Database userID
                            updateUserInfo(user, pickedImg, mAuth.getCurrentUser());

                        } else {
                            showMessage("Acocunt creation failed" + task.getException().getMessage());
                           // registerBtn.setVisibility(View.VISIBLE);
                           // loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void updateUserInfo(final User user, Uri pickedImageUri,final FirebaseUser currentUser) {



        //updating firebase Authentication user-profile
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //image uploaded successfully
                   imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {


                           UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                   .setDisplayName(user.getFullName())
                                   .setPhotoUri(uri)
                                   .build();

                           currentUser.updateProfile(profileUpdate)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if(task.isSuccessful()){
                                               showMessage("Registration Complete");
                                           }
                                       }
                                   });
                       }


                   });
            }
        });




        //adding user to the database
        mDatabase.child(user.getClientType()).child(user.getId())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage("Registration successful");

                            Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(i);
                        }

                    }
                });
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //the user has successfully picked an image
            //need to save it's reference to the Uri variable

            pickedImg = data.getData();
            imgUserPhoto.setImageURI(pickedImg);


        }
    }
}
