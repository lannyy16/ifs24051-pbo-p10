package org.delcom.app.services;

import org.delcom.app.entities.CashFlow;
import org.delcom.app.repositories.CashFlowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public CashFlowService(CashFlowRepository cashFlowRepository) {
        this.cashFlowRepository = cashFlowRepository;
    }

    public CashFlow createCashFlow(UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow cashFlow = new CashFlow(type, source, label, amount, description);
        cashFlow.setId(UUID.randomUUID());
        cashFlow.setUserId(userId); // Set userId di sini
        return cashFlowRepository.save(cashFlow);
    }

    public List<CashFlow> getAllCashFlows(UUID userId, String search) {
        if (search == null || search.trim().isEmpty()) {
            return cashFlowRepository.findByUserId(userId);
        }
        return cashFlowRepository.findByUserIdAndKeyword(userId, search);
    }

    public CashFlow getCashFlowById(UUID id, UUID userId) {
        return cashFlowRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    public List<String> getCashFlowLabels(UUID userId) {
        return cashFlowRepository.findDistinctLabelsByUserId(userId);
    }

    public CashFlow updateCashFlow(UUID id, UUID userId, String type, String source, String label, Integer amount, String description) {
        CashFlow existing = getCashFlowById(id, userId); // Cari berdasarkan id dan userId
        if (existing == null) {
            return null; // Pengguna tidak boleh mengedit data milik orang lain
        }
        
        existing.setType(type);
        existing.setSource(source);
        existing.setLabel(label);
        existing.setAmount(amount);
        existing.setDescription(description);
        
        return cashFlowRepository.save(existing);
    }

    public boolean deleteCashFlow(UUID id, UUID userId) {
        if (cashFlowRepository.existsByIdAndUserId(id, userId)) {
            cashFlowRepository.deleteById(id);
            return true;
        }
        return false;
    }
}