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

    // --- DETAY SAYFASI (VIEW MODE) STATE'LERİ ---
    const [viewMode, setViewMode] = useState(false); // Sayfa değişimi için
    const [detailPet, setDetailPet] = useState(null); // Detaydaki Pet Bilgisi
    const [records, setRecords] = useState([]); // Detaydaki Tıbbi Geçmiş
    const [detailLoading, setDetailLoading] = useState(false);

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

    // ✅ Modalde ID Kopyala
    const copyPetId = () => {
        if (selectedPet?.petId) {
            navigator.clipboard.writeText(selectedPet.petId);
            alert("Pet ID kopyalandı!");
        }
    };

    // ✅ Detay sayfasında ID Kopyala
    const copyDetailPetId = () => {
        const id = detailPet?.petId;
        if (!id) return;
        navigator.clipboard.writeText(id);
        alert("Pet ID kopyalandı!");
    };

    const getAge = (birthDate) => {
        if (!birthDate) return "-";
        const today = new Date();
        const birth = new Date(birthDate);
        let age = today.getFullYear() - birth.getFullYear();
        if (
            new Date(today.getFullYear(), today.getMonth(), today.getDate()) <
            new Date(today.getFullYear(), birth.getMonth(), birth.getDate())
        ) {
            age--;
        }
        return age < 0 ? 0 : age;
    };

    // --- 4. DETAY SAYFASINA GEÇİŞ (MODALDAN TETİKLENİR) ---
    const switchToDetailView = async () => {
        if (!selectedPet?.petId) return;

        const petSnapshot = selectedPet
        const petId = petSnapshot.petId;

        setSelectedPet(null);
        setViewMode(true);
        setDetailLoading(true);

        try {
            setDetailPet(petSnapshot); // Eldeki veriyi önden göster
            const recRes = await http.get(`/api/medical-records/pet/${petId}`);
            setRecords(recRes.data || []);
        } catch (err) {
            console.error("Detay yüklenemedi", err);
            alert("Kayıtlar çekilemedi.");
            setViewMode(false);
        } finally {
            setDetailLoading(false);
        }
    };

    if (loading) return <div style={{ padding: 20, color: "#64748b" }}>⏳ Randevular yükleniyor...</div>;
    if (error) return <div style={{ padding: 20, color: "#ef4444" }}>⚠️ {error}</div>;


    // =================================================================================
    // RENDER: EĞER VIEW MODE AKTİFSE (DETAY SAYFASI)
    // =================================================================================
    if (viewMode) {
        return (
            <div>
                {/* Header & Geri Butonu */}
                <div
                    style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        marginBottom: "25px",
                    }}
                >
                    <button onClick={() => setViewMode(false)} style={backBtnStyle}>
                        ← Listeye Dön
                    </button>

                    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                        <div style={{ fontSize: "13px", color: "#94a3b8", fontWeight: "500" }}>
                            Klinik Yönetimi / Pet Detay /{" "}
                            <span style={{ color: "#0284c7" }}>{detailPet?.name}</span>
                        </div>

                        <HoverButton
                            onClick={copyDetailPetId}
                            baseStyle={copyBtnBase}
                            hoverStyle={copyBtnHover}
                            disabled={!detailPet?.petId}
                            disabledstyle={{ ...copyBtnBase, cursor: "not-allowed", opacity: 0.6 }}
                        >
                            📋 ID Kopyala
                        </HoverButton>
                    </div>
                </div>

                {detailLoading ? (
                    <div style={{ padding: 40, textAlign: "center" }}>⏳ Dosya yükleniyor...</div>
                ) : (
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "320px 1fr",
                            gap: "30px",
                            alignItems: "start",
                        }}
                    >
                        {/* --- SOL KOLON: PROFİL KARTI --- */}
                        <div style={profileCard}>
                            <div style={photoWrapper}>
                                {detailPet?.photoUrl ? (
                                    <img src={detailPet.photoUrl} style={photoImg} alt="pet" />
                                ) : (
                                    <div style={photoPlaceholder}>{detailPet?.name?.charAt(0)}</div>
                                )}
                            </div>

                            <div style={{ textAlign: "center", marginBottom: "20px" }}>
                                <h1 style={petName}>{detailPet?.name}</h1>
                                <span style={speciesBadge}>{detailPet?.species}</span>
                            </div>

                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
                                <InfoBox label="IRK" value={detailPet?.breed} icon="🧬" />
                                <InfoBox
                                    label="CİNSİYET"
                                    value={detailPet?.gender === "MALE" ? "Erkek ♂" : "Dişi ♀"}
                                    icon="⚧"
                                />
                                <InfoBox label="DOGUM TARİHİ" value={`${detailPet?.birthDate}`} icon="🎂" />
                                <InfoBox label="KİLO" value={detailPet?.weight ? `${detailPet.weight} kg` : "-"} icon="⚖️" />
                                <div
                                    style={{
                                        gridColumn: "span 2",
                                        background: "#f8fafc",
                                        padding: "10px",
                                        borderRadius: "8px",
                                        border: "1px solid #f1f5f9",
                                    }}
                                >
                                    <div style={{ fontSize: "10px", fontWeight: "700", color: "#94a3b8" }}>SAHİP</div>
                                    <div style={{ fontSize: "13px", fontWeight: "600", color: "#334155" }}>
                                        {detailPet?.owner}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* --- SAĞ KOLON: TIBBİ GEÇMİŞ --- */}
                        <div style={{ minHeight: "500px" }}>
                            <div
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                    marginBottom: "20px",
                                }}
                            >
                                <h2 style={sectionHeader}>📋 Tıbbi Geçmiş ve Tedaviler</h2>
                                <span style={recordCountBadge}>{records.length} Kayıt</span>
                            </div>

                            {records.length === 0 ? (
                                <div style={emptyState}>
                                    <span style={{ fontSize: "40px" }}>📂</span>
                                    <p>Henüz tıbbi kayıt girilmemiş.</p>
                                </div>
                            ) : (
                                <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
                                    {[...records]
                                        .sort((a, b) => new Date(b.date) - new Date(a.date))
                                        .map((rec) => (
                                            <div key={rec.recordId || rec.id} style={recordCard}>
                                                {/* Kayıt Başlığı */}
                                                <div style={recordHeader}>
                                                    <div style={dateBox}>
                                                        <span style={{ fontSize: "18px", fontWeight: "800" }}>{rec.date.split("-")[2]}</span>
                                                        <span style={{ fontSize: "11px", textTransform: "uppercase" }}>
                              {new Date(rec.date).toLocaleString("tr-TR", { month: "short" })}
                            </span>
                                                        <span style={{ fontSize: "11px", color: "#94a3b8" }}>{rec.date.split("-")[0]}</span>
                                                    </div>

                                                    <div style={{ flex: 1 }}>
                                                        <div style={diagnosisTitle}>{rec.description || "Genel Muayene"}</div>
                                                        <div style={vetInfo}>
                                                            👨‍⚕️ <b>{rec.vetName || "Hekim Belirtilmemiş"}</b> &nbsp;|&nbsp; 🏥{" "}
                                                            {rec.clinicName || "Klinik Belirtilmemiş"}
                                                        </div>
                                                    </div>
                                                </div>

                                                {/* İlaçlar Bölümü */}
                                                {rec.medications && rec.medications.length > 0 && (
                                                    <div style={medicationSection}>
                                                        <div style={medTitle}>💊 REÇETELİ İLAÇLAR</div>
                                                        <div style={medGrid}>
                                                            {rec.medications.map((med, i) => {
                                                                const isActive = isMedicationActive(med.end);
                                                                return (
                                                                    <div key={i} style={medCard}>
                                                                        <div
                                                                            style={{
                                                                                display: "flex",
                                                                                justifyContent: "space-between",
                                                                                alignItems: "start",
                                                                            }}
                                                                        >
                                                                            <div>
                                                                                <div style={medName}>{med.medicineName}</div>
                                                                                <div style={medType}>{med.medicineType}</div>
                                                                            </div>
                                                                            <span style={isActive ? activeBadge : passiveBadge}>
                                        {isActive ? "DEVAM EDİYOR" : "BİTTİ"}
                                      </span>
                                                                        </div>

                                                                        <div style={medDates}>
                                                                            {med.start} — {med.end || "?"}
                                                                        </div>

                                                                        {med.instructions && <div style={medNote}>ℹ️ {med.instructions}</div>}
                                                                    </div>
                                                                );
                                                            })}
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>
        );
    }

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

                                <td style={tdStyle}>{appt.vetName}</td>
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

            {/* --- PET DETAY MODALI --- */}
            {selectedPet && (
                <div style={modalOverlayStyle}>
                    <div style={modalBoxStyle}>
                        <div
                            style={{
                                display: "flex",
                                justifyContent: "space-between",
                                marginBottom: "15px",
                                borderBottom: "1px solid #f1f5f9",
                                paddingBottom: "10px",
                            }}
                        >
                            <h3 style={{ margin: 0, color: "#1e293b" }}>🐾 {selectedPet.name}</h3>
                            <button onClick={() => setSelectedPet(null)} style={closeXBtn}>
                                ✕
                            </button>
                        </div>

                        {selectedPet.loading ? (
                            <div>Yükleniyor...</div>
                        ) : (
                            <div style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
                                <div style={{ display: "flex", gap: "15px", alignItems: "center" }}>
                                    <div style={photoContainerStyle}>
                                        {selectedPet.photoUrl ? (
                                            <img src={selectedPet.photoUrl} alt="pet" style={photoStyle} />
                                        ) : (
                                            <div style={{ fontWeight: 900, color: "#64748b" }}>?</div>
                                        )}
                                    </div>
                                    <div>
                                        <div style={{ fontWeight: "bold" }}>{selectedPet.breed || "-"}</div>
                                        <div style={{ fontSize: "12px", color: "#64748b" }}>{selectedPet.species || "-"}</div>
                                    </div>
                                </div>

                                <div style={gridContainer}>
                                    <div>
                                        <label style={detailLabel}>CİNSİYET</label>
                                        <div style={{ fontWeight: 700, color: "#334155" }}>{selectedPet.gender || "-"}</div>
                                    </div>
                                    <div>
                                        <label style={detailLabel}>KİLO</label>
                                        <div style={{ fontWeight: 700, color: "#334155" }}>
                                            {selectedPet.weight ? `${selectedPet.weight} kg` : "-"}
                                        </div>
                                    </div>
                                    <div style={{ gridColumn: "1" }}>
                                        <label style={detailLabel}>SAHİP</label>
                                        <div style={{ fontWeight: 700, color: "#334155" }}>{selectedPet.owner || "-"}</div>
                                    </div>

                                    <div style={{ gridColumn: "2" }}>
                                        <label style={detailLabel}>YAŞ</label>
                                        <div style={{ fontWeight: 700, color: "#334155" }}>{getAge(selectedPet.birthDate) || "-"}</div>
                                    </div>
                                </div>

                                {/* ✅ ID + KOPYALA */}
                                <div style={{ marginTop: "2px" }}>
                                    <label style={detailLabel}>ID</label>

                                    <div style={{ display: "flex", gap: "10px", alignItems: "center", marginTop: "4px" }}>
                                        <code style={{ ...codeStyle, flex: 1 }}>{selectedPet.petId}</code>

                                        <HoverButton onClick={copyPetId} baseStyle={copyBtnBase} hoverStyle={copyBtnHover}>
                                            Kopyala
                                        </HoverButton>
                                    </div>
                                </div>

                                {/* DETAY SAYFASINA GEÇİŞ BUTONU */}
                                <div style={{ marginTop: "5px", paddingTop: "15px", borderTop: "1px solid #f1f5f9" }}>
                                    <HoverButton onClick={switchToDetailView} baseStyle={detailsBtnBase} hoverStyle={detailsBtnHover}>
                                        📄 Detayları & Geçmişi Gör
                                    </HoverButton>
                                </div>
                            </div>
                        )}
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

// --- ALT BİLEŞEN: KUTUCUK ---
const InfoBox = ({ label, value, icon }) => (
    <div style={{ background: "#f8fafc", padding: "10px", borderRadius: "8px", border: "1px solid #f1f5f9" }}>
        <div style={{ fontSize: "10px", fontWeight: "700", color: "#94a3b8", marginBottom: "2px" }}>{label}</div>
        <div style={{ fontSize: "13px", fontWeight: "600", color: "#334155", display: "flex", alignItems: "center", gap: "5px" }}>
            <span>{icon}</span> {value || "-"}
        </div>
    </div>
);

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
const cancelBtnBase = {padding: "6px 12px", background: "#fee2e2", color: "#b91c1c", border: "1px solid #fca5a5",};
const cancelBtnHover = {background: "#fecaca", border: "1px solid #f87171"};

// Kopyala Butonu
const copyBtnBase = {padding: "8px 12px", background: "white", color: "#64748b", border: "1px solid #cbd5e1", borderRadius: "6px", cursor: "pointer", fontWeight: "600", fontSize: "12px"};
const copyBtnHover = {background: "#f8fafc", border: "1px solid #94a3b8", color: "#475569"};

const modalCancelBtnBase = { padding: "8px 16px", background: "#f1f5f9", color: "#64748b", border: "1px solid #cbd5e1", borderRadius: "6px", cursor: "pointer", fontWeight: "600" };
const modalCancelBtnHover = { background: "#e2e8f0", color: "#334155" };

const modalConfirmBtnBase = { padding: "8px 16px", background: "#ef4444", color: "white", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600", boxShadow: "0 2px 5px rgba(239, 68, 68, 0.3)" };
const modalConfirmBtnHover = { background: "#dc2626" };

// --- MODAL & DETAY STİLLERİ ---
const modalOverlayStyle = { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 9999 };
const modalBoxStyle = { background: "white", padding: "25px", borderRadius: "15px", width: "380px", boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.2)", position: "relative" };

const detailLabel = { fontSize: "11px", fontWeight: "800", color: "#94a3b8", marginBottom: "4px", display: "block", textTransform: "uppercase" };

const photoContainerStyle = { width: "60px", height: "60px", borderRadius: "50%", overflow: "hidden", border: "2px solid #e2e8f0", display: "flex", alignItems: "center", justifyContent: "center", background: "#f8fafc" };
const photoStyle = { width: "100%", height: "100%", objectFit: "cover" };

const gridContainer = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", background: "#f8fafc", padding: "10px", borderRadius: "8px", border: "1px solid #f1f5f9" };

const codeStyle = { background: "#f1f5f9", padding: "10px", borderRadius: "6px", flex: 1, fontFamily: "monospace", color: "#334155", fontWeight: "bold", border: "1px solid #e2e8f0", fontSize: "13px" };
const closeXBtn = { background:"transparent", border:"none", cursor:"pointer", fontSize:"18px", color:"#94a3b8" };

// --- DETAY SAYFASI ÖZEL STİLLERİ ---
const backBtnStyle = { background: "white", border: "1px solid #e2e8f0", padding: "8px 16px", borderRadius: "8px", cursor: "pointer", fontWeight: "600", color: "#475569", display: "flex", alignItems: "center", gap: "5px", boxShadow: "0 1px 2px rgba(0,0,0,0.05)" };
const profileCard = { background: "white", borderRadius: "16px", padding: "25px", border: "1px solid #e2e8f0", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)", height: "fit-content" };
const photoWrapper = { width: "120px", height: "120px", borderRadius: "50%", margin: "0 auto 15px auto", overflow: "hidden", border: "4px solid #f0f9ff", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)" };
const photoImg = { width: "100%", height: "100%", objectFit: "cover" };
const photoPlaceholder = { width: "100%", height: "100%", background: "#e0f2fe", color: "#0284c7", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "40px", fontWeight: "bold" };
const petName = { margin: "0 0 5px 0", fontSize: "22px", color: "#1e293b", fontWeight: "800" };
const speciesBadge = { background: "#e0f2fe", color: "#0284c7", padding: "4px 12px", borderRadius: "20px", fontSize: "12px", fontWeight: "700", textTransform: "uppercase" };
const sectionHeader = { fontSize: "18px", fontWeight: "800", color: "#1e293b", margin: 0 };
const recordCountBadge = { background: "#f1f5f9", color: "#64748b", padding: "4px 10px", borderRadius: "6px", fontSize: "12px", fontWeight: "600" };
const emptyState = { background: "white", padding: "50px", borderRadius: "12px", border: "2px dashed #e2e8f0", textAlign: "center", color: "#94a3b8" };
const recordCard = { background: "white", borderRadius: "12px", border: "1px solid #e2e8f0", overflow: "hidden", boxShadow: "0 2px 4px rgba(0,0,0,0.02)" };
const recordHeader = { display: "flex", gap: "15px", padding: "20px", borderBottom: "1px solid #f8fafc" };
const dateBox = { display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", background: "#f8fafc", width: "60px", height: "60px", borderRadius: "10px", border: "1px solid #e2e8f0", color: "#334155" };
const diagnosisTitle = { fontSize: "16px", fontWeight: "700", color: "#1e293b", marginBottom: "4px" };
const vetInfo = { fontSize: "13px", color: "#64748b" };
const medicationSection = { background: "#f8fafc", padding: "15px 20px" };
const medTitle = { fontSize: "11px", fontWeight: "800", color: "#64748b", marginBottom: "10px", textTransform: "uppercase", letterSpacing: "0.5px" };
const medGrid = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))", gap: "10px" };
const medCard = { background: "white", padding: "12px", borderRadius: "8px", border: "1px solid #e2e8f0", borderLeft: "3px solid #0284c7" };
const medName = { fontSize: "14px", fontWeight: "700", color: "#1e293b" };
const medType = { fontSize: "11px", color: "#64748b", fontStyle: "italic", marginBottom: "5px" };
const medDates = { fontSize: "11px", color: "#475569", marginTop: "5px", display: "flex", alignItems: "center", gap: "5px" };
const medNote = { marginTop: "8px", fontSize: "12px", color: "#334155", background: "#f1f5f9", padding: "6px", borderRadius: "4px", fontStyle: "italic" };
const activeBadge = { fontSize: "9px", fontWeight: "800", color: "white", background: "#10b981", padding: "2px 6px", borderRadius: "4px" };
const passiveBadge = { fontSize: "9px", fontWeight: "800", color: "white", background: "#94a3b8", padding: "2px 6px", borderRadius: "4px" };
const detailsBtnBase = { width: "100%", padding: "12px", background: "#6366f1", color: "white", border: "none", borderRadius: "8px", fontWeight: "bold", fontSize: "14px", cursor: "pointer", boxShadow: "0 4px 6px -1px rgba(99, 102, 241, 0.3)" };
const detailsBtnHover = { background: "#4f46e5", boxShadow: "0 6px 10px -1px rgba(99, 102, 241, 0.4)" };
