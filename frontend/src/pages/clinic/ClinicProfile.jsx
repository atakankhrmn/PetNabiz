import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function ClinicProfile({ me }) {
    const [clinic, setClinic] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // ClinicController.java @GetMapping("/my") kullanarak verileri çekiyoruz
        http.get("/api/clinics/my")
            .then(res => setClinic(res.data))
            .catch(err => console.error("Klinik verisi alınamadı", err))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div style={loadingStyle}>Klinik detayları hazırlanıyor...</div>;

    return (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 300px", gap: "25px" }}>
            {/* Sol Taraf: Klinik Bilgileri */}
            <div>
                <div style={sectionHeaderStyle}>🏥 Klinik Bilgileri</div>
                <div style={formGridStyle}>
                    <div style={boxStyle}><label style={smallLabel}>KLİNİK ADI</label><div style={infoValue}>{clinic?.name}</div></div>
                    <div style={boxStyle}><label style={smallLabel}>E-POSTA</label><div style={infoValue}>{clinic?.email}</div></div>
                    <div style={boxStyle}><label style={smallLabel}>TELEFON</label><div style={infoValue}>{clinic?.phone || "Girilmemiş"}</div></div>
                    <div style={boxStyle}><label style={smallLabel}>DURUM</label><div style={{...infoValue, color: clinic?.active ? "#22c55e" : "#ef4444"}}>{clinic?.active ? "Aktif İşletme" : "Pasif"}</div></div>

                    <div style={{...boxStyle, gridColumn: "span 2"}}><label style={smallLabel}>KONUM</label><div style={infoValue}>{clinic?.city} / {clinic?.district}</div></div>
                    <div style={{...boxStyle, gridColumn: "span 2"}}><label style={smallLabel}>ADRES</label><div style={infoValue}>{clinic?.address}</div></div>
                </div>
            </div>

            {/* Sağ Taraf: Veteriner Özet Listesi */}
            <div style={vetSidePaneStyle}>
                <div style={sectionHeaderStyle}>🩺 Veterinerlerimiz</div>
                <div style={{ display: "grid", gap: "10px" }}>
                    {clinic?.veterinaries && clinic.veterinaries.length > 0 ? (
                        clinic.veterinaries.map(vet => (
                            <div key={vet.vetId} style={vetCardMini}>
                                <div style={{ fontWeight: "700", color: "#0284c7" }}>{vet.name}</div>
                                <div style={{ fontSize: "11px", color: "#64748b" }}>{vet.specialization || "Genel Cerrah"}</div>
                            </div>
                        ))
                    ) : (
                        <div style={noDataStyle}>Henüz veteriner kaydı bulunmuyor.</div>
                    )}
                </div>
                <div style={hintStyle}>* Veteriner ekleme/çıkarma işlemlerini 'Veteriner Hekimler' sekmesinden yapabilirsiniz.</div>
            </div>
        </div>
    );
}

// Klinik Özel Stiller
const sectionHeaderStyle = { fontSize: "16px", fontWeight: "800", color: "#1e293b", marginBottom: "15px", display: "flex", alignItems: "center", gap: "8px" };
const formGridStyle = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "15px", background: "white", padding: "20px", borderRadius: "15px", border: "1px solid #e2e8f0" };
const boxStyle = { display: "flex", flexDirection: "column", gap: "4px" };
const smallLabel = { fontSize: "10px", fontWeight: "800", color: "#94a3b8", textTransform: "uppercase" };
const infoValue = { fontSize: "14px", fontWeight: "600", color: "#334155" };
const vetSidePaneStyle = { background: "#f8fafc", padding: "20px", borderRadius: "15px", border: "1px solid #e2e8f0", alignSelf: "start" };
const vetCardMini = { background: "white", padding: "10px", borderRadius: "10px", border: "1px solid #cbd5e1", boxShadow: "0 2px 4px rgba(0,0,0,0.02)" };
const noDataStyle = { fontSize: "12px", color: "#94a3b8", textAlign: "center", padding: "20px" };
const hintStyle = { fontSize: "10px", color: "#64748b", marginTop: "15px", fontStyle: "italic" };
const loadingStyle = { padding: "20px", color: "#64748b" };