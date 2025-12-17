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
            setErr("Giriş başarısız. Bilgileri kontrol et.");
        }
    }

    return (
        <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", fontFamily: "Arial" }}>
            <div style={{ width: 360, border: "1px solid #ddd", borderRadius: 10, padding: 18 }}>
                <h2 style={{ marginTop: 0 }}>PetNabiz Login</h2>

                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: 10 }}>
                        <label>Email</label>
                        <input
                            style={{ width: "100%", padding: 8 }}
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div style={{ marginBottom: 10 }}>
                        <label>Password</label>
                        <input
                            type="password"
                            style={{ width: "100%", padding: 8 }}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {err && <div style={{ color: "tomato", marginBottom: 10 }}>{err}</div>}

                    <button type="submit" style={{ width: "100%", padding: 10 }}>
                        Login
                    </button>
                </form>

                <div style={{ display: "grid", gap: 8, marginTop: 10 }}>
                    <button onClick={goRegister} style={{ width: "100%", padding: 10 }}>
                        Owner Register
                    </button>
                    <button onClick={goClinicApplication} style={{ width: "100%", padding: 10 }}>
                        Clinic Application
                    </button>
                </div>
            </div>
        </div>
    );
}
