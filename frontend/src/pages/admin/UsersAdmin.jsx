import { useEffect, useState } from "react";
import { http } from "../../api/http";

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
            setErr("Users yüklenemedi.");
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
            setErr("Status update başarısız.");
        }
    }

    async function del(userId) {
        setErr("");
        if (!confirm("Silmek istediğine emin misin?")) return;
        try {
            await http.delete(`/api/users/${userId}`);
            await load();
        } catch {
            setErr("Delete başarısız.");
        }
    }

    return (
        <div>
            <h3>Users</h3>

            <button onClick={load} style={{ marginBottom: 10 }}>Refresh</button>

            {loading && <div>Loading...</div>}
            {err && <div style={{ color: "tomato" }}>{err}</div>}

            <table border="1" cellPadding="8" style={{ borderCollapse: "collapse", width: "100%" }}>
                <thead>
                <tr>
                    <th>UserId</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Active</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {items.map((u) => (
                    <tr key={u.userId}>
                        <td>{u.userId}</td>
                        <td>{u.email}</td>
                        <td>{u.role}</td>
                        <td>{String(u.active)}</td>
                        <td style={{ display: "flex", gap: 8 }}>
                            <button onClick={() => setActive(u.userId, true)}>Activate</button>
                            <button onClick={() => setActive(u.userId, false)}>Deactivate</button>
                            <button onClick={() => del(u.userId)}>Delete</button>
                        </td>
                    </tr>
                ))}
                {items.length === 0 && (
                    <tr>
                        <td colSpan="5" style={{ textAlign: "center" }}>No users</td>
                    </tr>
                )}
                </tbody>
            </table>
        </div>
    );
}
