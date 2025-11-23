package org.delcom.app.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CashFlowTest {

    private CashFlow cashFlow;
    private final UUID id = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final String type = "PEMASUKAN";
    private final String source = "Gaji";
    private final String label = "Gaji Bulanan";
    private final Integer amount = 5000000;
    private final String description = "Gaji dari kantor";

    @BeforeEach
    void setUp() {
        cashFlow = new CashFlow();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(cashFlow, "Default constructor should create a non-null object.");
    }

    @Test
    void testParameterizedConstructor() {
        CashFlow parameterizedCashFlow = new CashFlow(type, source, label, amount, description);
        assertEquals(type, parameterizedCashFlow.getType());
        assertEquals(source, parameterizedCashFlow.getSource());
        assertEquals(label, parameterizedCashFlow.getLabel());
        assertEquals(amount, parameterizedCashFlow.getAmount());
        assertEquals(description, parameterizedCashFlow.getDescription());
    }

    @Test
    void testGettersAndSetters() {
        cashFlow.setId(id);
        cashFlow.setUserId(userId);
        cashFlow.setType(type);
        cashFlow.setSource(source);
        cashFlow.setLabel(label);
        cashFlow.setAmount(amount);
        cashFlow.setDescription(description);

        assertEquals(id, cashFlow.getId());
        assertEquals(userId, cashFlow.getUserId());
        assertEquals(type, cashFlow.getType());
        assertEquals(source, cashFlow.getSource());
        assertEquals(label, cashFlow.getLabel());
        assertEquals(amount, cashFlow.getAmount());
        assertEquals(description, cashFlow.getDescription());
    }

    @Test
    void testLifecycleCallbacksOnCreate() throws InterruptedException {
        // Panggil metode onCreate secara manual untuk simulasi @PrePersist
        cashFlow.onCreate();
        
        Instant createdAt = cashFlow.getCreatedAt();
        Instant updatedAt = cashFlow.getUpdatedAt();

        assertNotNull(createdAt);
        assertNotNull(updatedAt);
        assertEquals(createdAt, updatedAt);

        // Pastikan timestamp berbeda jika dipanggil lagi setelah delay
        Thread.sleep(10); // delay kecil
        cashFlow.onUpdate();
        assertTrue(cashFlow.getUpdatedAt().isAfter(createdAt));
    }

    @Test
    void testLifecycleCallbacksOnUpdate() {
        cashFlow.onCreate();
        Instant initialUpdatedAt = cashFlow.getUpdatedAt();

        // Panggil onUpdate untuk simulasi @PreUpdate
        cashFlow.onUpdate();
        Instant newUpdatedAt = cashFlow.getUpdatedAt();

        assertNotNull(newUpdatedAt);
        assertTrue(newUpdatedAt.isAfter(initialUpdatedAt) || newUpdatedAt.equals(initialUpdatedAt));
    }
}