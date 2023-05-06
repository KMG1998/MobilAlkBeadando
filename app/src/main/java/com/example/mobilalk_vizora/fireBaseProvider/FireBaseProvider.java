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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public final class FireBaseProvider {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = mStorage.getReferenceFromUrl("gs://mobilalk-vizora.appspot.com");
    private FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    private final CollectionReference userDataCollection = mfirestore.collection("UserData");
    private final CollectionReference statementCollection = mfirestore.collection("Statements");

    private final String LOG_TAG = FireBaseProvider.class.getName();

    public Task<AuthResult> loginWithEmail(String email, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDataCollection.whereEqualTo("userId", task.getResult().getUser().getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot docSnap : queryDocumentSnapshots) {
                        UserData item = docSnap.toObject(UserData.class);
                        item.setLastLogin(Timestamp.now());
                        userDataCollection.document(docSnap.getId()).set(item);
                    }
                });
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

    public Task<QuerySnapshot> getUserData() {
        return userDataCollection.whereEqualTo("userId", currentUser.getUid()).get();
    }

    public Task<QuerySnapshot> getStatementsForUser(long limit) {
        return statementCollection.whereEqualTo("userId", currentUser.getUid()).limit(limit).orderBy("timestamp",Query.Direction.DESCENDING).get();
    }

    public Task<QuerySnapshot> getStatementsForUser() {
        return statementCollection.whereEqualTo("userId", currentUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING).get();
    }

    public Task<DocumentReference> createNewStatement(String waterAmount, byte[] imageBytes) {
        Statement newStatement = new Statement(false, Timestamp.now(), currentUser.getUid(), waterAmount);
        return statementCollection.add(newStatement).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageReference.child(currentUser.getUid() + "/" + newStatement.getTimestamp().getSeconds() + ".png").putBytes(imageBytes);
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

    public Task<QuerySnapshot> deleteUser() {
        return statementCollection.whereEqualTo("userId", currentUser.getUid()).get().addOnCompleteListener(getStatementsTask -> {
            if (getStatementsTask.isSuccessful()) {
                Log.d(LOG_TAG,"statement get success");
                final boolean[] deleteStatementSuccess = {true};
                if (getStatementsTask.getResult().size() > 0) {
                    getStatementsTask.getResult().getDocuments().forEach(snap -> {
                        statementCollection.document(snap.getId()).delete().addOnCompleteListener(deleteTask -> {
                            if (!deleteTask.isSuccessful()) {
                                deleteStatementSuccess[0] = false;
                            }
                        });
                    });
                }
                if (deleteStatementSuccess[0]) {
                    Log.d(LOG_TAG,"statement delete success");
                    clearDataOnDeletion();
                }
            }
        });
    }

    public Task<QuerySnapshot> approveStatementsForCurrentUser() {
        return statementCollection.whereEqualTo("userId", currentUser.getUid()).whereEqualTo("approved",false).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().forEach(stmtSnap -> {
                    Statement item = stmtSnap.toObject(Statement.class);
                    item.setApproved(true);
                    statementCollection.document(stmtSnap.getId()).set(item).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Log.d(LOG_TAG,"approve set success");
                        }else {
                            Log.d(LOG_TAG,"approve set failed");
                        }
                    });
                });
            }else {
                Log.d(LOG_TAG,"task not success result is"+task.getResult().toString());
            }
        }).addOnFailureListener(e -> {
            Log.d(LOG_TAG,"approve fail error: "+e.getMessage());
        });
    }

    private Task<ListResult> clearDataOnDeletion() {
        return storageReference.child(currentUser.getUid()).listAll().addOnCompleteListener(storageItemsGetTask -> {
            final boolean[] storageDeleteSuccess = {true};
            storageItemsGetTask.getResult().getItems().forEach(ref ->ref.delete().addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        storageDeleteSuccess[0] = false;
                    }
            }));
            if(storageDeleteSuccess[0] == true){
                Log.d(LOG_TAG,"storage delete success");
                userDataCollection.whereEqualTo("userId", currentUser.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userDataCollection.document(task.getResult().getDocuments().get(0).getId()).delete().addOnCompleteListener(task2 -> {
                            Log.d(LOG_TAG, "user data delete success");
                            if (task2.isSuccessful()) {
                                currentUser.delete();
                            }
                        });
                    }
                });
                }
            });
    }
}
