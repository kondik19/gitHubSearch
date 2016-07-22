package pl.konradcygal.githubsearch;

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
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pl.konradcygal.githubsearch.api.RestClient;
import pl.konradcygal.githubsearch.databinding.ActivityMainBinding;
import pl.konradcygal.githubsearch.models.MessagesViewModel;
import pl.konradcygal.githubsearch.models.SearchItemModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements Callback<String> {
    ActivityMainBinding binding;
    private RestClient.ApiInterface service;
    private ArrayList<SearchItemModel> items;
    private ListRecyclerViewAdapter adapter;
    private MessagesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MessagesViewModel();
        binding.setMessage(viewModel);
        viewModel.setMessage(R.string.welcome);
        initService();
        items = new ArrayList<>();
        adapter = new ListRecyclerViewAdapter(this, items);
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
                    viewModel.setMessage(R.string.no_internet);
                    return true;
                }
                viewModel.hideProgressWheel(false);
                items.clear();
                adapter.notifyDataSetChanged();
                viewModel.setMessage(null);
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
            try {
                JSONObject data = new JSONObject(response.body());
                JSONArray resultsArray = data.getJSONArray("items");
                if (resultsArray.length() == 0) {
                    viewModel.setMessage(R.string.no_results);
                    return;
                }
                for (int i = 0; i < resultsArray.length(); i++) {
                    items.add(new SearchItemModel(resultsArray.getJSONObject(i)));
                }
                adapter.notifyDataSetChanged();
                hideKeyboard();
                viewModel.hideProgressWheel(true);
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
