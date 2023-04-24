#!/bin/bash

VERSION=$1

docker build -t harbor.k8s.elab.rs/bolnica-2/user-service:$VERSION ./user-service
docker build -t harbor.k8s.elab.rs/bolnica-2/laboratory-service:$VERSION ./laboratory-service
docker build -t harbor.k8s.elab.rs/bolnica-2/patient-service:$VERSION ./patient-service

## zakomentarisano za svaki slucaj, ako neko slucajno pokrene

# docker push harbor.k8s.elab.rs/bolnica-2/user-service:$VERSION
# docker push harbor.k8s.elab.rs/bolnica-2/patient-service:$VERSION
# docker push harbor.k8s.elab.rs/bolnica-2/laboratory-service:$VERSION
