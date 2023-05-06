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
        Log.d(LOG_TAG,"job started");
        fBaseProvider.approveStatementsForCurrentUser().addOnCompleteListener(approveTask -> {
            if(approveTask.isSuccessful()){
                if(approveTask.getResult().size() > 0){
                    Log.d(LOG_TAG,"approved more than 0");
                }else {
                    Log.d(LOG_TAG,"approved 0");
                }
            }else{
                Log.d(LOG_TAG,"approve failed");
            }
        });
        jobFinished(params,true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(LOG_TAG,"job stopped");
        return false;
    }
}
