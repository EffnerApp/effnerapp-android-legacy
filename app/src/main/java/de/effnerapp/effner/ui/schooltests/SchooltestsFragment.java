package de.effnerapp.effner.ui.schooltests;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.model.Content;
import de.effnerapp.effner.data.model.Schooltest;

public class SchooltestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SchooltestItemAdapter adapter;

    public SchooltestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schooltests, container, false);
        MainActivity.pageTextView.setText(R.string.title_schooltests);
        recyclerView = view.findViewById(R.id.recyclerview);
        Spinner spinner = view.findViewById(R.id.spinner);

        String sClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");


        if (!sClass.startsWith("11") && !sClass.startsWith("12")) {
            String[] items = {"Neuste zuerst", "Älteste zuerst"};
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, items);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            List<Schooltest> schooltests = new ArrayList<>(Arrays.asList(SplashActivity.getDataStack().getSchooltests()));
            adapter = new SchooltestItemAdapter(schooltests);
            recyclerView.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("SchooltestSpinner", "Item: " + parent.getItemAtPosition(position));
                    if (position == 1) {
                        List<Schooltest> list = new ArrayList<>(schooltests);
                        Collections.reverse(list);
                        adapter = new SchooltestItemAdapter(list);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter = new SchooltestItemAdapter(schooltests);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.w("SchooltestSpinner", "Nothing selected!");
                }
            });

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            GridLayout gridLayout = view.findViewById(R.id.grid_layout);
            gridLayout.setVisibility(View.VISIBLE);

            CardView h1_card = view.findViewById(R.id.h1_card);
            CardView h2_card = view.findViewById(R.id.h2_card);
            String key = "DATA_TOP_LEVEL_SA_DOC_" + (sClass.startsWith("11") ? 11 : 12) + "_";

            h1_card.setOnClickListener(v -> {
                Content content = SplashActivity.getDataStack().getContentByKey(key + 1);
                if (content != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content.getValue())));
                } else {
                    Toast.makeText(getContext(), "Dieses Dokument ist nicht verfügbar!", Toast.LENGTH_LONG).show();
                }
            });
            h2_card.setOnClickListener(v -> {
                Content content = SplashActivity.getDataStack().getContentByKey(key + 2);
                if (content != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content.getValue())));
                } else {
                    Toast.makeText(getContext(), "Dieses Dokument ist nicht verfügbar!", Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }

}
