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
            setErr("Please upload the required verification document.");
            return;
        }

        setLoading(true);
        try {
            const fd = new FormData();
            Object.entries(form).forEach(([k, v]) => fd.append(k, v));
            fd.append("document", document); //

            await http.post("/api/clinic-applications", fd, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            setMsg("Application submitted successfully. Our admins will review your documents.");
            setForm({ email: "", password: "", clinicName: "", city: "", district: "", address: "", phone: "" });
            setDocument(null);
        } catch {
            setErr("Submission failed. Please check your information and file type.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={containerStyle}>
            <div style={cardStyle}>
                <div style={{ textAlign: "center", marginBottom: 25 }}>
                    <div style={{ fontSize: 26, fontWeight: 900, color: "#0284c7" }}>✚ PetNabiz</div>
                    <div style={{ marginTop: 8, color: "#64748b", fontSize: 14 }}>Clinic Partnership Application</div>
                </div>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12 }}>
                    <input placeholder="Clinic Email" value={form.email} onChange={(e) => setField("email", e.target.value)} required style={inputStyle} />
                    <input type="password" placeholder="Account Password" value={form.password} onChange={(e) => setField("password", e.target.value)} required style={inputStyle} />
                    <input placeholder="Official Clinic Name" value={form.clinicName} onChange={(e) => setField("clinicName", e.target.value)} required style={inputStyle} />

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                        <input placeholder="City" value={form.city} onChange={(e) => setField("city", e.target.value)} required style={inputStyle} />
                        <input placeholder="District" value={form.district} onChange={(e) => setField("district", e.target.value)} required style={inputStyle} />
                    </div>

                    <input placeholder="Full Address" value={form.address} onChange={(e) => setField("address", e.target.value)} required style={inputStyle} />
                    <input placeholder="Contact Phone" value={form.phone} onChange={(e) => setField("phone", e.target.value)} required style={inputStyle} />

                    <div style={{ marginTop: 5 }}>
                        <label style={{ fontSize: 12, color: "#64748b", fontWeight: 700, marginBottom: 5, display: "block" }}>
                            VERIFICATION DOCUMENT (PDF/IMG)
                        </label>
                        <input type="file" onChange={(e) => setDocument(e.target.files?.[0] || null)} required style={fileInputStyle} />
                    </div>

                    {msg && <div style={successStyle}>{msg}</div>}
                    {err && <div style={errStyle}>{err}</div>}

                    <button type="submit" disabled={loading} style={primaryBtn}>
                        {loading ? "Submitting..." : "Submit Application"}
                    </button>
                    <button type="button" onClick={goBack} style={secondaryBtn}>
                        Go Back
                    </button>
                </form>
            </div>
        </div>
    );
}

const containerStyle = { minHeight: "100vh", display: "grid", placeItems: "center", background: "linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 100%)", padding: 20 };
const cardStyle = { width: "min(520px, 92vw)", background: "white", borderRadius: 20, padding: 35, boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1)" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #cbd5e1", outline: "none", boxSizing: "border-box", fontSize: "14px" };
const fileInputStyle = { ...inputStyle, padding: "8px", fontSize: "12px", background: "#f8fafc" };
const primaryBtn = { width: "100%", padding: "14px", borderRadius: 10, border: "none", background: "#0284c7", color: "white", fontWeight: 800, cursor: "pointer", marginTop: 10 };
const secondaryBtn = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #e2e8f0", background: "white", color: "#64748b", fontWeight: 600, cursor: "pointer" };
const errStyle = { background: "#fef2f2", color: "#991b1b", padding: "10px", borderRadius: 8, fontSize: 13, border: "1px solid #fee2e2" };
const successStyle = { background: "#f0fdf4", color: "#166534", padding: "10px", borderRadius: 8, fontSize: 13, border: "1px solid #dcfce7" };