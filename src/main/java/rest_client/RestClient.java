package rest_client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Abstract class which contains methods for most common RESTful API HTTP methods.
 * Implementations should hold a resource's URL and pass the full endpoint path
 * to the methods.
 * <p>
 * For example: Say we wanted to send a GET request to api.example.com/items/1,
 * then the resource URL would be: api.example.com/items and the path: /1
 *
 * @param <T> Type of data you want to send and receive in the implementation.
 *           For example, you can decide to recieve a JSON as String and send
 *           bodies as a JSON String, or parse the received JSON Strings and
 *           return a POJO (and also send it).
 */
public abstract class RestClient<T>
{
    private String resourceUrl;

    RestClient(String resourceUrl)
    {
        this.resourceUrl = resourceUrl;
    }

    public abstract T get(String path) throws Exception;

    public abstract T get(String path, HashMap<String, Object> params) throws Exception;

    public abstract T post(String path, T element) throws Exception;

    public abstract void put(String path, String id, T element) throws Exception;

    public abstract void delete(String path, String id) throws Exception;

    /**
     * Method which appends path to the resource's URL
     *
     * @param path
     * @return
     */
    protected String buildUrl(String path)
    {
        String url = getResourceUrl() + path;
        return url;
    }

    /**
     * Appends the path and query params to the resource's URL
     *
     * @param path
     * @param params
     * @return
     */
    protected String buildUrl(String path, HashMap<String, Object> params)
    {
        String url = buildUrl(path);
        String[] keys = (String[]) params.keySet().toArray();
        url += "?";
        for (int i = 0; i < params.size(); i++)
        {
            url += keys[i] + "=" + params.get(keys[i]) + "&";
        }
        return url.substring(0, url.length() - 1);
    }

    HttpURLConnection makeConnection(String connectionURL) throws IOException
    {
        URL url = new URL(buildUrl(connectionURL));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        return con;
    }

    public String getResourceUrl()
    {
        return this.resourceUrl;
    }
}
