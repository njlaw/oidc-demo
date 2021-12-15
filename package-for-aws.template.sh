#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

pushd "$SCRIPT_DIR" || exit

./mvnw clean package \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.group=<repositoryPrefix> \
  -Dquarkus.container-image.registry=<registry> \
  -Dquarkus.kubernetes.service-type=NodePort \
  -Dquarkus.kubernetes.ingress.expose=true \
  -Dquarkus.kubernetes.ingress.annotations.\"kubernetes.io/ingress.class\"=alb \
  -Dquarkus.kubernetes.ingress.annotations.\"alb.ingress.kubernetes.io/scheme\"=internet-facing \
  -Dquarkus.kubernetes.ingress.annotations.\"alb.ingress.kubernetes.io/backend-protocol-version\"=HTTP1 \
  -Dquarkus.kubernetes.ingress.annotations.\"alb.ingress.kubernetes.io/healthcheck-path\"=/q/health \
  -Dquarkus.kubernetes.ingress.annotations.\"alb.ingress.kubernetes.io/ssl-redirect\"=443 \
  -Dquarkus.kubernetes.ingress.annotations.\"alb.ingress.kubernetes.io/certificate-arn\"=<certificateArn>
