#!/bin/bash
mvn clean install
docker build -t localhost:5000/stock:version2.0 .
docker push localhost:5000/stock:version2.0
kubectl apply -f stock-deployment.yaml
#kubectl apply -f stock-service.yaml