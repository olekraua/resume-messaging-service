# resume-messaging-service

Single-service repository for `microservices/backend/services/messaging-service`.

## Build
`./mvnw -pl microservices/backend/services/messaging-service -am -DskipTests package`

## Run
`./mvnw -pl microservices/backend/services/messaging-service -am spring-boot:run`

## Realtime transport
Legacy broker-relay transport is fully removed from this service.
Realtime delivery is handled by Kafka outbox + `resume-realtime-service` fanout (`rt-v2` and optional MQTT over WebSocket).

## Shared libraries
This service depends on artifacts from `resume-platform-libs`.

Install/update shared libraries before building service repos:
`cd ../resume-platform-libs && ./mvnw -DskipTests install`

## Contracts
This service consumes artifacts from `resume-contracts`.

Install/update contracts before building service repos:
`cd ../resume-contracts && ./mvnw -DskipTests install`
