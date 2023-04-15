package com.example.mobilalk_vizora.fireBaseProvider;

import com.example.mobilalk_vizora.model.UserData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public final class FireBaseProvider {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    private final CollectionReference userDataCollection = mfirestore.collection("UserData");

    public Task<AuthResult> loginWithEmail(String email, String password){
        return mFirebaseAuth.signInWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> registerUser(String email, String password){
        return mFirebaseAuth.createUserWithEmailAndPassword(email,password);
    }

    public Task<DocumentReference> createUserData(String userId, String userName, String birthDate){
        return userDataCollection.add(new UserData());
    }
}
