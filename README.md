# kpipes

Kpipes provides Unix-like pipes API for Apache Kafka. It allows you to easily wire serverless-like functions
in order to create simple data pipelines on the top of Kafka topics.

Primary KPipes features:
- simple API for creating data pipelines over Kafka topics
- provides canonical format of the events, serializable to any hierarchical data
 representation (JSON, Avro, etc)
- KPipes brings protocol adapters (REST, AMQP, MQTT, etc) that can be used to ingest events
  into Kafka
- the same protocol adapters can be used to access your data in a request/reply manner
(so you can use REST, AMQP, MQTT, etc to access your data)
- KPipes supports multi-tenancy as a first class citizen

## Hello world examples


```
KPipes kpipes = new KPipes().start();
PipesBuilder pipesBuilder = kpipes.service(PipesBuilder.class);
pipesBuilder.pipe("myGeoData | geocoder | targetTopic");
```
```
myGeoData:
{ position: {
    lat: 40.714224, lng: -73.961452
  }
}

targetTopic:
{ position: {
    lat: 40.714224, lng: -73.961452,
    geocoding: "285 Bedford Avenue, Brooklyn, NY 11211, USA"
  }
}
```

You can also create your custom pipe function:

```
KPipes kpipes = new KPipes().start();

new FunctionBinding(kpipes, "helloWorld",
  { event -> event.body.set("world") = "world!" ; return event; }
).start();

PipesBuilder pipesBuilder = kpipes.service(PipesBuilder.class);
pipesBuilder.pipe("myData | helloWorld | targetTopic");
```
```
myData:
{hello: "Hello"}

targetTopic:
{hello: "Hello", world: "world!"}
```
