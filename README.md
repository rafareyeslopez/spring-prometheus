# Spring Batch reading from MySQL a large dataset and publish to RabbitMQ

In this example will be shown how to check the performance of a Spring Boot project using Prometheus. Everything will run on localhost. 



## Goal

What we pretend is to compare 4 different approaches for getting some "large" dataset from a MySQL database and publish to RabbitMQ.
Having Spring Actuator running along with a Prometheus instance we will be able to monitor de different metrics (JVM Heap, CPU, etc.) so we can see which one suits better to our needs. Also, as RabbitMQ will be running with the management plugin the performance of the different queues where items wil be publish can be checked to see some indicator of publishing performance.

## Environment

The complete environment will be set up by a docker-compose file. It will start a RabbitMQ instance, a MySQL database and 4 different instances of our example application, which will be based on the spring-batch-mysql example. It will bring up Prometheus and Grafana for seeing the metrics.

so lets start by packing our application.

    ./mvnw package -DskipTests=true
    
Then lets bring up our complete environment

    docker-compose build


    docker-compose up -d

Once up in Prometheus we should be able to see the different metrics of each application on http://localhost:9090

Next we have to set up Grafana to grab the metrics from Prometheus to see some cool and useful information.

For that we have to go to Grafana at http://localhost:3000 and configure it to read metrics from Prometheus and add the desired dashboard (in references section have included link to the one used in this example). 
    
## Test    

Once we have everything running there are different endpoints in the Controller to fire the different ways of getting items from MySQL and publish them to RabbitMQ: JPA, JPA and Streams, JDBC and finally by Spring Batch.

Now we can play around firing each of them, checking the different instances metrics and pick the best option. 



References:
[https://www.baeldung.com/spring-batch-start-stop-job](https://www.baeldung.com/spring-batch-start-stop-job)
[https://medium.com/aeturnuminc/configure-prometheus-and-grafana-in-dockers-ff2a2b51aa1d](https://medium.com/aeturnuminc/configure-prometheus-and-grafana-in-dockers-ff2a2b51aa1d)
[https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/](https://www.callicoder.com/spring-boot-actuator-metrics-monitoring-dashboard-prometheus-grafana/)
[https://grafana.com/grafana/dashboards/4701](https://grafana.com/grafana/dashboards/4701)

