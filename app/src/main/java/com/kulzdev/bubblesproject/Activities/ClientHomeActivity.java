package com.kulzdev.bubblesproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kulzdev.bubblesproject.Models.User;
import com.kulzdev.bubblesproject.R;

import org.w3c.dom.Text;

public class ClientHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage,mDisplayName;
    private FirebaseAuth mAuth;
    private User findUser;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mDisplayName = (TextView)findViewById(R.id.nav_displayName);
        mTextMessage = (TextView)findViewById(R.id.txtMessage);


        //find a user by ID.

        //mTextMessage.setText("Welcome User " + mAuth.getCurrentUser().getUid());

        Query findUser = FirebaseDatabase.getInstance().getReference("users").child("Client")
                .orderByChild("id")
                .equalTo(mAuth.getCurrentUser().getUid());



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findUser.addValueEventListener(findUsersByID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if(id == R.id.nav_profile) {
            // Handle the camera action
           Intent i = new Intent(this, ClientProfileActivity.class);
           startActivity(i);

        }
        /* else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ValueEventListener findUsersByID = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){

                for(DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    findUser = dataSnap.getValue(User.class);

                    //mTextMessage.setText("Welcome " + findUser.getFullName());
                    //mDisplayName.setText(findUser.getFullName());

                    updateNavHeader();

                }


            }else{
                showMessage("User doesn't exits, please register");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void updateNavHeader(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navDisplayName = headerView.findViewById(R.id.nav_displayName);

        //Update the profile image here also
        ImageView nav_profile_image = findViewById(R.id.nav_user_photo);

        navDisplayName.setText(currentUser.getDisplayName());

        //currentUser.up


        //use Glide to load user image

        Glide.with(this).load(currentUser.getPhotoUrl()).into(nav_profile_image);



    }
}
