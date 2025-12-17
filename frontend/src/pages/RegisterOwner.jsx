import { useState } from "react";
import { http } from "../api/http";
import { saveCreds } from "../auth/authStore";

export default function RegisterOwner({ onRegistered, goLogin }) {
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        phone: "",
        address: "",
        email: "",
        password: "",
    });

    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);

    function setField(k, v) {
        setForm((p) => ({ ...p, [k]: v }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setErr("");
        setLoading(true);

        try {
            await http.post("/api/auth/register/owner", form);
            saveCreds(form.email, form.password);
            const res = await http.get("/api/users/me");
            onRegistered(res.data);
        } catch {
            setErr("Register başarısız. Email kayıtlı olabilir.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", fontFamily: "Arial" }}>
            <div style={{ width: 420, border: "1px solid #ddd", borderRadius: 10, padding: 18 }}>
                <h2 style={{ marginTop: 0 }}>Owner Register</h2>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 10 }}>
                    <input placeholder="First Name" value={form.firstName} onChange={(e) => setField("firstName", e.target.value)} required />
                    <input placeholder="Last Name" value={form.lastName} onChange={(e) => setField("lastName", e.target.value)} required />
                    <input placeholder="Phone" value={form.phone} onChange={(e) => setField("phone", e.target.value)} />
                    <input placeholder="Address" value={form.address} onChange={(e) => setField("address", e.target.value)} />
                    <input placeholder="Email" value={form.email} onChange={(e) => setField("email", e.target.value)} required />
                    <input type="password" placeholder="Password" value={form.password} onChange={(e) => setField("password", e.target.value)} required />

                    {err && <div style={{ color: "tomato" }}>{err}</div>}

                    <button type="submit" disabled={loading} style={{ padding: 10 }}>
                        {loading ? "Registering..." : "Register"}
                    </button>

                    <button type="button" onClick={goLogin} style={{ padding: 10 }}>
                        Login’e dön
                    </button>
                </form>
            </div>
        </div>
    );
}
