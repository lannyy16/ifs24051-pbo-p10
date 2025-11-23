package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.types.EType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CashFlowControllerTest {
    @Test
    @DisplayName("Pengujian untuk semua metode di CashFlowController")
    void testCashFlowController() throws Exception {
        // --- SETUP ---
        // 1. Buat UUID acak untuk ID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentId = UUID.randomUUID();

        // 2. Buat data dummy
        User authUser = new User();
        authUser.setId(userId);
        authUser.setName("Test User");

        CashFlow cashFlow = new CashFlow();
        cashFlow.setId(cashFlowId);
        cashFlow.setUserId(userId);
        cashFlow.setType(EType.PEMASUKAN.name());
        cashFlow.setSource("Gaji");
        cashFlow.setLabel("Gaji Bulanan");
        cashFlow.setAmount(5000000);
        cashFlow.setDescription("Gaji dari kantor");

        // 3. Buat mock untuk Service dan Context
        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);
        AuthContext authContext = Mockito.mock(AuthContext.class);

        // 4. Buat instance controller dengan dependensi mock
        CashFlowController cashFlowController = new CashFlowController(cashFlowService, authContext);
        assert (cashFlowController != null);

        // 5. Atur perilaku mock utama (user yang sedang login)
        when(authContext.getAuthUser()).thenReturn(authUser);

        // --- PENGUJIAN METHOD createCashFlow ---
        {
            // Kasus 1: Gagal karena data tidak valid (tipe salah)
            {
                CashFlow invalidCashFlow = new CashFlow();
                invalidCashFlow.setType("TIPE_TIDAK_VALID");
                var result = cashFlowController.createCashFlow(invalidCashFlow);

                assert (result != null);
                assert (result.getStatus().equals("fail"));
                assert (result.getMessage().equals("Invalid data"));
            }

            // Kasus 2: Berhasil menambahkan data
            {
                // Atur perilaku mock service untuk create
                when(cashFlowService.createCashFlow(
                    eq(userId), // Pastikan userId yang dikirim benar
                    eq(cashFlow.getType()),
                    eq(cashFlow.getSource()),
                    eq(cashFlow.getLabel()),
                    eq(cashFlow.getAmount()),
                    eq(cashFlow.getDescription())
                )).thenReturn(cashFlow);

                var result = cashFlowController.createCashFlow(cashFlow);

                assert (result != null);
                assert (result.getStatus().equals("success"));
                assert (result.getMessage().equals("Berhasil menambahkan data"));
                assert (result.getData().get("id").equals(cashFlowId));
            }
        }

        // --- PENGUJIAN METHOD updateCashFlow ---
        {
            // Kasus 1: Gagal karena data tidak valid
            {
                CashFlow invalidCashFlow = new CashFlow();
                invalidCashFlow.setType("TIPE_TIDAK_VALID");
                var result = cashFlowController.updateCashFlow(cashFlowId, invalidCashFlow);

                assert (result != null);
                assert (result.getStatus().equals("fail"));
                assert (result.getMessage().equals("Invalid data"));
            }

            // Kasus 2: Gagal karena ID tidak ditemukan (service mengembalikan null)
            {
                when(cashFlowService.updateCashFlow(
                    eq(nonexistentId), any(), any(), any(), any(), any(), any()
                )).thenReturn(null);

                var result = cashFlowController.updateCashFlow(nonexistentId, cashFlow);

                assert (result != null);
                assert (result.getStatus().equals("fail"));
                assert (result.getMessage().equals("Data tidak ditemukan atau Anda tidak punya hak akses"));
            }

            // Kasus 3: Berhasil memperbarui data
            {
                when(cashFlowService.updateCashFlow(
                    eq(cashFlowId), any(), any(), any(), any(), any(), any()
                )).thenReturn(cashFlow);

                var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);

                assert (result != null);
                assert (result.getStatus().equals("success"));
                assert (result.getMessage().equals("Berhasil diperbarui"));
                assert (result.getData().getId().equals(cashFlowId));
            }
        }

        // --- PENGUJIAN METHOD getAllCashFlows ---
        {
            // Atur perilaku mock service untuk getAll
            List<CashFlow> dummyResponse = List.of(cashFlow);
            when(cashFlowService.getAllCashFlows(eq(userId), any())).thenReturn(dummyResponse);

            var result = cashFlowController.getAllCashFlows(null);
            assert (result != null);
            assert (result.getStatus().equals("success"));
            assert (result.getData().get("cashFlows").size() == 1);
            assert (result.getData().get("cashFlows").get(0).getId().equals(cashFlowId));
        }

        // --- PENGUJIAN METHOD getCashFlowById ---
        {
            // Kasus 1: Gagal karena ID tidak ditemukan
            {
                when(cashFlowService.getCashFlowById(eq(nonexistentId), eq(userId))).thenReturn(null);
                var result = cashFlowController.getCashFlowById(nonexistentId);

                assert (result != null);
                assert (result.getStatus().equals("fail"));
                assert (result.getMessage().equals("Data tidak ditemukan"));
            }

            // Kasus 2: Berhasil mengambil data by ID
            {
                when(cashFlowService.getCashFlowById(eq(cashFlowId), eq(userId))).thenReturn(cashFlow);
                var result = cashFlowController.getCashFlowById(cashFlowId);

                assert (result != null);
                assert (result.getStatus().equals("success"));
                assert (result.getData().get("cashFlow").getId().equals(cashFlowId));
            }
        }
        
        // --- PENGUJIAN METHOD getCashFlowLabels ---
        {
            List<String> dummyLabels = List.of("Gaji", "Bonus");
            when(cashFlowService.getCashFlowLabels(eq(userId))).thenReturn(dummyLabels);
            
            var result = cashFlowController.getCashFlowLabels();
            assert (result != null);
            assert (result.getStatus().equals("success"));
            assert (result.getData().get("labels").size() == 2);
            assert (result.getData().get("labels").get(0).equals("Gaji"));
        }

        // --- PENGUJIAN METHOD deleteCashFlow ---
        {
            // Kasus 1: Gagal menghapus karena ID tidak ada (service mengembalikan false)
            {
                when(cashFlowService.deleteCashFlow(eq(nonexistentId), eq(userId))).thenReturn(false);
                var result = cashFlowController.deleteCashFlow(nonexistentId);

                assert (result != null);
                assert (result.getStatus().equals("fail"));
                assert (result.getMessage().equals("Gagal menghapus, ID tidak ditemukan atau Anda tidak punya hak akses"));
            }
            
            // Kasus 2: Berhasil menghapus (service mengembalikan true)
            {
                when(cashFlowService.deleteCashFlow(eq(cashFlowId), eq(userId))).thenReturn(true);
                var result = cashFlowController.deleteCashFlow(cashFlowId);

                assert (result != null);
                assert (result.getStatus().equals("success"));
                assert (result.getMessage().equals("Berhasil menghapus data"));
            }
        }
    }
}