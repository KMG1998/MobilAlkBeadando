package com.example.mobilalk_vizora.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.activities.LoginActivity;
import com.example.mobilalk_vizora.adapters.StatementListAdapter;
import com.example.mobilalk_vizora.databinding.FragmentHomeBinding;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.model.Statement;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FireBaseProvider fBaseProvider;
    private FragmentHomeBinding binding;
    private StatementListAdapter stmtAdapter;
    private ArrayList<Statement> statements;

    final private String LOG_TAG = HomeFragment.class.getName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        fBaseProvider = new FireBaseProvider();
        setHasOptionsMenu(true);
        View root = binding.getRoot();
        binding.statementsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        statements = new ArrayList();
        loadData().addOnCompleteListener(loadTask -> {
            stmtAdapter = new StatementListAdapter(getContext(), statements);
            binding.statementsListView.setAdapter(stmtAdapter);
        });

        binding.homeRefreshLayout.setOnRefreshListener(() -> {
            statements.clear();
            Log.d(LOG_TAG, "before update, size is " + statements.size());
            loadData().addOnCompleteListener(loadTask -> {
                if(loadTask.isSuccessful()) {
                    Log.d(LOG_TAG, "in update, size is " + statements.size());
                    stmtAdapter.update(statements);
                }else{
                    Log.d(LOG_TAG,"update data getting failed");
                }
            });
            binding.homeRefreshLayout.setRefreshing(false);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutMenuButton:
                fBaseProvider.logOut();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(loginIntent);
                return true;
            default:
                return false;
        }
    }

    private Task<QuerySnapshot> loadData() {
        return fBaseProvider.getStatementsForUser(2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().forEach(snap -> {
                    Statement stm = snap.toObject(Statement.class);
                    statements.add(stm);
                });
            } else {
                Toast.makeText(getContext(), R.string.listing_failure, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, task.getException().getMessage());
            }
        });
    }
}