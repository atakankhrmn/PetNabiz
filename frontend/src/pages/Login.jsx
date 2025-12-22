import { useState } from "react";
import { http } from "../api/http";
import { saveCreds, clearCreds } from "../auth/authStore";

export default function Login({ onLoggedIn, goRegister, goClinicApplication }) {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [err, setErr] = useState("");

    async function handleSubmit(e) {
        e.preventDefault();
        setErr("");
        saveCreds(email, password);
        try {
            const res = await http.get("/api/users/me");
            onLoggedIn(res.data);
        } catch {
            clearCreds();
            setErr("Giriş başarısız. Bilgilerinizi kontrol edin.");
        }
    }

    return (
        <div style={containerStyle}>
            <div style={cardStyle}>
                <div style={{ textAlign: "center", marginBottom: 25 }}>
                    <div style={{ fontSize: 28, fontWeight: 900, color: "#0284c7" }}>✚ PetNabiz</div>
                    <div style={{ marginTop: 8, color: "#64748b", fontSize: 14 }}>Profesyonel Veteriner Sistemi</div>
                </div>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 16 }}>
                    <div>
                        <label style={labelStyle}>E-Posta</label>
                        <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="doktor@klinik.com" required style={inputStyle} />
                    </div>

                    <div>
                        <label style={labelStyle}>Şifre</label>
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" required style={inputStyle} />
                    </div>

                    {err && <div style={errStyle}>{err}</div>}

                    <button type="submit" style={primaryBtn}>Giriş Yap</button>

                    <div style={{ display: "grid", gap: 10, marginTop: 10 }}>
                        <button type="button" onClick={goRegister} style={secondaryBtn}>Register as Pet Owner</button>
                        <button type="button" onClick={goClinicApplication} style={secondaryBtn}>Apply for Clinic Account</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

const containerStyle = { minHeight: "100vh", display: "grid", placeItems: "center", background: "linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 100%)", padding: 20 };
const cardStyle = { width: "min(400px, 92vw)", background: "white", borderRadius: 20, padding: 35, boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1)" };
const labelStyle = { display: "block", color: "#475569", fontSize: 13, marginBottom: 6, fontWeight: 600 };
const inputStyle = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #cbd5e1", outline: "none", boxSizing: "border-box" };
const primaryBtn = { width: "100%", padding: "14px", borderRadius: 10, border: "none", background: "#0284c7", color: "white", fontWeight: 800, cursor: "pointer" };
const secondaryBtn = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #e2e8f0", background: "white", color: "#64748b", fontWeight: 600, cursor: "pointer" };
const errStyle = { background: "#fef2f2", color: "#991b1b", padding: "10px", borderRadius: 8, fontSize: 13, border: "1px solid #fee2e2" };