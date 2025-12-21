import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function BookAppointment({ me }) {
    const [expandedSlotId, setExpandedSlotId] = useState(null); // Hangi slotun barı açık?
    const [reason, setReason] = useState(""); // Yazılan randevu nedeni

    const [pets, setPets] = useState([]);
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState({ type: "", text: "" });

    // Arama Formu State'leri
    const [searchForm, setSearchForm] = useState({
        petId: "",
        city: "",
        district: "",
        startDate: "",
        endDate: ""
    });

    useEffect(() => {
        // Kullanıcının petlerini yükle
        http.get("/api/pets/my")
            .then(res => setPets(res.data || []))
            .catch(() => setMsg({ type: "error", text: "Pet listesi yüklenemedi." }));
    }, []);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchForm.petId) {
            setMsg({ type: "error", text: "Lütfen önce bir pet seçin." });
            return;
        }
        setLoading(true);
        setMsg({ type: "", text: "" });
        try {
            const res = await http.get("/api/slots/available/range", {
                params: {
                    startDate: searchForm.startDate,
                    endDate: searchForm.endDate,
                    city: searchForm.city,
                    district: searchForm.district
                }
            });

            // VERİYİ BURADA SIRALIYORUZ
            const sortedData = (res.data || []).sort((a, b) => {
                // Önce tarihe bak (2025-12-21 gibi)
                if (a.date !== b.date) {
                    return a.date.localeCompare(b.date);
                }
                // Tarihler aynıysa saate bak (09:00:00 gibi)
                return a.time.localeCompare(b.time);
            });

            setSlots(sortedData);

            if (sortedData.length === 0) {
                setMsg({ type: "info", text: "Aranan kriterlere uygun boş randevu bulunamadı." });
            }
        } catch (err) {
            setMsg({ type: "error", text: "Randevular aranırken bir hata oluştu." });
        } finally {
            setLoading(false);
        }
    };

    const handleBook = async (slotId) => {
        if (!reason.trim()) {
            setMsg({ type: "error", text: "Lütfen randevu nedenini belirtin." });
            return;
        }
        setMsg({ type: "", text: "" });
        try {
            await http.post(`/api/slots/${slotId}/book`, {
                petId: searchForm.petId,
                reason: reason // Nedeni buraya ekledik
            });
            setMsg({ type: "success", text: "Randevunuz başarıyla oluşturuldu!" });
            setSlots(slots.filter(s => s.slotId !== slotId));
            setExpandedSlotId(null);
            setReason("");
        } catch (err) {
            setMsg({ type: "error", text: "Randevu alınamadı." });
        }
    };

    return (
        <div style={{ maxWidth: "10000px" }}>
            <h3 style={{ color: "#1e293b", marginBottom: 20 }}>🗓️ Randevu Al</h3>

            {msg.text && (
                <div style={{ ...statusMsgStyle, background: msg.type === "success" ? "#f0fdf4" : msg.type === "error" ? "#fef2f2" : "#eff6ff" }}>
                    {msg.text}
                </div>
            )}

            {/* ARAMA FORMU */}
            <form onSubmit={handleSearch} style={searchBarGrid}>
                <div style={inputGroup}>
                    <label style={labelStyle}>Pet Seçimi</label>
                    <select
                        required
                        style={inputStyle}
                        value={searchForm.petId}
                        onChange={e => setSearchForm({...searchForm, petId: e.target.value})}
                    >
                        <option value="">Pet Seçiniz...</option>
                        {pets.map(p => <option key={p.petId || p.id} value={p.petId || p.id}>{p.name}</option>)}
                    </select>
                </div>
                <div style={inputGroup}>
                    <label style={labelStyle}>Şehir</label>
                    <input required placeholder="Örn: İstanbul" style={inputStyle} value={searchForm.city} onChange={e => setSearchForm({...searchForm, city: e.target.value})} />
                </div>
                <div style={inputGroup}>
                    <label style={labelStyle}>İlçe</label>
                    <input required placeholder="Örn: Beşiktaş" style={inputStyle} value={searchForm.district} onChange={e => setSearchForm({...searchForm, district: e.target.value})} />
                </div>
                <div style={inputGroup}>
                    <label style={labelStyle}>Başlangıç</label>
                    <input type="date" required style={inputStyle} value={searchForm.startDate} onChange={e => setSearchForm({...searchForm, startDate: e.target.value})} />
                </div>
                <div style={inputGroup}>
                    <label style={labelStyle}>Bitiş</label>
                    <input type="date" required style={inputStyle} value={searchForm.endDate} onChange={e => setSearchForm({...searchForm, endDate: e.target.value})} />
                </div>
                <button type="submit" disabled={loading} style={searchBtnStyle}>
                    {loading ? "Aranıyor..." : "Slotları Bul"}
                </button>
            </form>

            <hr style={{ margin: "30px 0", border: "0.5px solid #e2e8f0" }} />

            {/* SLOT LİSTESİ */}
            <div style={slotGridStyle}>
                {slots.map(slot => (
                    <div key={slot.slotId} style={slotCardStyle}>
                        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "10px" }}>
                            <span style={{ fontWeight: "800", color: "#0284c7" }}>{slot.date}</span>
                            <span style={{ fontWeight: "800", color: "#1e293b" }}>{slot.time.substring(0,5)}</span>
                        </div>
                        <div style={{ fontSize: "15px", fontWeight: "700", color: "#334155" }}>{slot.vetName}</div>
                        <div style={{ fontSize: "13px", color: "#64748b", margin: "4px 0" }}>{slot.clinicName}</div>

                        {/* Koşullu Buton ve Bar Gösterimi */}
                        {expandedSlotId === slot.slotId ? (
                            <div style={reasonBarContainer}>
                                <input
                                    placeholder="Randevu nedeni (örn: Aşılama, Kontrol)"
                                    style={reasonInputStyle}
                                    value={reason}
                                    onChange={(e) => setReason(e.target.value)}
                                    autoFocus
                                />
                                <div style={{ display: "flex", gap: "5px", marginTop: "10px" }}>
                                    <button onClick={() => handleBook(slot.slotId)} style={confirmBtnStyle}>Onayla</button>
                                    <button onClick={() => {setExpandedSlotId(null); setReason("");}} style={cancelBtnStyle}>İptal</button>
                                </div>
                            </div>
                        ) : (
                            <button onClick={() => setExpandedSlotId(slot.slotId)} style={bookBtnStyle}>Randevu Al</button>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

// Stiller
const searchBarGrid = { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(150px, 1fr))", gap: "15px", alignItems: "end", background: "white", padding: "20px", borderRadius: "15px", border: "1px solid #e2e8f0" };
const inputGroup = { display: "flex", flexDirection: "column", gap: "5px" };
const labelStyle = { fontSize: "12px", fontWeight: "700", color: "#64748b" };
const inputStyle = { padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "14px" };
const searchBtnStyle = { background: "#0284c7", color: "white", border: "none", padding: "12px", borderRadius: "10px", fontWeight: "700", cursor: "pointer" };
const slotGridStyle = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))", gap: "15px" };
const slotCardStyle = { background: "white", padding: "15px", borderRadius: "12px", border: "1px solid #e2e8f0", boxShadow: "0 2px 4px rgba(0,0,0,0.02)" };
const bookBtnStyle = { width: "100%", marginTop: "12px", background: "#f0f9ff", color: "#0284c7", border: "1px solid #0284c7", padding: "8px", borderRadius: "8px", fontWeight: "700", cursor: "pointer", transition: "0.2s" };
const statusMsgStyle = { padding: "12px", borderRadius: "10px", marginBottom: "20px", fontSize: "14px", fontWeight: "600" };
const reasonBarContainer = { marginTop: "15px", padding: "10px", background: "#f8fafc", borderRadius: "10px", border: "1px solid #e2e8f0" };
const reasonInputStyle = { width: "100%", padding: "8px", borderRadius: "6px", border: "1px solid #cbd5e1", fontSize: "13px", boxSizing: "border-box" };
const confirmBtnStyle = { flex: 1, background: "#059669", color: "white", border: "none", padding: "8px", borderRadius: "6px", fontWeight: "700", cursor: "pointer" };
const cancelBtnStyle = { flex: 1, background: "#64748b", color: "white", border: "none", padding: "8px", borderRadius: "6px", fontWeight: "700", cursor: "pointer" };