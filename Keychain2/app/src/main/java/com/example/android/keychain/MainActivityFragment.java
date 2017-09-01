package com.example.android.keychain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mAccountAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] dummy = {
                "gmail",
                "yahoo",
                "facebook",
                "twitter",
                "laptop",
                "icici",
                "gmail",
                "yahoo",
                "facebook",
                "twitter",
                "laptop",
                "icici"
        };

        List<String> account_name = new ArrayList<String>(Arrays.asList(dummy));

        mAccountAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_fragment, // The name of the layout ID.
                R.id.list_item_fragment_textview, // The ID of the textview to populate.
                dummy);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item);
        listView.setAdapter(mAccountAdapter);
        return rootView;
    }
}
