package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashFlowServiceTest {

    @Mock
    private CashFlowRepository cashFlowRepository;

    @InjectMocks
    private CashFlowService cashFlowService;

    private CashFlow cashFlow;
    private UUID userId;
    private UUID cashFlowId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cashFlowId = UUID.randomUUID();
        cashFlow = new CashFlow("PEMASUKAN", "Gaji", "Gaji Bulanan", 5000000, "Gaji dari kantor");
        cashFlow.setId(cashFlowId);
        cashFlow.setUserId(userId);
    }

    @Test
    void testCreateCashFlow() {
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);

        CashFlow created = cashFlowService.createCashFlow(userId, "PEMASUKAN", "Gaji", "Gaji Bulanan", 5000000, "Gaji dari kantor");

        assertNotNull(created);
        assertEquals(userId, created.getUserId());
        assertNotNull(created.getId());
        verify(cashFlowRepository, times(1)).save(any(CashFlow.class));
    }

    @Test
    void testGetAllCashFlows_withoutSearch() {
        when(cashFlowRepository.findByUserId(userId)).thenReturn(Collections.singletonList(cashFlow));

        List<CashFlow> result = cashFlowService.getAllCashFlows(userId, null);

        assertEquals(1, result.size());
        verify(cashFlowRepository, times(1)).findByUserId(userId);
        verify(cashFlowRepository, never()).findByUserIdAndKeyword(any(), any());
    }
    
    @Test
    void testGetAllCashFlows_withEmptySearch() {
        when(cashFlowRepository.findByUserId(userId)).thenReturn(Collections.singletonList(cashFlow));

        List<CashFlow> result = cashFlowService.getAllCashFlows(userId, "  ");

        assertEquals(1, result.size());
        verify(cashFlowRepository, times(1)).findByUserId(userId);
        verify(cashFlowRepository, never()).findByUserIdAndKeyword(any(), any());
    }

    @Test
    void testGetAllCashFlows_withSearch() {
        String keyword = "gaji";
        when(cashFlowRepository.findByUserIdAndKeyword(userId, keyword)).thenReturn(Collections.singletonList(cashFlow));

        List<CashFlow> result = cashFlowService.getAllCashFlows(userId, keyword);

        assertEquals(1, result.size());
        verify(cashFlowRepository, never()).findByUserId(userId);
        verify(cashFlowRepository, times(1)).findByUserIdAndKeyword(userId, keyword);
    }

    @Test
    void testGetCashFlowById_found() {
        when(cashFlowRepository.findByIdAndUserId(cashFlowId, userId)).thenReturn(Optional.of(cashFlow));

        CashFlow found = cashFlowService.getCashFlowById(cashFlowId, userId);

        assertNotNull(found);
        assertEquals(cashFlowId, found.getId());
        verify(cashFlowRepository, times(1)).findByIdAndUserId(cashFlowId, userId);
    }

    @Test
    void testGetCashFlowById_notFound() {
        when(cashFlowRepository.findByIdAndUserId(cashFlowId, userId)).thenReturn(Optional.empty());

        CashFlow found = cashFlowService.getCashFlowById(cashFlowId, userId);

        assertNull(found);
        verify(cashFlowRepository, times(1)).findByIdAndUserId(cashFlowId, userId);
    }

    @Test
    void testGetCashFlowLabels() {
        List<String> labels = List.of("Gaji Bulanan", "Makan Siang");
        when(cashFlowRepository.findDistinctLabelsByUserId(userId)).thenReturn(labels);

        List<String> result = cashFlowService.getCashFlowLabels(userId);

        assertEquals(2, result.size());
        assertEquals("Gaji Bulanan", result.get(0));
        verify(cashFlowRepository, times(1)).findDistinctLabelsByUserId(userId);
    }

    @Test
    void testUpdateCashFlow_success() {
        when(cashFlowRepository.findByIdAndUserId(cashFlowId, userId)).thenReturn(Optional.of(cashFlow));
        when(cashFlowRepository.save(any(CashFlow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CashFlow updated = cashFlowService.updateCashFlow(cashFlowId, userId, "PENGELUARAN", "Dompet", "Makan", 25000, "Makan siang");

        assertNotNull(updated);
        assertEquals("PENGELUARAN", updated.getType());
        assertEquals(25000, updated.getAmount());
        verify(cashFlowRepository, times(1)).findByIdAndUserId(cashFlowId, userId);
        verify(cashFlowRepository, times(1)).save(cashFlow);
    }

    @Test
    void testUpdateCashFlow_notFound() {
        when(cashFlowRepository.findByIdAndUserId(cashFlowId, userId)).thenReturn(Optional.empty());

        CashFlow updated = cashFlowService.updateCashFlow(cashFlowId, userId, "PENGELUARAN", "Dompet", "Makan", 25000, "Makan siang");
        
        assertNull(updated);
        verify(cashFlowRepository, times(1)).findByIdAndUserId(cashFlowId, userId);
        verify(cashFlowRepository, never()).save(any(CashFlow.class));
    }

    @Test
    void testDeleteCashFlow_success() {
        when(cashFlowRepository.existsByIdAndUserId(cashFlowId, userId)).thenReturn(true);
        doNothing().when(cashFlowRepository).deleteById(cashFlowId);

        boolean isDeleted = cashFlowService.deleteCashFlow(cashFlowId, userId);

        assertTrue(isDeleted);
        verify(cashFlowRepository, times(1)).existsByIdAndUserId(cashFlowId, userId);
        verify(cashFlowRepository, times(1)).deleteById(cashFlowId);
    }

    @Test
    void testDeleteCashFlow_notFound() {
        when(cashFlowRepository.existsByIdAndUserId(cashFlowId, userId)).thenReturn(false);

        boolean isDeleted = cashFlowService.deleteCashFlow(cashFlowId, userId);

        assertFalse(isDeleted);
        verify(cashFlowRepository, times(1)).existsByIdAndUserId(cashFlowId, userId);
        verify(cashFlowRepository, never()).deleteById(any(UUID.class));
    }
}