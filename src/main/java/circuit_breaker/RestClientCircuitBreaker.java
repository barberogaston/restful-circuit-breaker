package circuit_breaker;

import rest_client.RestClient;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Circuit breaker implementation for RESTful API calls. It works as a wrapper
 * to a RestClient.
 */
public class RestClientCircuitBreaker extends CircuitBreaker<String>
{
    private RestClient client;

    public RestClientCircuitBreaker(int threshold, long timeoutInMillis, RestClient client)
    {
        super(client.getResourceUrl(), threshold, timeoutInMillis);
        this.client = client;
    }

    /**
     * Checks if the resource to which the circuit makes calls is available
     * @return True if available, false otherwise
     */
    boolean isResourceAvailable()
    {
        try
        {
            URL resource = new URL(getResource());
            HttpURLConnection con = (HttpURLConnection) resource.openConnection();
            con.setRequestMethod("GET");
            return String.valueOf(con.getResponseCode()).matches("2[0-9]{2}");
        }catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Proxy method which makes all the circuit checks before sending any API
     * call. If the resource is available and the circuit is not open. Then,
     * it returns the RestClient from which the user can make the API calls.
     * @return RestClient
     * @throws Exception
     */
    public RestClient connect() throws Exception
    {
        // If circuit is OPEN then throw an Exception
        // and refuse connection
        if(getState().equals(CircuitBreakerState.OPEN))
        {
            throw new Exception("Circuit breaker state is OPEN.");
        }
        // If the retry count surpasses the
        // threshold, set circuit state to
        // OPEN and throw an Exception
        if(getRetryCount() >= getThreshold())
        {
            open();
            throw new Exception("Threshold limit has been reached. Circuit breaker state is now OPEN.");
        }
        // If resource is not available, increase
        // retry counter by 1 and throw an Exception
        if (!isResourceAvailable())
        {
            incrementRetryByOne();
            throw new Exception("Resource is not available");
        }
        // If the resource is available and the circuit's
        // state is HALF OPEN, then close it, as this means
        // the resource has become available and reset the
        // retry count
        if (getState().equals(CircuitBreakerState.HALF_OPEN))
        {
            close();
            resetRetryCount();
        }

        return this.client;
    }
}
