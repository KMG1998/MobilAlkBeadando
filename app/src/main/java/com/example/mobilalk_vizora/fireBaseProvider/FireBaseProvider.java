package com.example.mobilalk_vizora.fireBaseProvider;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mobilalk_vizora.model.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;

public final class FireBaseProvider {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private final CollectionReference userDataCollection = mfirestore.collection("UserData");
    private final CollectionReference statementCollection = mfirestore.collection("Statements");


    public Task<AuthResult> loginWithEmail(String email, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(e -> Log.e("error", e.getMessage())).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userDataCollection.orderBy("userId").whereEqualTo("userId", mFirebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot docSnap : queryDocumentSnapshots) {
                        UserData item = docSnap.toObject(UserData.class);
                        item.setLastLogin(Timestamp.now());
                        userDataCollection.document(docSnap.getId()).set(item);
                        currentUser = mFirebaseAuth.getCurrentUser();
                    }
                });
            }
        });
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return mFirebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<DocumentReference> createUserData(String userName, String birthDate, String userId){
        UserData userData = new UserData(birthDate, Timestamp.now(), userId, userName);
        return userDataCollection.add(userData);
    }

    public Task<QuerySnapshot> getStatementsForUser(){
        return statementCollection.whereEqualTo("userId",currentUser.getUid()).get();
    }
    public Task<QuerySnapshot> getStatementsForUser(long limit){
        return statementCollection.whereEqualTo("userId",currentUser.getUid()).limit(limit).get();
    }
    public void logOut(){
        mFirebaseAuth.signOut();
    }
}
