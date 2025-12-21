import { useState, useEffect } from "react";
import { http } from "../../api/http";

// --- SABİT AYARLAR ---
const BASE_IMAGE_URL = "http://localhost:8080/uploads/pets/";

// --- STİLLER ---
const containerStyle = { padding: "30px", maxWidth: "900px", margin: "0 auto" };
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "30px", border: "1px solid #e2e8f0", marginBottom: "25px" };
const labelStyle = { display: "block", fontSize: "13px", fontWeight: "700", color: "#64748b", marginBottom: "6px" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "14px", outline: "none", transition: "0.2s", background: "#fff" };
const headerStyle = { fontSize: "24px", fontWeight: "800", color: "#1e293b", marginBottom: "5px" };
const subHeaderStyle = { color: "#64748b", margin: "0 0 25px 0", fontSize: "14px" };

// Buton Bileşeni
const Button = ({ children, onClick, variant = "primary", disabled, style, type="button" }) => {
    const base = { padding: "12px 24px", borderRadius: "8px", border: "none", cursor: disabled ? "not-allowed" : "pointer", fontWeight: "600", transition: "0.2s", fontSize: "14px", ...style };
    const styles = {
        primary: { background: "#3b82f6", color: "white" },
        secondary: { background: "#f1f5f9", color: "#475569" },
        success: { background: "#10b981", color: "white" },
        danger: { background: "#fee2e2", color: "#ef4444" }, // Silme butonu için
        warning: { background: "#f59e0b", color: "white" } // Listeye ekle için
    };
    return <button type={type} onClick={disabled ? undefined : onClick} style={{ ...base, ...styles[variant], opacity: disabled ? 0.7 : 1 }}>{children}</button>;
};

const radioLabelStyle = { cursor: "pointer", display: "flex", alignItems: "center", gap: "8px", fontWeight: "600", color: "#334155" };

