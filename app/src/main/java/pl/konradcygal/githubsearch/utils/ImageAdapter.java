package pl.konradcygal.githubsearch.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pl.konradcygal.githubsearch.R;

public class ImageAdapter {
    @BindingAdapter({"bind:url"})
    public static void loadAvatar(ImageView view, String uri) {
        Picasso.with(view.getContext())
            .load(uri)
            .placeholder(R.drawable.profilbild)
            .into(view);
    }
}
