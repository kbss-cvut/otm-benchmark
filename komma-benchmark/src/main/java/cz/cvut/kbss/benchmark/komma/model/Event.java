package cz.cvut.kbss.benchmark.komma.model;

import cz.cvut.kbss.benchmark.model.Vocabulary;
import net.enilink.composition.annotations.Iri;
import net.enilink.komma.core.IReference;
import net.enilink.komma.core.URI;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Set;

@Iri(Vocabulary.s_c_Event)
public interface Event {

    @Iri(Vocabulary.s_p_has_key)
    String getKey();

    void setKey(String key);

    @Iri(Vocabulary.s_p_has_start_time)
    XMLGregorianCalendar getStart();

    void setStart(XMLGregorianCalendar start);

    @Iri(Vocabulary.s_p_has_end_time)
    XMLGregorianCalendar getEnd();

    void setEnd(XMLGregorianCalendar end);

    @Iri(Vocabulary.s_p_has_event_type)
    IReference getEventType();

    void setEventType(IReference eventType);

    @Iri(Vocabulary.s_p_has_part)
    Set<Event> getSubEvents();

    void setSubEvents(Set<Event> subEvents);
}
