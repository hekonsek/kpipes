# KPipes

[![Version](https://img.shields.io/badge/kpipes-0.0.0-blue.svg)](https://github.com/kpipes/kpipes/releases)
[![Build](https://api.travis-ci.org/kpipes/kpipes.svg)](https://travis-ci.org/kpipes/kpipes/)

Kpipes provides Unix-like pipes API for Apache Kafka. It allows you to easily wire serverless-like functions
in order to create simple data pipelines on the top of Kafka topics.

Primary KPipes features:
- simple API for creating data pipelines over Kafka topics
- provides canonical format of the events, supporting any hierarchical data
 representation (JSON, Avro, etc)
- KPipes brings protocol adapters (REST, AMQP, MQTT, etc) that can be used to ingest events
  into Kafka
- the same protocol adapters can be used to access your data in a request/reply manner
(so you can use REST, AMQP, MQTT, etc to access your data)
- Kipipes comes with an impressive collection predefined functions (including core transformations and geodata functions)
- KPipes supports multi-tenancy as a first class citizen

## Hello world examples


```
KPipes kpipes = new KPipes().start();
PipesBuilder pipesBuilder = kpipes.service(PipesBuilder.class).get();
pipesBuilder.pipe("myGeoData | geo.coding.reverse | targetTopic");
```
```
myGeoData:
{body: { lat: 40.714224, lng: -73.961452 }}

targetTopic:
{
  metaData: {
    response.geo.coding.reverse: {
      address: "285 Bedford Avenue, Brooklyn, NY 11211, USA",
      city: "New York"
    }
  },
  body: { lat: 40.714224, lng: -73.961452 }
}
```

You can also create your custom pipe function:

```
KPipes kpipes = new KPipes().start();

new FunctionBinding(kpipes, "helloWorld",
  { event -> event.body().set("world", "world!"); return event; }
).start();

PipesBuilder pipesBuilder = kpipes.service(PipesBuilder.class).get();
pipesBuilder.pipe("myData | helloWorld | targetTopic");
```
```
myData:
{hello: "Hello"}

targetTopic:
{hello: "Hello", world: "world!"}
```
