import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function SlotManager() {
    const [vets, setVets] = useState([]);
    const [selectedVet, setSelectedVet] = useState("");
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(false);

    // --- STATE'LER ---
    const [hoveredSlot, setHoveredSlot] = useState(null); // Hangi slotun üzerindeyiz?
    const [deleteModal, setDeleteModal] = useState({ show: false, slotId: null }); // Silme modalı

    useEffect(() => {
        fetchVets();
    }, []);

    useEffect(() => {
        if (selectedVet && selectedDate) {
            fetchSlots();
        } else {
            setSlots([]);
        }
    }, [selectedVet, selectedDate]);

    const fetchVets = async () => {
        try {
            const res = await http.get("/api/veterinaries/my");
            setVets(res.data);
            if (res.data.length > 0) {
                setSelectedVet(res.data[0].vetId);
            }
        } catch (error) {
            console.error("Veterinerler yüklenemedi", error);
        }
    };

    const fetchSlots = async () => {
        try {
            const res = await http.get(`/api/slots/all`, {
                params: { vetId: selectedVet, date: selectedDate }
            });
            setSlots(res.data);
        } catch (error) {
            console.error("Slotlar çekilemedi", error);
        }
    };

    const handleGenerateSlots = async () => {
        if (!selectedVet || !selectedDate) {
            alert("Lütfen veteriner ve tarih seçiniz.");
            return;
        }
        setLoading(true);
        try {
            await http.post(`/api/slots/${selectedVet}/${selectedDate}/generate`);
            alert("Randevu saatleri başarıyla oluşturuldu!");
            fetchSlots();
        } catch (error) {
            console.error("Hata:", error);
            alert("Slot oluşturulurken hata oluştu.");
        } finally {
            setLoading(false);
        }
    };

    // --- SİLME İŞLEMLERİ ---

    const handleDeleteClick = (slotId) => {
        setDeleteModal({ show: true, slotId: slotId });
    };

    const confirmDelete = async () => {
        if (!deleteModal.slotId) return;

        try {
            await http.delete(`/api/slots/${deleteModal.slotId}`);
            setSlots(slots.filter(s => s.slotId !== deleteModal.slotId));
            setDeleteModal({ show: false, slotId: null });
        } catch (error) {
            console.error("Silme hatası:", error);
            alert("Slot silinemedi.");
            setDeleteModal({ show: false, slotId: null });
        }
    };

    const closeDeleteModal = () => {
        setDeleteModal({ show: false, slotId: null });
    };

    // --- SLOT DURUMU ve SİLİNEBİLİRLİK KONTROLÜ ---
    const getSlotStatus = (slot) => {
        if (!slot.time) return { style: defaultStyle, text: "--", labelColor: "#000", canDelete: false };

        const slotDateTimeStr = `${selectedDate}T${slot.time}`;
        const slotDateObj = new Date(slotDateTimeStr);
        const now = new Date();

        // 1. Durum: GEÇMİŞ ZAMAN
        if (slotDateObj < now) {
            return {
                style: pastStyle,
                text: "Geçti",
                labelColor: "#64748b",
                canDelete: false // Geçmiş silinemez (butonu gizle)
            };
        }

        // 2. Durum: DOLU (BOOKED)
        if (slot.booked) {
            return {
                style: bookedStyle,
                text: "Dolu",
                labelColor: "#1d4ed8",
                canDelete: false // Dolu olan silinemez (butonu gizle)
            };
        }

        // 3. Durum: MÜSAİT
        return {
            style: availableStyle,
            text: "Müsait",
            labelColor: "#065f46",
            canDelete: true // Sadece bu silinebilir
        };
    };

    return (
        <div style={containerStyle}>
            <h2 style={headerStyle}>📅 Randevu Takvimi Yönetimi</h2>
            <p style={subHeaderStyle}>
                Veteriner hekimleriniz için çalışma saatlerini buradan yönetebilirsiniz.
            </p>

            <div style={controlPanelStyle}>
                <div style={inputGroup}>
                    <label style={labelStyle}>Veteriner Hekim</label>
                    <select
                        style={selectStyle}
                        value={selectedVet}
                        onChange={(e) => setSelectedVet(e.target.value)}
                    >
                        {vets.length === 0 && <option>Yükleniyor...</option>}
                        {vets.map(vet => (
                            <option key={vet.vetId} value={vet.vetId}>
                                {vet.firstName} {vet.lastName}
                            </option>
                        ))}
                    </select>
                </div>

                <div style={inputGroup}>
                    <label style={labelStyle}>Tarih</label>
                    <input
                        type="date"
                        style={inputStyle}
                        value={selectedDate}
                        onChange={(e) => setSelectedDate(e.target.value)}
                    />
                </div>

                <div style={{ display: "flex", alignItems: "end" }}>
                    <button
                        onClick={handleGenerateSlots}
                        style={generateButtonStyle}
                        disabled={loading}
                    >
                        {loading ? "İşlem sürüyor..." : "⚙️ Saatleri Oluştur"}
                    </button>
                </div>
            </div>

            <hr style={{ margin: "30px 0", border: "0", borderTop: "1px solid #e2e8f0" }} />

            <div>
                <h3 style={{ color: "#334155", fontSize: "18px", marginBottom: "15px" }}>
                    {selectedDate} Tarihindeki Durum
                </h3>

                {slots.length > 0 ? (
                    <div style={slotsGrid}>
                        {slots.map(slot => {
                            const status = getSlotStatus(slot);
                            const isHovered = hoveredSlot === slot.slotId;

                            return (
                                <div
                                    key={slot.slotId}
                                    style={{...status.style, position: "relative"}}
                                    onMouseEnter={() => setHoveredSlot(slot.slotId)}
                                    onMouseLeave={() => setHoveredSlot(null)}
                                >
                                    {/* --- SİLME BUTONU: Sadece Hover olunca VE Silinebilir ise göster --- */}
                                    {isHovered && status.canDelete && (
                                        <button
                                            onClick={() => handleDeleteClick(slot.slotId)}
                                            style={deleteIconStyle}
                                            title="Bu saati sil"
                                        >
                                            ✕
                                        </button>
                                    )}

                                    <div style={{...timeStyle, color: status.labelColor}}>
                                        {slot.time ? String(slot.time).substring(0, 5) : "--:--"}
                                    </div>
                                    <div style={{ fontSize: "11px", fontWeight: "600", color: status.labelColor, marginTop: "4px" }}>
                                        {status.text}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <div style={emptyStateStyle}>
                        Bu tarih için henüz slot oluşturulmamış.
                    </div>
                )}
            </div>

            {/* --- DELETE MODAL --- */}
            {deleteModal.show && (
                <div style={modalOverlayStyle}>
                    <div style={modalBoxStyle}>
                        <h3 style={{marginTop:0, color: "#1e293b"}}>Silme Onayı</h3>
                        <p style={{color: "#475569", marginBottom: "25px"}}>
                            Bu randevu saatini silmek istediğinize emin misiniz?
                        </p>
                        <div style={{display: "flex", gap: "10px", justifyContent: "flex-end"}}>
                            <button onClick={closeDeleteModal} style={modalCancelBtn}>İptal</button>
                            <button onClick={confirmDelete} style={modalConfirmBtn}>Evet, Sil</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

// --- STİLLER ---

const containerStyle = {
    padding: "20px", background: "white", borderRadius: "12px",
    border: "1px solid #e2e8f0", boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.05)"
};
const headerStyle = { fontSize: "22px", fontWeight: "800", color: "#1e293b", marginBottom: "8px" };
const subHeaderStyle = { fontSize: "14px", color: "#64748b", marginBottom: "25px" };
const controlPanelStyle = {
    display: "flex", gap: "20px", flexWrap: "wrap", alignItems: "center",
    background: "#f8fafc", padding: "20px", borderRadius: "10px", border: "1px solid #e2e8f0"
};
const inputGroup = { display: "flex", flexDirection: "column", gap: "5px", flex: 1, minWidth: "200px" };
const labelStyle = { fontSize: "12px", fontWeight: "700", color: "#475569", textTransform: "uppercase" };
const selectStyle = { padding: "10px", borderRadius: "6px", border: "1px solid #cbd5e1", fontSize: "14px", outline: "none", background: "white" };
const inputStyle = { padding: "9px", borderRadius: "6px", border: "1px solid #cbd5e1", fontSize: "14px", outline: "none" };
const generateButtonStyle = {
    padding: "10px 20px", background: "#3b82f6", color: "white", border: "none",
    borderRadius: "6px", fontWeight: "600", cursor: "pointer", boxShadow: "0 2px 4px rgba(59, 130, 246, 0.3)", height: "42px"
};
const slotsGrid = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(100px, 1fr))", gap: "15px" };
const timeStyle = { fontWeight: "800", fontSize: "16px" };
const emptyStateStyle = { textAlign: "center", padding: "40px", color: "#94a3b8", background: "#f8fafc", borderRadius: "10px", border: "1px dashed #cbd5e1", fontStyle: "italic" };

// --- DURUM STİLLERİ ---
const availableStyle = {
    background: "#ecfdf5", border: "1px solid #6ee7b7", borderRadius: "8px",
    padding: "10px", textAlign: "center", cursor: "default"
};
const bookedStyle = {
    background: "#eff6ff", border: "1px solid #60a5fa", borderRadius: "8px",
    padding: "10px", textAlign: "center", cursor: "default"
};
const pastStyle = {
    background: "#f1f5f9", border: "1px solid #cbd5e1", borderRadius: "8px",
    padding: "10px", textAlign: "center", opacity: 0.7, cursor: "not-allowed"
};
const defaultStyle = { padding: "10px" };

// --- DELETE ICON & MODAL ---
const deleteIconStyle = {
    position: "absolute", top: "-8px", right: "-8px",
    background: "#ef4444", color: "white", border: "2px solid white", borderRadius: "50%",
    width: "24px", height: "24px", display: "flex", alignItems: "center", justifyContent: "center",
    fontSize: "12px", cursor: "pointer", boxShadow: "0 2px 4px rgba(0,0,0,0.2)", fontWeight: "bold"
};
const modalOverlayStyle = {
    position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
    background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 2000
};
const modalBoxStyle = {
    background: "white", padding: "25px", borderRadius: "12px", width: "350px",
    boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.1)", textAlign: "center"
};
const modalCancelBtn = {
    padding: "8px 16px", background: "#f1f5f9", color: "#64748b", border: "none",
    borderRadius: "6px", cursor: "pointer", fontWeight: "600"
};
const modalConfirmBtn = {
    padding: "8px 16px", background: "#ef4444", color: "white", border: "none",
    borderRadius: "6px", cursor: "pointer", fontWeight: "600"
};