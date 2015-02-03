package com.example.alinaqvi.gridimagesearch;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by alinaqvi on 2/2/15.
 */
public class SettingsDialog extends DialogFragment {
    private int imageSize;
    private int colorFilter;
    private int imageType;
    private String site;
    private EditText etSiteFilter;
    private GridImageActivity gridImageActivity;

    public static SettingsDialog newInstance(String title, GridImageActivity gridImageActivity) {
        SettingsDialog frag = new SettingsDialog();
        frag.gridImageActivity = gridImageActivity;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        readItems();
        View view = inflater.inflate(R.layout.dialog_settings, container);

        etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);

        //Image Size
        Spinner spinner = (Spinner) view.findViewById(R.id.sImageSize);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageSize = position;
                Log.e(SettingsDialog.class.getName(), "imageSize: " + imageSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(imageSize);
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(imageSize);

        //Color Filter
        spinner = (Spinner) view.findViewById(R.id.sColorFilter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorFilter = position;
                Log.e(SettingsDialog.class.getName(), "colorFilter: " + colorFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(colorFilter);
            }
        });
        adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.color_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(colorFilter);

        //Image Type
        spinner = (Spinner) view.findViewById(R.id.sImageType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageType = position;
                Log.e(SettingsDialog.class.getName(), "imageType: " + imageType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(imageType);
            }
        });
        adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(imageType);

        ((Button) view.findViewById(R.id.btCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button) view.findViewById(R.id.btSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                site = etSiteFilter.getText().toString().trim();
                writeItems();
                gridImageActivity.imageSize = getResources().getStringArray(R.array.image_size)[imageSize];
                gridImageActivity.imageType = getResources().getStringArray(R.array.image_type)[imageType];
                gridImageActivity.colorFilter = getResources().getStringArray(R.array.color_type)[colorFilter];
                gridImageActivity.site = site;
                gridImageActivity.fetchMoviesAsync(1);
                dismiss();
            }
        });

        etSiteFilter.setText(site);
        getDialog().setTitle("Advanced Filters");
        return view;
    }

    private void readItems() {
        File settingsFile = new File(gridImageActivity.getFilesDir(), "settings.txt");
        try {
            ArrayList<String> items = new ArrayList<String>(FileUtils.readLines(settingsFile));
            imageSize = Integer.parseInt(items.get(0));
            colorFilter = Integer.parseInt(items.get(1));
            imageType = Integer.parseInt(items.get(2));
            site = items.get(3);
            Log.e(SettingsDialog.class.getName(), "read from file: imageSize - " + imageSize + ", colorFilter - " + colorFilter + ", imageType - " + imageType);
        } catch (Exception e) {
            Log.i(this.getClass().getName(), "No Settings present. Using default");
        }
    }

    private void writeItems() {
        File settingsFile = new File(gridImageActivity.getFilesDir(), "settings.txt");
        try {
            ArrayList<String> data = new ArrayList<>();
            data.add("" + imageSize);
            data.add("" + colorFilter);
            data.add("" + imageType);
            data.add(site);
            FileUtils.writeLines(settingsFile, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
