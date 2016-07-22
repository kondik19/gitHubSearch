package pl.konradcygal.githubsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import pl.konradcygal.githubsearch.databinding.ItemBinding;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<SearchItem> items;
    private LayoutInflater inflater;

    public ListRecyclerViewAdapter(Context context,
                                   ArrayList<SearchItem> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindTo(items.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ViewHolder.create(inflater, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemBinding binding;

        ViewHolder(ItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        static ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
            ItemBinding binding =
                    ItemBinding.inflate(inflater, parent, false);
            return new ViewHolder(binding);
        }

        public void bindTo(SearchItem entry) {
            binding.setResult(entry);
        }
    }
}

