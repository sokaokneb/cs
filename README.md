# CS assignment

Example usage:

```
mvn clean install
java -jar ./cs-1.0-SNAPSHOT.jar testfile.txt
```

Results for the sample data in the assignment:
```
Event(id=scsmbstgra, duration=5, type=APPLICATION_LOG, host=12345, alert=true)
Event(id=scsmbstgrb, duration=3, type=null, host=null, alert=false)
Event(id=scsmbstgrc, duration=8, type=null, host=null, alert=true)
```

Some notes on the implementation:
- I am not sanity checking the contents of the file, because the assignment explicitly says that every event has two
  entries in the file (start and finish), they all have id, state and timestamp, and they are in json format. I am
  assuming that the input file satisfies these conditions, and there are no missing or malformed entries.
- A lot of files could be removed from the github repo, but I've committed everything (except the binaries),
  because I'm not sure how much of it you want to see as part of the assignment.
- Similar to the above, I've left in some code that prints out debug information, in case you need those. The part
  where I print out the contents of the hsqldb is going to degrade performance for example.

Possible improvements / enhancements:
- Write a utility class to generate large log files for testing
- Run performance tests with those large files and improve performance where needed. LogReader.processLine() is a good
  candidate for parallelization for example.