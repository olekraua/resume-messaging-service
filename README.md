# messaging-service

Standalone repository for the messaging-service microservice.

## Local build

```bash
./mvnw -pl microservices/backend/services/messaging-service -am -Dmaven.test.skip=true package
```

## Local run

```bash
./mvnw -pl microservices/backend/services/messaging-service -am spring-boot:run
```

## Included modules

- shared
- staticdata
- profile
- notification
- auth
- media
- web
- messaging
- microservices/backend/services/messaging-service

