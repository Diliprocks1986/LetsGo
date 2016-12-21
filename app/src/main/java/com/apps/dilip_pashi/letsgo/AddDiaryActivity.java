package com.apps.dilip_pashi.letsgo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;

import static com.apps.dilip_pashi.letsgo.LetsGo.DETAILS;
import static com.apps.dilip_pashi.letsgo.LetsGo.USERS;
import static com.apps.dilip_pashi.letsgo.LetsGo.UserPic;
import static com.apps.dilip_pashi.letsgo.LetsGo.mReference;
import static com.apps.dilip_pashi.letsgo.LetsGo.mUsername;
import static com.apps.dilip_pashi.letsgo.LetsGo.storageRef;
import static com.apps.dilip_pashi.letsgo.R.id.addImage;
import static com.apps.dilip_pashi.letsgo.R.id.addVideo;
import static java.lang.String.valueOf;

public class AddDiaryActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST = 1;
    private static final int GALARY_REQUEST = 2;
    public static String diaryTitle, diaryDesc, diaryDate, diaryLocation, diaryImage, diaryVideo;
    static Button BTdiaryDate, BTdiaryLocation;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public String getKey = null;
    EditText ETdiaryTitle, ETdiaryDesc;
    ImageButton IBImage, IBVideo;
    Uri imageUri, imguri = null;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_add);
        diaryVideo = "NA";
        getKey = getIntent().getExtras().getString("key");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.tvtool);
        toolTitle.setText("Add Diary For " + getKey);
        setSupportActionBar(toolbar);
        BTdiaryLocation = (Button) findViewById(R.id.location);
        BTdiaryDate = (Button) findViewById(R.id.diaryDate);
        IBImage = (ImageButton) findViewById(addImage);
        IBVideo = (ImageButton) findViewById(addVideo);
        storageRef = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        ImageView UserImg = (ImageView) findViewById(R.id.profile_pic);
        Picasso.with(this).load(UserPic).resize(500, 500).centerCrop().into(UserImg);
        ImageView back = (ImageView) findViewById(R.id.backBtn);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddDiaryActivity.this, DiaryActivity.class);
                i.putExtra("key", getKey);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                mProgress.setMessage("Adding Diary to " + getKey);
                mProgress.show();
                ETdiaryTitle = (EditText) findViewById(R.id.diaryTitle);
                ETdiaryDesc = (EditText) findViewById(R.id.diaryDesc);
                diaryTitle = ETdiaryTitle.getText().toString();
                diaryDesc = ETdiaryDesc.getText().toString();
                if (!TextUtils.isEmpty(diaryTitle) && !TextUtils.isEmpty(diaryDesc) && !TextUtils.isEmpty(diaryDate) && !TextUtils.isEmpty(diaryLocation) && imageUri != null) {
                    StorageReference imagePath = storageRef.child(USERS).child(mUsername).child(getKey).child(diaryTitle).child(imageUri.getLastPathSegment());
                    imagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imguri = taskSnapshot.getDownloadUrl();
                            assert imguri != null;
                            diaryImage = String.valueOf(imguri);
                            DiaryModel DiaryEvent = new DiaryModel(diaryTitle, diaryDesc, diaryDate, diaryLocation, diaryImage, diaryVideo);
                            mReference.child(getKey).child(DETAILS).child(diaryTitle).setValue(DiaryEvent);
                            mProgress.dismiss();
                            Intent i = new Intent(AddDiaryActivity.this, DiaryActivity.class);
                            i.putExtra("key", getKey);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                } else {
                    mProgress.dismiss();
                    Toast.makeText(AddDiaryActivity.this, "Please Fill all Fields!", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.cancel:
                Toast.makeText(AddDiaryActivity.this, "Entry Canceled by " + mUsername, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void AddLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(this);
            startActivityForResult(intent, LOCATION_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (resultCode == RESULT_OK) {
                    com.google.android.gms.location.places.Place place = PlacePicker.getPlace(data, this);
                    diaryLocation = valueOf(place.getAddress());
                    BTdiaryLocation.setText("Location: " + diaryLocation);
                }
                break;
            case GALARY_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    IBImage.setImageURI(imageUri);

                }
        }

    }

    public void AddDiaryDate(View view) {
        DialogFragment AddDiaryDateFragment = new AddDiaryDateFragment();
        AddDiaryDateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addFiles(View view) {
        switch (view.getId()) {
            case addImage:
                Intent galaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent, GALARY_REQUEST);
                break;
            case addVideo:
                Toast.makeText(AddDiaryActivity.this, "This feature is not Available Now", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void PermissionCheck(View view) throws IOException {
        int hasWriteLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        AddLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    AddLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Location Permssions Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class AddDiaryDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            diaryDate = valueOf(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append(" "));
            SetDiaryDate(diaryDate);
        }

        private void SetDiaryDate(String diaryDate) {
            BTdiaryDate.setText("Diary Date:    " + diaryDate);
        }
    }
}