export default function AddMedicalRecord() {
    // --- STATE'LER ---
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const getTodayDate = () => new Date().toISOString().split('T')[0];
    const today = getTodayDate();

    // Arama
    const [searchMode, setSearchMode] = useState("detail");
    const [searchId, setSearchId] = useState("");
    const [searchName, setSearchName] = useState("");
    const [searchPhone, setSearchPhone] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [hasSearched, setHasSearched] = useState(false);

    // Form Verileri
    const [selectedPet, setSelectedPet] = useState(null);
    const [vets, setVets] = useState([]);
    const [selectedVetId, setSelectedVetId] = useState("");

    // Ana Form State
    const [formData, setFormData] = useState({
        description: "",
        weight: "",
    });

    // --- ÇOKLU İLAÇ SİSTEMİ STATE'LERİ ---
    const [addedMedications, setAddedMedications] = useState([]); // Sepetteki ilaçlar

    // Şu an girilmekte olan geçici ilaç bilgisi
    const [currentMed, setCurrentMed] = useState({
        id: null,
        name: "",
        start: today,
        end: today,
        instructions: ""
    });

    // İlaç Autocomplete
    const [allMedicines, setAllMedicines] = useState([]);
    const [filteredMedicines, setFilteredMedicines] = useState([]);
    const [showMedicineList, setShowMedicineList] = useState(false);

    // --- VERİ ÇEKME ---
    useEffect(() => {
        http.get("/api/medicines").then(res => setAllMedicines(res.data)).catch(console.error);
        http.get("/api/veterinaries/my").then(res => {
            setVets(res.data);
            if(res.data.length === 1) setSelectedVetId(res.data[0].vetId || res.data[0].id);
        }).catch(console.error);
    }, []);

    // İlaç Arama Inputu
    const handleMedicineSearch = (e) => {
        const value = e.target.value;
        setCurrentMed({ ...currentMed, name: value });

        if (value === "") setCurrentMed(prev => ({ ...prev, id: null }));

        if (value.length > 0) {
            setFilteredMedicines(allMedicines.filter(med => med.name.toLowerCase().includes(value.toLowerCase())));
            setShowMedicineList(true);
        } else {
            setShowMedicineList(false);
        }
    };

    // İlaç Seçimi
    const selectMedicine = (medicine) => {
        setCurrentMed({ ...currentMed, name: medicine.name, id: medicine.medicineId });
        setShowMedicineList(false);
    };

    // --- İLACI LİSTEYE EKLEME FONKSİYONU ---
    const addMedicationToList = () => {
        // Validasyon
        if (!currentMed.id) return alert("Lütfen listeden geçerli bir ilaç seçiniz.");
        if (!currentMed.start || !currentMed.end) return alert("Lütfen ilaç için başlangıç ve bitiş tarihlerini giriniz.");
        // Ekstra Validasyon: Bitiş tarihi başlangıçtan küçükse uyar (HTML min engellese de manuel girişe karşı)
        if (currentMed.end < currentMed.start) return alert("Bitiş tarihi başlangıç tarihinden önce olamaz.");
        if (!currentMed.instructions) return alert("Lütfen kullanım talimatını giriniz.");



        // Listeye Ekle
        setAddedMedications([...addedMedications, currentMed]);

        // Inputları Temizle
        setCurrentMed({
            id: null,
            name: "",
            start: today,
            end: today,
            instructions: ""
        });
    };

    // Listeden İlaç Çıkarma
    const removeMedicationFromList = (indexToRemove) => {
        setAddedMedications(addedMedications.filter((_, index) => index !== indexToRemove));
    };

    // Arama
    const handleSearch = async () => {
        setLoading(true);
        setHasSearched(true);
        setSearchResults([]);
        try {
            let res;
            if (searchMode === "id") {
                if (!searchId.trim()) { setLoading(false); return alert("Lütfen ID girin"); }
                res = await http.get(`/api/pets/${searchId}`);
            } else {
                if (!searchName.trim() || !searchPhone.trim()) { setLoading(false); return alert("İsim ve Telefon girin"); }
                res = await http.get(`/api/pets/search`, { params: { phone: searchPhone, petName: searchName } });
            }
            setSearchResults(res.data ? [res.data] : []);
        } catch (error) {
            if (error.response?.status === 404) setSearchResults([]);
            else alert("Arama hatası");
        } finally {
            setLoading(false);
        }
    };

    // --- ANA SUBMIT (KAYIT) İŞLEMİ ---
    const handleSubmit = async () => {
        if (!selectedVetId) return alert("Lütfen işlemi yapan veterineri seçiniz.");
        if (!formData.description) return alert("Lütfen açıklama giriniz.");

        // Eğer inputlarda yazılı ama "Listeye Ekle"ye basılmamış ilaç varsa uyar
        if (currentMed.id && currentMed.start) {
            const confirm = window.confirm("Şu an yazmakta olduğunuz ilacı listeye eklemediniz. Eklenmemiş ilaç kaydedilmeyecek. Devam edilsin mi?");
            if (!confirm) return;
        }

        setLoading(true);
        try {
            // 1. ADIM: Medical Record Oluştur
            const recordPayload = {
                date: new Date().toISOString().split('T')[0],
                description: formData.description,
                petId: selectedPet.petId,
                vetId: selectedVetId
            };

            const recordRes = await http.post("/api/medical-records", recordPayload);
            const newRecordId = recordRes.data.recordId;
            console.log("Kayıt Oluştu ID:", newRecordId);

            // 2. ADIM: Listedeki TÜM İlaçları Kaydet (Döngü ile)
            if (addedMedications.length > 0) {
                // Promise.all ile hepsini paralel gönderiyoruz, daha hızlı olur
                await Promise.all(addedMedications.map(med => {
                    const medicationPayload = {
                        medicineId: med.id,
                        recordId: newRecordId,
                        start: med.start,
                        end: med.end,
                        instructions: med.instructions
                    };
                    return http.post("/api/medications", medicationPayload);
                }));
            }

            // 3. ADIM: Kilo Güncelleme
            if (formData.weight) {
                const weightPayload = {
                    petId: selectedPet.petId,
                    weight: parseFloat(formData.weight)
                };
                await http.put("/api/pets/weight", weightPayload);
            }

            alert("✅ Kayıt ve " + addedMedications.length + " adet reçete başarıyla eklendi!");

            // Sıfırlama
            setStep(1);
            setFormData({ description: "", weight: "" });
            setAddedMedications([]); // Listeyi temizle
            setCurrentMed({ id: null, name: "", start: "", end: "", instructions: "" });
            setSearchResults([]);
            setHasSearched(false);

        } catch (error) {
            console.error("Kayıt Hatası:", error);
            alert("Bir hata oluştu: " + (error.response?.data?.message || "Sunucu hatası"));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={containerStyle}>
            <div>
                <h1 style={headerStyle}>🩺 Tıbbi Kayıt Ekle</h1>
                <p style={subHeaderStyle}>Muayene, Reçete ve Kilo takibi.</p>
            </div>

            {/* ADIM 1: ARAMA */}
            {step === 1 && (
                <div style={cardStyle}>
                    <div style={{ display: "flex", gap: "25px", marginBottom: "20px", paddingBottom: "15px", borderBottom: "1px solid #f1f5f9" }}>
                        <label style={radioLabelStyle}><input type="radio" name="searchMode" checked={searchMode === "detail"} onChange={() => { setSearchMode("detail"); setHasSearched(false); setSearchResults([]); }} /> İsim ve Telefon</label>
                        <label style={radioLabelStyle}><input type="radio" name="searchMode" checked={searchMode === "id"} onChange={() => { setSearchMode("id"); setHasSearched(false); setSearchResults([]); }} /> Pet ID</label>
                    </div>

                    <div style={{ display: "flex", alignItems: "end", gap: "15px" }}>
                        {searchMode === "detail" ? (
                            <>
                                <div style={{ flex: 1 }}><label style={labelStyle}>Pet İsmi</label><input type="text" style={inputStyle} value={searchName} onChange={e => setSearchName(e.target.value)} /></div>
                                <div style={{ flex: 1 }}><label style={labelStyle}>Telefon</label><input type="text" style={inputStyle} value={searchPhone} onChange={e => setSearchPhone(e.target.value)} /></div>
                            </>
                        ) : (
                            <div style={{ flex: 1 }}><label style={labelStyle}>Pet ID</label><input type="text" style={inputStyle} value={searchId} onChange={e => setSearchId(e.target.value)} /></div>
                        )}
                        <Button onClick={handleSearch} disabled={loading} style={{ height: "46px" }}>🔍 Ara</Button>
                    </div>

                    {hasSearched && (
                        <div style={{ marginTop: "25px" }}>
                            {searchResults.length === 0 ? <p style={{textAlign:"center", color:"#94a3b8"}}>Bulunamadı.</p> : searchResults.map(pet => (
                                <div key={pet.petId} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "15px", border: "1px solid #eee", borderRadius: "10px", marginTop:"10px" }}>
                                    <div style={{ display: "flex", gap: "15px", alignItems: "center" }}>
                                        <div style={{ width: "50px", height: "50px", borderRadius: "50%", overflow: "hidden", background: "#e0f2fe", display: "flex", alignItems: "center", justifyContent: "center" }}>
                                            {pet.photoUrl ? <img src={`${BASE_IMAGE_URL}${pet.photoUrl}`} style={{ width: "100%", height: "100%", objectFit: "cover" }} /> : <b>{pet.name?.[0]}</b>}
                                        </div>
                                        <div><div style={{ fontWeight: "bold" }}>{pet.name}</div><div style={{ fontSize: "12px", color: "#666" }}>{pet.breed} • {pet.ownerName}</div></div>
                                    </div>
                                    <Button variant="secondary" onClick={() => { setSelectedPet(pet); setStep(2); }}>Seç →</Button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}

            {/* ADIM 2: KAYIT FORMU */}
            {step === 2 && selectedPet && (
                <div style={cardStyle}>
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px", borderBottom: "1px solid #eee", paddingBottom: "15px" }}>
                        <div style={{display:'flex', alignItems:'center', gap:'10px'}}>
                            <div style={{ width: "40px", height: "40px", borderRadius: "50%", overflow: "hidden", background: "#e0f2fe", display: "flex", alignItems: "center", justifyContent: "center" }}>
                                {selectedPet.photoUrl ? <img src={`${BASE_IMAGE_URL}${selectedPet.photoUrl}`} style={{ width: "100%", height: "100%", objectFit: "cover" }} /> : <b>{selectedPet.name?.[0]}</b>}
                            </div>
                            <h2 style={{ margin: 0, fontSize: "18px" }}>{selectedPet.name}</h2>
                        </div>
                        <Button variant="secondary" onClick={() => setStep(1)}>← Geri</Button>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "20px" }}>

                        {/* Veteriner Seçimi */}
                        <div style={{ gridColumn: "span 2" }}>
                            <label style={labelStyle}>Veteriner Hekim <span style={{color:"red"}}>*</span></label>
                            <select style={inputStyle} value={selectedVetId} onChange={(e) => setSelectedVetId(e.target.value)}>
                                <option value="">Seçiniz...</option>
                                {vets.map(vet => <option key={vet.vetId || vet.id} value={vet.vetId || vet.vetId}>{vet.firstName} {vet.lastName}</option>)}
                            </select>
                        </div>

                        <div style={{ gridColumn: "span 2" }}>
                            <label style={labelStyle}>Yapılan İşlemler / Açıklama <span style={{color:"red"}}>*</span></label>
                            <textarea style={{ ...inputStyle, height: "100px" }} value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
                        </div>

                        <div>
                            <label style={labelStyle}>Güncel Kilo (kg)</label>
                            <input type="number" style={inputStyle} value={formData.weight} onChange={e => setFormData({...formData, weight: e.target.value})} />
                        </div>

                        {/* --- ÇOKLU İLAÇ EKLEME ALANI --- */}
                        <div style={{ gridColumn: "span 2", background: "#f8fafc", padding: "20px", borderRadius: "12px", border: "1px solid #e2e8f0" }}>
                            <h4 style={{margin:"0 0 15px 0", color:"#334155", fontSize:"16px"}}>💊 Reçete Oluştur</h4>

                            {/* İlaç Arama ve Detaylar Inputları */}
                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "15px" }}>
                                {/* İlaç Arama */}
                                <div style={{ position: 'relative', gridColumn: "span 2" }}>
                                    <label style={labelStyle}>İlaç Seçimi</label>
                                    <input type="text" style={inputStyle} placeholder="İlaç adı yazın..." value={currentMed.name} onChange={handleMedicineSearch} onBlur={() => setTimeout(() => setShowMedicineList(false), 200)} />
                                    {showMedicineList && filteredMedicines.length > 0 && (
                                        <ul style={{ position: 'absolute', zIndex: 10, width: '100%', background: 'white', border: '1px solid #cbd5e1', borderRadius: '8px', maxHeight: '150px', overflowY: 'auto', listStyle: 'none', padding: 0, margin: '5px 0' }}>
                                            {filteredMedicines.map(med => (
                                                <li key={med.medicineId} onClick={() => selectMedicine(med)} style={{ padding: '8px 12px', cursor: 'pointer', borderBottom: '1px solid #f1f5f9' }}>
                                                    {med.name} <small>({med.type})</small>
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </div>

                                {/* Tarihler ve Talimat - Sadece ilaç seçilince veya yazılınca aktif olsa iyi olur ama sürekli açık kalsın */}
                                <div>
                                    <label style={labelStyle}>Başlangıç</label>
                                    <input
                                        type="date"
                                        style={inputStyle}
                                        // 4. MİNİMUM TARİH AYARI
                                        min={today} // Bugünden öncesi seçilemez
                                        value={currentMed.start}
                                        onChange={e => {
                                            const newStart = e.target.value;
                                            // Eğer yeni başlangıç tarihi, mevcut bitiş tarihinden ilerideyse bitişi temizle
                                            let newEnd = currentMed.end;
                                            if (newEnd && newEnd < newStart) {
                                                newEnd = "";
                                            }
                                            setCurrentMed({...currentMed, start: newStart, end: newEnd});
                                        }}
                                    />
                                </div>

                                <div>
                                    <label style={labelStyle}>Bitiş</label>
                                    <input
                                        type="date"
                                        style={inputStyle}
                                        // 5. BİTİŞ TARİHİ KISITLAMASI
                                        // Bitiş tarihi, seçilen başlangıç tarihinden önce olamaz (ama aynı gün olabilir)
                                        min={currentMed.start || today}
                                        value={currentMed.end}
                                        onChange={e => setCurrentMed({...currentMed, end: e.target.value})}
                                    />
                                </div>
                                <div style={{ gridColumn: "span 2" }}><label style={labelStyle}>Talimatlar</label><input type="text" style={inputStyle} placeholder="Örn: Günde 2 defa..." value={currentMed.instructions} onChange={e => setCurrentMed({...currentMed, instructions: e.target.value})} /></div>

                                <div style={{ gridColumn: "span 2", textAlign: "right" }}>
                                    <Button variant="warning" onClick={addMedicationToList}>+ Listeye Ekle</Button>
                                </div>
                            </div>

                            {/* EKLENEN İLAÇLAR LİSTESİ */}
                            {addedMedications.length > 0 && (
                                <div style={{ marginTop: "20px" }}>
                                    <label style={labelStyle}>Eklenecek İlaçlar ({addedMedications.length})</label>
                                    <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                                        {addedMedications.map((med, index) => (
                                            <div key={index} style={{ background: "white", padding: "10px 15px", borderRadius: "8px", border: "1px solid #e2e8f0", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                                <div>
                                                    <div style={{ fontWeight: "bold", color: "#1e293b" }}>{med.name}</div>
                                                    <div style={{ fontSize: "12px", color: "#64748b" }}>{med.start} - {med.end} | {med.instructions}</div>
                                                </div>
                                                <Button variant="danger" style={{ padding: "6px 12px", fontSize: "12px" }} onClick={() => removeMedicationFromList(index)}>Sil</Button>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                        {/* --------------------------- */}

                    </div>

                    <div style={{ marginTop: "25px", textAlign: "right", borderTop: "1px solid #eee", paddingTop: "20px" }}>
                        <Button variant="success" onClick={handleSubmit} disabled={loading} style={{ padding: "14px 30px" }}>{loading ? "Kaydediliyor..." : "✅ Kaydı Tamamla"}</Button>
                    </div>
                </div>
            )}
        </div>
    );
}