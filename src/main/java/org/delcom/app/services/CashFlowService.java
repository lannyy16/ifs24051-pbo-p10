package org.delcom.app.services;

import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashFlowService {
    private final CashFlowRepository cashFlowRepository;

    
    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }
        
        @Transactional
        public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow cashFlow = new CashFlow(userId, type, source, label, amount, description);
        return cashFlowRepository.save(cashFlow);
    }

    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return cashFlowRepository.findByKeyword(userId, search);
        }
        return cashFlowRepository.findAll();
    }

    public CashFlow getCashFlowById(UUID userId, UUID id) {
        return cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    public List<String> getCashFlowLabels(UUID userId) {
        return cashFlowRepository.findDistinctLabels(userId);
    }


    @Transactional
    public CashFlow updateCashFlow(UUID userId, UUID id, String type, String source, String label, Integer amount, String description  ) {
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow != null) {
            cashFlow.setType(type);
            cashFlow.setSource(source);
            cashFlow.setLabel(label);
            cashFlow.setDescription(description);
            cashFlow.setAmount(amount);
            return cashFlowRepository.save(cashFlow);
        }
        return null;
    }

    @Transactional
    public boolean deleteCashFlow(UUID userId, UUID id) {
        CashFlow cashFlow = cashFlowRepository.findByUserIdAndId(userId, id).orElse(null);
        if (cashFlow == null) {
            return false;
        }
        cashFlowRepository.deleteById(id);
        return true;
    }
}