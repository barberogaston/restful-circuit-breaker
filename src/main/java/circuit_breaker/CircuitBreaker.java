package circuit_breaker;

/**
 * Circuit breaker abstract representation.
 * Resource can be, for example, a String,
 * in case it is a RESTful API resourece, a
 * file, etc.
 * @param <T> Resource type
 */
public abstract class CircuitBreaker<T>
{
    /**
     * Circuit breaker states.
     *
     * OPEN: The circuit will refuse
     * any resource requests until
     * the timeout has passed
     *
     * HALF OPEN: The circuit will
     * accept one resource request
     * and close if it is successful
     * or remain open if it is not
     *
     * CLOSED: The circuit is accepts
     * any resource request. If it fails
     * it should add 1 to the retryCount.
     */
    enum CircuitBreakerState
    {
        OPEN,
        HALF_OPEN,
        CLOSED
    }

    private int threshold;
    private CircuitBreakerState state;
    private long timeoutInMillis;
    private int retryCount;
    private T resource;

    CircuitBreaker(T resource, int threshold, long timeoutInMillis)
    {
        this.resource = resource;
        this.threshold = threshold;
        this.state = CircuitBreakerState.CLOSED;
        this.timeoutInMillis = timeoutInMillis;
        this.retryCount = 0;
    }

    abstract boolean isResourceAvailable();

    int getThreshold()
    {
        return threshold;
    }

    int getRetryCount()
    {
        return retryCount;
    }

    void incrementRetryByOne()
    {
        this.retryCount += 1;
    }

    void resetRetryCount()
    {
        this.retryCount = 0;
    }

    T getResource()
    {
        return resource;
    }

    CircuitBreakerState getState()
    {
        return state;
    }

    /**
     * Open the circuit ¯\_(ツ)_/¯
     */
    public void open()
    {
        setState(CircuitBreakerState.OPEN);
    }

    /**
     * Close the circuit ¯\_(ツ)_/¯
     */
    public void close()
    {
        setState(CircuitBreakerState.CLOSED);
    }

    /**
     * Half open the circuit ¯\_(ツ)_/¯
     */
    public void halfOpen()
    {
        setState(CircuitBreakerState.HALF_OPEN);
    }

    /**
     * Sets the circuit breaker's state. If the
     * circuit changes to OPEN, then starts a Thread
     * which will set the circuit's state to HALF
     * OPEN after timeoutInMillis.
     * @param state
     */
    private void setState(CircuitBreakerState state)
    {
        this.state = state;
        if (state.equals(CircuitBreakerState.OPEN))
        {
            new Thread()
            {
                @Override
                public void run()
                {
                    super.run();
                    try
                    {
                        wait(timeoutInMillis);
                        halfOpen();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
