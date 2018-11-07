package cz.cvut.kbss.benchmark.data;


import cz.cvut.kbss.benchmark.model.*;
import cz.cvut.kbss.benchmark.util.Constants;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.cvut.kbss.benchmark.util.Constants.*;

/**
 * Generates data for the benchmark.
 * <p>
 * The generator takes a factor parameter, which is used to configure the size of the generated dataset by multiplying
 * the {@link Constants#ITEM_COUNT} with it.
 * <p>
 * Implement the abstract methods generating implementations of the model classes in concrete OTM frameworks.
 * <p>
 * If the generator is able to create detached instances of the object model classes, {@link #generate()} should be
 * called in the subclass constructor so that instance generation is not executed during benchmark, possibly skewing performance data.
 *
 * @param <P> Concrete implementation of {@link Person}
 * @param <R> Concrete implementation of {@link OccurrenceReport}
 */
public abstract class DataGenerator<P extends Person, R extends OccurrenceReport> {

    protected final Random random = new Random();

    private final int itemCount;

    private List<R> reports;
    private List<P> persons;

    public DataGenerator(float factor) {
        assert factor > 0;
        this.itemCount = Math.round(ITEM_COUNT * factor);
    }

    public void generate() {
        this.persons = generatePersonsImpl();
        this.reports = generateReportsImpl();
    }

    public void generatePersons() {
        generatePersonsImpl();
    }

    public void generateReports() {
        generateReportsImpl();
    }

    protected List<R> generateReportsImpl() {
        final List<R> reports = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            final R r = report();
            r.setOccurrence(generateOccurrence());
            r.setAuthor(randomItem(persons));
            r.setFileNumber(random.nextLong());
            r.setKey(generateKey());
            r.setDateCreated(currentTime());
            r.setLastModified(currentTime());
            r.setLastModifiedBy(randomItem(persons));
            r.setAttachments(generateAttachments());
            r.setRevision(random.nextInt(ITEM_COUNT));
            r.setSeverityAssessment(random.nextInt(MAX_SEVERITY));
            r.setSummary(SUMMARY + System.currentTimeMillis() + i);
            reports.add(r);
        }
        return reports;
    }

    public int randomInt(int max) {
        return random.nextInt(max);
    }

    /**
     * Gets current date rounded to whole seconds. This is because some libraries have trouble working with times in
     * milliseconds.
     *
     * @return Current date and time rounded to whole seconds
     */
    private Date currentTime() {
        final long time = (System.currentTimeMillis() / 1000) * 1000;
        return new Date(time);
    }

    protected abstract R report();

    public <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    protected Occurrence generateOccurrence() {
        final Occurrence occurrence = occurrence();
        occurrence.setName("Occurrence" + random.nextInt());
        occurrence.setKey(generateKey());
        occurrence.setStartTime(new Date(currentTime().getTime() - 10000));
        occurrence.setEndTime(currentTime());
        occurrence.setSubEvents(generateEventHierarchy(occurrence));
        occurrence.setEventType(EVENT_TYPES[random.nextInt(EVENT_TYPES.length)]);
        return occurrence;
    }

    protected abstract Occurrence occurrence();

    private String generateKey() {
        return Long.toString(System.currentTimeMillis()) + random.nextInt();
    }

    private Set<Event> generateEventHierarchy(Occurrence occurrence) {
        return generateEvents(occurrence, 0, Constants.MAX_EVENT_DEPTH);
    }

    private Set<Event> generateEvents(Occurrence occurrence, int level, int maxLevel) {
        if (level >= maxLevel) {
            return null;
        }
        final Set<Event> events = new HashSet<>();
        for (int i = 0; i < Constants.MAX_CHILD_EVENTS; i++) {
            final Event evt = event();
            evt.setStartTime(occurrence.getStartTime());
            evt.setEndTime(occurrence.getEndTime());
            evt.setSubEvents(generateEvents(occurrence, level + 1, maxLevel));
            evt.setEventType(EVENT_TYPES[random.nextInt(EVENT_TYPES.length)]);
            evt.setKey(generateKey());
            events.add(evt);
        }
        return events;
    }

    protected abstract Event event();

    protected Set<Resource> generateAttachments() {
        return IntStream.range(0, Constants.ATTACHMENT_COUNT).mapToObj(i -> generateAttachment())
                        .collect(Collectors.toSet());
    }

    public Resource generateAttachment() {
        final Resource attachment = resource();
        attachment.setIdentifier("resource" + random.nextInt() + ".doc");
        attachment.setKey(generateKey());
        attachment.setDescription("This resource was attached to further document the reported occurrence.");
        return attachment;
    }

    protected abstract Resource resource();

    protected List<P> generatePersonsImpl() {
        final List<P> list = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            final P p = person();
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

    protected abstract P person();

    protected URI generateUri(Class<?> type) {
        return URI.create(Vocabulary.BASE_URI + type.getSimpleName() + random.nextInt());
    }

    public List<P> getPersons() {
        return Collections.unmodifiableList(persons);
    }

    public List<R> getReports() {
        return Collections.unmodifiableList(reports);
    }
}
