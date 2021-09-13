package com.example.uhealth;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.uhealth.Activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentUpdater extends AppCompatActivity  implements PermissionInterface {
    final static int INITIAL_REQUESTCODE = 10000;
    List<UpdatedPhoto> PhotoList = new ArrayList<>();
    private int requestCode= INITIAL_REQUESTCODE;
    private PermissionHelper mPermissionHelper;
    private FireBaseInfo mFireBaseInfo;
    private AlertDialog mFurtherUpdateDialog;
    String currentPhotoPath;
    String currentPhotoDir;
    private PhotoAdapter photoAdapter;

    String target_path;
    String target_name;
    private List<String> PhotoNameList = new ArrayList<String>();
    private List<String> PhotoTitleList = new ArrayList<String>();
    final static int REQUEST_TAKE_PHOTO =255;
    public void initPermission(){

        mPermissionHelper = new PermissionHelper(AppointmentUpdater.this, this);
        mPermissionHelper.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA});
    }
    private static Bitmap big(Bitmap bitmap) {
    Matrix matrix = new Matrix();
    matrix.postScale(2.2f,2.2f);
    Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    return resizeBmp; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_TAKE_PHOTO  && resultCode == RESULT_OK ) {
            if(data!=null)
            {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap)extras.get("data");
                //Toast.makeText(MainActivity.this, "Bad boy!!!", Toast.LENGTH_LONG).show();
                //
                Bitmap bigmap = big(bitmap);
                int ind = PhotoTitleList.size()-1;
                String title = PhotoTitleList.get(ind);
                UpdatedPhoto mPhoto = new UpdatedPhoto(title ,bigmap );
                PhotoList.add(mPhoto);
                photoAdapter.notifyDataSetChanged();
                uploadPhoto(bigmap );
            }
            else
            {
                // Toast.makeText(MainActivity.this, "Good boy!!!", Toast.LENGTH_LONG).show();
            }



        }

    }
    public void takePhoto(String t_path,String t_name){
        target_name = t_name;
        target_path = t_path;
        dispatchTakePictureIntent();
    }
    public void uploadPhoto(Bitmap mbitmap){
        String cloudpath = "images/";
        uploadmodule(mbitmap,cloudpath);

    }
    private String titleholder;
    public void furtherUpdate(String StringDate,String StringType){
      //  AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AppointmentUpdater.this);

       //LayoutInflater minflater = LayoutInflater.from(AppointmentUpdater.this);
       // View em_view = minflater.inflate(R.layout.appointment_update_dialog, null);
        //alertBuilder.setView(em_view );
        EditText EdNote = (EditText)(findViewById(R.id.appointment_note));
        Button btnPhoto = (Button)(findViewById(R.id.btn_photo_uploader));
        Button btnAdder =  (Button)(findViewById(R.id.btn_information_adder));
        //TextView PhotoTitle  =(TextView)(findViewById(R.id.current_title));

        final String str_date = StringDate;
        final String str_type = StringType;
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date mdate = new Date();

               PhotoNameList.add("images/"+mdate.getTime()+".jpg");//cloud path and local path
                PhotoTitleList.add(mdate.getTime()+".jpg");
                titleholder =   mdate.getTime()+".jpg";
                String t_name = mdate.getTime()+".jpg";
                String t_path ="/ece1778";
                takePhoto(t_path,t_name);
            }
        });
        btnAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String m_Note =EdNote.getText().toString();
                SimpleDateFormat mdateformat= new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                Date d_instance_date = new Date();
                int i_initdate = 0;
                try{
                    d_instance_date = mdateformat.parse(str_date);
                    i_initdate = (int)(d_instance_date.getTime()/1000);
                  //  Toast.makeText(AppointmentUpdater.this,str_date+" ;"+mFireBaseInfo.mUser.getUid()+" ;"+str_type,Toast.LENGTH_LONG).show();
                }catch (ParseException e){
                    e.printStackTrace();

                }
                final List<String> ImagePaths= PhotoNameList;
                final String mNote = EdNote.getText().toString();
                mFireBaseInfo.mFirestore.collection("AAppointment").whereEqualTo("patientid",mFireBaseInfo.mUser.getUid())
                        .whereEqualTo("type",str_type ).whereEqualTo("date",i_initdate)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {//////
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {////
                                if(task.isSuccessful()){

                                    QuerySnapshot query_res = task.getResult();
                                    List<DocumentSnapshot> documents = query_res.getDocuments();

                                    DocumentSnapshot delDocument = documents.get(0);


                                    if(delDocument!=null){
                                        DocumentReference delDocumentRef = delDocument.getReference();
                                        delDocumentRef.update("path",ImagePaths);
                                        delDocumentRef.update("note",mNote);
                                        Intent mintent = new Intent();
                                        setResult(RESULT_OK, mintent);

                                        finish();


                                    }
                                    else{

                                    }
                                }
                                else{

                                }

                            }////


                        });//////

               // mFurtherUpdateDialog.dismiss();
                //Intent mintent = new Intent();
               // setResult(RESULT_OK, mintent);

             //   finish();
            }
        });

       // mFurtherUpdateDialog = alertBuilder.create();
      //  mFurtherUpdateDialog.show();

    }
    @Override
    protected void onStart(){
        super.onStart();
        initPermission();
        Intent starter = getIntent();
        final String StrType = starter.getStringExtra("apttype");
        final String StrDate  =starter.getStringExtra("apttime");
        furtherUpdate(StrDate,StrType);
        //final List<String>
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_updater);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_photolist);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(PhotoList);
        recyclerView.setAdapter(photoAdapter);
        mFireBaseInfo = new FireBaseInfo();

        //FloatingActionButton fab = findViewById(R.id.fab);

       // mFireBaseInfo = new FireBaseInfo();
        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void requestPermissionsSuccess() {

        if (requestCode == 10000) {

        } else if (requestCode == 1) {
            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent1, 1357);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }


    @Override
    public void requestPermissionsFail() {



    }

    @Override
    public int getPermissionsRequestCode() {

        return requestCode;
    }
    private void dispatchTakePictureIntent() {
      //  Toast.makeText(AppointmentUpdater.this, "dispatchTakePictureIntent", Toast.LENGTH_LONG).show();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = null;
            //try {
                //photoFile = createImageFile();
            //} catch (IOException ex) {
                // Error occurred while creating the File
              //  ex.printStackTrace();
            //}
            // Continue only if the File was successfully created
            //if (photoFile != null) {

                Uri photoURI = null;
                /*
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                {
                    // Toast.makeText(RegisterActivity.this,"SDK>22R" ,Toast.LENGTH_LONG).show();
                    photoURI = Uri.fromFile(photoFile);
                }
                else
                {
                    //Toast.makeText(RegisterActivity.this,"SDK<22" ,Toast.LENGTH_LONG).show();
                    photoURI = Uri.fromFile(photoFile);
                }*/


               // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            //}
        }
        else
        {
            //Toast.makeText(RegisterActivity.this, "dgetPackageManager NULL!!!", Toast.LENGTH_LONG).show();
        }

    }
    //private File createImageFile() throws IOException {
        // Create an image file name
        //Toast.makeText(RegisterActivity.this, "Creating picFiles!!!", Toast.LENGTH_LONG).show();
     //   String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //String impath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/ece1778";
       // String impath =  AppointmentUpdater.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //String impath =  Environment.getExternalStorageDirectory().getAbsolutePath()+target_path;
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       // File storageDir = new File(impath);
      //  if (!storageDir.exists()) {
      //      storageDir.mkdirs();
      //  }
       // File image = new File(storageDir ,target_name + ".jpg");
        /*
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
*/
        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
       // currentPhotoDir = impath;
      //  return image;
   // }
    public void uploadmodule(Bitmap mbitmap, String cloudpath){
        // File or Blob
        //Uri file = Uri.fromFile(new File(localpath));

// Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

// Upload file and metadata to the path 'images/mountains.jpg'
        StorageReference PhotoRef = mFireBaseInfo.mStorage.getReference().child(cloudpath+target_name);
     //   UploadTask uploadTask = mFireBaseInfo.mStorage.getReference().child(cloudpath+"/"+file.getLastPathSegment()).putFile(file, metadata);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
// Listen for state changes, errors, and completion of the upload.
        mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = PhotoRef.putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //Log.d(TAG, "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //  Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete

                // ...
            }
        });
    }

}
