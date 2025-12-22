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
            await http.post("/api/auth/register/owner", form); //
            saveCreds(form.email, form.password);
            const res = await http.get("/api/users/me"); //
            onRegistered(res.data);
        } catch {
            setErr("Registration failed. Email might already be in use.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={containerStyle}>
            <div style={cardStyle}>
                <div style={{ textAlign: "center", marginBottom: 25 }}>
                    <div style={{ fontSize: 26, fontWeight: 900, color: "#0284c7" }}>✚ PetNabiz</div>
                    <div style={{ marginTop: 8, color: "#64748b", fontSize: 14 }}>Create Pet Owner Account</div>
                </div>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12 }}>
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                        <input placeholder="First Name" value={form.firstName} onChange={(e) => setField("firstName", e.target.value)} required style={inputStyle} />
                        <input placeholder="Last Name" value={form.lastName} onChange={(e) => setField("lastName", e.target.value)} required style={inputStyle} />
                    </div>
                    <input placeholder="Phone" value={form.phone} onChange={(e) => setField("phone", e.target.value)} style={inputStyle} />
                    <input placeholder="Address" value={form.address} onChange={(e) => setField("address", e.target.value)} style={inputStyle} />
                    <input placeholder="Email Address" type="email" value={form.email} onChange={(e) => setField("email", e.target.value)} required style={inputStyle} />
                    <input placeholder="Password" type="password" value={form.password} onChange={(e) => setField("password", e.target.value)} required style={inputStyle} />

                    {err && <div style={errStyle}>{err}</div>}

                    <button type="submit" disabled={loading} style={primaryBtn}>
                        {loading ? "Registering..." : "Register Now"}
                    </button>

                    <button type="button" onClick={goLogin} style={secondaryBtn}>
                        Back to Login
                    </button>
                </form>
            </div>
        </div>
    );
}

const containerStyle = { minHeight: "100vh", display: "grid", placeItems: "center", background: "linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 100%)", padding: 20 };
const cardStyle = { width: "min(450px, 92vw)", background: "white", borderRadius: 20, padding: 35, boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1)" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #cbd5e1", outline: "none", boxSizing: "border-box", fontSize: "14px" };
const primaryBtn = { width: "100%", padding: "14px", borderRadius: 10, border: "none", background: "#0284c7", color: "white", fontWeight: 800, cursor: "pointer", marginTop: 10 };
const secondaryBtn = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #e2e8f0", background: "white", color: "#64748b", fontWeight: 600, cursor: "pointer" };
const errStyle = { background: "#fef2f2", color: "#991b1b", padding: "10px", borderRadius: 8, fontSize: 13, border: "1px solid #fee2e2" };