package com.example.mobilalk_vizora.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.activities.LoginActivity;
import com.example.mobilalk_vizora.databinding.FragmentHomeBinding;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;

public class HomeFragment extends Fragment {

    FireBaseProvider mFbaseProvider;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mFbaseProvider = new FireBaseProvider();
        setHasOptionsMenu(true);
        View root = binding.getRoot();
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
                mFbaseProvider.logOut();
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(loginIntent);
                return true;
            default: return false;
        }
    }
}