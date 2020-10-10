package com.example.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    TextView navUsername, navEmail;
    CircleImageView navProfilePic;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setupInfo();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_notes);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotesFragment()).commit();
                break;
            case R.id.nav_tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TasksFragment()).commit();
                break;
            case R.id.nav_update_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UpdateProfileFragment()).commit();
                break;
            case R.id.nav_change_email:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChangeEmailFragment()).commit();
                break;
            case R.id.nav_change_password:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChangePasswordFragment()).commit();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_refresh:
                swipeRefreshLayout.setRefreshing(true);
                refresh();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(HomePageActivity.this, "Logged out successfully!!!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
        finish();
    }

    public void refresh() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof NotesFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotesFragment()).commit();
        } else if (currentFragment instanceof TasksFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TasksFragment()).commit();
        } else if (currentFragment instanceof ChangeEmailFragment) {
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else if (currentFragment instanceof ChangePasswordFragment) {
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else if (currentFragment instanceof UpdateProfileFragment) {
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new NotesFragment()).commit();
        }
    }

    public void setupInfo() {
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_header_username);
        navEmail = headerView.findViewById(R.id.nav_header_email);
        navProfilePic = headerView.findViewById(R.id.nav_header_profile_pic);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navProfilePic);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

//                if (user != null) {
//                    for (UserInfo profile : user.getProviderData()) {
//                        // Id of the provider (ex: google.com)
//                        String providerId = profile.getProviderId();
//
//                        // UID specific to the provider
//                        String uid = profile.getUid();
//
//                        // Name, email address, and profile photo Url
//                        String name = profile.getDisplayName();
//                        String email = profile.getEmail();
//                        Uri photoUrl = profile.getPhotoUrl();
//                    }
//                }

                String txt_email = "Email: " + firebaseAuth.getCurrentUser().getEmail();
                String txt_username = "Username: " + userProfile.getUsername();

                navEmail.setText(txt_email);
                navUsername.setText(txt_username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomePageActivity.this, "Unable to retrieve data, Error code: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
