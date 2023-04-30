package com.example.mobilalk_vizora.fragments.upload;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobilalk_vizora.R;
import com.example.mobilalk_vizora.databinding.FragmentUploadBinding;
import com.example.mobilalk_vizora.fireBaseProvider.FireBaseProvider;
import com.example.mobilalk_vizora.model.Statement;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class UploadFragment extends Fragment {

    private FragmentUploadBinding binding;
    private List<Statement> userStatements;
    private byte[] imageFileBytes;
    private FireBaseProvider mFbaseProvider = new FireBaseProvider();

    private Bitmap img;

    private int tag = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UploadViewModel uploadViewModel =
                new ViewModelProvider(this).get(UploadViewModel.class);

        binding = FragmentUploadBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        View root = binding.getRoot();

        binding.takePic.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                } else {
                    takePhoto();
                }
            }
        });

        binding.sendButton.setOnClickListener(v -> {
           if(validateInputs()){
                binding.sendButton.setEnabled(false);
                mFbaseProvider.createNewStatement(binding.newUploadAmount.getText().toString(), imageFileBytes).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(UploadFragment.this.getContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                        binding.sendButton.setEnabled(true);
                        img.recycle();
                    }else{
                        Toast.makeText(UploadFragment.this.getContext(), R.string.upload_failed, Toast.LENGTH_SHORT).show();
                        binding.sendButton.setEnabled(true);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(UploadFragment.this.getContext(), R.string.upload_failed, Toast.LENGTH_SHORT).show();
                });
            }else {
                Toast.makeText(UploadFragment.this.getContext() ,R.string.error_invalid_input, Toast.LENGTH_SHORT).show();
            }
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

    private void takePhoto(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == tag && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            img = (Bitmap) b.get("data");
            binding.uploadPic.setImageBitmap(img);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageFileBytes = stream.toByteArray();
        }
    }

    private boolean validateInputs(){
        return binding.newUploadAmount.getText().length() > 0 && binding.newUploadAmount.getError() == null
                && binding.uploadPic.getDrawable() != null;
    }
}