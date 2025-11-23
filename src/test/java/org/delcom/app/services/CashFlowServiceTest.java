package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CashFlowServiceTest {
    @Test
    @DisplayName("Pengujian untuk service CashFlow")
    void testCashFlowService() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId,"IN", "BANK", "Salary", 1000, "Monthly Salary");
        cashFlow.setId(cashFlowId);

        // Membuat mock CashFlowRepository
        // Buat mock
        CashFlowRepository cashFlowRepository = Mockito.mock(CashFlowRepository.class);

        // Atur perilaku mock
        when(cashFlowRepository.save(any(CashFlow.class))).thenReturn(cashFlow);
        when(cashFlowRepository.findByKeyword(userId,"BANK")).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findAll()).thenReturn(java.util.List.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, cashFlowId)).thenReturn(java.util.Optional.of(cashFlow));
        when(cashFlowRepository.findByUserIdAndId(userId, nonexistentCashFlowId)).thenReturn(java.util.Optional.empty());
        when(cashFlowRepository.existsById(cashFlowId)).thenReturn(true);
        when(cashFlowRepository.existsById(nonexistentCashFlowId)).thenReturn(false);
        doNothing().when(cashFlowRepository).deleteById(any(UUID.class));

        // Membuat instance service
        CashFlowService cashFlowService = new CashFlowService(cashFlowRepository);
        assert (cashFlowService != null);

        // Menguji create cash flow
        {
            CashFlow createdCashFlow = cashFlowService.createCashFlow(userId, "IN", "BANK", "Salary", 1000, "Monthly Salary");
            assert (createdCashFlow != null);
            assert (createdCashFlow.getId().equals(cashFlowId));
            assert (createdCashFlow.getType().equals("IN"));
            assert (createdCashFlow.getSource().equals("BANK"));
            assert (createdCashFlow.getLabel().equals("Salary"));
            assert (createdCashFlow.getAmount().equals(1000));
            assert (createdCashFlow.getDescription().equals("Monthly Salary"));
        }

        // Menguji getAllCashFlows
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId,null);
            assert (cashFlows.size() == 1);
        }

        // Menguji getAllCashFlows dengan pencarian
        {
            var cashFlows = cashFlowService.getAllCashFlows(userId, "BANK");
            assert (cashFlows.size() == 1);

            cashFlows = cashFlowService.getAllCashFlows(userId,"     ");
            assert (cashFlows.size() == 1);
        }

        // Menguji getCashFlowById
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, cashFlowId);
            assert (fetchedCashFlow != null);
            assert (fetchedCashFlow.getId().equals(cashFlowId));
            assert (fetchedCashFlow.getType().equals(cashFlow.getType()));
            assert (fetchedCashFlow.getDescription().equals(cashFlow.getDescription()));
        }

        // Menguji getCashFlowById dengan ID yang tidak ada
        {
            CashFlow fetchedCashFlow = cashFlowService.getCashFlowById(userId, nonexistentCashFlowId);
            assert (fetchedCashFlow == null);
        }

        // Menguji getCashFlowLabels
        {
            // Atur perilaku mock untuk labels
            when(cashFlowRepository.findDistinctLabels(userId)).thenReturn(java.util.List.of("Salary"));
            var labels = cashFlowService.getCashFlowLabels(userId);
            assert (labels.size() == 1);
            assert (labels.get(0).equals("Salary"));
        }

        // Menguji updateCashFlow
        {
            String updatedType = "OUT";
            String updatedSource = "ATM";
            String updatedLabel = "Withdraw";
            Integer updatedAmount = 500;
            String updatedDescription = "Monthly Withdraw";

            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, cashFlowId, updatedType, updatedSource,
                    updatedLabel, updatedAmount, updatedDescription);
            assert (updatedCashFlow != null);
            assert (updatedCashFlow.getType().equals(updatedType));
            assert (updatedCashFlow.getSource().equals(updatedSource));
            assert (updatedCashFlow.getLabel().equals(updatedLabel));
            assert (updatedCashFlow.getAmount().equals(updatedAmount));
            assert (updatedCashFlow.getDescription().equals(updatedDescription));
        }

        // Menguji update CashFlow dengan ID yang tidak ada
        {
            String updatedType = "OUT";
            String updatedSource = "ATM";
            String updatedLabel = "Withdraw";
            Integer updatedAmount = 500;
            String updatedDescription = "Monthly Withdraw";

            CashFlow updatedCashFlow = cashFlowService.updateCashFlow(userId, nonexistentCashFlowId, updatedType, updatedSource,
                    updatedLabel, updatedAmount, updatedDescription);
            assert (updatedCashFlow == null);
        }

        // Menguji deleteCashFlow
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, cashFlowId);
            assert (deleted == true);
        }

        // Menguji deleteCashFlow dengan ID yang tidak ada
        {
            boolean deleted = cashFlowService.deleteCashFlow(userId, nonexistentCashFlowId);
            assert (deleted == false);
        }
    }
}
