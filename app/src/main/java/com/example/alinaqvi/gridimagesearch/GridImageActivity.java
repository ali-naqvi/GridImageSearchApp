package com.example.alinaqvi.gridimagesearch;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GridImageActivity extends ActionBarActivity {
    GridImageAdapter gridImageAdapter = null;
    String query = "fall";
    String imageSize;
    String colorFilter;
    String imageType;
    String site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_image);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setCustomView(R.layout.actionbar_search);
        getSupportActionBar().setElevation(5);
        readItems();
        ((EditText) getSupportActionBar().getCustomView().findViewById(R.id.etSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Button btClear = (Button) getSupportActionBar().getCustomView().findViewById(R.id.btClear);
                if (s.length() > 0 && btClear.getVisibility() != View.VISIBLE) {
                    getSupportActionBar().getCustomView().findViewById(R.id.btClear).setVisibility(View.VISIBLE);
                }
                if (s.toString().trim().length() > 2) {
                    query = s.toString().trim();
                    fetchMoviesAsync(1);
                }
            }
        });
        getSupportActionBar().getCustomView().findViewById(R.id.btClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) getSupportActionBar().getCustomView().findViewById(R.id.etSearch)).setText("");
                v.setVisibility(View.INVISIBLE);
            }
        });
        gridImageAdapter = new GridImageAdapter(this);
        StaggeredGridView gridView = (StaggeredGridView) findViewById(R.id.gvImages);
        gridView.setAdapter(gridImageAdapter);
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchMoviesAsync(page);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Image image = gridImageAdapter.getItem(position);
                Intent intent = new Intent(GridImageActivity.this, ImageViewActivity.class);
                intent.putExtra("IMAGE", image);
                startActivity(intent);
            }
        });
        fetchMoviesAsync(1);
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialog editNameDialog = SettingsDialog.newInstance("Advanced Filters", this);
        editNameDialog.show(fm, "fragment_settings_dialog");
    }

    protected void fetchMoviesAsync(final int page) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String url = "https://ajax.googleapis.com/ajax/services/search/images?q=" + query + "&v=1.0&rsz=8&safe=active&start=" + (8 * (page - 1)) + (!TextUtils.isEmpty(imageSize) ? "&imgsz=" + imageSize : "") + (!TextUtils.isEmpty(colorFilter) ? "&imgcolor=" + colorFilter : "") + (!TextUtils.isEmpty(imageType) ? "&imgtype==" + imageType : "") + (!TextUtils.isEmpty(site) ? "&as_sitesearch=" + site : "");
        Log.e(this.getClass().getName(), "Page is: " + page + "; url is: " + url);
        httpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(GridImageActivity.this, "Network Error", Toast.LENGTH_SHORT);
                Log.e(this.getClass().getName(), statusCode + " -- " + responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<Image> movies = new ArrayList<Image>();
                if (page == 1) {
                    gridImageAdapter.clear();
                }
                try {
                    JSONArray jsonArray = response.getJSONObject("responseData").getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = (JSONObject) jsonArray.get(i);
                        Image image = new Image();
                        image.tbUrl = data.getString("tbUrl");
                        image.url = data.getString("unescapedUrl");
                        image.title = data.getString("contentNoFormatting");
                        image.height = data.getInt("height");
                        image.width = data.getInt("width");
                        gridImageAdapter.add(image);
                    }
                } catch (JSONException e) {
                    Log.d(this.getClass().getName(), "End of stream");
                }
                gridImageAdapter.notifyDataSetChanged();
                super.onSuccess(statusCode, headers, response);
            }
        });
    }

    private void readItems() {
        File settingsFile = new File(getFilesDir(), "settings.txt");
        try {
            ArrayList<String> items = new ArrayList<String>(FileUtils.readLines(settingsFile));
            imageSize = getResources().getStringArray(R.array.image_size)[Integer.parseInt(items.get(0))];
            colorFilter = getResources().getStringArray(R.array.color_type)[Integer.parseInt(items.get(1))];
            imageType = getResources().getStringArray(R.array.image_type)[Integer.parseInt(items.get(2))];
            site = items.get(3);
            Log.e(SettingsDialog.class.getName(), "read from file: imageSize - " + imageSize + ", colorFilter - " + colorFilter + ", imageType - " + imageType);
        } catch (Exception e) {
            Log.i(this.getClass().getName(), "No Settings present. Will use default");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miSettings) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
