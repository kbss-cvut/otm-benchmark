# Processing Results in R

- Assuming R is installed.
- Assuming Python 3 is installed.
- Assuming the `otm-benchmark` is the current directory.

### Generate Box-plots

- Process data using the `transform-performance.py` script (e.g. `python3 scripts/transform-performance.py data/1g r-data/1g.csv`)

```R
# Install (if necessary) and load the ggplot2 library
install.packages("ggplot2")
library(ggplot2)
# Load the script file
source("scripts/otm-benchmark-boxplot.R")

# Generate the boxplot
performance_boxplot(read.csv("r-data/1g.csv"), "1G", TRUE)

# Save the plot to a file
pdf("boxplot.pdf")
performance_boxplot(read.csv("r-data/1g.csv"), "1G", TRUE)
dev.off()
```

### Compute Performance Statistics

```R
# Load the script file
source("scripts/otm-benchmark-stats.R")

# Compute the stats for JOPA, retrieve-all operation, on 1GB heap size
performance_stats("data/", "retrieve-all", "jopa", "1g")
```