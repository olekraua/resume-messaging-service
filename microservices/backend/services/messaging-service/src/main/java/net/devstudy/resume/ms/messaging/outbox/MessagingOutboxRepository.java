package net.devstudy.resume.ms.messaging.outbox;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessagingOutboxRepository extends JpaRepository<MessagingOutboxEvent, Long> {

    @Query(value = """
            select *
            from messaging_outbox
            where status in ('NEW', 'ERROR')
              and available_at <= :now
              and attempts < :maxAttempts
            order by id
            limit :limit
            for update skip locked
            """, nativeQuery = true)
    List<MessagingOutboxEvent> lockNextBatch(@Param("now") Instant now,
            @Param("limit") int limit,
            @Param("maxAttempts") int maxAttempts);
}
