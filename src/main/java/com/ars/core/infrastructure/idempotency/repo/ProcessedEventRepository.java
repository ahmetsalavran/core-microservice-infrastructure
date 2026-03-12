package com.ars.core.infrastructure.idempotency.repo;

import com.ars.core.infrastructure.idempotency.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {

    @Modifying
    @Query("""
      update ProcessedEvent p
         set p.status = :status
       where p.eventId = :eventId
    """)
    int updateStatus(@Param("eventId") String eventId,
                     @Param("status") String status);

    @Modifying
    @Query(value = """
      insert into processed_event(event_id, event_type, order_id, status, created_at, updated_at)
      values (:eventId, :eventType, :orderId, :status, now(), now())
      on conflict (event_id) do nothing
      """, nativeQuery = true)
    int tryInsert(@Param("eventId") String eventId,
                  @Param("eventType") String eventType,
                  @Param("orderId") long orderId,
                  @Param("status") String status);

    @Transactional
    @Modifying
    @Query(value = """
      delete from processed_event
       where ctid in (
         select ctid
           from processed_event
          where created_at < :cutoff
          order by created_at
          limit :batchSize
       )
      """, nativeQuery = true)
    int deleteBatchOlderThan(@Param("cutoff") Instant cutoff,
                             @Param("batchSize") int batchSize);

}
