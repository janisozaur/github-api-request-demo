package com.example.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        binding.buttonFirst.setOnClickListener(v ->
                {
                    HttpAndroid.Request request = new HttpAndroid.Request();
                    request.url = "https://api.github.com/repos/OpenRCT2/OpenRCT2/releases/latest";
                    HttpAndroid.Response response = HttpAndroid.request(request);
                    if (response.status == HttpAndroid.Status.Ok) {
                        binding.textviewFirst.setText(response.body);
                    } else {
                        binding.textviewFirst.setText("Request failed: " + response.error);
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}