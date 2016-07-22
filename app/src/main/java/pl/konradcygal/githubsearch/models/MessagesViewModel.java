package pl.konradcygal.githubsearch.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import pl.konradcygal.githubsearch.BR;
import pl.konradcygal.githubsearch.GithubSearchApp;

public class MessagesViewModel extends BaseObservable {
    private String message;
    private boolean hide;

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(Integer id) {
        if (id == null) {
            message = null;
            notifyPropertyChanged(BR.message);
            return;
        }
        hideProgressWheel(true);
        this.message = GithubSearchApp.getContext().getString(id);
        notifyPropertyChanged(BR.message);
    }

    @Bindable
    public boolean isHide() {
        return hide;
    }

    public void hideProgressWheel(boolean status) {
        hide = status;
        notifyPropertyChanged(BR.hide);
    }
}
