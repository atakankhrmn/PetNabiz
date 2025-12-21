import { useState } from "react";
import ClinicApplicationsAdmin from "./ClinicApplicationsAdmin";
import UsersAdmin from "./UsersAdmin";
import AllPetsAdmin from "./AllPetsAdmin"; // <-- YENİ IMPORT

export default function AdminPanel() {
    const [tab, setTab] = useState("applications"); // applications | users | pets

    // Basit Tab Buton Stili
    const getBtnStyle = (active) => ({
        padding: "10px 20px",
        borderRadius: "8px",
        border: "none",
        cursor: "pointer",
        background: active ? "#3b82f6" : "#e2e8f0",
        color: active ? "white" : "#64748b",
        fontWeight: "600",
        fontSize: "14px",
        transition: "0.2s"
    });

    return (
        <div>
            <div style={{ marginBottom: "25px" }}>
                <h2 style={{ fontSize: "24px", color: "#1e293b", margin: 0 }}>⚙️ Sistem Yönetimi</h2>
                <p style={{ color: "#64748b", fontSize: "14px", marginTop: "5px" }}>Kullanıcılar, başvurular ve pet kayıtlarını buradan yönetebilirsiniz.</p>
            </div>

            {/* TAB MENU */}
            <div style={{ display: "flex", gap: 10, marginBottom: 20, borderBottom: "1px solid #e2e8f0", paddingBottom: "15px" }}>
                <button
                    onClick={() => setTab("applications")}
                    style={getBtnStyle(tab === "applications")}
                >
                    🏥 Klinik Başvuruları
                </button>

                <button
                    onClick={() => setTab("users")}
                    style={getBtnStyle(tab === "users")}
                >
                    👥 Kullanıcılar
                </button>

                <button
                    onClick={() => setTab("pets")}
                    style={getBtnStyle(tab === "pets")}
                >
                    🐾 Tüm Petler
                </button>
            </div>

            {/* TAB İÇERİKLERİ */}
            <div style={{ animation: "fadeIn 0.3s" }}>
                {tab === "applications" && <ClinicApplicationsAdmin />}
                {tab === "users" && <UsersAdmin />}
                {tab === "pets" && <AllPetsAdmin />} {/* <-- YENİ EKLENDİ */}
            </div>
        </div>
    );
}