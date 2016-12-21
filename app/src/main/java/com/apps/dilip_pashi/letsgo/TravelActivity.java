package com.apps.dilip_pashi.letsgo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static com.apps.dilip_pashi.letsgo.LetsGo.UserPic;
import static com.apps.dilip_pashi.letsgo.LetsGo.auth;
import static com.apps.dilip_pashi.letsgo.LetsGo.mFirebaseUser;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;
import static com.apps.dilip_pashi.letsgo.LetsGo.mUsername;


public class TravelActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 0;
    ImageView nothing;
    private ProgressDialog mProgressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.tvtool);
        toolTitle.setText("Lets Go");
        setSupportActionBar(toolbar);
        nothing = (ImageView) findViewById(R.id.nothing);
        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser == null) {
            //User not Logged In
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(/*new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),*/
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.LoginThemeUI)
                            .build(),
                    RC_SIGN_IN);
        } else {
            SignInDone();
        }
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                auth = FirebaseAuth.getInstance();
                mFirebaseUser = auth.getCurrentUser();
                mUsername = mFirebaseUser.getDisplayName();
                UserPic = mFirebaseUser.getPhotoUrl();
                Toast.makeText(this, "Welcome " + mUsername, Toast.LENGTH_LONG).show();
                SignInDone();
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    public void SignInDone() {
        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();
        mUsername = mFirebaseUser.getDisplayName();
        UserPic = mFirebaseUser.getPhotoUrl();
        ImageView UserImg = (ImageView) findViewById(R.id.profile_pic);
        Picasso.with(this).load(UserPic).resize(500, 500).centerCrop().into(UserImg);
        mProgressBar = new ProgressDialog(this);
        mProgressBar.setMessage("Retrieving Data...");
        mProgressBar.show();
        mReference = FirebaseDatabase.getInstance().getReference().child(LetsGo.USERS).child(mUsername);
        mReference.keepSynced(true);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TravelActivity.this, AddTravelActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        final FirebaseRecyclerAdapter<TravelModel, PlaceViewHolder> fRecyclerAdapter = new FirebaseRecyclerAdapter<TravelModel, PlaceViewHolder>(
                TravelModel.class,
                R.layout.travel_row,
                PlaceViewHolder.class,
                mReference
        ) {
            @Override
            protected void populateViewHolder(PlaceViewHolder viewHolder, TravelModel model, int position) {
                final String getKey = getRef(position).getKey();
                viewHolder.setPlace(model.getPlaceName());
                viewHolder.setStartDate(model.getStartDate());
                viewHolder.setEndDate(model.getEndDate());
                mProgressBar.dismiss();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(TravelActivity.this, DiaryActivity.class);
                        i.putExtra("key", getKey);
                        startActivity(i);
                    }
                });

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent i = new Intent(TravelActivity.this, EditTravelActivity.class);
                        i.putExtra("key", getKey);
                        startActivity(i);
                        return false;
                    }
                });
            }

        };
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nothing.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(fRecyclerAdapter);
                } else {
                    nothing.setVisibility(View.VISIBLE);
                    mProgressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("While Closing! You want to Sign Out?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AuthUI.getInstance().signOut(TravelActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPlace(String place) {
            TextView placeTitle = (TextView) mView.findViewById(R.id.placeName);
            placeTitle.setText(place);
        }

        public void setStartDate(String sDate) {
            TextView sDateval = (TextView) mView.findViewById(R.id.tripStartDate);
            sDateval.setText(sDate);
        }

        public void setEndDate(String eDate) {
            TextView eDateval = (TextView) mView.findViewById(R.id.tripEndDate);
            eDateval.setText(eDate);
        }

    }
}

