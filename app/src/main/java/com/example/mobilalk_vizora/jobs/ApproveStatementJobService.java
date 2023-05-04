package com.example.mobilalk_vizora.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;


public class ApproveStatementJobService extends JobService {

    String LOG_TAG = ApproveStatementJobService.class.getName();

    FireBaseProvider fBaseProvider = new FireBaseProvider();

    public ApproveStatementJobService(){}

    @Override
    public boolean onStartJob(JobParameters params) {
        final boolean[] approveSuccess = new boolean[1];
        fBaseProvider.approveStatementsForCurrentUser().addOnCompleteListener(approveTask -> {
            if(approveTask.isSuccessful()){
                approveSuccess[0] = false;
                Toast.makeText(getApplicationContext(), R.string.statement_approve_success,Toast.LENGTH_SHORT).show();
            }else{
                approveSuccess[0] = true;
                Toast.makeText(getApplicationContext(), R.string.statement_approve_failed,Toast.LENGTH_SHORT).show();
            }
        });
        return approveSuccess[0];
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
