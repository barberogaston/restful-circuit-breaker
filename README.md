# Java Restful API Circuit Breaker

Simple circuit breaker written in Java that works as a wrapper of Restful API's HTTP calls.

## Table of contents

- Cloning
- Usage
  - RestClient implementation
    - Helper methods

# Cloning

```bash
git clone https://www.github.com/barberogaston/java-circuit-breaker.git
```

# Usage

1. Create a child class of `RestClient.java`.

2. Pass the data type which your child will be using to communicate with the Restful API. It can be either a `String` (if you're planning to receive and send `JSON` objects as `Strings`) or a custom `POJO` made by yourself. For example: say you have made a `User.java` class for sending and receiving to and from the API. Then you should create the child class like this:

```java
public class UsersRestClient extends RestClient<User>{...}
```

3. Wrap your custom client with the CircuitBreaker by passing the client to the breaker's constructor:

```java
RestClientCircuitBreaker cb = new RestClientCircuitBreaker(..., new UserRestClient(...));
```

4. Call the `connect()` method of the CircuitBreaker to test the resource and get a `RestClient` instance.
   
5. Chain the API Client method which you wish to call.

```java
cb.connect().get("/");
```

## RestClientCircuitBreaker parameters

* **threshold:** how many times will the CircuitBreaker attempt to connect to the resource before it becomes **OPEN**

* **timeoutInMillis:** time (in milliseconds) that the CircuitBreaker will wait before changin to **HALF OPEN** state

* **client:** a `RestClient` implementation instance

```java
public RestClientCircuitBreaker(int threshold, long timeoutInMillis, RestClient client){...}
```

## RestClient implementation

Once you extend the abstract `RestClient` class you'll have to implement the abstract method it provides: **get**, **post**, **put** and **delete**.

Please note that in the Constructor there's a **resourceUrl** parameter (`RestClient(String resourceUrl)`). This is the base URL from which you'll make your API calls. For example, say I want to send a `GET` request to: `api.example.com/users/1`. The **base** URL would be `api.example.com/users`. Then, when calling the `get(String path)` method, the **path** would be `/users`.

### Helper methods
The `RestClient.java` abstract class has some helper methods for your child implementation

* `buildUrl(String path)` / `buildUrl(String path, HashMap<String, Object> params)` => Use this method to build the full URL to which you'll make the call more easily

* `makeConnection(String connectionUrl)`=> Use this method to establish the connection without all the boilerplate code