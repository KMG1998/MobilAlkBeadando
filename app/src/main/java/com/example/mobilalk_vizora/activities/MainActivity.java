package com.example.mobilalk_vizora.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.adapters.StatementListAdapter;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobilalk_vizora.databinding.ActivityMainBinding;
import com.example.mobilalk_vizora.jobs.ApproveStatementJobService;
import com.example.mobilalk_vizora.model.Statement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final FireBaseProvider fBaseProvider = new FireBaseProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_upload, R.id.navigation_home, R.id.navigation_history)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //job scheduler to simulate admin side statement approval
        JobScheduler jobScheduler = getSystemService(JobScheduler.class);
        ComponentName compName = new ComponentName(this, ApproveStatementJobService.class);
        JobInfo.Builder jobInfo = new JobInfo.Builder((int) (Math.random() * 100), compName);
        jobInfo.setMinimumLatency(1 * 20 * 1000).setTriggerContentMaxDelay(1 * 60 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        if (jobScheduler != null) {
            jobScheduler.schedule(jobInfo.build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenuButton:
                fBaseProvider.logOut();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;
            case R.id.profileMenuButton:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //empty on back pressed to prevent accidental closing of the app
    }
}