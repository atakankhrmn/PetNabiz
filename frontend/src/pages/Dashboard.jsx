import { useState, useEffect } from "react";
import AdminPanel from "./admin/AdminPanel";
import Pets from "./owner/Pets";
import Veterinaries from "./clinic/Veterinaries";
import SlotManager from "./clinic/SlotManager";
import AdminProfile from "./admin/AdminProfile";
import ClinicProfile from "./clinic/ClinicProfile.jsx";
import OwnerProfile from "./owner/OwnerProfile";
import { http } from "../api/http";
import BookAppointment from "./owner/BookAppointment";
import MyAppointments from "./owner/MyAppointments";
import WeeklyAppointments from "./clinic/WeeklyAppointments";
import AppointmentManagement from "./clinic/AppointmentManagement";
import AddMedicalRecord from "./clinic/AddMedicalRecord";


export default function Dashboard({ me, onLogout }) {
    const [ownerInfo, setOwnerInfo] = useState(null);
    const [isDrawerOpen, setIsDrawerOpen] = useState(true);
    const role = me?.role || "UNKNOWN";
    const menu = getMenu(role);
    // Eğer menü boş gelirse veya sayfa yenilenirse default olarak ilk elemanı seç
    const [page, setPage] = useState(menu[0]?.key || "profile");

    useEffect(() => {
        // Sadece Owner rolü için ek detayları çekiyoruz
        if (role === "ROLE_OWNER") {
            http.get("/api/pet-owners/me")
                .then(res => setOwnerInfo(res.data))
                .catch(err => console.error("Profil bilgisi alınamadı", err));
        }
    }, [role]);

    // Sağ üstte görünecek isim ve temizlenmiş rol gösterimi
    const displayUserName = ownerInfo ? `${ownerInfo.firstName} ${ownerInfo.lastName}` : me?.email;
    const cleanRole = role.replace("ROLE_", "");

    return (
        <div style={{ minHeight: "100vh", background: "#f8fafc", display: "flex", flexDirection: "column" }}>
            {/* TOPBAR */}
            <div style={topbarStyle}>
                <div style={{ display: "flex", alignItems: "center", gap: 15 }}>
                    <button onClick={() => setIsDrawerOpen(!isDrawerOpen)} style={hamburgerBtnStyle}>
                        {isDrawerOpen ? "◀" : "☰"}
                    </button>
                    <div style={{ fontWeight: 900, fontSize: 22, display: "flex", alignItems: "center", gap: 10 }}>
                        <span style={{ background: "white", color: "#0284c7", padding: "2px 8px", borderRadius: "8px" }}>✚</span>
                        PetNabiz
                    </div>
                </div>

                <div style={{ display: "flex", gap: 20, alignItems: "center" }}>
                    <div style={{ textAlign: "right" }}>
                        <div style={{ fontSize: 14, fontWeight: 700, color: "white" }}>{displayUserName}</div>
                        <div style={{ fontSize: 11, opacity: 0.9, color: "#e0f2fe", fontWeight: 800 }}>{cleanRole}</div>
                    </div>
                    <button onClick={onLogout} style={logoutBtn}>Güvenli Çıkış</button>
                </div>
            </div>

            <div style={{ display: "flex", flex: 1, position: "relative", overflow: "hidden" }}>

                {/* DİNAMİK SIDE DRAWER */}
                <div
                    style={{
                        ...drawerContentStyle,
                        width: isDrawerOpen ? "280px" : "0px",
                        opacity: isDrawerOpen ? 1 : 0,
                        visibility: isDrawerOpen ? "visible" : "hidden",
                        borderRight: isDrawerOpen ? "1px solid #e2e8f0" : "none"
                    }}
                >
                    <div style={{ padding: "20px", width: "280px" }}>
                        <div style={sidebarLabel}>Yönetim Menüsü</div>
                        <div style={{ marginTop: "10px" }}>
                            {menu.map((m) => (
                                <button
                                    key={m.key}
                                    onClick={() => setPage(m.key)}
                                    style={page === m.key ? activeMenuBtn : menuBtn}
                                >
                                    {m.label}
                                </button>
                            ))}
                            <div style={{ margin: "20px 10px", borderTop: "1px solid #f1f5f9" }}></div>

                            {/* Ortak Profil Butonu */}
                            <button
                                onClick={() => setPage("profile")}
                                style={page === "profile" ? activeMenuBtn : menuBtn}
                            >
                                👤 Profil Ayarlarım
                            </button>
                        </div>
                    </div>
                </div>

                {/* ANA İÇERİK ALANI */}
                <div style={{ flex: 1, padding: "30px", transition: "all 0.3s ease-in-out", overflowY: "auto", maxHeight: "calc(100vh - 70px)" }}>
                    <div style={contentCardStyle}>
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
                            <h2 style={{ margin: 0, color: "#1e293b", fontSize: 20 }}>
                                {page === "profile" ? "Hesap ve Güvenlik" : menu.find(x => x.key === page)?.label}
                            </h2>
                            <div style={breadcrumbStyle}>Dashboard / {page === "profile" ? "Profil" : "Yönetim"}</div>
                        </div>
                        <hr style={{ border: "0.5px solid #f1f5f9", marginBottom: 25 }} />

                        {/* Sayfa İçeriği Render */}
                        {renderPage(page, role, me, ownerInfo, setOwnerInfo)}
                    </div>
                </div>
            </div>
        </div>
    );
}

