import { useState } from "react";
import AdminPanel from "./admin/AdminPanel";
import Pets from "./owner/Pets";
import Veterinaries from "./clinic/Veterinaries";

export default function Dashboard({ me, onLogout }) {
    const role = me?.role;
    const email = me?.email;

    const menu = getMenu(role);
    const [page, setPage] = useState(menu[0]?.key || "home");

    return (
        <div style={{ fontFamily: "Arial", minHeight: "100vh", background: "#f6f7fb" }}>
            <div style={{ background: "white", padding: "12px 18px", borderBottom: "1px solid #ddd", display: "flex", justifyContent: "space-between" }}>
                <div style={{ fontWeight: 700 }}>PetNabiz Dashboard</div>
                <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
                    <div style={{ color: "#444" }}>{email} <span style={{ color: "#888" }}>({role})</span></div>
                    <button onClick={onLogout}>Logout</button>
                </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "240px 1fr" }}>
                <div style={{ background: "white", borderRight: "1px solid #ddd", padding: 12 }}>
                    <div style={{ fontWeight: 700, marginBottom: 10 }}>Menu</div>
                    {menu.map((m) => (
                        <button
                            key={m.key}
                            onClick={() => setPage(m.key)}
                            style={{
                                width: "100%",
                                textAlign: "left",
                                padding: "10px 12px",
                                marginBottom: 8,
                                border: "1px solid #ddd",
                                background: page === m.key ? "#eef2ff" : "white",
                                cursor: "pointer",
                            }}
                        >
                            {m.label}
                        </button>
                    ))}
                </div>

                <div style={{ padding: 18 }}>
                    <div style={{ background: "white", padding: 16, borderRadius: 10, border: "1px solid #ddd", minHeight: 500 }}>
                        {renderPage(page, role)}
                    </div>
                </div>
            </div>
        </div>
    );
}

function getMenu(role) {
    if (role === "ROLE_ADMIN") return [{ key: "admin", label: "Admin Panel" }];
    if (role === "ROLE_CLINIC") return [{ key: "clinic_vets", label: "My Veterinaries" }];
    if (role === "ROLE_OWNER") return [{ key: "owner_pets", label: "My Pets" }];
    return [{ key: "home", label: "Home" }];
}

function renderPage(page, role) {
    if (role === "ROLE_ADMIN" && page === "admin") return <AdminPanel />;
    if (role === "ROLE_CLINIC" && page === "clinic_vets") return <Veterinaries />;
    if (role === "ROLE_OWNER" && page === "owner_pets") return <Pets />;
    return <div>Sayfa bulunamadı.</div>;
}
