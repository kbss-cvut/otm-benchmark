package cz.cvut.kbss.benchmark.komma.model;

public class DefaultOccurrence extends DefaultEvent implements Occurrence {

    private String name;

    public DefaultOccurrence(Occurrence occurrence) {
        super(occurrence);
        this.name = occurrence.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Occurrence{" + name + " <" + getUri() + ">}";
    }
}
