package cz.cvut.kbss.benchmark.komma.model;

import net.enilink.komma.core.URI;

import java.util.Objects;

public class DefaultResource implements Resource {

    private URI uri;

    private String key;

    private String identifier;

    private String description;

    public DefaultResource(Resource resource) {
        this.key = resource.getKey();
        this.identifier = resource.getIdentifier();
        this.description = resource.getDescription();
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
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getKey(), resource.getKey()) &&
                Objects.equals(identifier, resource.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), identifier);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "uri=" + uri +
                ", identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
