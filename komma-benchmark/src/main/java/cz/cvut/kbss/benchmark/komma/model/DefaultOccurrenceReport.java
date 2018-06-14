package cz.cvut.kbss.benchmark.komma.model;

import net.enilink.komma.core.URI;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Set;

public class DefaultOccurrenceReport implements OccurrenceReport {

    private URI uri;

    private String key;

    private Long fileNumber;

    private Occurrence occurrence;

    private Integer severityAssessment;

    private Person author;

    private XMLGregorianCalendar dateCreated;

    private XMLGregorianCalendar lastModified;

    private Person lastModifiedBy;

    private Set<Resource> attachments;

    private Integer revision;

    private String summary;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI id) {
        this.uri = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Long getFileNumber() {
        return fileNumber;
    }

    @Override
    public void setFileNumber(Long fileNumber) {
        this.fileNumber = fileNumber;
    }

    @Override
    public Occurrence getOccurrence() {
        return occurrence;
    }

    @Override
    public void setOccurrence(Occurrence occurrence) {
        this.occurrence = occurrence;
    }

    @Override
    public Integer getSeverityAssessment() {
        return severityAssessment;
    }

    @Override
    public void setSeverityAssessment(Integer severityAssessment) {
        this.severityAssessment = severityAssessment;
    }

    @Override
    public Person getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public XMLGregorianCalendar getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(XMLGregorianCalendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public XMLGregorianCalendar getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(XMLGregorianCalendar lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Person getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(Person lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Set<Resource> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(Set<Resource> attachments) {
        this.attachments = attachments;
    }

    @Override
    public Integer getRevision() {
        return revision;
    }

    @Override
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "OccurrenceReport{" +
                "uri=" + uri +
                ", fileNumber=" + fileNumber +
                ", revision=" + revision +
                ", occurrence=" + occurrence +
                '}';
    }
}
