import { useEffect, useState } from "react";
import { http } from "../../api/http";

export default function Pets({ me }) { // 'me' objesini Dashboard'dan prop olarak alıyoruz
    const [pets, setPets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");
    const [showModal, setShowModal] = useState(false);

    const [form, setForm] = useState({
        name: "",
        species: "",
        breed: "",
        gender: "MALE",
        birthDate: ""
    });

    useEffect(() => {
        loadPets();
    }, []);

    async function loadPets() {
        setLoading(true);
        setErr("");
        try {
            const res = await http.get("/api/pets/my");
            setPets(res.data || []);
        } catch (e) {
            setErr("Failed to retrieve your pet records.");
        } finally {
            setLoading(false);
        }
    }

    async function handleAddPet(e) {
        e.preventDefault();
        setErr("");
        try {
            // HATA ÇÖZÜMÜ: Backend'in beklediği ownerId alanını me objesinden ekliyoruz
            const payload = {
                ...form,
                ownerId: me?.userId // Login olan kullanıcının ID'si
            };

            await http.post("/api/pets", payload);
            setShowModal(false);
            setForm({ name: "", species: "", breed: "", gender: "MALE", birthDate: "" });
            loadPets();
        } catch (e) {
            // Hata mesajını daha detaylı görmek için backend mesajını basıyoruz
            const detail = e.response?.data?.message || "Check your information.";
            setErr("Could not register pet: " + detail);
        }
    }

    function getAge(birthDate) {
        if (!birthDate) return "N/A";
        const today = new Date();
        const birth = new Date(birthDate);
        let age = today.getFullYear() - birth.getFullYear();
        const monthDiff = today.getMonth() - birth.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
            age--;
        }
        return age < 0 ? 0 : age;
    }

    if (loading) return <div style={{ color: "#64748b", padding: 20 }}>Syncing with pet database...</div>;

    return (
        <div style={{ position: "relative" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
                <h3 style={{ margin: 0, color: "#1e293b", fontWeight: 800 }}>My Registered Pets</h3>
                <button onClick={() => setShowModal(true)} style={addPetBtn}>+ Register New Pet</button>
            </div>

            {err && <div style={errStyle}>{err}</div>}

            <div style={gridStyle}>
                {pets.map((pet) => (
                    <div key={pet.petId} style={petCardStyle}>
                        <div style={badgeStyle}>{pet.species || "Pet"}</div>
                        <h4 style={{ margin: "10px 0 5px 0", color: "#0284c7" }}>{pet.name}</h4>
                        <div style={infoRow}>
                            <span style={labelStyle}>Breed:</span>
                            <span style={valueStyle}>{pet.breed || "Mixed"}</span>
                        </div>
                        <div style={infoRow}>
                            <span style={labelStyle}>Age:</span>
                            <span style={valueStyle}>{getAge(pet.birthDate)} yrs</span>
                        </div>
                        {/* DOĞUM TARİHİ EKLENDİ */}
                        <div style={infoRow}>
                            <span style={labelStyle}>Birth Date:</span>
                            <span style={valueStyle}>{pet.birthDate}</span>
                        </div>
                        <div style={{ marginTop: 15, display: "grid", gap: 8 }}>
                            <button style={actionBtn}>Medical Records</button>
                        </div>
                    </div>
                ))}
            </div>

            {showModal && (
                <div style={modalOverlay}>
                    <div style={modalContent}>
                        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 20 }}>
                            <h3 style={{ margin: 0, color: "#0284c7" }}>✚ New Pet Registration</h3>
                            <button onClick={() => setShowModal(false)} style={{ border: "none", background: "none", cursor: "pointer", fontSize: 20 }}>&times;</button>
                        </div>
                        <form onSubmit={handleAddPet} style={{ display: "grid", gap: 12 }}>
                            <input placeholder="Pet Name" value={form.name} onChange={e => setForm({...form, name: e.target.value})} required style={inputStyle} />
                            <input placeholder="Species (e.g. Dog, Cat)" value={form.species} onChange={e => setForm({...form, species: e.target.value})} required style={inputStyle} />
                            <input placeholder="Breed" value={form.breed} onChange={e => setForm({...form, breed: e.target.value})} style={inputStyle} />

                            <select value={form.gender} onChange={e => setForm({...form, gender: e.target.value})} style={inputStyle}>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>

                            <div>
                                <label style={{ fontSize: 12, color: "#64748b", marginBottom: 5, display: "block" }}>Birth Date</label>
                                <input type="date" value={form.birthDate} onChange={e => setForm({...form, birthDate: e.target.value})} required style={inputStyle} />
                            </div>

                            <button type="submit" style={primaryBtn}>Complete Registration</button>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

// Stiller aynı kalıyor...
const modalOverlay = { position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(15, 23, 42, 0.6)", display: "grid", placeItems: "center", zIndex: 1000 };
const modalContent = { background: "white", padding: 30, borderRadius: 20, width: "min(400px, 90%)", boxShadow: "0 25px 50px -12px rgba(0,0,0,0.25)" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: 10, border: "1px solid #cbd5e1", outline: "none", boxSizing: "border-box" };
const primaryBtn = { width: "100%", padding: "14px", borderRadius: 10, border: "none", background: "#0284c7", color: "white", fontWeight: 800, cursor: "pointer", marginTop: 10 };
const gridStyle = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))", gap: "20px" };
const petCardStyle = { background: "white", border: "1px solid #e2e8f0", borderRadius: "16px", padding: "20px", boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.05)", position: "relative" };
const badgeStyle = { position: "absolute", top: 15, right: 15, background: "#f0f9ff", color: "#0284c7", padding: "4px 10px", borderRadius: "20px", fontSize: "11px", fontWeight: "800" };
const infoRow = { display: "flex", justifyContent: "space-between", marginBottom: "6px", fontSize: "13px" };
const labelStyle = { color: "#64748b", fontWeight: "600" };
const valueStyle = { color: "#1e293b", fontWeight: "700" };
const addPetBtn = { background: "#0284c7", color: "white", border: "none", padding: "10px 18px", borderRadius: "10px", fontWeight: "700", cursor: "pointer" };
const actionBtn = { width: "100%", padding: "10px", borderRadius: "8px", border: "none", background: "#f1f5f9", color: "#0284c7", fontWeight: "700", cursor: "pointer", fontSize: "12px" };
const errStyle = { background: "#fef2f2", color: "#991b1b", padding: "12px", borderRadius: "10px", marginBottom: "15px", fontSize: "13px" };