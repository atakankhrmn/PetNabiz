import { useState } from "react";
import { http } from "../../api/http";

// --- STİLLER ---
const containerStyle = { padding: "30px", maxWidth: "600px", margin: "0 auto" };
const cardStyle = { background: "white", borderRadius: "16px", boxShadow: "0 4px 20px rgba(0,0,0,0.02)", padding: "30px", border: "1px solid #e2e8f0" };
const labelStyle = { display: "block", fontSize: "13px", fontWeight: "700", color: "#64748b", marginBottom: "6px" };
const inputStyle = { width: "100%", padding: "12px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "14px", outline: "none", transition: "0.2s", background: "#fff" };
const headerStyle = { fontSize: "24px", fontWeight: "800", color: "#1e293b", marginBottom: "5px" };
const subHeaderStyle = { color: "#64748b", margin: "0 0 25px 0", fontSize: "14px" };

const Button = ({ children, onClick, disabled }) => {
    return (
        <button
            onClick={disabled ? undefined : onClick}
            disabled={disabled}
            style={{
                padding: "12px 24px", borderRadius: "8px", border: "none", cursor: disabled ? "not-allowed" : "pointer",
                fontWeight: "600", fontSize: "14px", background: "#3b82f6", color: "white", opacity: disabled ? 0.7 : 1,
                width: "100%", marginTop: "20px"
            }}
        >
            {children}
        </button>
    );
};

export default function AddAdmin() {
    // Router hook'larını (useNavigate) kaldırdım.
    const [loading, setLoading] = useState(false);

    // DTO'ya uygun State yapısı
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: ""
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.name || !formData.password || !formData.email) {
            return alert("Lütfen tüm alanları doldurun.");
        }

        setLoading(true);
        try {
            // Backend'e sadece verileri atıyoruz, yönlendirme yapmıyoruz.
            await http.post("/api/admins", formData);

            alert("✅ Yeni Admin başarıyla oluşturuldu!");

            // Formu sıfırla ki yeni kayıt eklenebilsin
            setFormData({ name: "", email: "", password: "" });

        } catch (error) {
            console.error("Admin ekleme hatası:", error);
            const msg = error.response?.data?.message || "Kayıt sırasında bir hata oluştu.";
            alert("Hata: " + msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={containerStyle}>
            <div>
                <h1 style={headerStyle}>🛡️ Yeni Yönetici Ekle</h1>
                <p style={subHeaderStyle}>Sisteme tam yetkili yönetici tanımlayın.</p>
            </div>

            <div style={cardStyle}>
                <form onSubmit={handleSubmit}>

                    <div style={{ marginBottom: "15px" }}>
                        <label style={labelStyle}>Ad Soyad <span style={{color:"red"}}>*</span></label>
                        <input
                            type="text"
                            name="name"
                            style={inputStyle}
                            placeholder="Örn: Ahmet Yılmaz"
                            value={formData.name}
                            onChange={handleChange}
                        />
                    </div>

                    <div style={{ marginBottom: "15px" }}>
                        <label style={labelStyle}>Email <span style={{color:"red"}}>*</span></label>
                        <input
                            type="email"
                            name="email"
                            style={inputStyle}
                            placeholder="admin@petnabiz.com"
                            value={formData.email}
                            onChange={handleChange}
                        />
                    </div>

                    <div style={{ marginBottom: "15px" }}>
                        <label style={labelStyle}>Şifre <span style={{color:"red"}}>*</span></label>
                        <input
                            type="password"
                            name="password"
                            style={inputStyle}
                            placeholder="******"
                            value={formData.password}
                            onChange={handleChange}
                        />
                    </div>

                    <Button disabled={loading}>
                        {loading ? "Kaydediliyor..." : "💾 Admin Olarak Kaydet"}
                    </Button>
                </form>
            </div>
        </div>
    );
}