# KOMMA Benchmark

[KOMMA](https://github.com/komma/komma)

Latest commit in repository: October 13, 2017

Not present in Maven central, need to clone sources and install into local repository.
Based on Eclipse, complicated Maven build.

Can run with latest RDF4J, i.e. **RDF4J 2.2.4**.

### Notes

* Had to define setters in interfaces (overriding the inherited ones), otherwise ASM did not generate implementation
for them in the proxies.
* Also could not inherit `OccurrenceReport` from the core one, because class generation by ASM was failing with duplicate field/attribute
in class.
* This also meant that the `KommaGenerator` could not reuse code from core, but had to copy it with correct types.
Curiously, this issue did not apply to `Person` and `Occurrence`.
* Have to explicitly register entity classes.
* Implemented and used a disabled cache to have comparable test conditions.
* Instances do not keep reference to their URI (at least not explicitly accessible), which makes them difficult to work with.
* `IEntityManager.find` returns non-null proxy even for non-existent individuals.