# KPipes

[![Version](https://img.shields.io/badge/kpipes-0.0.4-blue.svg)](https://github.com/kpipes/kpipes/releases)
[![Build](https://api.travis-ci.org/kpipes/kpipes.svg)](https://travis-ci.org/kpipes/kpipes/)

Kpipes provides data pipelines on the top of the Apache Kafka topics.

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

## Starting KPipes

First of all, start KPipes server:

    docker run -d \
      -e KAFKA_DATADIRECTORY=/var/kpipes/kafka_data -e ZOOKEEPER_DATADIRECTORY=/var/kpipes/zookeeper_data \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v /var/kpipes:/var/kpipes -p 8080:8080 kpipes/server:0.0.4
    
Then execute KPipes client:

    $ docker run --net=host -it kpipes/cmd:0.0.4 kpipes version
    0.0.4