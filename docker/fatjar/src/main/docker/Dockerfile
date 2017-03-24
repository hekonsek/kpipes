FROM java:8u91

MAINTAINER hekonsek@gmail.com

RUN wget https://get.docker.com/builds/Linux/x86_64/docker-1.9.1.tgz
RUN tar xvzpf docker-1.9.1.tgz
RUN cp usr/local/bin/docker /usr/bin/docker

RUN mkdir /app
ADD fatjar.sh /opt/
RUN chmod +x /opt/fatjar.sh

ENTRYPOINT ["/opt/fatjar.sh"]