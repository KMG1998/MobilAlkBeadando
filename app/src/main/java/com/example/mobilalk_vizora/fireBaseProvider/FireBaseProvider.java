package com.example.mobilalk_vizora.fireBaseProvider;

import android.net.Uri;
import android.util.Log;

import com.example.mobilalk_vizora.model.Statement;
import com.example.mobilalk_vizora.model.UserData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class FireBaseProvider {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = mStorage.getReferenceFromUrl("gs://mobilalk-vizora.appspot.com");
    private FirebaseUser currentUser;
    private final CollectionReference userDataCollection = mfirestore.collection("UserData");
    private final CollectionReference statementCollection = mfirestore.collection("Statements");

    public Task<AuthResult> loginWithEmail(String email, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().getUser();
                Log.d("firbase_tag", "login task result user is"+ currentUser.getUid());
                userDataCollection.whereEqualTo("userId", currentUser.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot docSnap : queryDocumentSnapshots) {
                        UserData item = docSnap.toObject(UserData.class);
                        item.setLastLogin(Timestamp.now());
                        userDataCollection.document(docSnap.getId()).set(item);
                    }
                });
            } else {
                Log.d("firbase_tag", "login exception");
            }
        });
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return mFirebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<DocumentReference> createUserData(String birthDate, String userId) {
        UserData userData = new UserData(birthDate, Timestamp.now(), userId);
        return userDataCollection.add(userData);
    }

   public QuerySnapshot getStatementsForUser(long limit) {
        return statementCollection.whereEqualTo("userId", currentUser.getUid()).limitToLast(limit).get().getResult();
    }

    public void getStatementsForUser() {
        Log.d("firebase_tag", "in stmt getter");
        Log.d("firebase_tag", currentUser.getUid());
        //QuerySnapshot querySnapshot = statementCollection.whereEqualTo("userId", currentUser.getUid()).get().getResult();
        //Log.d("firebase_tag", querySnapshot.toString());
        // statementCollection.whereEqualTo("userId", currentUser.getUid()).get();
    }

    public Task<DocumentReference> createNewStatement(String waterAmount, Uri imageUri) {
        Statement newStatement = new Statement(false, Timestamp.now(), mFirebaseAuth.getCurrentUser().getUid(), waterAmount);
        return statementCollection.add(newStatement).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageReference.child(mFirebaseAuth.getCurrentUser().getUid() + "/" + newStatement.getTimestamp().getSeconds() + ".png").putFile(imageUri);
            }
        });
    }

    public Task<Uri> getImageForStatement(Statement stmt) {
        return storageReference.child(stmt.getUserId() + "/" + stmt.getTimestamp().getSeconds() + ".png").getDownloadUrl();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void logOut() {
        mFirebaseAuth.signOut();
    }

    public Task<Void> sendPasswReset(String email) {
        return mFirebaseAuth.sendPasswordResetEmail(email);
    }
}
