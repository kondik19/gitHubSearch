package pl.konradcygal.githubsearch.api;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public class RestClient {

    private static ApiInterface apiInterface;

    public static void destroyApiInterface() {
        apiInterface = null;
    }

    public static ApiInterface getClient(final HashMap<String, String> headers) {
        if (apiInterface == null) {

            OkHttpClient okClient = new OkHttpClient();
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, String> header = it.next();
                        requestBuilder.header(header.getKey(), header.getValue());
                        it.remove();
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            Retrofit client = new Retrofit.Builder()
                .baseUrl(ApiConfigurationData.API_URL)
                .addConverter(String.class, new ToStringConverter())
                .client(okClient)
                .build();
            apiInterface = client.create(ApiInterface.class);
        }
        return apiInterface;
    }

    public interface ApiInterface {
        @GET("/search/repositories")
        Call<String> searchRepo(
                @Query("q") String query
        );
    }
}
