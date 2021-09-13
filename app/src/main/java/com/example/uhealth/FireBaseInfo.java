package com.example.uhealth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FireBaseInfo {

    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;
    public FirebaseUser mUser;
    public FirebaseStorage mStorage;
    public StorageReference mStorageRef;

    public FireBaseInfo(){
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
    }
}
