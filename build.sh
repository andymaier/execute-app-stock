#!/bin/bash
mvn clean install
docker build -t localhost:5000/msa-stock-test:version1.0 .
docker push localhost:5000/msa-stock-test:version1.0
