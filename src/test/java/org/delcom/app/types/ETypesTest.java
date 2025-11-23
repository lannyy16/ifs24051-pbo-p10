package org.delcom.app.types;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ETypeTest {

    @Test
    @DisplayName("Test fromString dengan input valid (huruf besar)")
    void fromString_shouldReturnEnum_whenGivenValidUppercaseString() {
        // Menguji "PEMASUKAN"
        EType resultPemasukan = EType.fromString("PEMASUKAN");
        assertEquals(EType.PEMASUKAN, resultPemasukan);

        // Menguji "PENGELUARAN"
        EType resultPengeluaran = EType.fromString("PENGELUARAN");
        assertEquals(EType.PENGELUARAN, resultPengeluaran);
    }

    @Test
    @DisplayName("Test fromString dengan input valid (huruf kecil)")
    void fromString_shouldReturnEnum_whenGivenValidLowercaseString() {
        // Menguji "pemasukan" (case-insensitive)
        EType result = EType.fromString("pemasukan");
        assertEquals(EType.PEMASUKAN, result);
    }

    @Test
    @DisplayName("Test fromString dengan input valid yang memiliki spasi di awal/akhir")
    void fromString_shouldReturnEnum_whenGivenStringWithWhitespace() {
        // Menguji "  PENGELUARAN  " (trim)
        EType result = EType.fromString("  PENGELUARAN  ");
        assertEquals(EType.PENGELUARAN, result);
    }

    @Test
    @DisplayName("Test fromString dengan input string yang tidak valid")
    void fromString_shouldReturnNull_whenGivenInvalidString() {
        // Menguji input yang tidak ada di dalam enum
        EType result = EType.fromString("TIDAK_VALID");
        assertNull(result);
    }
    
    @Test
    @DisplayName("Test fromString dengan input string kosong")
    void fromString_shouldReturnNull_whenGivenEmptyString() {
        // Menguji input string kosong ""
        EType result = EType.fromString("");
        assertNull(result);
    }

    @Test
    @DisplayName("Test fromString dengan input null (menguji branch yang belum tercover)")
    void fromString_shouldReturnNull_whenGivenNullInput() {
        // Secara spesifik menguji cabang `if (text == null)`
        EType result = EType.fromString(null);
        assertNull(result);
    }
}