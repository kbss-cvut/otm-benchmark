package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.model.*;
import cz.cvut.kbss.benchmark.komma.util.BenchmarkUtil;
import cz.cvut.kbss.benchmark.model.Vocabulary;
import cz.cvut.kbss.benchmark.util.Constants;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.URI;
import net.enilink.komma.core.URIs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.cvut.kbss.benchmark.util.Constants.EVENT_TYPES;
import static cz.cvut.kbss.benchmark.util.Constants.ITEM_COUNT;

public class KommaGenerator {

    private final int itemCount;

    private IEntityManager em;

    private Map<Object, URI> instances;

    public KommaGenerator(float factor) {
        this.itemCount = Math.round(ITEM_COUNT * factor);
    }

    public void setEm(IEntityManager em) {
        this.em = em;
    }

    private final Random random = new Random();

    private List<OccurrenceReport> reports;
    private List<DefaultOccurrenceReport> detachedReports;
    private List<Person> persons;

    public void persistData() {
        this.instances = new IdentityHashMap<>();
        em.getTransaction().begin();
        this.persons = generatePersons();
        this.reports = generateReports();
        em.getTransaction().commit();
    }

    public void persistPersons() {
        em.getTransaction().begin();
        this.persons = generatePersons();
        em.getTransaction().commit();
    }

    public void persistDataWithDetached() {
        this.instances = new IdentityHashMap<>();
        em.getTransaction().begin();
        this.persons = generatePersons();
        this.reports = generateReports();
        em.getTransaction().commit();
        // Have to create them after transaction, otherwise the getters all return null
        this.detachedReports = reports.stream().map(r -> createDetachedReport(r, getUri(r)))
                                      .collect(Collectors.toList());
    }

