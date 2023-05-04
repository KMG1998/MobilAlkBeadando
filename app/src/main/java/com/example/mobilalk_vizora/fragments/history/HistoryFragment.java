package com.example.mobilalk_vizora.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.adapters.StatementListAdapter;
import com.example.mobilalk_vizora.databinding.FragmentHistoryBinding;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.model.Statement;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private FireBaseProvider fBaseProvider = new FireBaseProvider();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel notificationsViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        View root = binding.getRoot();
        binding.historyList.setLayoutManager(new LinearLayoutManager(getContext()));
        fBaseProvider.getStatementsForUser().addOnCompleteListener(task -> {
            ArrayList<Statement> statements = new ArrayList<>();
            if (task.isSuccessful()) {
                task.getResult().forEach(snap -> {
                    Statement stm = snap.toObject(Statement.class);
                    statements.add(stm);
                });
            } else {
                Toast.makeText(getContext(), R.string.listing_failure, Toast.LENGTH_SHORT).show();
            }
            StatementListAdapter stmtAdapter = new StatementListAdapter(getContext(), statements);
            binding.historyList.setAdapter(stmtAdapter);
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
}