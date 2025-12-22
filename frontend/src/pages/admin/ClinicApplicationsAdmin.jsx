import { useEffect, useState } from "react";
import { http } from "../../api/http";

// --- AYARLAR ---
// Backend'de dosyaları sunduğun klasör yolu (Controller'daki yapıya göre değişebilir)
const BASE_CERT_URL = "http://localhost:8080/";

// --- STİLLER ---
const containerStyle = { padding: "0 20px 20px 0" };
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "20px", border: "1px solid #e2e8f0", marginTop: "20px" };
const tableHeaderStyle = { padding: "12px", textAlign: "left", color: "#64748b", borderBottom: "1px solid #e2e8f0", fontSize: "13px", fontWeight: "700", textTransform: "uppercase", letterSpacing: "0.5px" };
const tableCellStyle = { padding: "12px", borderBottom: "1px solid #f1f5f9", fontSize: "14px", color: "#334155", verticalAlign: "middle" };
const headerGroupStyle = { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" };

// Buton Helper
const ActionButton = ({ onClick, color, bg, border, children, disabled }) => (
    <button
        onClick={disabled ? undefined : onClick}
        disabled={disabled}
        style={{
          background: bg, color: color, border: `1px solid ${border}`,
          padding: "6px 12px", borderRadius: "6px", cursor: disabled ? "not-allowed" : "pointer",
          fontSize: "12px", fontWeight: "600", transition: "0.2s", opacity: disabled ? 0.5 : 1
        }}
    >
      {children}
    </button>
);

// Tab Butonu
const TabButton = ({ active, onClick, label, count }) => (
    <button
        onClick={onClick}
        style={{
          padding: "8px 16px",
          background: active ? "#3b82f6" : "transparent",
          color: active ? "white" : "#64748b",
          borderRadius: "20px",
          border: active ? "none" : "1px solid #e2e8f0",
          cursor: "pointer",
          fontWeight: "600",
          fontSize: "13px",
          transition: "0.2s"
        }}
    >
      {label} {count !== undefined && <span style={{ opacity: 0.8, fontSize: "11px", marginLeft: "4px" }}>({count})</span>}
    </button>
);

export default function ClinicApplicationsAdmin() {
  const [status, setStatus] = useState("PENDING"); // PENDING | APPROVED | REJECTED
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function load() {
    setErr("");
    setLoading(true);
    try {
      const res = await http.get("/api/clinic-applications", { params: { status } });
      setItems(res.data || []);
    } catch {
      setErr("Başvurular yüklenemedi.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, [status]);

  async function approve(id) {
    if (!confirm("Bu kliniği onaylamak istiyor musunuz?")) return;
    setErr("");
    try {
      await http.post(`/api/clinic-applications/${id}/approve`);
      await load(); // Listeyi yenile
    } catch {
      setErr("Onaylama işlemi başarısız.");
    }
  }

  async function reject(id) {
    if (!confirm("Bu başvuruyu reddetmek istiyor musunuz?")) return;
    setErr("");
    try {
      await http.post(`/api/clinic-applications/${id}/reject`);
      await load(); // Listeyi yenile
    } catch {
      setErr("Reddetme işlemi başarısız.");
    }
  }

  // Sertifikayı yeni sekmede açar
  const viewCertificate = (filename) => {
    if (!filename) return alert("Dosya bulunamadı.");
    window.open(`${BASE_CERT_URL}${filename}`, "_blank");
  };

  return (
      <div style={containerStyle}>

        {/* Header */}
        <div style={headerGroupStyle}>
          <div>
            <h3 style={{ fontSize: "18px", color: "#1e293b", margin: 0 }}>🏥 Klinik Başvuruları</h3>
            <p style={{ fontSize: "13px", color: "#64748b", margin: "4px 0 0 0" }}>Yeni klinik kayıt isteklerini inceleyin ve onaylayın.</p>
          </div>
          <button
              onClick={load}
              style={{ background: "#f1f5f9", color: "#475569", border: "1px solid #cbd5e1", padding: "8px 16px", borderRadius: "8px", cursor: "pointer", fontWeight: "600", fontSize: "13px" }}
          >
            🔄 Yenile
          </button>
        </div>

        {/* TABLAR (Status Seçimi) */}
        <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
          <TabButton active={status === "PENDING"} onClick={() => setStatus("PENDING")} label="⏳ Bekleyenler" />
          <TabButton active={status === "APPROVED"} onClick={() => setStatus("APPROVED")} label="✅ Onaylananlar" />
          <TabButton active={status === "REJECTED"} onClick={() => setStatus("REJECTED")} label="❌ Reddedilenler" />
        </div>

        {/* Hata Mesajı */}
        {err && (
            <div style={{ background: "#fee2e2", color: "#ef4444", padding: "10px", borderRadius: "8px", marginBottom: "15px", fontSize: "14px", border: "1px solid #fecaca" }}>
              ⚠️ {err}
            </div>
        )}

        {/* Tablo Kartı */}
        <div style={cardStyle}>
          {loading ? (
              <div style={{ textAlign: "center", padding: "40px", color: "#94a3b8" }}>Yükleniyor...</div>
          ) : items.length === 0 ? (
              <div style={{ textAlign: "center", padding: "40px", color: "#94a3b8" }}>
                {status === "PENDING" ? "Bekleyen başvuru yok." : "Kayıt bulunamadı."}
              </div>
          ) : (
              <div style={{ overflowX: "auto" }}>
                <table style={{ width: "100%", borderCollapse: "collapse" }}>
                  <thead>
                  <tr style={{ background: "#f8fafc" }}>
                    <th style={tableHeaderStyle}>Klinik Adı</th>
                    <th style={tableHeaderStyle}>İletişim</th>
                    <th style={tableHeaderStyle}>Konum</th>
                    <th style={tableHeaderStyle}>Durum</th>
                    <th style={tableHeaderStyle}>Belge</th>
                    <th style={{ ...tableHeaderStyle, textAlign: "right" }}>İşlemler</th>
                  </tr>
                  </thead>
                  <tbody>
                  {items.map((x) => (
                      <tr key={x.id} style={{ transition: "0.2s" }}>

                        {/* Klinik Bilgisi */}
                        <td style={tableCellStyle}>
                          <div style={{ fontWeight: "600", color: "#1e293b" }}>{x.clinicName}</div>
                          <div style={{ fontSize: "11px", color: "#94a3b8" }}>ID: {x.id}</div>
                        </td>

                        {/* İletişim */}
                        <td style={tableCellStyle}>
                          <div style={{ fontSize: "13px" }}>📧 {x.email}</div>
                          <div style={{ fontSize: "13px", marginTop: "2px" }}>📞 {x.phone}</div>
                        </td>

                        {/* Konum */}
                        <td style={tableCellStyle}>
                                            <span style={{ background: "#f1f5f9", padding: "4px 8px", borderRadius: "6px", fontSize: "12px", fontWeight: "600", color: "#475569" }}>
                                                {x.city} / {x.district}
                                            </span>
                          <div style={{ fontSize: "12px", color: "#64748b", marginTop: "4px", maxWidth: "200px", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }} title={x.address}>
                            {x.address}
                          </div>
                        </td>

                        {/* Durum Badge */}
                        <td style={tableCellStyle}>
                          {x.status === "PENDING" && <span style={{ background: "#fef9c3", color: "#854d0e", padding: "4px 10px", borderRadius: "12px", fontSize: "11px", fontWeight: "bold", border:"1px solid #fde047" }}>BEKLİYOR</span>}
                          {x.status === "APPROVED" && <span style={{ background: "#dcfce7", color: "#166534", padding: "4px 10px", borderRadius: "12px", fontSize: "11px", fontWeight: "bold", border:"1px solid #86efac" }}>ONAYLANDI</span>}
                          {x.status === "REJECTED" && <span style={{ background: "#fee2e2", color: "#991b1b", padding: "4px 10px", borderRadius: "12px", fontSize: "11px", fontWeight: "bold", border:"1px solid #fca5a5" }}>REDDEDİLDİ</span>}
                        </td>

                        {/* Sertifika Görüntüleme */}
                        <td style={tableCellStyle}>
                          {x.documentPath ? (
                              <button
                                  onClick={() => viewCertificate(x.documentPath)}
                                  style={{ display: "flex", alignItems: "center", gap: "5px", background: "white", border: "1px solid #cbd5e1", padding: "6px 10px", borderRadius: "6px", cursor: "pointer", fontSize: "12px", color: "#334155", fontWeight: "600" }}
                              >
                                📄 Görüntüle
                              </button>
                          ) : (
                              <span style={{ color: "#94a3b8", fontSize: "12px", fontStyle: "italic" }}>Belge Yok</span>
                          )}
                        </td>

                        {/* İşlemler (Sadece PENDING ise aktif) */}
                        <td style={{ ...tableCellStyle, textAlign: "right" }}>
                          {status === "PENDING" && (
                              <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
                                <ActionButton onClick={() => approve(x.id)} bg="#ecfdf5" color="#059669" border="#a7f3d0">
                                  ✅ Onayla
                                </ActionButton>

                                <ActionButton onClick={() => reject(x.id)} bg="#fff1f2" color="#be123c" border="#fecdd3">
                                  🚫 Reddet
                                </ActionButton>
                              </div>
                          )}
                        </td>
                      </tr>
                  ))}
                  </tbody>
                </table>
              </div>
          )}
        </div>
      </div>
  );
}