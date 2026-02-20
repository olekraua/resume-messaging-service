# resume-messaging-service

Single-service repository for `microservices/backend/services/messaging-service`.

## Build
`./mvnw -pl microservices/backend/services/messaging-service -am -DskipTests package`

## Run
`./mvnw -pl microservices/backend/services/messaging-service -am spring-boot:run`

## Realtime transport
STOMP broker relay is now optional and disabled by default:
- `APP_MESSAGING_STOMP_ENABLED=false` (default)

When STOMP mode is enabled, configure:
- `APP_MESSAGING_BROKER_RELAY_HOST` (default: `localhost`)
- `APP_MESSAGING_BROKER_RELAY_PORT` (default: `61613`)
- `APP_MESSAGING_BROKER_RELAY_CLIENT_LOGIN`
- `APP_MESSAGING_BROKER_RELAY_CLIENT_PASSCODE`
- `APP_MESSAGING_BROKER_RELAY_SYSTEM_LOGIN`
- `APP_MESSAGING_BROKER_RELAY_SYSTEM_PASSCODE`
- `APP_MESSAGING_BROKER_RELAY_VHOST` (default: `/`)

Recommended mode is Kafka outbox + `resume-realtime-service` MQTT/WebSocket fanout.

## Shared libraries
This service depends on artifacts from `resume-platform-libs`.

Install/update shared libraries before building service repos:
`cd ../resume-platform-libs && ./mvnw -DskipTests install`

## Contracts
This service consumes artifacts from `resume-contracts`.

Install/update contracts before building service repos:
`cd ../resume-contracts && ./mvnw -DskipTests install`
