package cz.cvut.kbss.benchmark.rdfbeans;

import cz.cvut.kbss.benchmark.AbstractBenchmark;
import cz.cvut.kbss.benchmark.BenchmarkRunner;

public class RDFBeansBenchmark extends AbstractBenchmark {

    public static void main(String[] args) {
        final RDFBeansBenchmark benchmark = new RDFBeansBenchmark();
        benchmark.run(args);
    }

    @Override
    protected BenchmarkRunner createBenchmarkRunner(String type) {
        switch (type) {
            case CREATE:
                return new CreateBenchmarkRunner();
            case BATCH_CREATE:
                return new BatchCreateBenchmarkRunner();
            case RETRIEVE:
                return new RetrieveBenchmarkRunner();
            case RETRIEVE_ALL:
                return new RetrieveAllBenchmarkRunner();
            case UPDATE:
                return new UpdateBenchmarkRunner();
            case DELETE:
                return new DeleteBenchmarkRunner();
            default:
                throw new IllegalArgumentException("Unsupported benchmark type " + type + ".");
        }
    }
}
