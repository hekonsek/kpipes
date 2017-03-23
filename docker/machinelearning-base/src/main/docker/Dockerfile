FROM fedora

MAINTAINER hekonsek@gmail.com

RUN yum install -y python
RUN yum install -y libstdc++ # Required by SciKit Learn
RUN pip install sklearn
RUN pip install numpy
RUN pip install scipy

ADD kpipes.py /kpipes/kpipes.py