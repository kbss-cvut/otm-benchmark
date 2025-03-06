# Empire Benchmark

[Empire](https://github.com/mhgrove/Empire)

Latest commit in repository: September 19, 2016

Not present in Maven central, need to clone sources and install into local repository.

Requires **Sesame 4.0.0**.

### Notes

* It seems that Empire executes for each EM call a single transaction, so persisting a large number of instances results
  in execution of a large number of transactions.
* Trying to manage the transactions manually (through `em.getTransaction().begin/commit()`) leads to exceptions in
  Sesame client.
* Have to explicitly register entity classes.
* Empire rounds times in milliseconds to whole seconds. This caused issues in the update benchmark, so the data
  generator had to be
  updated to generate dates in whole seconds.

## Update 2025-03-06

No longer able to get Empire running - the project can be built locally (with some effort), but some of its
dependencies (`com.complexible.stardog.utils:stardog-utils-common`, `com.complexible.common:cp-openrdf-utils`)
are not available in Maven central and the benchmark is not buildable.