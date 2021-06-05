package nl.ou.se.rest.fuzzer.components.fuzzer.executor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthentication implements Authentication {

    // variable(s)
    private String username;
    private String password;

    // constructor(s)
    public BasicAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // method(s)
    public String getHeader() {
        String auth = String.format("%s:%s", this.username, this.password);
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        return "Basic " + new String(encodedAuth);
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}