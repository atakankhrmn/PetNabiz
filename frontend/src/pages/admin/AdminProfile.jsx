import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function AdminProfile({ me }) {
    const [adminData, setAdminData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Admin verilerini çekmek için varsayılan me endpointini veya
        // AdminController üzerindeki özel bir GET metodunu kullanabilirsin.
        http.get("/api/users/me")
            .then(res => setAdminData(res.data))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div style={loadingStyle}>Admin verileri yükleniyor...</div>;

    return (
        <div style={{ maxWidth: "600px" }}>
            <div style={profileCardStyle}>
                <div style={headerStyle}>
                    <div style={badgeStyle}>SİSTEM YÖNETİCİSİ</div>
                    <h3 style={{ margin: "10px 0" }}>{adminData?.name || "Yönetici"}</h3>
                </div>

                <div style={infoGridStyle}>
                    <div style={infoItemStyle}>
                        <label style={labelStyle}>ADMIN ID</label>
                        <div style={valueStyle}>{adminData?.adminId || adminData?.userId}</div>
                    </div>
                    <div style={infoItemStyle}>
                        <label style={labelStyle}>E-POSTA</label>
                        <div style={valueStyle}>{adminData?.email}</div>
                    </div>
                    <div style={infoItemStyle}>
                        <label style={labelStyle}>HESAP DURUMU</label>
                        <div style={{...valueStyle, color: adminData?.active ? "#166534" : "#991b1b"}}>
                            {adminData?.active ? "● Aktif" : "● Pasif"}
                        </div>
                    </div>
                </div>

                <div style={footerNoticeStyle}>
                    ⚠️ Tüm sistem yetkilerine sahipsiniz. Kullanıcı yönetimi ve klinik onaylarını Admin Panel sekmesinden yapabilirsiniz.
                </div>
            </div>
        </div>
    );
}

// Admin Özel Stiller
const profileCardStyle = { background: "white", borderRadius: "15px", border: "1px solid #e2e8f0", padding: "25px" };
const headerStyle = { borderBottom: "1px solid #f1f5f9", marginBottom: "20px", paddingBottom: "10px" };
const badgeStyle = { background: "#1e293b", color: "white", padding: "4px 10px", borderRadius: "6px", fontSize: "11px", fontWeight: "800" };
const infoGridStyle = { display: "grid", gap: "20px" };
const infoItemStyle = { display: "flex", flexDirection: "column" };
const labelStyle = { fontSize: "11px", color: "#94a3b8", fontWeight: "800", marginBottom: "4px" };
const valueStyle = { fontSize: "15px", color: "#1e293b", fontWeight: "600" };
const footerNoticeStyle = { marginTop: "25px", padding: "15px", background: "#f8fafc", borderRadius: "10px", fontSize: "13px", color: "#475569", border: "1px solid #e2e8f0" };
const loadingStyle = { padding: "20px", color: "#64748b" };