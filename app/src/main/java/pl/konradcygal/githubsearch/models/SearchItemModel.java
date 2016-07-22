package pl.konradcygal.githubsearch.models;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchItemModel {
    private String title;
    private String description;
    private String avatar;

    public SearchItemModel(JSONObject item) {
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
