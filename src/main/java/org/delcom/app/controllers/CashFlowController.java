package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext; // IMPORT AuthContext
import org.delcom.app.entities.User;       // IMPORT User
import org.delcom.app.entities.CashFlow;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.types.EType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cash-flows")
public class CashFlowController {

    private final CashFlowService cashFlowService;
    private final AuthContext authContext; // INJEKSI AuthContext

    public CashFlowController(CashFlowService cashFlowService, AuthContext authContext) {
        this.cashFlowService = cashFlowService;
        this.authContext = authContext; // Inisialisasi AuthContext
    }

    private boolean isInvalid(CashFlow cashFlow) {
        if (EType.fromString(cashFlow.getType()) == null) {
            return true;
        }
        return false;
    }
    
    // Helper untuk mendapatkan user yang sedang login
    private User getAuthenticatedUser() {
        return authContext.getAuthUser();
    }

    @PostMapping
    public ApiResponse<Map<String, UUID>> createCashFlow(@RequestBody CashFlow cashFlow) {
        if (isInvalid(cashFlow)) {
            return new ApiResponse<>("fail", "Invalid data", null);
        }
        
        User authUser = getAuthenticatedUser();
        CashFlow newCashFlow = cashFlowService.createCashFlow(
            authUser.getId(), // Kirim userId
            cashFlow.getType(),
            cashFlow.getSource(),
            cashFlow.getLabel(),
            cashFlow.getAmount(),
            cashFlow.getDescription()
        );

        Map<String, UUID> data = new HashMap<>();
        data.put("id", newCashFlow.getId());
        return new ApiResponse<>("success", "Berhasil menambahkan data", data);
    }

    @PutMapping("/{id}")
    public ApiResponse<CashFlow> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow cashFlow) {
        if (isInvalid(cashFlow)) {
            return new ApiResponse<>("fail", "Invalid data", null);
        }
        
        User authUser = getAuthenticatedUser();
        CashFlow updated = cashFlowService.updateCashFlow(
            id,
            authUser.getId(), // Kirim userId
            cashFlow.getType(),
            cashFlow.getSource(),
            cashFlow.getLabel(),
            cashFlow.getAmount(),
            cashFlow.getDescription()
        );
        
        if (updated == null) {
            return new ApiResponse<>("fail", "Data tidak ditemukan atau Anda tidak punya hak akses", null);
        }

        return new ApiResponse<>("success", "Berhasil diperbarui", updated);
    }

    @GetMapping
    public ApiResponse<Map<String, List<CashFlow>>> getAllCashFlows(@RequestParam(required = false) String search) {
        User authUser = getAuthenticatedUser();
        Map<String, List<CashFlow>> data = new HashMap<>();
        data.put("cashFlows", cashFlowService.getAllCashFlows(authUser.getId(), search));
        return new ApiResponse<>("success", "Berhasil mengambil data", data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, CashFlow>> getCashFlowById(@PathVariable UUID id) {
        User authUser = getAuthenticatedUser();
        CashFlow cashFlow = cashFlowService.getCashFlowById(id, authUser.getId());
        if (cashFlow != null) {
            Map<String, CashFlow> data = new HashMap<>();
            data.put("cashFlow", cashFlow);
            return new ApiResponse<>("success", "Berhasil mengambil data", data);
        }
        return new ApiResponse<>("fail", "Data tidak ditemukan", null);
    }

    @GetMapping("/labels")
    public ApiResponse<Map<String, List<String>>> getCashFlowLabels() {
        User authUser = getAuthenticatedUser();
        Map<String, List<String>> data = new HashMap<>();
        data.put("labels", cashFlowService.getCashFlowLabels(authUser.getId()));
        return new ApiResponse<>("success", "Berhasil mengambil data", data);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCashFlow(@PathVariable UUID id) {
        User authUser = getAuthenticatedUser();
        if (cashFlowService.deleteCashFlow(id, authUser.getId())) {
            return new ApiResponse<>("success", "Berhasil menghapus data", null);
        }
        return new ApiResponse<>("fail", "Gagal menghapus, ID tidak ditemukan atau Anda tidak punya hak akses", null);
    }
}