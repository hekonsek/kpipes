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
    
Your data is stored in `/var/kpipes/default/` directory:

    $ ls /var/kpipes/default
    kafka_data  zookeeper_data
