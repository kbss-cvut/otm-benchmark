package cz.cvut.kbss.benchmark.komma.model;

import net.enilink.komma.core.IReference;
import net.enilink.komma.core.URI;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultEvent implements Event {

    private URI uri;

    private String key;

    private XMLGregorianCalendar startTime;

    private XMLGregorianCalendar endTime;

    private Set<Event> subEvents;

    private IReference eventType;

    public DefaultEvent(Event other) {
        this.key = other.getKey();
        this.startTime = other.getStart();
        this.endTime = other.getEnd();
        this.eventType = other.getEventType();
        if (other.getSubEvents() != null) {
            this.subEvents = other.getSubEvents().stream().map(DefaultEvent::new).collect(Collectors.toSet());
        }
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
    public XMLGregorianCalendar getStart() {
        return startTime;
    }

    @Override
    public void setStart(XMLGregorianCalendar startTime) {
        this.startTime = startTime;
    }

    @Override
    public XMLGregorianCalendar getEnd() {
        return endTime;
    }

    @Override
    public void setEnd(XMLGregorianCalendar endTime) {
        this.endTime = endTime;
    }

    @Override
    public Set<Event> getSubEvents() {
        return subEvents;
    }

    public void setSubEvents(Set<Event> subEvents) {
        this.subEvents = subEvents;
    }

    @Override
    public IReference getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(IReference eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "uri=" + uri +
                ", subEvents=" + subEvents +
                ", eventType=" + eventType +
                '}';
    }
}
