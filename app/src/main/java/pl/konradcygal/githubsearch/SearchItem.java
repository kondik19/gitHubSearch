package pl.konradcygal.githubsearch;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchItem {
    private String title;
    private String description;
    private String avatar;

    public SearchItem(JSONObject item) {
        try {
            this.title = item.getString("full_name");
            this.description = item.getString("description");
            this.avatar = item.getJSONObject("owner").getString("avatar_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }
}
