# Java Modules in Tests

### How SUREFIRE PLUGIN starts JVM?

- Surefire plugin creates both, classpath and module-path
- The classpath is occupied by code which belongs to `test scope` by default
- Module-path contains all modules and automatic modules from our project

```
$ mvn clean test -X

.
[DEBUG] test classpath:  
    /Users/pbouda/experimental/java-modules-in-tests/module-app/target/test-classes  
    /Users/pbouda/experimental/java-modules-in-tests/module-test/target/classes  
    /Users/pbouda/.m2/repository/junit/junit/4.12/junit-4.12.jar  
    /Users/pbouda/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
    
[DEBUG] test modulepath:  
    /Users/pbouda/experimental/java-modules-in-tests/module-app/target/classes  
    /Users/pbouda/experimental/java-modules-in-tests/module-library/target/classes
.
.
[INFO] Running test.ModulesTest
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.054 s <<< FAILURE! - in test.ModulesTest
[ERROR] accessibility(test.ModulesTest)  Time elapsed: 0.008 s  <<< ERROR!
java.lang.NoClassDefFoundError: pbouda/modules/TestComponent
	at module.app/test.ModulesTest.accessibility(ModulesTest.java:10)
Caused by: java.lang.ClassNotFoundException: pbouda.modules.TestComponent
	at module.app/test.ModulesTest.accessibility(ModulesTest.java:10)

[INFO]
[INFO] Results:
[INFO]
[ERROR] Errors:
[ERROR]   ModulesTest.accessibility:10 NoClassDefFound pbouda/modules/TestComponent
[INFO]
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] java-modules-in-tests 1.0-SNAPSHOT ................. SUCCESS [  0.423 s]
[INFO] module-library ..................................... SUCCESS [  2.580 s]
[INFO] module-test ........................................ SUCCESS [  1.088 s]
[INFO] module-app 1.0-SNAPSHOT ............................ FAILURE [  2.720 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 7.002 s
[INFO] Finished at: 2018-11-22T19:52:56+01:00
[INFO] ------------------------------------------------------------------------

```

### How INTELLIJ IDEA starts JVM? (Running tests directly from INTELLIJ)

- IntelliJ chooses different approach, it puts everything on classpath even my modularized project.

```
/Users/pbouda/Programs/jdk-11.jdk/Contents/Home/bin/java -Dvisualvm.id=106577255086293 -ea -Didea.test.cyclic.buffer.size=1048576 
"-javaagent:/Users/pbouda/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4588.3/IntelliJ IDEA 2018.3 EAP.app/Contents/lib/idea_rt.jar=56093:
/Users/pbouda/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4588.3/IntelliJ IDEA 2018.3 EAP.app/Contents/bin" -Dfile.encoding=UTF-8 
-classpath 
"/Users/pbouda/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4588.3/IntelliJ IDEA 2018.3 EAP.app/Contents/lib/idea_rt.jar
:/Users/pbouda/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4588.3/IntelliJ IDEA 2018.3 EAP.app/Contents/plugins/junit/lib/junit-rt.jar
:/Users/pbouda/Library/Application Support/JetBrains/Toolbox/apps/IDEA-U/ch-0/183.4588.3/IntelliJ IDEA 2018.3 EAP.app/Contents/plugins/junit/lib/junit5-rt.jar
:/Users/pbouda/experimental/java-modules-in-tests/module-app/target/test-classes
:/Users/pbouda/experimental/java-modules-in-tests/module-app/target/classes
:/Users/pbouda/experimental/java-modules-in-tests/module-library/target/classes
:/Users/pbouda/experimental/java-modules-in-tests/module-test/target/classes
:/Users/pbouda/.m2/repository/junit/junit/4.12/junit-4.12.jar
:/Users/pbouda/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" 
com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 test.ModulesTest,accessibility
```

### Why I am able to test it in INTELLIJ but not in MAVEN ?

- The problem is that we created `package split` between two modules/projects, one from CLASSPATH (UNNAMED MODULE) and second from MODULE-PATH
- JVM can detect only package split between two modules placed on module-path
- In the case of `PACKAGE SPLIT` between classpath and module-path, module-path has a priority and classes in the same package from classpath are not discovered
- IntelliJ puts everything on classpath which does not track package split and is able to discover all classes

==> IN GENERAL: By adding the classpath into a play we automatically violates `RELIABLE CONFIGURATION` => Compiler cannot check our dependencies/modules at compile time.