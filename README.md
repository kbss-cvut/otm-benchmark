# OTM Benchmark

Performance/memory benchmark of object-triple mapping (OTM) libraries.

An OTM library comparison paper including this benchmark is has been published in the [Semantic Web](http://www.semantic-web-journal.net/content/comparison-object-triple-mapping-libraries) journal [1].

Currently supported libraries:

* [AliBaba](https://bitbucket.org/openrdf/alibaba/)
* [Empire](https://github.com/mhgrove/Empire)
* [JOPA](https://github.com/kbss-cvut/jopa)
* [KOMMA](https://github.com/komma/komma)
* [RDFBeans](https://rdfbeans.github.io/)

The benchmark is executed against a locally running GraphDB (but any RDF4J-compatible triple store can be used without the need to modify anything).

## Specification

The following types of operations are supported:

* Create
* Batch create
* Retrieve
* Retrieve all
* Update
* Delete

##### Create

To simulate a regular transactional behavior of web applications the create benchmark persists reports one by one in separate transactions. 
This means that each transaction persists the report, its occurrence and attachments.

Person instances, required by the reports, are pre-persisted in round setup, so that no check needs to be done for their (non)existence.


##### Batch create

This is basically the same as _Create_, but all the reports are now persisted in one big transaction, simulating batch processing. E.g. when a
system processes reports exported from another system.


##### Retrieve

Retrieve benchmark retrieves all the reports one by one using their identifier, checking for their attributes and some attributes of the referenced entities (e.g. contacts
of the report's author and last editor, name of the reported occurrence).

##### Retrieve all

Retrieve all uses either a SPARQL `SELECT` query or the frameworks API method to find all instances of type `OccurrenceReport`. The instances
are then checked the same way as in Retrieve.

##### Update

The update benchmark takes every odd report and updates several of its attributes. Then it merges the instance into the storage.
The updates are: change last editor, change last modified date, update occurrence name, update severity assessment, increase revision number,
add a new attachment (which has to be persisted). Each report is updated in a separate transaction. Since AliBaba does not support
detached objects, update in its case requires making the changes on a managed object loaded from the storage.

##### Delete

Deletes every odd report, including all its references (except for author/last editor). Each report is deleted in a separate transaction.
Correct removal is verified.

#### Model

The benchmark uses a relatively simple model consisting of the following entity classes:
* **Person** contains basic information about a person, all modelled as data properties,
* **Event** contains basic information about an event, its temporal specification, type and sub events,
* **Occurrence** subclass of `Event`, adds occurrence name,
* **OccurrenceReport** represents a report documenting an occurrence. It references the occurrence (a 1:1 relationship), person as its creator and last editor and a set of resources as attachments,
* **Resource** represents a reference to an external resource, be it a file stored on local file system or a remote URL.

The model is depicted in a diagram below:

![Model diagram](model.png "Diagram of the object model used in the benchmark.")


#### Data

The benchmark generates a number of persons, occurrences and reports. The exact number can be found in `cz.cvut.kbss.benchmark.util.Constants`.
All of these are persisted either as part of the Create benchmark or during setup of other types of benchmark.

Each report is assigned a random person as creator and another as last editor. An occurrence is generated for each report since they 
are in a 1:1 relationship. The occurrence has a balanced binary tree of subevents. Its depth is specified in constants.
Each report is also assigned three randomly generated attachments, which are persisted with it.

All attributes of all entities are set, none is left empty. Also, lazy loading is disabled on entities.

The data are generated in `DataGenerator`.

## Installation and Execution

Information about requirements, installation and execution can be found in [howto.md](howto.md).

## Results

Execution results are available at [https://kbss.felk.cvut.cz/web/otm-benchmark](https://kbss.felk.cvut.cz/web/otm-benchmark).

### Result Processing

The `scripts` folder contains a couple of scripts used to process the resulting data files.

* `transform_performance.py` - a Python script used to consolidate raw execution results for each library into one CSV file, suitable for processing in R.
The following R scripts expect datasets generated using `transform_performance.py`.
* `otm-benchmark-boxplot.R` - generates boxplots visualizing the results for one heap size
* `otm-benchmark-scalability.R` - generates scalability line plots for all operations and all supported heap sizes
* `otm-benchmark-stats.R` - generates basic statistics - mean execution time, standard deviation and the 95% confidence interval

## References
[1] Ledvinka, M.; Křemen, P., A comparison of object-triple mapping libraries, Semantic Web, 2019, 1-43. ISSN 2210-4968. doi: [10.3233/SW-190345](http://dx.doi.org/10.3233/SW-190345).
