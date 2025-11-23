package org.delcom.app.repositories;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {

    // Kueri untuk mencari berdasarkan keyword DAN userId
    @Query("SELECT c FROM CashFlow c WHERE c.userId = :userId AND (lower(c.label) LIKE lower(concat('%', :keyword, '%')) OR lower(c.description) LIKE lower(concat('%', :keyword, '%')))")
    List<CashFlow> findByUserIdAndKeyword(@Param("userId") UUID userId, @Param("keyword") String keyword);

    // Kueri untuk mendapatkan semua data berdasarkan userId
    List<CashFlow> findByUserId(UUID userId);
    
    // Kueri untuk mendapatkan label unik berdasarkan userId
    @Query("SELECT DISTINCT c.label FROM CashFlow c WHERE c.userId = :userId")
    List<String> findDistinctLabelsByUserId(@Param("userId") UUID userId);

    // Kueri untuk mencari data berdasarkan ID DAN userId
    Optional<CashFlow> findByIdAndUserId(UUID id, UUID userId);

    // Kueri untuk mengecek keberadaan data berdasarkan ID DAN userId
    boolean existsByIdAndUserId(UUID id, UUID userId);
}