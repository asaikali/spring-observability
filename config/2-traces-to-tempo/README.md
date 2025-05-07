# trace-to-tempo

This project shows how to configure distributed traces to flow from a spring boot
application to tempo. Tempo is configured to accept span data using the zipkin
format, form the prspective of the spring boot application it thinks that it
is sending spans to Zipkin.
