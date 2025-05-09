# Friction 

This document is a log of all the pain points and issues observed while 
building this project. 

## Configuration is Complex

Configuring observability in a Spring Boot application is quite complex because
there are too many choices. It reminds me of what it was like to configure Spring 
applications before Spring Boot introduced conventions over configuration.

### Classpath is Too Complex to Configure 

As a developer, I must know exactly how I want observability to be configured.
I need to add too many dependencies. For example, here are the 
decisions I have to make:

1. Do I want tracing turned on?
2. Which tracing bridge should I choose? OTel or Brave?
3. Which tracing exporter do I want to use?
4. Which metrics registry do I want?
5. Where do I configure URLs of the backend observability systems?
6. What configuration keys in the `application.yaml` need to be turned on?

### Unexpected Things During OTel Configuration 

1. Adding OTLP registry got metrics exported to OTel collector, but adding 
   tracing bridge did not because I forgot to add the OTLP exporter. Why 
   do metrics automatically export but traces don't? I am sure there are
   users that need the flexibility, but the point of Spring Boot is that it 
   has conventions that mean I don't have to make all these choices as a dev. 
   Rather, I should simply choose a starter and auto-configuration should do the right
   thing. Typical configurations should be easy, hard ones should be doable. Current setup
   makes the getting started experience too difficult.

The experience for OTLP should be something like this: 

1. Add Boot OTLP-starter 
2. Configure the URL to the OTel collector 
3. Run app 
4. Metrics, logs, traces flow 
5. A tool like the OTel-LGTM can be launched with one command and start getting data

As a developer, I really don't care about the low-level details of how the 
observability libraries work. I just want my metrics, logs, and traces to show up
in some backend. 

#### OTel is Confusing and Means Too Many Things 

This is not really a Spring Boot issue, rather it is an OTel issue. Simply 
put, the word "OTel" makes a ton of promises about how it will make things 
simpler and standardized, but then it forces the developer to learn too 
much about OTel. OTel can mean any one of the following things:

1. The protocol
2. The collector
3. The SDK 
4. Traces, metrics, logs
5. gRPC vs. HTTP endpoints 
6. Semantic conventions 

Bottom line, there are too many parts of OTel, each of which has its own
version number and status. This makes it very hard to know which version of what 
signal with what version of what library works. OTel needs a release train
model. Then, as a user, I can say I am using OTel version X and get some help 
and some expectation about what has been tested and validated together.

Configuring each OTel signal has its own set of settings, and it's unclear how 
to do each one. I was not able to get OTel to export logs from the Boot app.
I understand that most production systems are using an agent to tail stdout and stderr
for issues, but still, it is not possible to find a cut-and-paste solution that 
just gets the logs out to the agent.

I think it is important for Spring Boot to offer a starter that at the very 
least gets metrics, traces, and logs to an OTLP collector.
