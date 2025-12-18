import { useState } from "react";
import AdminPanel from "./admin/AdminPanel";
import Pets from "./owner/Pets";
import Veterinaries from "./clinic/Veterinaries";

export default function Dashboard({ me, onLogout }) {
    const role = me?.role || "UNKNOWN";
    const menu = getMenu(role);
    const [page, setPage] = useState(menu[0]?.key || "home");

    // ROLE_ kısmını temizleyen ve ismi formatlayan yardımcılar
    const cleanRole = role.replace("ROLE_", "");
    const fullName = me?.firstName ? `${me.firstName} ${me.lastName || ""}` : me?.email;

    return (
        <div style={{ minHeight: "100vh", background: "#f8fafc" }}>
            {/* Topbar */}
            <div style={topbarStyle}>
                <div style={{ fontWeight: 900, fontSize: 20, display: "flex", alignItems: "center", gap: 8 }}>
                    <span style={{color: "#fff"}}>✚</span> PetNabiz
                </div>
                <div style={{ display: "flex", gap: 15, alignItems: "center" }}>
                    <div style={{ textAlign: "right" }}>
                        {/* Email yerine Hoş geldin + İsim Soyisim */}
                        <div style={{ fontSize: 13, fontWeight: 700 }}>Hoş geldin, {fullName}</div>
                        {/* ROLE_ temizlenmiş hali */}
                        <div style={{ fontSize: 11, opacity: 0.8, fontWeight: 800 }}>{cleanRole}</div>
                    </div>
                    <button onClick={onLogout} style={logoutBtn}>Güvenli Çıkış</button>
                </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "260px 1fr" }}>
                {/* Sidebar */}
                <div style={sidebarStyle}>
                    <div style={{ padding: "20px 15px", color: "#64748b", fontSize: 12, fontWeight: 800, textTransform: "uppercase" }}>Yönetim Menüsü</div>
                    {menu.map((m) => (
                        <button key={m.key} onClick={() => setPage(m.key)} style={page === m.key ? activeMenuBtn : menuBtn}>
                            {m.label}
                        </button>
                    ))}
                </div>

                {/* Main Content */}
                <div style={{ padding: 30 }}>
                    <div style={contentCardStyle}>
                        <h2 style={{ marginTop: 0, color: "#1e293b", fontSize: 18 }}>{menu.find(x => x.key === page)?.label}</h2>
                        <hr style={{ border: "0.5px solid #f1f5f9", margin: "15px 0" }} />
                        {/* me objesini renderPage fonksiyonuna iletiyoruz */}
                        {renderPage(page, role, me)}
                    </div>
                </div>
            </div>
        </div>
    );
}

function getMenu(role) {
    if (role === "ROLE_ADMIN") return [{ key: "admin", label: "Sistem Yönetimi" }];
    if (role === "ROLE_CLINIC") return [{ key: "clinic_vets", label: "Veteriner Hekimler" }];
    if (role === "ROLE_OWNER") return [{ key: "owner_pets", label: "Can Dostlarım" }];
    return [{ key: "home", label: "Ana Sayfa" }];
}

// me parametresini ekledik ki Pets bileşenine geçebilelim
function renderPage(page, role, me) {
    if (role === "ROLE_ADMIN" && page === "admin") return <AdminPanel />;
    if (role === "ROLE_CLINIC" && page === "clinic_vets") return <Veterinaries />;
    if (role === "ROLE_OWNER" && page === "owner_pets") return <Pets me={me} />;
    return <div>Sayfa Hazırlanıyor...</div>;
}

const topbarStyle = { height: 65, display: "flex", alignItems: "center", justifyContent: "space-between", padding: "0 25px", background: "#0284c7", color: "white", boxShadow: "0 2px 10px rgba(0,0,0,0.1)" };
const logoutBtn = { padding: "8px 15px", borderRadius: 8, border: "1px solid rgba(255,255,255,0.3)", background: "rgba(255,255,255,0.1)", color: "white", fontWeight: 700, cursor: "pointer" };
const sidebarStyle = { background: "white", borderRight: "1px solid #e2e8f0", minHeight: "calc(100vh - 65px)", padding: 10 };
const menuBtn = { width: "100%", textAlign: "left", padding: "12px 15px", borderRadius: 10, border: "none", background: "transparent", color: "#64748b", cursor: "pointer", fontWeight: 600, marginBottom: 5 };
const activeMenuBtn = { ...menuBtn, background: "#f0f9ff", color: "#0284c7" };
const contentCardStyle = { background: "white", borderRadius: 15, padding: 25, boxShadow: "0 1px 3px rgba(0,0,0,0.05)", border: "1px solid #e2e8f0" };