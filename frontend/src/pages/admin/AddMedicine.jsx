import { useState } from "react";
import { http } from "../../api/http";

// --- STİLLER ---
const containerStyle = { padding: "30px", maxWidth: "800px", margin: "0 auto" };
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "30px", border: "1px solid #e2e8f0" };
const labelStyle = { display: "block", fontSize: "13px", fontWeight: "700", color: "#64748b", marginBottom: "6px" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "14px", outline: "none", transition: "0.2s", background: "#fff" };
const headerStyle = { fontSize: "24px", fontWeight: "800", color: "#1e293b", marginBottom: "5px" };
const subHeaderStyle = { color: "#64748b", margin: "0 0 25px 0", fontSize: "14px" };

// Buton Bileşeni
const Button = ({ children, onClick, disabled, variant = "primary", style }) => {
    const baseStyle = {
        padding: "12px 24px", borderRadius: "8px", border: "none", cursor: disabled ? "not-allowed" : "pointer",
        fontWeight: "600", fontSize: "14px", width: "100%", marginTop: "10px", transition: "0.2s", ...style
    };

    const variants = {
        primary: { background: "#3b82f6", color: "white", opacity: disabled ? 0.7 : 1 },
        secondary: { background: "#f1f5f9", color: "#475569", border: "1px solid #cbd5e1" },
        danger: { background: "#fee2e2", color: "#ef4444", border: "1px solid #fecaca" } // Silme butonu için stil
    };

    return (
        <button
            onClick={disabled ? undefined : onClick}
            disabled={disabled}
            style={{ ...baseStyle, ...variants[variant] }}
        >
            {children}
        </button>
    );
};

export default function AddMedicine() {
    const [loading, setLoading] = useState(false);

    // Form State
    const [formData, setFormData] = useState({ name: "", type: "" });

    // Liste State'leri
    const [medicines, setMedicines] = useState([]);
    const [showList, setShowList] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // İLAÇLARI GETİR
    const handleToggleList = async () => {
        if (!showList) {
            fetchMedicines();
        }
        setShowList(!showList);
    };

    const fetchMedicines = async () => {
        try {
            const res = await http.get("/api/medicines");
            setMedicines(res.data);
        } catch (error) {
            console.error("İlaç listesi alınamadı", error);
        }
    };

    // --- SİLME İŞLEMİ ---
    const handleDelete = async (medicineId) => {
        // Kullanıcıya soralım, yanlışlıkla silmesin
        if (!window.confirm("Bu ilacı sistemden silmek istediğinize emin misiniz?")) {
            return;
        }

        try {
            // Backend'e silme isteği gönder
            await http.delete(`/api/medicines/${medicineId}`);

            // Başarılı olursa listeyi güncelle (UI'dan kaldır)
            setMedicines(medicines.filter(med => med.medicineId !== medicineId));

            alert("🗑️ İlaç başarıyla silindi.");
        } catch (error) {
            console.error("Silme hatası:", error);
            alert("Silme işlemi başarısız. Bu ilaç kullanımda olabilir.");
        }
    };

    // KAYDETME İŞLEMİ
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!formData.name || !formData.type) return alert("Lütfen tüm alanları doldurun.");

        setLoading(true);
        try {
            await http.post("/api/medicines", formData);
            alert("✅ İlaç sisteme eklendi!");
            setFormData({ name: "", type: "" });

            // Eğer liste açıksa yenile
            if (showList) fetchMedicines();

        } catch (error) {
            console.error("Hata:", error);
            const msg = error.response?.data?.message || "Kayıt hatası.";
            alert("Hata: " + msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={containerStyle}>
            <div>
                <h1 style={headerStyle}>💊 İlaç Yönetimi</h1>
                <p style={subHeaderStyle}>Yeni ilaç ekleyin, listeleyin veya silin.</p>
            </div>

            <div style={cardStyle}>
                <form onSubmit={handleSubmit}>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px" }}>
                        <div>
                            <label style={labelStyle}>İlaç Adı <span style={{color:"red"}}>*</span></label>
                            <input type="text" name="name" style={inputStyle} placeholder="Örn: Parol" value={formData.name} onChange={handleChange} />
                        </div>
                        <div>
                            <label style={labelStyle}>Türü <span style={{color:"red"}}>*</span></label>
                            <input type="text" name="type" style={inputStyle} placeholder="Örn: Ağrı Kesici" value={formData.type} onChange={handleChange} />
                        </div>
                    </div>

                    <div style={{ display: "flex", gap: "15px", marginTop: "10px" }}>
                        <Button disabled={loading} onClick={handleSubmit}>
                            {loading ? "Kaydediliyor..." : "💾 Kaydet"}
                        </Button>

                        <Button
                            type="button"
                            variant="secondary"
                            onClick={(e) => { e.preventDefault(); handleToggleList(); }}
                        >
                            {showList ? "🔼 Listeyi Gizle" : "📋 Mevcut İlaçları Gör"}
                        </Button>
                    </div>
                </form>

                {/* --- İLAÇ LİSTESİ ALANI --- */}
                {showList && (
                    <div style={{ marginTop: "30px", borderTop: "1px solid #e2e8f0", paddingTop: "20px" }}>
                        <h3 style={{ fontSize: "16px", color: "#334155", marginBottom: "15px" }}>
                            Sistemdeki İlaçlar ({medicines.length})
                        </h3>

                        {medicines.length === 0 ? (
                            <p style={{ color: "#94a3b8", fontStyle: "italic" }}>Henüz kayıtlı ilaç yok.</p>
                        ) : (
                            <div style={{ maxHeight: "400px", overflowY: "auto", border: "1px solid #e2e8f0", borderRadius: "8px" }}>
                                <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "14px" }}>
                                    <thead style={{ background: "#f8fafc", position: "sticky", top: 0, zIndex: 1 }}>
                                    <tr>
                                        <th style={{ padding: "12px", textAlign: "left", color: "#64748b", borderBottom: "1px solid #e2e8f0" }}>İlaç Adı</th>
                                        <th style={{ padding: "12px", textAlign: "left", color: "#64748b", borderBottom: "1px solid #e2e8f0" }}>Türü</th>
                                        <th style={{ padding: "12px", textAlign: "right", color: "#64748b", borderBottom: "1px solid #e2e8f0" }}>İşlem</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {medicines.map((med) => (
                                        <tr key={med.medicineId} style={{ borderBottom: "1px solid #f1f5f9" }}>
                                            <td style={{ padding: "12px", color: "#334155", fontWeight: "600" }}>{med.name}</td>
                                            <td style={{ padding: "12px", color: "#64748b" }}>
                                                    <span style={{ background: "#f1f5f9", padding: "4px 8px", borderRadius: "12px", fontSize: "12px" }}>
                                                        {med.type}
                                                    </span>
                                            </td>
                                            <td style={{ padding: "12px", textAlign: "right" }}>
                                                {/* Küçük SİL Butonu */}
                                                <button
                                                    onClick={() => handleDelete(med.medicineId)}
                                                    style={{
                                                        background: "#fee2e2",
                                                        color: "#ef4444",
                                                        border: "1px solid #fecaca",
                                                        padding: "6px 12px",
                                                        borderRadius: "6px",
                                                        cursor: "pointer",
                                                        fontWeight: "600",
                                                        fontSize: "12px",
                                                        transition: "0.2s"
                                                    }}
                                                    onMouseEnter={(e) => e.target.style.background = "#fecaca"}
                                                    onMouseLeave={(e) => e.target.style.background = "#fee2e2"}
                                                >
                                                    🗑️ Sil
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}