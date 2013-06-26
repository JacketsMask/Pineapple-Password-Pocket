package password.pocket;

import java.io.Serializable;

/**
 * An Entry in the PasswordPocket consists of a location (such as a site, or
 * game), a login name, and a password.
 *
 * @author Jacob
 */
public class Entry implements Serializable {

    private String location;
    private String login;
    private String password;

    /**
     * Creates a new Entry with the passed location, login, and password
     *
     * @param location
     * @param login
     * @param password
     */
    public Entry(String location, String login, String password) {
        this.location = location;
        this.login = login;
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return location + ":" + login;
    }
}
