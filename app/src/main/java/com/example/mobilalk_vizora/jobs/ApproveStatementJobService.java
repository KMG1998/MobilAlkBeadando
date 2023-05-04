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
        fBaseProvider.approveStatementsForCurrentUser().addOnCompleteListener(approveTask -> {
            if(approveTask.isSuccessful()){
                if(approveTask.getResult().size() > 0){
                    Toast.makeText(getApplicationContext(), R.string.statement_approve_success,Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.statement_approve_failed,Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
