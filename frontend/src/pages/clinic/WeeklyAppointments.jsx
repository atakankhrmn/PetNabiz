import { useState, useEffect } from "react";
import { http } from "../../api/http";

// --- YARDIMCI BİLEŞENLER (HOVER EFEKTLERİ İÇİN) ---

const HoverButton = ({ baseStyle, hoverStyle, children, ...props }) => {
    const [isHovered, setIsHovered] = useState(false);
    return (
        <button
            style={{
                ...baseStyle,
                ...(isHovered ? hoverStyle : {}),
                transition: "all 0.2s ease-in-out",
                transform: isHovered ? "scale(1.05)" : "scale(1)"
            }}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            {...props}
        >
            {children}
        </button>
    );
};

const HoverBadge = ({ children, onClick }) => {
    const [isHovered, setIsHovered] = useState(false);
    return (
        <span
            onClick={onClick}
            style={{
                background: isHovered ? "#bae6fd" : "#e0f2fe",
                color: "#0284c7",
                padding: "6px 12px",
                borderRadius: "20px",
                fontSize: "13px",
                fontWeight: "700",
                cursor: "pointer",
                display: "inline-block",
                transition: "all 0.2s ease-in-out",
                transform: isHovered ? "translateY(-2px)" : "translateY(0)",
                boxShadow: isHovered ? "0 4px 6px -1px rgba(0,0,0,0.1)" : "none"
            }}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            title="Detaylar için tıkla"
        >
            {children}
        </span>
    );
};

