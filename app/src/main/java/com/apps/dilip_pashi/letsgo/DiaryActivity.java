package com.apps.dilip_pashi.letsgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.apps.dilip_pashi.letsgo.LetsGo.DETAILS;
import static com.apps.dilip_pashi.letsgo.LetsGo.UserPic;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;

public class DiaryActivity extends AppCompatActivity {
    public String getKey;
    LinearLayout ll;
    TextView emptyDiary;
    private ProgressDialog mProgressBar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        getKey = getIntent().getExtras().getString("key");
        //mReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(mUsername).child(getKey);
        //storage = FirebaseStorage.getInstance();
       // storageRef = storage.getReferenceFromUrl("gs://lets-go-99921.appspot.com");
       // mReference.keepSynced(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.tvtool);
        toolTitle.setText("Diary of " + getKey);
        setSupportActionBar(toolbar);
        ll = (LinearLayout) findViewById(R.id.ll);
        emptyDiary = (TextView) findViewById(R.id.emptyDiary);
        ImageView UserImg = (ImageView) findViewById(R.id.profile_pic);
        Picasso.with(this).load(UserPic).resize(500, 500).centerCrop().into(UserImg);
        ImageView back = (ImageView) findViewById(R.id.backBtn);
        back.setVisibility(VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiaryActivity.this, TravelActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        loadingRecyleView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem register = menu.findItem(R.id.nearbySearch);
        register.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nearbySearch:
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=nearby places to visit");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
                break;
        }
        return false;
    }

    public void loadingRecyleView() {
        mProgressBar = new ProgressDialog(DiaryActivity.this);
        mProgressBar.setMessage("Retrieving Data...");
        mProgressBar.show();
        recyclerView = (RecyclerView) findViewById(R.id.detailsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.dfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiaryActivity.this, AddDiaryActivity.class);
                i.putExtra("key", getKey);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

     final FirebaseRecyclerAdapter<DiaryModel, DiaryViewHolder> dRecyclerAdapter = new FirebaseRecyclerAdapter<DiaryModel, DiaryViewHolder>(
                DiaryModel.class,
                R.layout.diary_row,
                DiaryViewHolder.class,
                mReference.child(getKey).child(DETAILS)
        ) {
            @Override
            protected void populateViewHolder(DiaryViewHolder viewHolder, DiaryModel model, int position) {
                final String getDiaryKey = getRef(position).getKey();
                viewHolder.setDiaryTitle(model.getDiaryTitle());
                viewHolder.setDiaryDate(model.getDiaryDate());
                viewHolder.setDiaryDesc(model.getDiaryDesc());
                viewHolder.setDiaryLocation(model.getDiaryLocation());
                viewHolder.setImageFromUrl(getApplicationContext(), model.getDiaryImage());
                mProgressBar.dismiss();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(DiaryActivity.this, DiaryDetailsActivity.class);
                        i.putExtra("DiaryKey", getDiaryKey);
                        i.putExtra("Key", getKey);
                        startActivity(i);
                    }
                });
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiaryActivity.this);
                        alertDialogBuilder.setMessage("Want to delete this Diary?");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        mReference.child(getKey).child(DETAILS).child(getDiaryKey).removeValue();
                                        /*StorageReference a =storageRef.child(USERS+"/"+mUsername+"/"+getKey+"/"+DETAILS+"/"+getDiaryKey);
                                        String b = a.getName().toString();
                                        Toast.makeText(DiaryActivity.this,b,Toast.LENGTH_LONG).show();*/
                                        CheckRecylerViewData();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return false;
                    }
                });
            }

        };
        CheckRecylerViewData();
        recyclerView.setAdapter(dRecyclerAdapter);
    }
public void CheckRecylerViewData(){
    mReference.child(getKey).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if ((snapshot.hasChild(DETAILS)) && (snapshot.child(DETAILS).hasChildren())) {
                ll.setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);

            } else {
                ll.setVisibility(VISIBLE);
                emptyDiary.setText("Add diary entry for " + getKey);
                mProgressBar.dismiss();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDiaryTitle(String title) {
            TextView diaryTitle = (TextView) mView.findViewById(R.id.dTitle);
            diaryTitle.setText("Diary: " + title);
        }

        public void setDiaryDate(String Date) {
            TextView diaryDate = (TextView) mView.findViewById(R.id.dDate);
            diaryDate.setText("Date: " + Date);
        }

        public void setDiaryDesc(String Desc) {
            TextView diaryDesc = (TextView) mView.findViewById(R.id.dDesk);
            diaryDesc.setText("Desc: " + Desc);
        }

        public void setDiaryLocation(String Location) {
            TextView diaryLocation = (TextView) mView.findViewById(R.id.dLocation);
            diaryLocation.setText("Loc: " + Location);
        }

        public void setImageFromUrl(Context ctx, String Image) {
            ImageView diaryImageUrl = (ImageView) mView.findViewById(R.id.dLoadImage);
            Picasso.with(ctx).load(Image).resize(500, 500).centerCrop().into(diaryImageUrl);
        }

    }
}
