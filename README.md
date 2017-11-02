# MQTT Demo

Quick demo to play with MQTT

Run a local MQTT broker (mosquitto), it should work with any v3 MQTT broker 
```bash
docker run -it -p 1883:1883 -p 9001:9001 eclipse-mosquitto
```

Run the App
```bash
mvn clean package && java -jar target/mqtt-demo-1.0-SNAPSHOT-jar-with-dependencies.jar
```

In App.java we are creating 3 subscribers, one of them will listen to the main topic **home**, the other 2 are listening to 
children topics of that parent **home/temperature** and **home/security**.  

After setting up the subscriber we are creating a publisher that publishes to the parent topic and the children.  

In **AppTest** we have an example using [moquette-broker](http://andsel.github.io/moquette/), the main difference is 
the start and stop of the broker, the rest is pretty much the same modified just to do some assertions.
