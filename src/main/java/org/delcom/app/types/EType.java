package org.delcom.app.types;
public enum EType {
PEMASUKAN,
PENGELUARAN;

public static EType fromString(String text) {
    if (text == null) {
        return null;
    }
    // Membersihkan spasi di awal/akhir dan membandingkan tanpa case
    for (EType e : EType.values()) {
        if (e.name().equalsIgnoreCase(text.trim())) {
            return e;
        }
    }
    return null;
}
}