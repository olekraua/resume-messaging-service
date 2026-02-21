package net.devstudy.resume.ms.messaging.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.devstudy.resume.ms.messaging.outbox.MessagingOutboxEvent;
import net.devstudy.resume.ms.messaging.outbox.MessagingOutboxRepository;

@Configuration
@EntityScan(basePackageClasses = MessagingOutboxEvent.class)
@EnableJpaRepositories(basePackageClasses = MessagingOutboxRepository.class)
public class MessagingOutboxJpaConfig {
}
