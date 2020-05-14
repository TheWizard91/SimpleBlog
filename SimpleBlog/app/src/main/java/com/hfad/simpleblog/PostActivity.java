package com.hfad.simpleblog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDescription;
    private Button mSubmitButton;
    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgress;//TODO: Deprecated error that causes the compilation error to appear! FIX it!!!!!
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mStorage = FirebaseStorage.getInstance().getReference();

        mSelectImage = (ImageButton)findViewById(R.id.imageSelect);
        mPostTitle = (EditText)findViewById(R.id.titleField);
        mPostDescription = (EditText)findViewById(R.id.descriptionField);
        mSubmitButton = (Button)findViewById(R.id.submitButton);
        mProgress = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private void startPosting(){
        mProgress.setMessage("Posting to Blog ...");
        mProgress.show();
        final String title_vl=mPostTitle.getText().toString().trim();
        final String desc_vel = mPostDescription.getText().toString().trim();

        if(!TextUtils.isEmpty(title_vl)&&!TextUtils.isEmpty(desc_vel)&&mImageUri!=null) {
            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();// To do
                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title_vl);
                    newPost.child("desc").setValue(desc_vel);
                    newPost.child("image").setValue(downloadUrl.toString());
//                    newPost.child("uid").setValue(FirebaseAuth.getUid);
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    mProgress.dismiss();
                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }
    }
}