    public void executeCreate() {
        this.instances = new IdentityHashMap<>();
        this.reports = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            em.getTransaction().begin();
            final OccurrenceReport report = report();
            reports.add(report);
            em.getTransaction().commit();
        }
    }

    public void executeBatchCreate() {
        this.instances = new IdentityHashMap<>();
        this.reports = new ArrayList<>(itemCount);
        em.getTransaction().begin();
        this.reports = generateReports();
        em.getTransaction().commit();
    }

    private List<OccurrenceReport> generateReports() {
        final List<OccurrenceReport> reports = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            final OccurrenceReport r = report();
            reports.add(r);
        }
        return reports;
    }

    private OccurrenceReport report() {
        final URI uri = URIs.createURI(generateUri(OccurrenceReport.class).toString());
        final OccurrenceReport r = em.createNamed(uri, OccurrenceReport.class);
        r.setKey(generateKey());
        r.setOccurrence(generateOccurrence());
        r.setSeverityAssessment(random.nextInt(Constants.MAX_SEVERITY));
        r.setAuthor(randomItem(persons));
        r.setFileNumber(random.nextLong());
        r.setDateCreated(BenchmarkUtil.datatypeFactory().newXMLGregorianCalendar(new GregorianCalendar()));
        r.setLastModified(BenchmarkUtil.datatypeFactory().newXMLGregorianCalendar(new GregorianCalendar()));
        r.setLastModifiedBy(randomItem(persons));
        r.setAttachments(generateAttachments());
        r.setRevision(random.nextInt(ITEM_COUNT));
        r.setSummary(Constants.SUMMARY + System.currentTimeMillis());
        instances.put(r, uri);
        return r;
    }

    private Set<Resource> generateAttachments() {
        return IntStream.range(0, Constants.ATTACHMENT_COUNT).mapToObj(i -> generateAttachment())
                        .collect(Collectors.toSet());
    }

    public Resource generateAttachment() {
        final URI uri = URIs.createURI(generateUri(Resource.class).toString());
        final Resource attachment = em.createNamed(uri, Resource.class);
        attachment.setIdentifier("resource" + random.nextInt() + ".doc");
        attachment.setKey(generateKey());
        attachment.setDescription("This resource was attached to further document the reported occurrence.");
        instances.put(attachment, uri);
        return attachment;
    }

    public <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    private Occurrence generateOccurrence() {
        final URI uri = URIs.createURI(generateUri(Occurrence.class).toString());
        final Occurrence occurrence = em.createNamed(uri, Occurrence.class);
        occurrence.setKey(generateKey());
        occurrence.setName("Occurrence" + random.nextInt());
        occurrence.setStart(BenchmarkUtil.toXmlGregorianCalendar(new Date(System.currentTimeMillis() - 10000)));
        occurrence.setEnd(BenchmarkUtil.toXmlGregorianCalendar(new Date()));
        occurrence.setSubEvents(generateEventHierarchy());
        occurrence.setEventType(URIs.createURI(EVENT_TYPES[random.nextInt(EVENT_TYPES.length)].toString()));
        instances.put(occurrence, uri);
        return occurrence;
    }

    private Set<Event> generateEventHierarchy() {
        return generateEvents(0, Constants.MAX_EVENT_DEPTH);
    }

    private Set<Event> generateEvents(int level, int maxLevel) {
        if (level >= maxLevel) {
            return null;
        }
        final Set<Event> events = new HashSet<>();
        for (int i = 0; i < Constants.MAX_CHILD_EVENTS; i++) {
            final Event evt = generateEvent();
            evt.setSubEvents(generateEvents(level + 1, maxLevel));
            events.add(evt);
        }
        return events;
    }

    private Event generateEvent() {
        final URI uri = URIs.createURI(generateUri(Event.class).toString());
        final Event event = em.createNamed(uri, Event.class);
        event.setKey(generateKey());
        // Can't reuse the value from occurrence, since it is null.
        // It appears that getters of newly created instances do not return proper values until commit
        event.setStart(BenchmarkUtil.toXmlGregorianCalendar(new Date(System.currentTimeMillis() - 10000)));
        event.setEnd(BenchmarkUtil.toXmlGregorianCalendar(new Date()));
        event.setEventType(URIs.createURI(EVENT_TYPES[random.nextInt(EVENT_TYPES.length)].toString()));
        instances.put(event, uri);
        return event;
    }

    private List<Person> generatePersons() {
        final List<Person> list = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            final Person p = em.createNamed(URIs.createURI(generateUri(Person.class).toString()), Person.class);
            p.setKey(generateKey());
            p.setPassword("password-" + i);
            p.setFirstName("firstName" + i);
            p.setLastName("lastName" + i);
            p.setUsername("user" + i);
            p.setContacts(IntStream.range(0, 5)
                                   .mapToObj(j -> String.format("%s_%d@kbss.felk.cvut.cz", p.getUsername(), j))
                                   .collect(Collectors.toSet()));
            list.add(p);
        }
        return list;
    }

    public int randomInt(int max) {
        return random.nextInt(max);
    }

    private java.net.URI generateUri(Class<?> type) {
        return java.net.URI.create(Vocabulary.BASE_URI + type.getSimpleName() + random.nextInt());
    }

    private String generateKey() {
        return Long.toString(System.currentTimeMillis()) + random.nextInt();
    }

    private DefaultOccurrenceReport createDetachedReport(OccurrenceReport report, URI reportUri) {
        final DefaultOccurrenceReport detached = new DefaultOccurrenceReport();
        detached.setUri(reportUri);
        detached.setKey(report.getKey());
        detached.setOccurrence(new DefaultOccurrence(report.getOccurrence()));
        detached.setSeverityAssessment(report.getSeverityAssessment());
        detached.setAuthor(new DefaultPerson(report.getAuthor()));
        detached.setFileNumber(report.getFileNumber());
        detached.setDateCreated(report.getDateCreated());
        detached.setLastModified(report.getLastModified());
        detached.setLastModifiedBy(new DefaultPerson(report.getLastModifiedBy()));
        detached.setAttachments(report.getAttachments().stream().map(DefaultResource::new).collect(Collectors.toSet()));
        detached.setSummary(report.getSummary());
        detached.setRevision(report.getRevision());
        return detached;
    }

    public List<OccurrenceReport> getReports() {
        return Collections.unmodifiableList(reports);
    }

    public List<DefaultOccurrenceReport> getDetachedReports() {
        return Collections.unmodifiableList(detachedReports);
    }

    public List<Person> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public URI getUri(Object instance) {
        return instances.get(instance);
    }
}
