package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.entities.User;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.services.CashFlowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class CashFlowControllerTests {
    @Test
    @DisplayName("Pengujian untuk controller CashFlow")
    void testCashFlowController() throws Exception {
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID cashFlowId = UUID.randomUUID();
        UUID nonexistentCashFlowId = UUID.randomUUID();

        // Membuat dummy data
        CashFlow cashFlow = new CashFlow(userId, "Pemasukan", "Gaji", "Gaji Bulanan", 5000000, "Gaji bulan ini");
        cashFlow.setId(cashFlowId);

        // Membuat mock ServiceRepository
        // Buat mock
        CashFlowService cashFlowService = Mockito.mock(CashFlowService.class);

        // Atur perilaku mock
        when(cashFlowService.createCashFlow(any(UUID.class) ,any(String.class), any(String.class), any(String.class), any(Integer.class),
                any(String.class))).thenReturn(cashFlow);

        // Membuat instance controller
        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        assert (cashFlowController != null);

        cashFlowController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);
        // Menguji create cash flow dengan data valid
        {
           ResponseEntity<ApiResponse<Map<String, UUID>>> result = cashFlowController.createCashFlow(cashFlow);
            assert (result != null);
            assert (result.getStatusCode().is4xxClientError());
            assert (result.getBody().getStatus().equals("fail"));
        }

        List<CashFlow> invalidCashFlows = List.of(
                // Type null
                new CashFlow(null, "Source", "Label", 1000, "Description"),
                // Type Empty
                new CashFlow("", "Source", "Label", 1000, "Description"),
                // Source null
                new CashFlow("Type", null, "Label", 1000, "Description"),
                // Source Empty
                new CashFlow("Type", "", "Label", 1000, "Description"),
                // Label null
                new CashFlow("Type", "Source", null, 1000, "Description"),
                // Label Empty
                new CashFlow("Type", "Source", "", 1000, "Description"),
                // Amount null
                new CashFlow("Type", "Source", "Label",null , "Description"),
                // Amount zero
                new CashFlow("Type", "Source", "Label", 0, "Description"),
                // Description null
                new CashFlow("Type", "Source", "Label", 1000, null),
                // Description Empty
                new CashFlow("Type", "Source", "Label", 1000, ""));

        // Menguji create cash flow dengan data tidak valid
        {
            ResponseEntity<ApiResponse<Map<String, UUID>>> result;
            for (CashFlow cf : invalidCashFlows) {
                result = cashFlowController.createCashFlow(cf);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Tidak terautentikasi untuk menambahkan CashFlow
        {
            cashFlowController.authContext.setAuthUser(null);

            var result = cashFlowController.createCashFlow(cashFlow);
            assert (result != null);
            assert (result.getStatusCode().is4xxClientError());
            assert (result.getBody().getStatus().equals("fail"));
        }

        // Berhasil menambahkan cash flow
        {
            cashFlowController.authContext.setAuthUser(authUser);
            var result = cashFlowController.createCashFlow(cashFlow);
            assert (result != null);
            assert (result.getBody().getStatus().equals("success"));
        }

        // Menguji getAllCashFlows
        {
            // Tidak terautentikasi untuk getAllCashFlows
            {
                cashFlowController.authContext.setAuthUser(null);
                
                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllCashFlows dengan search null
            {
                cashFlowController.authContext.setAuthUser(authUser);

                List<CashFlow> dummyResponse = List.of(cashFlow);
                when(cashFlowService.getAllCashFlows(any(UUID.class), any(String.class))).thenReturn(dummyResponse);
                var result = cashFlowController.getAllCashFlows(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getCashFlowByID
        {
            // Tidak terautentikasi untuk getTodoByID
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);

            // Menguji getCashFlowById dengan ID yang ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(cashFlow);
                var result = cashFlowController.getCashFlowById(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("cashFlow").getId().equals(cashFlowId));
            }

            // Menguji getCashFlowById dengan ID yang tidak ada
            {
                when(cashFlowService.getCashFlowById(any(UUID.class), any(UUID.class))).thenReturn(null);
                var result = cashFlowController.getCashFlowById(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }
        // Menguji getCashFlowLabels
        {
            // Tidak terautentikasi untuk getCashFlowLabels
            {
                cashFlowController.authContext.setAuthUser(null);
                
                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            {   
                cashFlowController.authContext.setAuthUser(authUser);

                List<String> dummyLabels = List.of("Gaji", "Investasi", "Hadiah");
                when(cashFlowService.getCashFlowLabels(any(UUID.class))).thenReturn(dummyLabels);
                var result = cashFlowController.getCashFlowLabels();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("labels").size() == 3);
            }
        }

        // Menguji method updateCashFlow
        {   
            // Menguji updateCashFlow dengan data tidak valid
            {
                
                for (CashFlow cf : invalidCashFlows) {
                    var result = cashFlowController.updateCashFlow(cashFlowId, cf);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk updateCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.updateCashFlow(cashFlowId, cashFlow);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);
        // Menguji updateCashFlow dengan data valid
            {
                CashFlow updatedCashFlow = new CashFlow(userId, "Pemasukan", "Gaji", "Deskripsi updated", 5000, "2023-01-01");
                updatedCashFlow.setId(cashFlowId);
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), any(String.class),
                        any(String.class),
                        any(Integer.class), any(String.class)))
                        .thenReturn(updatedCashFlow);

                var result = cashFlowController.updateCashFlow(cashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }


            // Menguji updateCashFlow dengan ID yang tidak ada
            {
                when(cashFlowService.updateCashFlow(any(UUID.class), any(UUID.class), any(String.class), any(String.class),
                        any(String.class),
                        any(Integer.class), any(String.class)))
                        .thenReturn(null);
                CashFlow updatedCashFlow = new CashFlow(userId,"Belajar Spring Boot - Updated", "Deskripsi updated", "Belajar",
                        1000,
                        "Belajar");
                updatedCashFlow.setId(nonexistentCashFlowId);

                var result = cashFlowController.updateCashFlow(nonexistentCashFlowId, updatedCashFlow);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method deleteCashFlow
        { 
            // Tidak terauntetikasi untuk deleteCashFlow
            {
                cashFlowController.authContext.setAuthUser(null);

                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            cashFlowController.authContext.setAuthUser(authUser);
            // Menguji deleteCashFlow dengan ID yang ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(true);
                var result = cashFlowController.deleteCashFlow(cashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // Menguji deleteCashFlow dengan ID yang tidak ada
            {
                when(cashFlowService.deleteCashFlow(any(UUID.class), any(UUID.class))).thenReturn(false);
                var result = cashFlowController.deleteCashFlow(nonexistentCashFlowId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }
    }
}
