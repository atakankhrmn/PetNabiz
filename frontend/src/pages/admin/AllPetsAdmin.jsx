import { useState, useEffect } from "react";
import { http } from "../../api/http";

// --- SABİT ---
const BASE_IMAGE_URL = "http://localhost:8080/uploads/pets/";

// --- STİLLER ---
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "20px", border: "1px solid #e2e8f0", marginTop: "20px" };
const tableHeaderStyle = { padding: "12px", textAlign: "left", color: "#64748b", borderBottom: "1px solid #e2e8f0", fontSize: "13px", fontWeight: "700", textTransform:"uppercase", letterSpacing:"0.5px" };
const tableCellStyle = { padding: "12px", borderBottom: "1px solid #f1f5f9", fontSize: "14px", color: "#334155", verticalAlign: "middle" };

// Modal Stilleri
const modalOverlayStyle = { position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center", zIndex: 1000 };
const modalContentStyle = { background: "white", width: "800px", maxHeight: "90vh", borderRadius: "16px", padding: "30px", overflowY: "auto", position: "relative", boxShadow: "0 10px 40px rgba(0,0,0,0.2)" };
const inputStyle = { width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #cbd5e1", marginTop: "5px", marginBottom: "15px", fontSize: "14px" };
const labelStyle = { fontSize: "12px", fontWeight: "700", color: "#475569", marginBottom: "4px", display: "block" };

export default function AllPetsAdmin() {
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);

    // Modal State'leri
    const [selectedPet, setSelectedPet] = useState(null);
    const [activeTab, setActiveTab] = useState("info");
    const [petRecords, setPetRecords] = useState([]);

    // Düzenlenen Kayıt State'i
    const [editingRecordId, setEditingRecordId] = useState(null);
    const [editRecordData, setEditRecordData] = useState({});

    useEffect(() => {
        fetchPets();
    }, []);

    const fetchPets = async () => {
        try {
            const res = await http.get("/api/pets");
            setPets(res.data);
        } catch (error) {
            console.error("Pet listesi hatası:", error);
        } finally {
            setLoading(false);
        }
    };

    // --- MODAL AÇMA / KAPAMA ---
    const openModal = (pet) => {
        setSelectedPet({ ...pet });
        setActiveTab("info");
        fetchPetRecords(pet.petId);
    };

    const closeModal = () => {
        setSelectedPet(null);
        setPetRecords([]);
        setEditingRecordId(null);
    };

    // --- PET GÜNCELLEME ---
    const handlePetUpdate = async (e) => {
        e.preventDefault();
        try {
            await http.put(`/api/pets/${selectedPet.petId}`, selectedPet);
            alert("✅ Pet bilgileri güncellendi!");
            fetchPets();
        } catch (error) {
            alert("Güncelleme hatası: " + (error.response?.data?.message || "Bilinmeyen hata"));
        }
    };

    // --- MEDİKAL KAYITLARI ÇEKME ---
    const fetchPetRecords = async (petId) => {
        try {
            const res = await http.get(`/api/medical-records/pet/${petId}`);
            setPetRecords(res.data);
        } catch (error) {
            console.error("Kayıtlar çekilemedi", error);
        }
    };

    // --- MEDİKAL KAYIT DÜZENLEME MODUNU BAŞLAT ---
    const startEditingRecord = (record) => {
        setEditingRecordId(record.recordId);
        // İlaçları da state'e alıyoruz (Deep copy yaparak)
        setEditRecordData({
            description: record.description,
            date: record.date,
            medications: record.medications ? JSON.parse(JSON.stringify(record.medications)) : []
        });
    };

    // --- İLAÇ GÜNCELLEME HANDLER ---
    const handleMedicationChange = (index, field, value) => {
        const updatedMeds = [...editRecordData.medications];
        updatedMeds[index] = { ...updatedMeds[index], [field]: value };
        setEditRecordData({ ...editRecordData, medications: updatedMeds });
    };

    // --- MEDİKAL KAYIT KAYDET ---
    const saveMedicalRecord = async (recordId) => {
        try {
            const payload = {
                description: editRecordData.description,
                date: editRecordData.date,
                petId: selectedPet.petId,
                vetId: null,
                medications: editRecordData.medications // Güncellenmiş ilaçları da gönderiyoruz
            };
            await http.put(`/api/medical-records/${recordId}`, payload);
            alert("✅ Muayene kaydı ve reçete güncellendi.");
            setEditingRecordId(null);
            fetchPetRecords(selectedPet.petId);
        } catch (error) {
            console.error(error);
            alert("Kayıt güncellenemedi.");
        }
    };

    // --- SİLME ---
    const handleDelete = async (petId) => {
        if (!window.confirm("Bu kaydı silmek istediğinize emin misiniz?")) return;
        try {
            await http.delete(`/api/pets/${petId}`);
            setPets(pets.filter(p => p.petId !== petId));
        } catch (error) {
            alert("Silinemedi.");
        }
    };

    if (loading) return <div>Yükleniyor...</div>;

    return (
        <div>
            <h3 style={{ fontSize: "18px", color: "#1e293b" }}>🐾 Tüm Evcil Hayvanlar</h3>

            <div style={cardStyle}>
                {pets.length === 0 ? <p>Kayıtlı pet yok.</p> : (
                    <table style={{ width: "100%", borderCollapse: "collapse" }}>
                        <thead>
                        <tr style={{ background: "#f8fafc" }}>
                            <th style={tableHeaderStyle}>Pet</th>
                            <th style={tableHeaderStyle}>Tür / Irk</th>
                            <th style={tableHeaderStyle}>Cinsiyet</th>
                            <th style={tableHeaderStyle}>Doğum Tarihi</th>
                            <th style={tableHeaderStyle}>Sahip</th>
                            <th style={{ ...tableHeaderStyle, textAlign: "right" }}>İşlem</th>
                        </tr>
                        </thead>
                        <tbody>
                        {pets.map((pet) => (
                            <tr key={pet.petId} style={{ borderBottom: "1px solid #f1f5f9" }}>
                                {/* PET RESMİ VE ADI */}
                                <td style={tableCellStyle}>
                                    <div style={{display:"flex", alignItems:"center", gap:"12px"}}>
                                        <div style={{ width: "36px", height: "36px", borderRadius: "50%", overflow: "hidden", background: "#e0f2fe", display: "flex", alignItems: "center", justifyContent: "center", border:"1px solid #cbd5e1" }}>
                                            {pet.photoUrl ? <img src={`${BASE_IMAGE_URL}${pet.photoUrl}`} style={{ width: "100%", height: "100%", objectFit: "cover" }} /> : <b>{pet.name?.[0]}</b>}
                                        </div>
                                        <div style={{fontWeight:"600", color:"#1e293b"}}>{pet.name}</div>
                                    </div>
                                </td>
                                <td style={tableCellStyle}>
                                    <div style={{display:"flex", flexDirection:"column"}}>
                                        <span style={{fontSize:"11px", fontWeight:"800", color:"#94a3b8", textTransform:"uppercase"}}>{pet.species || "TÜR"}</span>
                                        <span style={{fontWeight:"600", fontSize:"13px"}}>{pet.breed}</span>
                                    </div>
                                </td>
                                <td style={tableCellStyle}>
                                    {pet.gender === 'MALE' ? (
                                        <span style={{background:"#eff6ff", color:"#2563eb", padding:"4px 10px", borderRadius:"12px", fontSize:"12px", fontWeight:"600", border:"1px solid #bfdbfe"}}>Erkek ♂</span>
                                    ) : (
                                        <span style={{background:"#fdf2f8", color:"#db2777", padding:"4px 10px", borderRadius:"12px", fontSize:"12px", fontWeight:"600", border:"1px solid #fbcfe8"}}>Dişi ♀</span>
                                    )}
                                </td>
                                <td style={tableCellStyle}>
                                    {pet.birthDate ? <span style={{fontFamily:"monospace", fontSize:"13px", color:"#475569"}}>{pet.birthDate}</span> : "-"}
                                </td>
                                <td style={tableCellStyle}>
                                    <div style={{display:"flex", alignItems:"center", gap:"6px", color:"#475569"}}><span>👤</span> {pet.ownerName}</div>
                                </td>
                                <td style={{ ...tableCellStyle, textAlign: "right" }}>
                                    <div style={{display:"flex", gap:"8px", justifyContent:"flex-end"}}>
                                        <button onClick={() => openModal(pet)} style={{ background: "#3b82f6", color: "white", border: "none", padding: "6px 12px", borderRadius: "6px", cursor: "pointer", fontSize:"12px", fontWeight:"600" }}>Düzenle</button>
                                        <button onClick={() => handleDelete(pet.petId)} style={{ background: "white", color: "#ef4444", border: "1px solid #fecaca", padding: "6px 12px", borderRadius: "6px", cursor: "pointer", fontSize:"12px", fontWeight:"600" }}>Sil</button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>

            {/* --- MODAL --- */}
            {selectedPet && (
                <div style={modalOverlayStyle} onClick={(e) => { if(e.target === e.currentTarget) closeModal(); }}>
                    <div style={modalContentStyle}>
                        <button onClick={closeModal} style={{ position: "absolute", top: "20px", right: "20px", border: "none", background: "none", fontSize: "20px", cursor: "pointer", color: "#64748b" }}>✕</button>

                        <div style={{display:"flex", alignItems:"center", gap:"15px", marginBottom:"20px", paddingBottom:"20px", borderBottom:"1px solid #f1f5f9"}}>
                            <div style={{ width: "64px", height: "64px", borderRadius: "50%", overflow: "hidden", background: "#f1f5f9", display: "flex", alignItems: "center", justifyContent: "center", fontSize:"24px", border:"1px solid #e2e8f0" }}>
                                {selectedPet.photoUrl ? <img src={`${BASE_IMAGE_URL}${selectedPet.photoUrl}`} style={{ width: "100%", height: "100%", objectFit: "cover" }} /> : <b>{selectedPet.name?.[0]}</b>}
                            </div>
                            <div>
                                <h2 style={{ margin: 0, color: "#1e293b", fontSize:"20px" }}>{selectedPet.name}</h2>
                                <div style={{display:"flex", gap:"10px", marginTop:"5px"}}>
                                    <span style={{background:"#f1f5f9", padding:"2px 8px", borderRadius:"4px", fontSize:"12px", color:"#64748b", fontWeight:"600"}}>{selectedPet.species || "Tür Belirsiz"}</span>
                                    <span style={{background:"#f1f5f9", padding:"2px 8px", borderRadius:"4px", fontSize:"12px", color:"#64748b", fontWeight:"600"}}>{selectedPet.breed}</span>
                                </div>
                            </div>
                        </div>

                        <div style={{ display: "flex", gap: "20px", marginBottom: "25px", borderBottom: "1px solid #e2e8f0" }}>
                            <button onClick={() => setActiveTab("info")} style={{ padding: "10px 5px", border: "none", background: "none", cursor: "pointer", borderBottom: activeTab === "info" ? "3px solid #3b82f6" : "3px solid transparent", fontWeight: activeTab === "info" ? "700" : "500", color: activeTab === "info" ? "#3b82f6" : "#64748b", fontSize:"14px" }}>
                                ℹ️ Pet Bilgileri
                            </button>
                            <button onClick={() => setActiveTab("records")} style={{ padding: "10px 5px", border: "none", background: "none", cursor: "pointer", borderBottom: activeTab === "records" ? "3px solid #3b82f6" : "3px solid transparent", fontWeight: activeTab === "records" ? "700" : "500", color: activeTab === "records" ? "#3b82f6" : "#64748b", fontSize:"14px" }}>
                                🩺 Tıbbi Geçmiş ({petRecords.length})
                            </button>
                        </div>

                        {/* --- TAB 1: PET BİLGİLERİ FORM --- */}
                        {activeTab === "info" && (
                            <form onSubmit={handlePetUpdate}>
                                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px" }}>
                                    <div><label style={labelStyle}>Pet Adı</label><input type="text" style={inputStyle} value={selectedPet.name} onChange={(e) => setSelectedPet({ ...selectedPet, name: e.target.value })} /></div>
                                    <div><label style={labelStyle}>Tür (Species)</label><input type="text" style={inputStyle} value={selectedPet.species || ""} onChange={(e) => setSelectedPet({ ...selectedPet, species: e.target.value })} placeholder="Örn: DOG" /></div>
                                    <div><label style={labelStyle}>Irk (Breed)</label><input type="text" style={inputStyle} value={selectedPet.breed} onChange={(e) => setSelectedPet({ ...selectedPet, breed: e.target.value })} /></div>
                                    <div>
                                        <label style={labelStyle}>Cinsiyet</label>
                                        <select style={inputStyle} value={selectedPet.gender} onChange={(e) => setSelectedPet({ ...selectedPet, gender: e.target.value })}>
                                            <option value="MALE">Erkek</option>
                                            <option value="FEMALE">Dişi</option>
                                        </select>
                                    </div>
                                    <div><label style={labelStyle}>Doğum Tarihi</label><input type="date" style={inputStyle} value={selectedPet.birthDate} onChange={(e) => setSelectedPet({ ...selectedPet, birthDate: e.target.value })} /></div>
                                </div>
                                <div style={{ marginTop: "15px", textAlign: "right" }}>
                                    <button type="submit" style={{ padding: "10px 24px", background: "#10b981", color: "white", border: "none", borderRadius: "8px", cursor: "pointer", fontWeight:"bold", fontSize:"14px" }}>
                                        Bilgileri Güncelle
                                    </button>
                                </div>
                            </form>
                        )}

                        {/* --- TAB 2: TIBBİ GEÇMİŞ --- */}
                        {activeTab === "records" && (
                            <div>
                                {petRecords.length === 0 ? (
                                    <div style={{ textAlign: "center", padding: "30px", background: "#f8fafc", borderRadius: "8px", color: "#94a3b8", fontSize:"14px" }}>
                                        Kayıt bulunamadı.
                                    </div>
                                ) : (
                                    <div style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
                                        {petRecords.map(record => (
                                            <div key={record.recordId} style={{ border: "1px solid #e2e8f0", padding: "15px", borderRadius: "8px", background: editingRecordId === record.recordId ? "#eff6ff" : "white" }}>
                                                {editingRecordId === record.recordId ? (
                                                    // --- DÜZENLEME MODU (GÜNCELLENDİ) ---
                                                    <div>
                                                        <div style={{marginBottom:"10px", fontWeight:"bold", color:"#3b82f6", fontSize:"13px"}}>Kayıt Düzenleniyor</div>

                                                        <div style={{ marginBottom: "10px" }}><label style={labelStyle}>Tarih</label><input type="date" style={inputStyle} value={editRecordData.date} onChange={(e) => setEditRecordData({...editRecordData, date: e.target.value})} /></div>
                                                        <div style={{ marginBottom: "15px" }}><label style={labelStyle}>Açıklama / Teşhis</label><textarea style={{ ...inputStyle, height: "80px", fontFamily: "inherit" }} value={editRecordData.description} onChange={(e) => setEditRecordData({...editRecordData, description: e.target.value})} /></div>

                                                        {/* --- İLAÇ DÜZENLEME ALANI --- */}
                                                        {editRecordData.medications && editRecordData.medications.length > 0 && (
                                                            <div style={{ background: "white", padding: "15px", borderRadius: "8px", border: "1px solid #cbd5e1", marginBottom: "15px" }}>
                                                                <h4 style={{fontSize:"13px", color:"#334155", margin:"0 0 10px 0"}}>💊 Reçete Düzenle</h4>
                                                                {editRecordData.medications.map((med, idx) => (
                                                                    <div key={idx} style={{ marginBottom: "15px", borderBottom: "1px solid #f1f5f9", paddingBottom: "10px" }}>
                                                                        <div style={{fontWeight:"bold", fontSize:"13px", color:"#c2410c", marginBottom:"5px"}}>{med.medicineName || "İlaç"}</div>
                                                                        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "5px" }}>
                                                                            <div><label style={{fontSize:"11px", color:"#64748b"}}>Başlangıç</label><input type="date" style={{...inputStyle, marginBottom:0, padding:"6px"}} value={med.start} onChange={(e) => handleMedicationChange(idx, "start", e.target.value)} /></div>
                                                                            <div><label style={{fontSize:"11px", color:"#64748b"}}>Bitiş</label><input type="date" style={{...inputStyle, marginBottom:0, padding:"6px"}} value={med.end} onChange={(e) => handleMedicationChange(idx, "end", e.target.value)} /></div>
                                                                        </div>
                                                                        <div>
                                                                            <label style={{fontSize:"11px", color:"#64748b"}}>Talimatlar</label>
                                                                            <input type="text" style={{...inputStyle, marginBottom:0, padding:"6px"}} value={med.instructions} onChange={(e) => handleMedicationChange(idx, "instructions", e.target.value)} />
                                                                        </div>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        )}
                                                        {/* --------------------------- */}

                                                        <div style={{ display: "flex", gap: "10px", justifyContent: "flex-end" }}>
                                                            <button onClick={() => setEditingRecordId(null)} style={{ padding: "6px 12px", border: "1px solid #cbd5e1", background: "white", borderRadius: "6px", cursor: "pointer", fontSize:"12px" }}>İptal</button>
                                                            <button onClick={() => saveMedicalRecord(record.recordId)} style={{ padding: "6px 12px", border: "none", background: "#3b82f6", color: "white", borderRadius: "6px", cursor: "pointer", fontSize:"12px", fontWeight:"600" }}>Kaydet</button>
                                                        </div>
                                                    </div>
                                                ) : (
                                                    // --- GÖRÜNTÜLEME MODU (AYNI KALDI) ---
                                                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
                                                        <div>
                                                            <div style={{ fontSize: "12px", color: "#64748b", marginBottom: "4px" }}>📅 {record.date} • 👨‍⚕️ {record.vetName || "Veteriner"}</div>
                                                            <div style={{ fontSize: "14px", color: "#334155", whiteSpace: "pre-wrap" }}>{record.description}</div>

                                                            {record.medications && record.medications.length > 0 && (
                                                                <div style={{ marginTop: "12px", fontSize: "13px", background: "#fff7ed", padding: "12px", borderRadius: "8px", border: "1px solid #ffedd5", color:"#9a3412" }}>
                                                                    <div style={{marginBottom:"8px", fontWeight:"700"}}>💊 Verilen İlaçlar:</div>
                                                                    <ul style={{ margin: "0 0 0 15px", padding: 0 }}>
                                                                        {record.medications.map((m, index) => (
                                                                            <li key={index} style={{ marginBottom: "8px" }}>
                                                                                <strong style={{color:"#c2410c"}}>{m.medicineName}</strong>
                                                                                <div style={{ fontSize: "11px", opacity: 0.9, marginTop:"2px" }}>
                                                                                    📅 {m.start} / {m.end}
                                                                                </div>
                                                                                {m.instructions && (
                                                                                    <div style={{ fontStyle: "italic", fontSize:"12px", marginTop:"2px" }}>
                                                                                        🗣️ {m.instructions}
                                                                                    </div>
                                                                                )}
                                                                            </li>
                                                                        ))}
                                                                    </ul>
                                                                </div>
                                                            )}
                                                        </div>
                                                        <button onClick={() => startEditingRecord(record)} style={{ fontSize: "12px", color: "#0ea5e9", background: "#f0f9ff", border: "1px solid #bae6fd", padding: "6px 12px", borderRadius: "6px", cursor: "pointer", fontWeight:"600" }}>🖊️</button>
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}