# KPipes

[![Version](https://img.shields.io/badge/kpipes-0.0.5-blue.svg)](https://github.com/kpipes/kpipes/releases)
[![Build](https://api.travis-ci.org/kpipes/kpipes.svg)](https://travis-ci.org/kpipes/kpipes/)

KPipes - simple data pipelines on the top of Apache Kafka.

KPipes goal is to provide simple framework for managing and running distributed data pipelines on the top of the Apache Kafka. The main
purpose of having this project is:
- making Red Hat's Internet Of Things R&D efforts easier
- simplify providing an environment for Eclipse Kapua and Eclipse Hono experiments

## Starting KPipes

A command below starts Docker container with KPipes server including embedded Apache ZooKeeper and Apache Kafka broker:

    docker run -d --name kpipes --net=host -v /var/kpipes:/var/kpipes kpipes/server:0.0.5
    
By default KPipes starts Kafka on port 9092 and ZooKeeper on port 2181. Your data is stored in `/var/kpipes/default/` directory:

    $ ls /var/kpipes/default
    kafka_data  zookeeper_data

### Changing to a different data set

If you need to work on on-default data set, you can specify it using `APPLICATIONID` environment variable:

    docker run -d --name kpipes --net=host -v /var/kpipes:/var/kpipes -e APPLICATIONID=anotherDataSet kpipes/server:0.0.5
    
In such case your data is stored in `/var/kpipes/anotherDataSet/` directory:

    $ ls /var/kpipes/anotherDataSet
    kafka_data  zookeeper_data

Of course Kafka and ZooKeeper initializes new data set if one with given name doesn’t exist.