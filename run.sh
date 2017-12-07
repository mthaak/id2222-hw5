#!/bin/bash
java -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -Xmx5000m -jar target/assignment4-jabeja-1.0-jar-with-dependencies.jar $@
