import { useState, useEffect } from "react";
import { http } from "../../api/http.js";

export default function OwnerProfile({ me }) {
    const [userData, setUserData] = useState(me);
    const [ownerDetails, setOwnerDetails] = useState({ firstName: "", lastName: "", phone: "", address: "" });
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState({ type: "", text: "" });

    // Şifre güncelleme state'leri
    const [passForm, setPassForm] = useState({ oldPassword: "", newPassword: "" });
    const [showPassModal, setShowPassModal] = useState(false);

    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const userRes = await http.get("/api/users/me");
                setUserData(userRes.data);
                if (userRes.data.role === "ROLE_OWNER") {
                    const ownerRes = await http.get("/api/pet-owners/me");
                    setOwnerDetails(ownerRes.data);
                }
            } catch (err) { console.error(err); }
            finally { setLoading(false); }
        };
        loadData();
    }, []);

    // Genel Bilgileri Güncelleme (İsim, Soyisim, Telefon, Adres)
    async function handleUpdateProfile(e) {
        e.preventDefault();
        setMsg({ type: "", text: "" });
        try {
            await http.put(`/api/pet-owners/${ownerDetails.ownerId}`, ownerDetails);
            setMsg({ type: "success", text: "Profil bilgileri başarıyla güncellendi." });
        } catch (err) {
            setMsg({ type: "error", text: "Güncelleme başarısız." });
        }
    }

    // Şifre Güncelleme (Eski şifre doğrulamalı)
    async function handleUpdatePassword(e) {
        e.preventDefault();
        setMsg({ type: "", text: "" });
        try {
            // Backend'deki updateMyPassword endpoint'ine (oldPassword ve newPassword ile) istek atar
            await http.put("/api/users/me/password", passForm);
            setMsg({ type: "success", text: "Şifre başarıyla değiştirildi." });
            setShowPassModal(false);
            setPassForm({ oldPassword: "", newPassword: "" });
        } catch (err) {
            setMsg({ type: "error", text: "Şifre güncellenemedi. Eski şifrenizi kontrol edin." });
        }
    }

    if (loading) return <div>Yükleniyor...</div>;

    const displayRole = userData?.role?.replace("ROLE_", "");

    return (
        <div style={{ maxWidth: "800px" }}>
            <div style={profileHeaderStyle}>
                <div style={avatarStyle}>{ownerDetails.firstName?.charAt(0)}</div>
                <div>
                    <h3 style={{ margin: 0 }}>{ownerDetails.firstName} {ownerDetails.lastName}</h3>
                    <p style={{ margin: 0, color: "#64748b" }}>{displayRole} Hesabı</p>
                </div>
            </div>

            {msg.text && (
                <div style={{ ...statusMsgStyle, background: msg.type === "success" ? "#f0fdf4" : "#fef2f2", color: msg.type === "success" ? "#166534" : "#991b1b" }}>
                    {msg.text}
                </div>
            )}

            <form onSubmit={handleUpdateProfile} style={infoGridStyle}>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>E-Posta (Değiştirilemez)</label>
                    <input value={userData?.email || ""} disabled style={inputDisabledStyle} />
                </div>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>Şifre</label>
                    <div style={{ display: "flex", gap: "10px" }}>
                        <input type="password" value="********" disabled style={{ ...inputDisabledStyle, flex: 1 }} />
                        <button type="button" onClick={() => setShowPassModal(true)} style={editBtnStyle}>Değiştir</button>
                    </div>
                </div>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>Ad</label>
                    <input value={ownerDetails.firstName} onChange={e => setOwnerDetails({...ownerDetails, firstName: e.target.value})} style={inputStyle} />
                </div>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>Soyad</label>
                    <input value={ownerDetails.lastName} onChange={e => setOwnerDetails({...ownerDetails, lastName: e.target.value})} style={inputStyle} />
                </div>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>Telefon</label>
                    <input value={ownerDetails.phone} onChange={e => setOwnerDetails({...ownerDetails, phone: e.target.value})} style={inputStyle} />
                </div>
                <div style={infoBoxStyle}>
                    <label style={labelStyle}>Rol</label>
                    <input value={displayRole} disabled style={inputDisabledStyle} />
                </div>
                <div style={{ ...infoBoxStyle, gridColumn: "span 2" }}>
                    <label style={labelStyle}>Adres</label>
                    <textarea value={ownerDetails.address} onChange={e => setOwnerDetails({...ownerDetails, address: e.target.value})} style={{ ...inputStyle, height: "60px" }} />
                </div>
                <button type="submit" style={saveBtnStyle}>Değişiklikleri Kaydet</button>
            </form>

            {/* ŞİFRE DEĞİŞTİRME MODALI */}
            {showPassModal && (
                <div style={modalOverlay}>
                    <div style={modalContent}>
                        <h3>Şifre Değiştir</h3>
                        <form onSubmit={handleUpdatePassword} style={{ display: "grid", gap: "12px" }}>
                            <input type="password" placeholder="Eski Şifre" required value={passForm.oldPassword} onChange={e => setPassForm({...passForm, oldPassword: e.target.value})} style={inputStyle} />
                            <input type="password" placeholder="Yeni Şifre" required value={passForm.newPassword} onChange={e => setPassForm({...passForm, newPassword: e.target.value})} style={inputStyle} />
                            <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
                                <button type="button" onClick={() => setShowPassModal(false)} style={cancelBtnStyle}>İptal</button>
                                <button type="submit" style={saveBtnStyle}>Güncelle</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

// Stiller
const profileHeaderStyle = { display: "flex", alignItems: "center", gap: "20px", marginBottom: "30px" };
const avatarStyle = { width: "60px", height: "60px", borderRadius: "50%", background: "#0284c7", color: "white", display: "grid", placeItems: "center", fontSize: "24px", fontWeight: "bold" };
const infoGridStyle = { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "15px" };
const infoBoxStyle = { display: "flex", flexDirection: "column" };
const labelStyle = { fontSize: "12px", color: "#64748b", fontWeight: "bold", marginBottom: "5px" };
const inputStyle = { padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", outline: "none" };
const inputDisabledStyle = { ...inputStyle, background: "#f1f5f9", color: "#64748b", border: "1px solid #e2e8f0" };
const saveBtnStyle = { gridColumn: "span 2", padding: "12px", background: "#0284c7", color: "white", border: "none", borderRadius: "8px", fontWeight: "bold", cursor: "pointer", marginTop: "10px" };
const editBtnStyle = { padding: "0 15px", background: "#f8fafc", border: "1px solid #cbd5e1", borderRadius: "8px", cursor: "pointer", fontSize: "12px" };
const modalOverlay = { position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(0,0,0,0.5)", display: "grid", placeItems: "center", zIndex: 1000 };
const modalContent = { background: "white", padding: "30px", borderRadius: "15px", width: "320px" };
const cancelBtnStyle = { flex: 1, padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", background: "white", cursor: "pointer" };
const statusMsgStyle = { padding: "12px", borderRadius: "8px", marginBottom: "20px", fontSize: "14px", fontWeight: "600" };