export default function WeeklyAppointments() {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- STATE'LER ---
    const [selectedPet, setSelectedPet] = useState(null);
    const [cancelModal, setCancelModal] = useState({ show: false, apptId: null });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const clinicRes = await http.get("/api/clinics/my");
            const myClinicId = clinicRes.data.clinicId;
            const apptRes = await http.get(`/api/appointments/clinic/${myClinicId}/upcoming`);
            setAppointments(apptRes.data);
        } catch (err) {
            console.error("Veri çekme hatası:", err);
            setError("Randevular yüklenirken bir sorun oluştu.");
        } finally {
            setLoading(false);
        }
    };

    // --- İPTAL İŞLEMLERİ ---
    const handleCancelClick = (apptId) => {
        setCancelModal({ show: true, apptId: apptId });
    };

    const confirmCancellation = async () => {
        if (!cancelModal.apptId) return;
        try {
            await http.post(`/api/appointments/${cancelModal.apptId}/cancel`);
            alert("Randevu başarıyla iptal edildi. ✅");
            setAppointments(prev => prev.filter(a => a.appointmentId !== cancelModal.apptId));
            setCancelModal({ show: false, apptId: null });
        } catch (error) {
            console.error("İptal hatası:", error);
            alert("İptal işlemi başarısız oldu.");
            setCancelModal({ show: false, apptId: null });
        }
    };

    const closeCancelModal = () => setCancelModal({ show: false, apptId: null });

    // --- PET DETAY İŞLEMLERİ ---
    // --- PET DETAY İŞLEMLERİ ---
    const handlePetClick = async (appt) => {
        // Modalı hemen aç, yükleniyor göster
        setSelectedPet({ name: appt.petName, loading: true });

        try {
            // 1. Backend'den detayları çek
            const res = await http.get(`/api/pets/${appt.petId}`);
            const data = res.data;

            // 2. FOTOĞRAF URL AYARI
            // DİKKAT: Değişkeni IF bloğunun dışında, en başta tanımlıyoruz!
            let finalPhotoUrl = null;

            if (data.photoUrl) {
                if (data.photoUrl.startsWith("http")) {
                    // Veritabanında zaten uzun link varsa
                    finalPhotoUrl = data.photoUrl;
                } else {
                    // Sadece dosya ismi varsa (örn: kedi.jpg) -> Başına sunucu adresini ekle
                    finalPhotoUrl = `http://localhost:8080/uploads/pets/${data.photoUrl}`;
                }
            }

            // 3. State'i güncelle
            setSelectedPet({
                ...data,
                owner: appt.petOwnerName,
                photoUrl: finalPhotoUrl, // Artık bu değişkeni tanıyor
                loading: false
            });

        } catch (error) {
            console.error("Pet detay hatası", error);
            alert("Pet detaylarına erişilemedi.");
            setSelectedPet(null);
        }
    };

    const copyPetId = () => {
        if(selectedPet?.id) {
            navigator.clipboard.writeText(selectedPet.id);
            alert("Pet ID kopyalandı!");
        }
    };

    const handleAddMedicalRecord = () => {
        alert(`"${selectedPet.name}" için Tıbbi Kayıt ekleme ekranı açılıyor... (ID: ${selectedPet.id})`);
    };

    const closePetModal = () => setSelectedPet(null);

    if (loading) return <div style={{ padding: 20, color: "#64748b" }}>⏳ Randevular yükleniyor...</div>;
    if (error) return <div style={{ padding: 20, color: "#ef4444" }}>⚠️ {error}</div>;

    return (
        <div>
            <h2 style={headerStyle}>🗓️ Gelecek Randevular</h2>
            <p style={subHeaderStyle}>Önümüzdeki 2 hafta içindeki tüm planlı randevular.</p>

            {appointments.length === 0 ? (
                <div style={emptyStyle}>
                    Önümüzdeki 14 gün için hiç randevu görünmüyor.
                </div>
            ) : (
                <div style={tableContainer}>
                    <table style={tableStyle}>
                        <thead>
                        <tr style={thRowStyle}>
                            <th style={thStyle}>TARİH & SAAT</th>
                            <th style={thStyle}>VETERİNER HEKİM</th>
                            <th style={thStyle}>HASTA SAHİBİ</th>
                            <th style={thStyle}>PET</th>
                            <th style={thStyle}>SEBEP / NOT</th>
                            <th style={{...thStyle, textAlign:"right"}}>İŞLEM</th>
                        </tr>
                        </thead>
                        <tbody>
                        {appointments.map((appt) => (
                            <tr key={appt.appointmentId} style={trStyle}>
                                <td style={tdStyle}>
                                    <div style={{fontWeight: "800", color: "#0f172a", fontSize: "14px"}}>
                                        {appt.date}
                                    </div>
                                    <div style={{color: "#64748b", fontSize: "12px", marginTop: "2px"}}>
                                        {String(appt.time).substring(0,5)}
                                    </div>
                                </td>

                                <td style={tdStyle}>{appt.veterinaryName}</td>
                                <td style={tdStyle}>{appt.petOwnerName}</td>

                                <td style={tdStyle}>
                                    <HoverBadge onClick={() => handlePetClick(appt)}>
                                        {appt.petName}
                                    </HoverBadge>
                                </td>

                                <td style={tdStyle}>
                                    {appt.reason ? appt.reason : <span style={{color:"#cbd5e1", fontStyle:"italic"}}>-</span>}
                                </td>

                                <td style={{...tdStyle, textAlign:"right"}}>
                                    <HoverButton
                                        onClick={() => handleCancelClick(appt.appointmentId)}
                                        baseStyle={cancelBtnBase}
                                        hoverStyle={cancelBtnHover}
                                        title="Randevuyu İptal Et"
                                    >
                                        İptal
                                    </HoverButton>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* --- PET DETAY & MEDICAL RECORD MODALI --- */}
            {selectedPet && (
                <div style={modalOverlayStyle}>
                    <div style={modalBoxStyle}>

                        {/* Başlık */}
                        <div style={{display:"flex", justifyContent:"space-between", marginBottom:"15px", borderBottom:"1px solid #f1f5f9", paddingBottom:"10px"}}>
                            <h3 style={{margin:0, color: "#1e293b", display:"flex", alignItems:"center", gap:"10px"}}>
                                🐾 {selectedPet.name}
                            </h3>
                            <button onClick={closePetModal} style={closeXBtn}>✕</button>
                        </div>

                        <div style={{display:"flex", flexDirection:"column", gap:"15px"}}>

                            {/* FOTOĞRAF VE TEMEL BİLGİ */}
                            <div style={{display:"flex", gap:"15px", alignItems:"center"}}>
                                <div style={photoContainerStyle}>
                                    {selectedPet.photoUrl ? (
                                        <img src={selectedPet.photoUrl} alt={selectedPet.name} style={photoStyle} />
                                    ) : (
                                        <div style={placeholderPhotoStyle}>{selectedPet.name ? selectedPet.name.charAt(0) : "?"}</div>
                                    )}
                                </div>
                                <div>
                                    <div style={{fontSize:"14px", fontWeight:"700", color:"#334155"}}>
                                        {selectedPet.breed || "Irk Bilinmiyor"}
                                    </div>
                                    <div style={{fontSize:"12px", color:"#64748b"}}>
                                        {selectedPet.species || "Tür Bilinmiyor"}
                                    </div>
                                </div>
                            </div>

                            {/* BİLGİ KUTUCUKLARI (Grid) */}
                            <div style={gridContainer}>
                                <div style={infoBox}>
                                    <label style={detailLabel}>CİNSİYET</label>
                                    <div style={detailValue}>{selectedPet.gender || "-"}</div>
                                </div>
                                <div style={infoBox}>
                                    <label style={detailLabel}>KİLO</label>
                                    <div style={detailValue}>{selectedPet.weight ? `${selectedPet.weight} kg` : "-"}</div>
                                </div>
                                <div style={infoBox}>
                                    <label style={detailLabel}>DOĞUM TARİHİ</label>
                                    <div style={detailValue}>{selectedPet.birthDate || "-"}</div>
                                </div>
                                <div style={infoBox}>
                                    <label style={detailLabel}>HASTA SAHİBİ</label>
                                    <div style={detailValue}>{selectedPet.owner}</div>
                                </div>
                            </div>

                            {/* ID VE KOPYALA */}
                            <div>
                                <label style={detailLabel}>PET ID (Sistem No)</label>
                                <div style={{display:"flex", gap:"10px", alignItems:"center", marginTop:"2px"}}>
                                    <code style={codeStyle}>{selectedPet.petId || "Yok"}</code>
                                    <HoverButton onClick={copyPetId} baseStyle={copyBtnBase} hoverStyle={copyBtnHover}>
                                        Kopyala
                                    </HoverButton>
                                </div>
                            </div>

                            {/* AKSİYON */}
                            <div style={{marginTop:"5px", paddingTop:"15px", borderTop:"1px solid #f1f5f9"}}>
                                <HoverButton
                                    onClick={handleAddMedicalRecord}
                                    baseStyle={medicalRecordBtnBase}
                                    hoverStyle={medicalRecordBtnHover}
                                >
                                    ✚ Tıbbi Kayıt Ekle
                                </HoverButton>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* --- İPTAL ONAY MODALI --- */}
            {cancelModal.show && (
                <div style={modalOverlayStyle}>
                    <div style={modalBoxStyle}>
                        <h3 style={{marginTop:0, color: "#991b1b"}}>⚠️ İptal Onayı</h3>
                        <p style={{color: "#475569", marginBottom: "25px"}}>
                            Bu randevuyu iptal etmek istediğinize emin misiniz?
                        </p>
                        <div style={{display: "flex", gap: "10px", justifyContent: "flex-end"}}>
                            <HoverButton onClick={closeCancelModal} baseStyle={modalCancelBtnBase} hoverStyle={modalCancelBtnHover}>
                                Vazgeç
                            </HoverButton>
                            <HoverButton onClick={confirmCancellation} baseStyle={modalConfirmBtnBase} hoverStyle={modalConfirmBtnHover}>
                                Evet, İptal Et
                            </HoverButton>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

// --- STİLLER ---
const headerStyle = { fontSize: "20px", fontWeight: "800", color: "#1e293b", marginBottom: "5px" };
const subHeaderStyle = { fontSize: "13px", color: "#64748b", marginBottom: "25px" };
const emptyStyle = { padding: "40px", textAlign: "center", background: "white", borderRadius: "10px", border: "1px dashed #cbd5e1", color: "#94a3b8" };

const tableContainer = { overflowX: "auto", background: "white", borderRadius: "12px", border: "1px solid #e2e8f0", boxShadow: "0 2px 5px rgba(0,0,0,0.02)", overflow: "hidden" };
const tableStyle = { width: "100%", borderCollapse: "collapse", minWidth: "600px" };
const thRowStyle = { background: "#f8fafc", borderBottom: "2px solid #e2e8f0" };

// Hizalama Çözümü: verticalAlign middle eklendi
const thStyle = { padding: "15px", textAlign: "left", fontSize: "12px", fontWeight: "700", color: "#475569", textTransform: "uppercase", verticalAlign: "middle" };
const trStyle = { borderBottom: "1px solid #f1f5f9" };
const tdStyle = { padding: "12px 15px", fontSize: "14px", color: "#334155", verticalAlign: "middle" };

// --- BUTON STİLLERİ ---
const cancelBtnBase = { padding: "6px 12px", background: "#fee2e2", color: "#b91c1c", border: "1px solid #fca5a5", borderRadius: "6px", cursor: "pointer", fontSize: "12px", fontWeight: "bold" };
const cancelBtnHover = { background: "#fecaca", borderColor: "#f87171" };

const medicalRecordBtnBase = { width: "100%", padding: "12px", background: "#3b82f6", color: "white", border: "none", borderRadius: "8px", fontWeight: "bold", fontSize: "14px", cursor: "pointer", boxShadow: "0 4px 6px -1px rgba(59, 130, 246, 0.3)" };
const medicalRecordBtnHover = { background: "#2563eb", boxShadow: "0 6px 10px -1px rgba(59, 130, 246, 0.4)" };

const copyBtnBase = { padding: "8px 12px", background: "white", color: "#64748b", border: "1px solid #cbd5e1", borderRadius: "6px", cursor: "pointer", fontWeight: "600", fontSize: "12px" };
const copyBtnHover = { background: "#f8fafc", borderColor: "#94a3b8", color: "#475569" };

const modalCancelBtnBase = { padding: "8px 16px", background: "#f1f5f9", color: "#64748b", border: "1px solid #cbd5e1", borderRadius: "6px", cursor: "pointer", fontWeight: "600" };
const modalCancelBtnHover = { background: "#e2e8f0", color: "#334155" };

const modalConfirmBtnBase = { padding: "8px 16px", background: "#ef4444", color: "white", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600", boxShadow: "0 2px 5px rgba(239, 68, 68, 0.3)" };
const modalConfirmBtnHover = { background: "#dc2626" };

// --- MODAL & DETAY STİLLERİ ---
const modalOverlayStyle = { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 9999 };
const modalBoxStyle = { background: "white", padding: "25px", borderRadius: "15px", width: "380px", boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.2)", position: "relative" };

const detailLabel = { fontSize: "11px", fontWeight: "800", color: "#94a3b8", marginBottom: "4px", display: "block", textTransform: "uppercase" };
const detailValue = { fontSize: "15px", fontWeight: "600", color: "#334155" };

const photoContainerStyle = { width: "60px", height: "60px", borderRadius: "50%", overflow: "hidden", border: "2px solid #e2e8f0", display: "flex", alignItems: "center", justifyContent: "center", background: "#f8fafc" };
const photoStyle = { width: "100%", height: "100%", objectFit: "cover" };
const placeholderPhotoStyle = { width: "100%", height: "100%", display: "flex", alignItems: "center", justifyContent: "center", background: "#e0f2fe", color: "#0284c7", fontSize: "24px", fontWeight: "bold" };

const gridContainer = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", background: "#f8fafc", padding: "10px", borderRadius: "8px", border: "1px solid #f1f5f9" };
const infoBox = { display: "flex", flexDirection: "column" };

const codeStyle = { background: "#f1f5f9", padding: "10px", borderRadius: "6px", flex: 1, fontFamily: "monospace", color: "#334155", fontWeight: "bold", border: "1px solid #e2e8f0", fontSize: "13px" };
const closeXBtn = { background:"transparent", border:"none", cursor:"pointer", fontSize:"18px", color:"#94a3b8" };