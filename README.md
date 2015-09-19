Vertx Extra
===========

Vertx compatible wrapper around the metrics Java client. 


Instrumenting Your Application
------------------------------

### Add Dependency

Determine the latest version of the vertx extra in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.arpnetworking.metrics.extras%22%20a%3A%22vertx-extra%22).

#### Maven

Add a dependency to your pom:

```xml
<dependency>
    <groupId>com.arpnetworking.metrics.extras</groupId>
    <artifactId>vertx-extra</artifactId>
    <version>VERSION</version>
</dependency>
```

The Maven Central repository is included by default.

#### Gradle

Add a dependency to your build.gradle:

    compile group: 'com.arpnetworking.metrics.extras', name: 'vertx-extra', version: 'VERSION'

Add the Maven Central Repository into your *build.gradle*:

```groovy
repositories {
    mavenCentral()
}
```

#### SBT

Add a dependency to your project/Build.scala:

```scala
val appDependencies = Seq(
    "com.arpnetworking.metrics.extras" % "vertx-extra" % "VERSION"
)
```

The Maven Central repository is included by default.

### SharedMetrics

To create a shareable MetricsFactory instance in Vertx wrap a MetricsFactory instance in a SharedMetrics instance.  For example:

```java
final MetricsFactory shareableMetricsFactory = new SharedMetricsFactory(
    new MetricsFactory.Builder()
        .setSinks(Collections.singletonList(
            new TsdQueryLogSink.Builder()
                .setPath("/var/logs")
                .setName("myapp-query")
                .build()));
        .build());
```

To create a shareable Metrics instance in Vertx wrap the Metrics instance from either the MetricsFactory or SharedMetricsFactory in a SharedMetrics instance.  For example:

```java
final MetricsFactory metricsFactory = new MetricsFactory.Builder()
    .setSinks(Collections.singletonList(
        new TsdQueryLogSink.Builder()
            .setPath("/var/logs")
            .setName("myapp-query")
            .build()));
    .build();
final Metrics metrics = new SharedMetrics(metricsFactory.create());
```

If you do not want to use a shared MetricsFactory instance, but still have multiple verticles write to the same sink, you will need to implement the abstract class SinkVerticle. For example:

```java
public final class MySinkVerticle extends SinkVerticle {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sink> createSinks() {
        final Sink sink = Arrays.asList(
            new TsdQueryLogSink.Builder()
                .setPath("/var/logs")
                .setName("myapp-query")
                .build());
        return ImmutableList.of(sink);
    }
}
```

Once you have implemented the SinkVerticle, you will need to define a MetricsFactory instance that communicates with this verticle. This MetricsFactory instance will write to an EventBusSink configured to send events to the Verticle defined above. Example:

```java
final Sink sink = new EventBusSink.Builder()
        .setEventBus(vertx.eventBus())
        .setSinkAddress("metrics.sink.default")
        .build();
final MetricsFactory metricsFactory = new TsdMetricsFactory.Builder()
    .setSinks(Collections.singletonList(sink))
    .build();
```

Please refer to the Java metrics client documentation [metrics-client-java/README.md](https://github.com/ArpNetworking/metrics-client-java/blob/master/README.md) for more information on using Metrics and MetricsFactory.

Building
--------

Prerequisites:
* [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.2.5+](http://maven.apache.org/download.cgi)

Building:
    metrics-vertx-extra> mvn verify

To use the local version you must first install it locally:

    metrics-vertx-extra> mvn install

You can determine the version of the local build from the pom file.  Using the local version is intended only for testing or development.

You may also need to add the local repository to your build in order to pick-up the local version:

* Maven - Included by default.
* Gradle - Add *mavenLocal()* to *build.gradle* in the *repositories* block.
* SBT - Add *resolvers += Resolver.mavenLocal* into *project/plugins.sbt*.

License
-------

Published under Apache Software License 2.0, see LICENSE

&copy; Groupon Inc., 2014
