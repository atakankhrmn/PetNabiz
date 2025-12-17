import { useState } from "react";
import { http } from "../api/http";

export default function ClinicApplication({ goBack }) {
    const [form, setForm] = useState({
        email: "",
        password: "",
        clinicName: "",
        city: "",
        district: "",
        address: "",
        phone: "",
    });

    const [document, setDocument] = useState(null);
    const [msg, setMsg] = useState("");
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);

    function setField(key, value) {
        setForm((p) => ({ ...p, [key]: value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setMsg("");
        setErr("");

        if (!document) {
            setErr("Lütfen gerekli belgeyi yükleyin.");
            return;
        }

        setLoading(true);
        try {
            const fd = new FormData();
            Object.entries(form).forEach(([k, v]) => fd.append(k, v));
            fd.append("document", document);

            await http.post("/api/clinic-applications", fd, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            setMsg("Başvuru alındı. Admin onayından sonra clinic hesabınız aktif edilir.");
            setForm({
                email: "",
                password: "",
                clinicName: "",
                city: "",
                district: "",
                address: "",
                phone: "",
            });
            setDocument(null);
        } catch {
            setErr("Başvuru başarısız. Alanları ve dosyayı kontrol edin.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", fontFamily: "Arial" }}>
            <div style={{ width: 520, border: "1px solid #ddd", borderRadius: 10, padding: 18 }}>
                <h2 style={{ marginTop: 0 }}>Clinic Application</h2>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 10 }}>
                    <input placeholder="Email" value={form.email} onChange={(e) => setField("email", e.target.value)} required />
                    <input type="password" placeholder="Password" value={form.password} onChange={(e) => setField("password", e.target.value)} required />
                    <input placeholder="Clinic Name" value={form.clinicName} onChange={(e) => setField("clinicName", e.target.value)} required />
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                        <input placeholder="City" value={form.city} onChange={(e) => setField("city", e.target.value)} required />
                        <input placeholder="District" value={form.district} onChange={(e) => setField("district", e.target.value)} required />
                    </div>
                    <input placeholder="Address" value={form.address} onChange={(e) => setField("address", e.target.value)} required />
                    <input placeholder="Phone" value={form.phone} onChange={(e) => setField("phone", e.target.value)} required />
                    <input type="file" onChange={(e) => setDocument(e.target.files?.[0] || null)} required />

                    {msg && <div style={{ color: "green" }}>{msg}</div>}
                    {err && <div style={{ color: "tomato" }}>{err}</div>}

                    <button type="submit" disabled={loading} style={{ padding: 10 }}>
                        {loading ? "Gönderiliyor..." : "Apply"}
                    </button>
                    <button type="button" onClick={goBack} style={{ padding: 10 }}>
                        Geri
                    </button>
                </form>
            </div>
        </div>
    );
}
