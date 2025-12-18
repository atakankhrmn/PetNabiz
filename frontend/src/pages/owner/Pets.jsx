import { useEffect, useState, useRef } from "react";
import { http } from "../../api/http";

export default function Pets({ me }) {
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [selectedPetId, setSelectedPetId] = useState(null);
    const fileInputRef = useRef(null);

    // YENİ: Silme onayı için state
    const [deleteConfirm, setDeleteConfirm] = useState({ show: false, petId: null, petName: "" });

    const [form, setForm] = useState({
        name: "", species: "", breed: "", gender: "MALE", birthDate: "", weight: ""
    });

    useEffect(() => { loadPets(); }, []);

    async function loadPets() {
        setLoading(true);
        setErr("");
        try {
            const res = await http.get("/api/pets/my");
            setPets(res.data || []);
        } catch (e) { setErr("Pet listesi yüklenemedi."); }
        finally { setLoading(false); }
    }

    // GÜNCELLENEN SİLME MANTIĞI (Popup yerine modal açar)
    function openDeleteConfirm(petId, petName) {
        setDeleteConfirm({ show: true, petId, petName });
    }

    async function handleConfirmDelete() {
        try {
            await http.delete(`/api/pets/${deleteConfirm.petId}`);
            setDeleteConfirm({ show: false, petId: null, petName: "" });
            loadPets();
        } catch (e) { alert("Silme işlemi sırasında bir hata oluştu."); }
    }

    function openModal(pet = null) {
        if (pet) {
            setIsEditMode(true);
            setSelectedPetId(pet.petId);
            setForm({
                name: pet.name || "", species: pet.species || "", breed: pet.breed || "",
                gender: pet.gender || "MALE", birthDate: pet.birthDate || "", weight: pet.weight || ""
            });
        } else {
            setIsEditMode(false);
            setSelectedPetId(null);
            setForm({ name: "", species: "", breed: "", gender: "MALE", birthDate: "", weight: "" });
        }
        setShowModal(true);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setErr("");
        try {
            const payload = { ...form, ownerId: me?.userId };
            if (isEditMode) await http.put(`/api/pets/${selectedPetId}`, payload);
            else await http.post("/api/pets", payload);
            setShowModal(false);
            loadPets();
        } catch (e) { setErr("İşlem başarısız, lütfen alanları kontrol edin."); }
    }

    async function handlePhotoUpload(e) {
        const file = e.target.files[0];
        if (!file || !selectedPetId) return;
        const formData = new FormData();
        formData.append("file", file);
        try {
            await http.post(`/api/pets/${selectedPetId}/upload-image`, formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            setShowModal(false);
            loadPets();
        } catch (err) { alert("Fotoğraf yüklenemedi!"); }
    }

    function getAge(birthDate) {
        if (!birthDate) return "N/A";
        const today = new Date();
        const birth = new Date(birthDate);
        let age = today.getFullYear() - birth.getFullYear();
        if (new Date(today.getFullYear(), today.getMonth(), today.getDate()) <
            new Date(today.getFullYear(), birth.getMonth(), birth.getDate())) {
            age--;
        }
        return age < 0 ? 0 : age;
    }

    if (loading) return <div style={{ color: "#64748b", padding: 20 }}>Yükleniyor...</div>;

    return (
        <div style={{ position: "relative" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
                <h3 style={{ margin: 0, color: "#1e293b", fontWeight: 800 }}>Evcil Hayvanlarım</h3>
                <button onClick={() => openModal()} style={addPetBtn}>+ Yeni Pet Kaydet</button>
            </div>

            {err && <div style={errStyle}>{err}</div>}

            <div style={gridStyle}>
                {pets.map((pet) => (
                    <div key={pet.petId} className="pet-card" style={petCardStyle}>
                        {/* CSS İLE HOVER EFEKTİ VERİLEN SİLME BUTONU */}
                        <button
                            onClick={() => openDeleteConfirm(pet.petId, pet.name)}
                            className="delete-hover-btn"
                            style={deleteBtnIconStyle}
                            title="Sil"
                        >
                            &times;
                        </button>

                        <div style={photoContainerStyle}>
                            <img
                                src={pet.photoUrl ? `http://localhost:8080/uploads/pets/${pet.photoUrl}` : "https://via.placeholder.com/300?text=No+Photo"}
                                alt={pet.name}
                                style={imgStyle}
                            />
                        </div>

                        <div style={badgeStyle}>{pet.species}</div>
                        <h4 style={{ margin: "10px 0 5px 0", color: "#0284c7" }}>{pet.name}</h4>

                        <div style={infoRow}><span style={labelStyle}>Cins:</span><span style={valueStyle}>{pet.breed || "Belirtilmemiş"}</span></div>
                        <div style={infoRow}><span style={labelStyle}>Cinsiyet:</span><span style={valueStyle}>{pet.gender}</span></div>
                        <div style={infoRow}><span style={labelStyle}>Kilo:</span><span style={valueStyle}>{pet.weight ? `${pet.weight} kg` : "N/A"}</span></div>
                        <div style={infoRow}><span style={labelStyle}>Yaş:</span><span style={valueStyle}>{getAge(pet.birthDate)} Yaş</span></div>
                        <div style={infoRow}><span style={labelStyle}>Doğum Tarihi:</span><span style={valueStyle}>{pet.birthDate}</span></div>



                        <div style={{ marginTop: 15, display: "grid", gap: 8 }}>
                            <button style={actionBtn}>Tıbbi Kayıtlar</button>
                            <button onClick={() => openModal(pet)} style={updateBtnStyle}>✎ Bilgileri Güncelle</button>
                        </div>
                    </div>
                ))}
            </div>

            {/* ÖZEL SİLME ONAY MODALI */}
            {deleteConfirm.show && (
                <div style={modalOverlay}>
                    <div style={{...modalContent, textAlign: "center", width: "300px"}}>
                        <div style={{fontSize: "40px", marginBottom: "10px"}}>⚠️</div>
                        <h3 style={{margin: "0 0 10px 0", color: "#1e293b"}}>Emin misiniz?</h3>
                        <p style={{fontSize: "14px", color: "#64748b", marginBottom: "20px"}}>
                            <b>{deleteConfirm.petName}</b> kalıcı olarak silinecektir.
                        </p>
                        <div style={{display: "flex", gap: "10px"}}>
                            <button onClick={() => setDeleteConfirm({show: false, petId: null, petName: ""})} style={{...actionBtn, flex: 1}}>İptal</button>
                            <button onClick={handleConfirmDelete} style={{...primaryBtn, background: "#ef4444", flex: 1, marginTop: 0}}>Evet, Sil</button>
                        </div>
                    </div>
                </div>
            )}

            {/* KAYIT VE GÜNCELLEME MODALI */}
            {showModal && (
                <div style={modalOverlay}>
                    <div style={modalContent}>
                        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 15 }}>
                            <h3 style={{ margin: 0, color: "#0284c7" }}>{isEditMode ? "Pet Güncelle" : "Yeni Pet"}</h3>
                            <button onClick={() => setShowModal(false)} style={{ border: "none", background: "none", cursor: "pointer", fontSize: "20px" }}>&times;</button>
                        </div>

                        {isEditMode && (
                            <div style={{ marginBottom: 15, padding: "10px", border: "1px dashed #0284c7", borderRadius: "10px", textAlign: "center" }}>
                                <input type="file" ref={fileInputRef} onChange={handlePhotoUpload} style={{ display: "none" }} accept="image/*" />
                                <button type="button" onClick={() => fileInputRef.current.click()} style={uploadBtnStyle}>
                                    📷 Fotoğraf Değiştir
                                </button>
                            </div>
                        )}

                        <form onSubmit={handleSubmit} style={{ display: "grid", gap: 10 }}>
                            <input placeholder="Pet Adı" value={form.name} onChange={e => setForm({...form, name: e.target.value})} required style={inputStyle} />
                            <input placeholder="Tür" value={form.species} onChange={e => setForm({...form, species: e.target.value})} required style={inputStyle} />
                            <input placeholder="Cins" value={form.breed} onChange={e => setForm({...form, breed: e.target.value})} style={inputStyle} />
                            <input placeholder="Kilo (kg)" type="number" step="0.1" value={form.weight} onChange={e => setForm({...form, weight: e.target.value})} style={inputStyle} />
                            <select value={form.gender} onChange={e => setForm({...form, gender: e.target.value})} style={inputStyle}>
                                <option value="MALE">Erkek</option>
                                <option value="FEMALE">Dişi</option>
                            </select>
                            <div style={{ display: "grid", gap: 4 }}>
                                <label style={{ fontSize: "12px", color: "#64748b" }}>Doğum Tarihi</label>
                                <input type="date" value={form.birthDate} onChange={e => setForm({...form, birthDate: e.target.value})} required style={inputStyle} />
                            </div>
                            <button type="submit" style={primaryBtn}>{isEditMode ? "Kaydet" : "Kaydet"}</button>
                        </form>
                    </div>
                </div>
            )}

            {/* HOVER EFEKTİ İÇİN CSS (Bu kısmı index.css veya App.css içine koyman en sağlıklısıdır ama buraya ekliyorum) */}
            <style>{`
                .pet-card .delete-hover-btn {
                    opacity: 0;
                    transition: opacity 0.2s ease;
                }
                .pet-card:hover .delete-hover-btn {
                    opacity: 1;
                }
            `}</style>
        </div>
    );
}

// STİLLER
const deleteBtnIconStyle = {
    position: "absolute",
    top: "-10px",
    right: "-10px",
    width: "28px",
    height: "28px",
    borderRadius: "50%",
    background: "#ef4444",
    color: "white",
    border: "2px solid white",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "18px",
    fontWeight: "bold",
    boxShadow: "0 2px 4px rgba(0,0,0,0.2)",
    zIndex: 10
};

const photoContainerStyle = { width: "100%", paddingTop: "100%", position: "relative", borderRadius: "15px", overflow: "hidden", marginBottom: "12px", background: "#f1f5f9", border: "1px solid #e2e8f0" };
const imgStyle = { position: "absolute", top: 0, left: 0, width: "100%", height: "100%", objectFit: "cover" };
const gridStyle = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))", gap: "20px" };
const petCardStyle = { background: "white", border: "1px solid #e2e8f0", borderRadius: "20px", padding: "15px", boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.05)", position: "relative" };
const badgeStyle = { display: "inline-block", background: "#f0f9ff", color: "#0284c7", padding: "2px 8px", borderRadius: "10px", fontSize: "11px", fontWeight: "800", textTransform: "uppercase" };
const infoRow = { display: "flex", justifyContent: "space-between", fontSize: "13px", marginTop: "6px" };
const labelStyle = { color: "#64748b", fontWeight: "600" };
const valueStyle = { fontWeight: "700", color: "#1e293b" };
const addPetBtn = { background: "#0284c7", color: "white", border: "none", padding: "10px 20px", borderRadius: "10px", fontWeight: "700", cursor: "pointer" };
const uploadBtnStyle = { background: "#f0f9ff", color: "#0284c7", border: "1px solid #0284c7", padding: "8px", borderRadius: "8px", cursor: "pointer", fontWeight: "600", fontSize: "12px", width: "100%" };
const updateBtnStyle = { width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #bae6fd", background: "#f0f9ff", color: "#0369a1", fontWeight: "700", cursor: "pointer", fontSize: "12px" };
const actionBtn = { background: "#f1f5f9", border: "none", padding: "10px", borderRadius: "8px", color: "#475569", fontWeight: "700", fontSize: "12px", cursor: "pointer" };
const modalOverlay = { position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(15, 23, 42, 0.7)", display: "grid", placeItems: "center", zIndex: 2000 };
const modalContent = { background: "white", padding: "25px", borderRadius: "20px", width: "360px", position: "relative" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: "10px", border: "1px solid #cbd5e1", boxSizing: "border-box" };
const primaryBtn = { background: "#0284c7", color: "white", border: "none", padding: "14px", borderRadius: "10px", fontWeight: "700", cursor: "pointer", marginTop: "10px" };
const errStyle = { background: "#fef2f2", color: "#991b1b", padding: "10px", borderRadius: "8px", marginBottom: "15px" };