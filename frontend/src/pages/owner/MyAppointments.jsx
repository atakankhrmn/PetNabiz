import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function MyAppointments() {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState({ type: "", text: "" });
    const [deletingId, setDeletingId] = useState(null); // Onay bekleyen randevu ID'si

    useEffect(() => {
        loadAppointments();
    }, []);

    const loadAppointments = async () => {
        setLoading(true);
        try {
            // Backend: GET /api/appointments/my
            const res = await http.get("/api/appointments/my");
            setAppointments(res.data || []);
        } catch (e) {
            setMsg({ type: "error", text: "Randevular yüklenirken bir hata oluştu." });
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (id) => {
        try {
            await http.post(`/api/appointments/${id}/cancel`);
            setMsg({ type: "success", text: "Randevunuz iptal edildi." });
            setDeletingId(null); // State'i temizle
            loadAppointments();
        } catch (e) {
            setMsg({ type: "error", text: "İşlem başarısız." });
        }
    };

    // Randevuları tarihe göre ayır
    const now = new Date();
    const futureApps = appointments.filter(app => new Date(app.date) >= now);
    const pastApps = appointments.filter(app => new Date(app.date) < now);

    if (loading) return <div style={{ padding: "20px", color: "#64748b" }}>Randevular yükleniyor...</div>;

    return (
        <div style={{ maxWidth: "1200px" }}>
            <h3 style={{ color: "#1e293b", marginBottom: "25px", fontWeight: "800" }}>📋 Randevularım</h3>

            {msg.text && (
                <div style={{
                    padding: "15px", borderRadius: "10px", marginBottom: "20px", fontSize: "14px",
                    background: msg.type === "success" ? "#f0fdf4" : "#fef2f2",
                    color: msg.type === "success" ? "#166534" : "#991b1b",
                    border: `1px solid ${msg.type === "success" ? "#bbf7d0" : "#fecaca"}`
                }}>
                    {msg.text}
                </div>
            )}

            {/* GELECEK RANDEVULAR */}
            <div style={{ marginBottom: "40px" }}>
                <h4 style={sectionTitle}>🔵 Gelecek Randevular</h4>
                <div style={gridStyle}>
                    {futureApps.length === 0 ? <p style={emptyText}>Aktif randevunuz bulunmuyor.</p> :
                        futureApps.map(app => (
                            <AppointmentCard key={app.appointmentId} app={app} onCancel={handleCancel} canCancel={true} deletingId={deletingId} setDeletingId={setDeletingId}/>
                        ))
                    }
                </div>
            </div>

            {/* GEÇMİŞ RANDEVULAR */}
            <div>
                <h4 style={{ ...sectionTitle, color: "#94a3b8" }}>⚪ Geçmiş Randevular</h4>
                <div style={gridStyle}>
                    {pastApps.length === 0 ? <p style={emptyText}>Geçmiş randevu kaydı yok.</p> :
                        pastApps.map(app => (
                            <AppointmentCard key={app.appointmentId} app={app} canCancel={false} />
                        ))
                    }
                </div>
            </div>
        </div>
    );
}

// Kart Bileşeni
function AppointmentCard({ app, onCancel, canCancel, deletingId, setDeletingId }) {
    return (
        <div style={{ ...cardStyle, opacity: canCancel ? 1 : 0.8 }}>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "12px" }}>
                <span style={dateBadge}>{app.date} | {app.time?.substring(0, 5)}</span>
                <span style={petBadge}>🐾 {app.petName || "Pet"}</span>
            </div>
            <div style={infoText}><strong>Veteriner:</strong> {app.vetName}</div>
            <div style={infoText}><strong>Klinik:</strong> {app.clinicName}</div>
            {app.reason && <div style={reasonBox}><strong>Neden:</strong> {app.reason}</div>}

            {canCancel && (
                <div style={{ marginTop: "15px" }}>
                    {deletingId === app.appointmentId ? (
                        <div style={{ display: "flex", gap: "10px" }}>
                            <button
                                onClick={() => onCancel(app.appointmentId)}
                                style={{ ...cancelBtn, marginTop: 0, flex: 2, background: "#ef4444", color: "white", border: "none" }}
                            >
                                Onayla
                            </button>
                            <button
                                onClick={() => setDeletingId(null)}
                                style={{ ...cancelBtn, marginTop: 0, flex: 1, background: "#f1f5f9", color: "#475569", border: "1px solid #cbd5e1" }}
                            >
                                Vazgeç
                            </button>
                        </div>
                    ) : (
                        <button onClick={() => setDeletingId(app.appointmentId)} style={cancelBtn}>
                            Randevuyu İptal Et
                        </button>
                    )}
                </div>
            )}
        </div>
    );
}

// Stiller
const gridStyle = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(320px, 1fr))", gap: "20px" };
const cardStyle = { background: "white", padding: "20px", borderRadius: "15px", border: "1px solid #e2e8f0", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)" };
const dateBadge = { background: "#0284c7", color: "white", padding: "5px 10px", borderRadius: "8px", fontSize: "13px", fontWeight: "700" };
const petBadge = { background: "#f0f9ff", color: "#0284c7", padding: "5px 10px", borderRadius: "20px", fontSize: "12px", fontWeight: "800" };
const infoText = { fontSize: "14px", color: "#334155", margin: "4px 0" };
const reasonBox = { marginTop: "10px", padding: "8px", background: "#f8fafc", borderRadius: "8px", fontSize: "13px", color: "#64748b", fontStyle: "italic" };
const cancelBtn = { width: "100%", marginTop: "15px", background: "#fef2f2", color: "#ef4444", border: "1px solid #fee2e2", padding: "8px", borderRadius: "10px", fontWeight: "700", cursor: "pointer" };
const sectionTitle = { fontSize: "18px", fontWeight: "700", marginBottom: "15px", borderBottom: "2px solid #f1f5f9", paddingBottom: "8px" };
const emptyText = { color: "#94a3b8", fontSize: "14px", fontStyle: "italic" };