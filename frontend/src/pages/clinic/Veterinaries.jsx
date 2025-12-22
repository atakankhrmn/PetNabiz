import { useState, useEffect } from "react";
import { http } from "../../api/http";

export default function Veterinaries() {
    const [vets, setVets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [clinicId, setClinicId] = useState(null);

    // Modal ve Form State'leri
    const [showModal, setShowModal] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);

    // Form verileri state'i
    const [currentVet, setCurrentVet] = useState({
        vetId: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
        certificate: "", // Backend'den gelen dosya adı (String)
        address: "",
        file: null       // Frontend'de seçilen dosya objesi (File)
    });

    useEffect(() => {
        fetchInitialData();
    }, []);

    const fetchInitialData = async () => {
        setLoading(true);
        try {
            // 1. Önce Clinic ID'yi alıyoruz
            const clinicRes = await http.get("/api/clinics/my");
            setClinicId(clinicRes.data.clinicId);

            // 2. Veteriner listesini çekiyoruz
            const vetRes = await http.get("/api/veterinaries/my");
            setVets(vetRes.data);
        } catch (error) {
            console.error("Veri yüklenirken hata:", error);
        } finally {
            setLoading(false);
        }
    };

    // Text input değişikliklerini yakalar
    const handleChange = (e) => {
        const { name, value } = e.target;
        setCurrentVet(prev => ({ ...prev, [name]: value }));
    };

    // Dosya seçimini yakalar
    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        setCurrentVet(prev => ({ ...prev, file: selectedFile }));
    };

    // "Yeni Ekle" butonuna basınca
    const handleAddNew = () => {
        setIsEditMode(false);
        // Formu temizle
        setCurrentVet({
            firstName: "",
            lastName: "",
            phoneNumber: "",
            certificate: "",
            address: "",
            file: null
        });
        setShowModal(true);
    };

    // "Düzenle" butonuna basınca
    const handleEdit = (vet) => {
        setIsEditMode(true);
        // Listeden gelen veriyi forma doldur
        setCurrentVet({
            vetId: vet.vetId,
            firstName: vet.firstName,
            lastName: vet.lastName,
            phoneNumber: vet.phoneNumber,
            certificate: vet.certificate, // Mevcut dosya adı
            address: vet.address,
            file: null // Düzenlemede yeni dosya seçilmediyse null kalır
        });
        setShowModal(true);
    };

    // SERTİFİKA GÖRÜNTÜLEME (Senin http kütüphanen ile)
    const handleViewCertificate = async (vetId) => {
        try {
            // http.get kullanıyoruz çünkü senin sistemin giriş bilgisini bu yönetiyor.
            // Backend'e "Bana dosya (blob) ver" diyoruz.
            const response = await http.get(`/api/veterinaries/${vetId}/certificate`, {
                responseType: 'blob' // BU ÇOK ÖNEMLİ! Yoksa yine garip yazılar çıkar.
            });

            // Axios'ta veri 'response.data' içindedir.
            const blob = new Blob([response.data], { type: response.headers['content-type'] });

            // Eğer dosya boşsa uyaralım
            if (blob.size === 0) {
                alert("Dosya içeriği boş veya hatalı.");
                return;
            }

            // Link oluştur ve aç
            const fileURL = window.URL.createObjectURL(blob);
            window.open(fileURL, '_blank');

        } catch (error) {
            console.error("Görüntüleme Hatası:", error);
            // 401 hatası alırsan konsolda görürsün
            if (error.response && error.response.status === 401) {
                alert("Yetkiniz yok veya giriş yapmamışsınız.");
            } else {
                alert("Dosya açılamadı. Dosya yüklenmemiş olabilir.");
            }
        }
    };

    // KAYDET (Create veya Update)
    const handleSave = async (e) => {
        e.preventDefault();
        try {
            if (isEditMode) {
                // --- GÜNCELLEME (PUT) ---
                // Not: Şu an sadece metin alanlarını güncelliyoruz (JSON).
                // Dosya güncellemesi gerekirse backend PUT metodunu da Multipart yapmalısın.

                const { file, ...updateData } = currentVet; // file objesini JSON'a katmamak için ayırıyoruz
                await http.put(`/api/veterinaries/${currentVet.vetId}`, updateData);

            } else {
                // --- YENİ EKLEME (POST) - FORM DATA ---

                // Diploma zorunlu olsun istersen bu yorumu aç:
                /*
                if (!currentVet.file) {
                    alert("Lütfen diploma dosyasını yükleyiniz!");
                    return;
                }
                */

                const formData = new FormData();

                // 1. String alanları ekle
                formData.append("firstName", currentVet.firstName);
                formData.append("lastName", currentVet.lastName);
                formData.append("phoneNumber", currentVet.phoneNumber);
                formData.append("address", currentVet.address || "");
                formData.append("clinicId", clinicId);

                // 2. Dosya varsa ekle (Backend'de @RequestParam("file") ile eşleşmeli)
                if (currentVet.file) {
                    formData.append("file", currentVet.file);
                }

                // 3. İsteği gönder (Content-Type: multipart/form-data)
                // Axios FormData gördüğünde boundary header'ını genelde kendi ayarlar
                await http.post("/api/veterinaries", formData, {
                    headers: { "Content-Type": "multipart/form-data" }
                });
            }

            // Başarılı ise listeyi yenile ve modali kapat
            const res = await http.get("/api/veterinaries/my");
            setVets(res.data);
            setShowModal(false);

            // State temizliği
            setCurrentVet(prev => ({ ...prev, file: null }));

        } catch (error) {
            console.error("Kaydetme işlemi başarısız:", error);
            alert("İşlem sırasında bir hata oluştu. Zorunlu alanları kontrol ediniz.");
        }
    };

    // Silme İşlemi
    const handleDelete = async (vetId) => {
        if (!window.confirm("Bu veteriner hekimi silmek istediğinize emin misiniz?")) return;

        try {
            await http.delete(`/api/veterinaries/${vetId}`);
            setVets(vets.filter(v => v.vetId !== vetId));
        } catch (error) {
            console.error("Silme hatası:", error);
            alert("Silinemedi.");
        }
    };

    if (loading) return <div style={{ padding: "20px", color: "#64748b" }}>Veteriner listesi yükleniyor...</div>;

    return (
        <div>
            {/* Üst Başlık */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
                <div style={headerTitle}>👨‍⚕️ Veteriner Hekim Yönetimi</div>
                <button onClick={handleAddNew} style={addButtonStyle}>
                    + Yeni Veteriner Ekle
                </button>
            </div>

            {/* Liste Grid */}
            <div style={gridContainer}>
                {vets.length > 0 ? (
                    vets.map(vet => (
                        <div key={vet.vetId} style={cardStyle}>
                            <div style={cardHeader}>
                                <div style={vetName}>{vet.firstName} {vet.lastName}</div>

                                {/* Sertifika Durumu ve Görüntüleme Butonu */}
                                <div style={{ marginTop: "5px" }}>
                                    {vet.certificate ? (
                                        <button
                                            onClick={() => handleViewCertificate(vet.vetId)}
                                            style={viewCertBtnStyle}
                                        >
                                            📄 Diplomayı Görüntüle
                                        </button>
                                    ) : (
                                        <span style={{ fontSize: "11px", color: "#94a3b8", fontStyle: "italic" }}>
                                            Belge Yüklenmemiş
                                        </span>
                                    )}
                                </div>
                            </div>

                            <div style={cardBody}>
                                <div style={infoRow}>📞 {vet.phoneNumber}</div>
                                <div style={infoRow}>📍 {vet.address || "Adres girilmemiş"}</div>
                            </div>

                            <div style={cardFooter}>
                                <button onClick={() => handleEdit(vet)} style={editBtn}>Düzenle</button>
                                <button onClick={() => handleDelete(vet.vetId)} style={deleteBtn}>Sil</button>
                            </div>
                        </div>
                    ))
                ) : (
                    <div style={{ color: "#94a3b8", fontStyle: "italic", gridColumn: "1/-1", textAlign: "center" }}>
                        Henüz kayıtlı veteriner hekiminiz bulunmamaktadır.
                    </div>
                )}
            </div>

            {/* Modal Form */}
            {showModal && (
                <div style={overlayStyle}>
                    <div style={modalStyle}>
                        <h3 style={{ margin: "0 0 15px 0", color: "#334155" }}>
                            {isEditMode ? "Veteriner Düzenle" : "Yeni Veteriner Ekle"}
                        </h3>
                        <form onSubmit={handleSave} style={{ display: "grid", gap: "10px" }}>

                            {/* Ad ve Soyad Yanyana */}
                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" }}>
                                <input
                                    style={inputStyle}
                                    name="firstName"
                                    placeholder="Ad"
                                    value={currentVet.firstName}
                                    onChange={handleChange}
                                    required
                                />
                                <input
                                    style={inputStyle}
                                    name="lastName"
                                    placeholder="Soyad"
                                    value={currentVet.lastName}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <input
                                style={inputStyle}
                                name="phoneNumber"
                                placeholder="Telefon Numarası"
                                value={currentVet.phoneNumber}
                                onChange={handleChange}
                                required
                            />

                            <textarea
                                style={{...inputStyle, height: "60px", resize: "none"}}
                                name="address"
                                placeholder="Adres"
                                value={currentVet.address}
                                onChange={handleChange}
                            />

                            {/* Dosya Yükleme Alanı (Sadece Ekleme Modunda Gösterilebilir veya her zaman) */}
                            {/* Düzenleme modunda backend Multipart desteklemediği sürece gizlemek mantıklı olabilir */}
                            {!isEditMode && (
                                <div style={{ marginTop: "5px" }}>
                                    <label style={{ fontSize: "12px", color: "#64748b", display: "block", marginBottom: "4px" }}>
                                        Diploma / Sertifika Dosyası (PDF/Resim)
                                    </label>
                                    <input
                                        type="file"
                                        onChange={handleFileChange}
                                        style={fileInputStyle}
                                        accept=".pdf,.jpg,.jpeg,.png"
                                    />
                                </div>
                            )}

                            {isEditMode && (
                                <div style={{ fontSize: "12px", color: "#64748b", fontStyle: "italic" }}>
                                    * Belge güncellemek için kaydı silip tekrar oluşturunuz.
                                </div>
                            )}

                            <div style={{ display: "flex", gap: "10px", marginTop: "15px", justifyContent: "flex-end" }}>
                                <button type="button" onClick={() => setShowModal(false)} style={cancelBtn}>İptal</button>
                                <button type="submit" style={saveBtn}>Kaydet</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

// --- Stiller ---
const headerTitle = { fontSize: "20px", fontWeight: "bold", color: "#1e293b" };
const addButtonStyle = {
    background: "#3b82f6", color: "white", border: "none", padding: "10px 20px",
    borderRadius: "8px", cursor: "pointer", fontWeight: "600",
    boxShadow: "0 4px 6px -1px rgba(59, 130, 246, 0.5)"
};
const gridContainer = { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))", gap: "20px" };

const cardStyle = {
    background: "white", borderRadius: "12px", border: "1px solid #e2e8f0",
    overflow: "hidden", display: "flex", flexDirection: "column",
    boxShadow: "0 1px 3px rgba(0,0,0,0.1)"
};
const cardHeader = { background: "#f8fafc", padding: "15px", borderBottom: "1px solid #e2e8f0" };
const vetName = { fontWeight: "700", color: "#334155", fontSize: "16px" };
const viewCertBtnStyle = {
    background: "none", border: "none", color: "#0ea5e9",
    cursor: "pointer", textDecoration: "underline", fontSize: "12px",
    padding: 0, fontWeight: "600"
};

const cardBody = { padding: "15px", flex: 1 };
const infoRow = { fontSize: "14px", color: "#475569", marginBottom: "8px" };
const cardFooter = { padding: "15px", background: "#fff", display: "flex", gap: "10px", borderTop: "1px solid #f1f5f9" };

const editBtn = { flex: 1, padding: "8px", borderRadius: "6px", border: "1px solid #cbd5e1", background: "white", color: "#475569", cursor: "pointer", fontWeight: "600" };
const deleteBtn = { flex: 1, padding: "8px", borderRadius: "6px", border: "none", background: "#fee2e2", color: "#ef4444", cursor: "pointer", fontWeight: "600" };

// Modal Styles
const overlayStyle = {
    position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
    background: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center", zIndex: 1000
};
const modalStyle = {
    background: "white", padding: "25px", borderRadius: "12px", width: "400px",
    boxShadow: "0 20px 25px -5px rgba(0, 0, 0, 0.1)"
};
const inputStyle = {
    width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1",
    fontSize: "14px", outline: "none", fontFamily: "inherit"
};
const fileInputStyle = {
    width: "100%", padding: "8px", borderRadius: "8px", border: "1px dashed #cbd5e1",
    fontSize: "13px", background: "#f8fafc"
};
const saveBtn = { background: "#22c55e", color: "white", border: "none", padding: "10px 20px", borderRadius: "6px", cursor: "pointer", fontWeight: "600" };
const cancelBtn = { background: "#f1f5f9", color: "#64748b", border: "none", padding: "10px 20px", borderRadius: "6px", cursor: "pointer", fontWeight: "600" };