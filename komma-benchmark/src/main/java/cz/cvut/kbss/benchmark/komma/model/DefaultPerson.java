package cz.cvut.kbss.benchmark.komma.model;

import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIs;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultPerson implements Person {

    private URI uri;

    private String key;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private Set<String> contacts;

    public DefaultPerson(Person person) {
        this.key = person.getKey();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.username = person.getUsername();
        this.password = person.getPassword();
        this.contacts = new HashSet<>(person.getContacts());
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public String getId() {
        return uri.toString();
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Set<String> getContacts() {
        return contacts;
    }

    @Override
    public void setContacts(Set<String> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(getKey(), person.getKey()) &&
                Objects.equals(firstName, person.getFirstName()) &&
                Objects.equals(lastName, person.getLastName()) &&
                Objects.equals(username, person.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), firstName, lastName, username);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " <" + uri + ">";
    }
}
