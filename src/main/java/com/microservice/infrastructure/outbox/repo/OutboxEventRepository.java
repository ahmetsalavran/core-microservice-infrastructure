package com.microservice.infrastructure.outbox.repo;

import com.microservice.infrastructure.outbox.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    // TODO(multi-pod): NEW/FAILED kayitlarini dogrudan toplu cekmek yerine
    // "claim batch" repository metodu ekleyin:
    // 1) FOR UPDATE SKIP LOCKED ile sinirli batch sec
    // 2) secilenleri atomik olarak PROCESSING'e cek
    // 3) yalniz claim eden pod publish etsin
    List<OutboxEvent> findByStatusInOrderByIdAsc(List<String> statuses);

    @Modifying
    @Query("update OutboxEvent o set o.status='SENT' where o.id = :id")
    void markSent(@Param("id") Long id);

    @Modifying
    @Query("update OutboxEvent o set o.status='FAILED' where o.id = :id")
    void markFailed(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OutboxEvent o where o.id = :id")
    Optional<OutboxEvent> findByIdForUpdate(@Param("id") Long id);
}
