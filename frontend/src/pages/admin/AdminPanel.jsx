import { useState } from "react";
import ClinicApplicationsAdmin from "./ClinicApplicationsAdmin";
import UsersAdmin from "./UsersAdmin";

export default function AdminPanel() {
    const [tab, setTab] = useState("applications"); // applications | users

    return (
        <div>
            <h2>Admin Panel</h2>

            <div style={{ display: "flex", gap: 10, marginBottom: 16 }}>
                <button onClick={() => setTab("applications")}>Clinic Applications</button>
                <button onClick={() => setTab("users")}>Users</button>
            </div>

            {tab === "applications" && <ClinicApplicationsAdmin />}
            {tab === "users" && <UsersAdmin />}
        </div>
    );
}
