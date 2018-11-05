package cz.cvut.kbss.benchmark.util;

/**
 * API for transactional behavior support.
 */
public interface Transactional {

    void begin();

    void commit();

    /**
     * Closes the repository accessor.
     */
    void close();
}
