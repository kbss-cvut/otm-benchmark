package cz.cvut.kbss.benchmark.jopa;

import cz.cvut.kbss.benchmark.AbstractBenchmark;
import cz.cvut.kbss.benchmark.BenchmarkRunner;

public class JopaBenchmark extends AbstractBenchmark {

    public static void main(String[] args) {
        final JopaBenchmark benchmark = new JopaBenchmark();
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
            case UPDATE:
                return new UpdateBenchmarkRunner();
            case DELETE:
                return new DeleteBenchmarkRunner();
            default:
                throw new IllegalArgumentException("Unsupported benchmark type " + type + '.');
        }
    }
}