// Menü Elemanları Tanımı
function getMenu(role) {
    switch(role) {
        case "ROLE_ADMIN": return [{ key: "admin", label: "⚙️ Sistem Yönetimi" }];

        case "ROLE_CLINIC": return [
            { key: "clinic_vets", label: "👨‍⚕️ Veteriner Hekimler" },
            { key: "clinic_slots", label: "📅 Randevu Takvimi" },
            { key: "weekly_appts", label: "🗓️ Bu Haftaki Randevular" },
            { key: "history", label: "📂 Randevu Yönetimi" },
            { key: "add_record", label: "📝 Muayene Kaydı Oluştur" }
        ];

        case "ROLE_OWNER": return [
            { key: "owner_pets", label: "🐾 Can Dostlarım" },
            { key: "book_appointment", label: "🗓️ Randevu Al" },
            { key: "my_appointments", label: "📋 Randevularım" }
        ];
        default: return [];
    }
}

// MODÜLER RENDER MANTIĞI
function renderPage(page, role, me, ownerInfo, setOwnerInfo) {
    // 1. Profil Sayfaları
    if (page === "profile") {
        if (role === "ROLE_ADMIN") return <AdminProfile me={me} />;
        if (role === "ROLE_CLINIC") return <ClinicProfile me={me} />;
        if (role === "ROLE_OWNER") return <OwnerProfile me={me} ownerInfo={ownerInfo} setOwnerInfo={setOwnerInfo} />;
    }

    // 2. Fonksiyonel Sayfalar
    if (role === "ROLE_ADMIN" && page === "admin") return <AdminPanel />;

    // <-- 3. CLINIC SAYFALARI RENDER MANTIĞI
    if (role === "ROLE_CLINIC") {
        if (page === "clinic_vets") return <Veterinaries />;
        if (page === "clinic_slots") return <SlotManager />;
        if (page === "weekly_appts") return <WeeklyAppointments/>
        if (page === "history") return <AppointmentManagement/>
        if (page === "add_record") return <AddMedicalRecord/>;
    }

    if (role === "ROLE_OWNER") {
        if (page === "owner_pets") return <Pets me={me} />;
        if (page === "book_appointment") return <BookAppointment me={me} />;
        if (page === "my_appointments") return <MyAppointments me={me} />;
    }

    return <div style={{ textAlign: "center", padding: 50, color: "#64748b" }}>Sayfa Yükleniyor...</div>;
}

// --- TASARIM STİLLERİ ---
const topbarStyle = {
    position: "sticky",
    top: 0,
    height: 70,
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "0 30px",
    background: "linear-gradient(90deg, #0284c7 0%, #0369a1 100%)",
    color: "white",
    boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
    zIndex: 1200
};

const hamburgerBtnStyle = {
    background: "rgba(255,255,255,0.2)", border: "none", color: "white", fontSize: "18px",
    cursor: "pointer", width: "40px", height: "40px", borderRadius: "10px",
    display: "flex", alignItems: "center", justifyContent: "center", transition: "0.2s"
};

const drawerContentStyle = {
    position: "sticky",
    top: 0,
    height: "calc(100vh - 70px)",
    background: "white",
    transition: "all 0.3s ease-in-out",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column"
};

const logoutBtn = { padding: "8px 18px", borderRadius: 10, border: "1px solid rgba(255,255,255,0.4)", background: "rgba(255,255,255,0.1)", color: "white", fontWeight: 700, cursor: "pointer" };
const sidebarLabel = { color: "#94a3b8", fontSize: 12, fontWeight: 800, textTransform: "uppercase", letterSpacing: "0.5px" };
const menuBtn = { width: "100%", textAlign: "left", padding: "14px 20px", borderRadius: 12, border: "none", background: "transparent", color: "#64748b", cursor: "pointer", fontWeight: 600, marginBottom: 5, fontSize: "15px" };
const activeMenuBtn = { ...menuBtn, background: "#eff6ff", color: "#0284c7" };
const contentCardStyle = { background: "white", borderRadius: 20, padding: "30px", boxShadow: "0 4px 20px rgba(0,0,0,0.03)", border: "1px solid #e2e8f0", minHeight: "75vh" };
const breadcrumbStyle = { fontSize: "12px", color: "#94a3b8", fontWeight: 500 };