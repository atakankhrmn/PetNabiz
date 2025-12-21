import { useEffect, useState } from "react";
import { http } from "../../api/http";

// --- STİLLER ---
const containerStyle = { padding: "0 20px 20px 0" }; // Tab içinde olduğu için biraz padding ayarı
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "20px", border: "1px solid #e2e8f0", marginTop: "20px" };
const tableHeaderStyle = { padding: "12px", textAlign: "left", color: "#64748b", borderBottom: "1px solid #e2e8f0", fontSize: "13px", fontWeight: "700", textTransform: "uppercase", letterSpacing: "0.5px" };
const tableCellStyle = { padding: "12px", borderBottom: "1px solid #f1f5f9", fontSize: "14px", color: "#334155", verticalAlign: "middle" };
const headerGroupStyle = { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "10px" };

// Buton Helper
const ActionButton = ({ onClick, color, bg, border, children }) => (
    <button
        onClick={onClick}
        style={{
            background: bg, color: color, border: `1px solid ${border}`,
            padding: "6px 12px", borderRadius: "6px", cursor: "pointer",
            fontSize: "12px", fontWeight: "600", transition: "0.2s"
        }}
    >
        {children}
    </button>
);

export default function UsersAdmin() {
    const [items, setItems] = useState([]);
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);

    async function load() {
        setErr("");
        setLoading(true);
        try {
            const res = await http.get("/api/users");
            setItems(res.data || []);
        } catch {
            setErr("Kullanıcı listesi yüklenemedi.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => { load(); }, []);

    async function setActive(userId, active) {
        setErr("");
        try {
            await http.put(`/api/users/${userId}/status`, null, { params: { active } });
            await load();
        } catch {
            setErr("Durum güncellemesi başarısız.");
        }
    }

    async function del(userId) {
        setErr("");
        if (!confirm("Bu kullanıcıyı silmek istediğinize emin misiniz?")) return;
        try {
            await http.delete(`/api/users/${userId}`);
            await load();
        } catch {
            setErr("Silme işlemi başarısız.");
        }
    }

    return (
        <div style={containerStyle}>
            {/* Üst Başlık ve Refresh Butonu */}
            <div style={headerGroupStyle}>
                <div>
                    <h3 style={{ fontSize: "18px", color: "#1e293b", margin: 0 }}>👥 Kullanıcı Listesi</h3>
                    <p style={{ fontSize: "13px", color: "#64748b", margin: "4px 0 0 0" }}>Sistemdeki tüm kullanıcıların rolleri ve durumları.</p>
                </div>
                <button
                    onClick={load}
                    style={{ background: "#f1f5f9", color: "#475569", border: "1px solid #cbd5e1", padding: "8px 16px", borderRadius: "8px", cursor: "pointer", fontWeight: "600", fontSize: "13px" }}
                >
                    🔄 Yenile
                </button>
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
                    <div style={{ textAlign: "center", padding: "40px", color: "#94a3b8" }}>Kayıtlı kullanıcı bulunamadı.</div>
                ) : (
                    <table style={{ width: "100%", borderCollapse: "collapse" }}>
                        <thead>
                        <tr style={{ background: "#f8fafc" }}>
                            <th style={tableHeaderStyle}>Email</th>
                            <th style={tableHeaderStyle}>Rol</th>
                            <th style={tableHeaderStyle}>Durum</th>
                            <th style={{ ...tableHeaderStyle, textAlign: "right" }}>İşlemler</th>
                        </tr>
                        </thead>
                        <tbody>
                        {items.map((u) => (
                            <tr key={u.userId} style={{ transition: "0.2s" }}>

                                {/* Email */}
                                <td style={tableCellStyle}>
                                    <div style={{ fontWeight: "600" }}>{u.email}</div>
                                    <div style={{ fontSize: "11px", color: "#94a3b8", fontFamily: "monospace" }}>ID: {u.userId}</div>
                                </td>

                                {/* Rol */}
                                <td style={tableCellStyle}>
                                        <span style={{
                                            background: u.role === 'ROLE_ADMIN' ? '#e0e7ff' : '#f1f5f9',
                                            color: u.role === 'ROLE_ADMIN' ? '#4338ca' : '#475569',
                                            padding: "4px 8px", borderRadius: "6px", fontSize: "12px", fontWeight: "bold"
                                        }}>
                                            {u.role.replace('ROLE_', '')}
                                        </span>
                                </td>

                                {/* Aktif/Pasif Durumu */}
                                <td style={tableCellStyle}>
                                    {u.active ? (
                                        <span style={{ background: "#dcfce7", color: "#166534", padding: "4px 10px", borderRadius: "12px", fontSize: "12px", fontWeight: "bold", display: "inline-flex", alignItems: "center", gap: "5px" }}>
                                                <span style={{width:6, height:6, borderRadius:"50%", background:"#166534"}}></span> Aktif
                                            </span>
                                    ) : (
                                        <span style={{ background: "#fee2e2", color: "#991b1b", padding: "4px 10px", borderRadius: "12px", fontSize: "12px", fontWeight: "bold", display: "inline-flex", alignItems: "center", gap: "5px" }}>
                                                <span style={{width:6, height:6, borderRadius:"50%", background:"#991b1b"}}></span> Pasif
                                            </span>
                                    )}
                                </td>

                                {/* Aksiyon Butonları */}
                                <td style={{ ...tableCellStyle, textAlign: "right" }}>
                                    <div style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
                                        {!u.active ? (
                                            <ActionButton onClick={() => setActive(u.userId, true)} bg="#ecfdf5" color="#059669" border="#a7f3d0">
                                                ✅ Aktifleştir
                                            </ActionButton>
                                        ) : (
                                            <ActionButton onClick={() => setActive(u.userId, false)} bg="#fff1f2" color="#be123c" border="#fecdd3">
                                                ⛔ Pasife Al
                                            </ActionButton>
                                        )}

                                        <ActionButton onClick={() => del(u.userId)} bg="white" color="#ef4444" border="#fca5a5">
                                            🗑️ Sil
                                        </ActionButton>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}