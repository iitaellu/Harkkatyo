//java -> eka file

package com.example.harjoitustyo_ida_viia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatisticsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics_fragment, container, false);

        //TODO kaavioita tallennettujen tietoje n perusteella
        // kuinka kirjoitetaan XML https://www.youtube.com/watch?v=IJEIQ1f5KB8
    }
}
