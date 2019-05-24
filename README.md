# Java Restful API Circuit Breaker

Simple circuit breaker written in Java that works as a wrapper of Restful API's HTTP calls.

## Table of contents

- Cloning
- Example
- Usage
  - RestClientCircuitBreaker parameters
  - RestClient implementation
    - Resource URL
    - Helper methods

# Cloning

```bash
git clone https://www.github.com/barberogaston/java-circuit-breaker.git
```

# Example

```java
RestClient client = new SitesRestClient("https://api.mercadolibre.com/sites");
RestClientCircuitBreaker cb = new RestClientCircuitBreaker(3, 1000, client);

cb.connect().get("/MLA");
```

# Usage

1. Create a child class of `RestClient.java`.

```java
public class SitesRestClient extends RestClient<T>{...}
```

2. Replace the generic `T` with the data type your `RestClient` will be handling. It can be a `POJO` or `String` in case you want to work with `JSON` objects as strings.

```java
public class SitesRestClient extends RestClient<Site>{...}
```

3. Wrap your custom client with the CircuitBreaker by passing the client to the breaker's constructor:

```java
RestClient client = new SitesRestClient(...);
CircuitBreaker cb = new RestClientCircuitBreaker(..., client);
```

4. Call the `connect()` method of the CircuitBreaker to test the resource and get a `RestClient` instance.

```java
cb.connect()
```
   
5. Chain the RestClient method which you wish to call.

```java
cb.connect().get("/");
```

## RestClientCircuitBreaker parameters

* **threshold:** how many times will the CircuitBreaker attempt to connect to the resource before its state becomes **OPEN**

* **timeoutInMillis:** time (in milliseconds) that the CircuitBreaker will wait before changing to **HALF OPEN** state

* **client:** a `RestClient` implementation instance

```java
public RestClientCircuitBreaker(int threshold, long timeoutInMillis, RestClient client){...}
```

## RestClient implementation

Once you extend the abstract `RestClient` class you'll have to implement its abstract methods which include the most common API requests:
- `get()`
- `post()`
- `put()`
- `delete()`

### Resource URL
This is the base URL from which you'll make your API calls from your `RestClient`. It is passed to its constructor.

For example:

Say I want to send a `GET` request to: `api.example.com/users/1`. The **base** URL would be `api.example.com/users`. Then, when calling the `get(String path)` method, the **path** would be `/users`.

### Helper methods
The `RestClient.java` abstract class has some helper methods for your  implementation

```java
buildUrl(String path){...};
buildUrl(String path, HashMap<String, Object> params){...};
makeConnection(String connectionUrl){...};
``` 
* **buildUrl:** Use this method to build the full URL to which you'll make the call more easily
* **makeConnection:** Use this method to establish the connection without all the boilerplate code