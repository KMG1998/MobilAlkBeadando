package com.example.mobilalk_vizora.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.formatters.DateFormatters;
import com.example.mobilalk_vizora.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    FireBaseProvider fBaseProvider = new FireBaseProvider();
    TextView birthDateEditText;
    TextView idEditText;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        birthDateEditText = findViewById(R.id.profilePageBirth);
        deleteButton = findViewById(R.id.profileDeleteButton);
        idEditText = findViewById(R.id.profilePageId);

        fBaseProvider.getUserData().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("profile_tag","in user getting success");
                UserData data = task.getResult().getDocuments().get(0).toObject(UserData.class);
                Log.d("profile_tag","user data is " + data == null ? "null" : "not null");
                birthDateEditText.setText(data.getBirthDate());
                idEditText.setText(data.getUserId());
            }
        });

        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete_profile);
            LinearLayout linearLayout = new LinearLayout(this);
            final TextView warningText = new TextView(this);
            warningText.setGravity(Gravity.CENTER);
            warningText.setPadding(50,0,0,0);
            // write the email using which you registered
            warningText.setHint(R.string.delete_prof_warning);
            linearLayout.addView(warningText);
            linearLayout.setPadding(10, 10, 10, 10);
            builder.setView(linearLayout);

            // Click on Recover and a email will be sent to your registered email id
            builder.setPositiveButton(R.string.delete_warning_positive, (dialog, which) ->
                    fBaseProvider.deleteUser().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_LONG);
                            Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(ProfileActivity.this, R.string.delete_failed, Toast.LENGTH_LONG);
                        }
                    }));
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }
}