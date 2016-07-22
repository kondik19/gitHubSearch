package pl.konradcygal.githubsearch;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pl.konradcygal.githubsearch.api.RestClient;
import pl.konradcygal.githubsearch.databinding.ActivityMainBinding;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements Callback<String> {
    ActivityMainBinding binding;
    private RestClient.ApiInterface service;
    private ArrayList<SearchItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initService();
        items = new ArrayList<>();
        ListRecyclerViewAdapter adapter = new ListRecyclerViewAdapter(this, items);
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                if (!isNetworkAvailable()) {
                    binding.rvInfo.setVisibility(View.VISIBLE);
                    binding.info.setText(getString(R.string.no_internet));
                    return false;
                }
                binding.rvInfo.setVisibility(View.GONE);
                search(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    private void initService() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "en");
        service = RestClient.getClient(headers);
    }

    public void search(String repo) {
        Call<String> call = service.searchRepo(repo);
        if (call == null) {
            return;
        }
        call.enqueue(this);
    }


    @Override
    public void onResponse(Response<String> response) {
        if (response.isSuccess()) {
            String result = response.body();

            JSONObject data;
            try {
                JSONArray dataArray = new JSONArray(result);
                String requestUrl = response.raw().request().urlString();
                RestClient.destroyApiInterface();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            RestClient.destroyApiInterface();
            Timber.i(response.message());
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Timber.e(t.getMessage());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
