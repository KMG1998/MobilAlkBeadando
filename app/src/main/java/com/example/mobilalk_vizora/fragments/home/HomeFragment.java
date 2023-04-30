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
import android.widget.FrameLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    FireBaseProvider fBaseProvider;
    private FragmentHomeBinding binding;
    private StatementListAdapter stmtAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        fBaseProvider = new FireBaseProvider();
        setHasOptionsMenu(true);
        View root = binding.getRoot();
        binding.statementsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("home_tag", "before void get");
        ArrayList<Statement> statements = new ArrayList();
        fBaseProvider.getStatementsForUser(5).forEach(snap -> {
            Statement stm = snap.toObject(Statement.class);
            statements.add(stm);
        });/*addOnCompleteListener(task -> {
            ArrayList<Statement> statements = new ArrayList();
            if (task.isSuccessful()) {
                task.getResult().forEach(snap -> {
                    Statement stm = snap.toObject(Statement.class);
                    statements.add(stm);
                });
            } else {
                Toast.makeText(getContext(), R.string.listing_failure, Toast.LENGTH_SHORT).show();
            }*/
            stmtAdapter = new StatementListAdapter(getContext(), statements);
            binding.statementsListView.setAdapter(stmtAdapter);
        //});
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

}