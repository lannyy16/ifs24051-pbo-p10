package org.delcom.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/cash-flows")
public class CashFlowController {
    private final CashFlowService cashFlowService;

    @Autowired
    protected AuthContext authContext;

    public CashFlowController(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    // Menambahkan Cash Flow baru
    // -------------------------------
    @PostMapping
    public ResponseEntity <ApiResponse<Map<String, UUID>>> createCashFlow(@RequestBody CashFlow reqCashFlow) {
        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        } 
        else if(reqCashFlow.getSource() == null || reqCashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data source tidak valid", null));
        }
        else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data label tidak valid", null));
        }
        else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data amount tidak valid", null));
        }
        else if (reqCashFlow.getDescription() == null || reqCashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data description tidak valid", null));
        }

        // Validasi Autentikasi
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }

        User authUser = authContext.getAuthUser();

        CashFlow newCashFlow = cashFlowService.createCashFlow(authUser.getId(), reqCashFlow.getType(), reqCashFlow.getSource(), reqCashFlow.getLabel(), reqCashFlow.getAmount(), reqCashFlow.getDescription());
        return ResponseEntity.ok(new ApiResponse<Map<String, UUID>>(
                "success",
                "Berhasil menambahkan data",
                Map.of("id", newCashFlow.getId())));
    }

    // Mendapatkan semua Cash Flow dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<CashFlow>>>> getAllCashFlows(@RequestParam(required = false) String search) {
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }

        User authUser = authContext.getAuthUser();

        List<CashFlow> cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("cash_flows", cashFlows)));
    }


    // Mendapatkan Cash Flow berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, CashFlow>>> getCashFlowById(@PathVariable UUID id) {
        // Validasi Autentikasi
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }

        User authUser = authContext.getAuthUser();

        CashFlow cashFlow = cashFlowService.getCashFlowById(authUser.getId(), id);
        if (cashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>(
                    "fail",
                    "Gagal mengambil data",
                    null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("cashFlow", cashFlow)));
    }

    // Mendapatkan Cash Flow berdasarkan labels
    // -------------------------------
    @GetMapping("/labels")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getCashFlowLabels() {
        // Validasi Autentikasi
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }

        User authUser = authContext.getAuthUser();
        List<String> labels = cashFlowService.getCashFlowLabels(authUser.getId());
        
        Map<String, List<String>> data = new HashMap<>();
        data.put("labels", labels);
        
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil mengambil data label.", data));
    }


    // Memperbarui Cash Flow berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CashFlow>> updateCashFlow(@PathVariable UUID id, @RequestBody CashFlow reqCashFlow) {
        if (reqCashFlow.getType() == null || reqCashFlow.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        }
        else if (reqCashFlow.getSource() == null || reqCashFlow.getSource().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data source tidak valid", null));
        }
        else if (reqCashFlow.getLabel() == null || reqCashFlow.getLabel().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data label tidak valid", null));
        }
        else if (reqCashFlow.getAmount() == null || reqCashFlow.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data amount tidak valid", null));
        }
        else if (reqCashFlow.getDescription() == null || reqCashFlow.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data description tidak valid", null));
        } 
        
        // Validasi Autentikasi
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("fail", "Data tidak valid", null));
        }

        User authUser = authContext.getAuthUser();

        CashFlow updatedCashFlow = cashFlowService.updateCashFlow(authUser.getId(), id, reqCashFlow.getType(), reqCashFlow.getSource(), reqCashFlow.getLabel(), reqCashFlow.getAmount(), reqCashFlow.getDescription());
        if (updatedCashFlow == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil memperbarui data", null));
    }

    // Menghapus todo berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCashFlow(@PathVariable UUID id) {
        // Validasi Autentikasi
        if(!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }

        User authUser = authContext.getAuthUser();
        boolean status = cashFlowService.deleteCashFlow(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>(
                    "fail",
                    "Data tidak ditemukan",
                    null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil menghapus data",
                null));
    }
}