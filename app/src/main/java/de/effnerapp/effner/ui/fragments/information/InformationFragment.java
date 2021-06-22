/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:20.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.ui.fragments.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.api.ApiClient;
import de.effnerapp.effner.data.api.json.data.Content;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment {

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.information_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<Content> items = new ArrayList<>();

        if(ApiClient.getInstance() == null) {
            requireActivity().recreate();
            return view;
        }

        for (Content content : ApiClient.getInstance().getData().getContent()) {
            if (content.getName().startsWith("DATA_INFORMATION")) {
                items.add(content);
            }
        }

        InformationItemAdapter adapter = new InformationItemAdapter(items);

        recyclerView.setAdapter(adapter);
        return view;
    }
}