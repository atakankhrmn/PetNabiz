import { useEffect, useState } from "react";
import { http } from "../../api/http";

export default function ClinicApplicationsAdmin() {
  const [status, setStatus] = useState("PENDING"); // PENDING | APPROVED | REJECTED
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  async function load() {
    setErr("");
    setLoading(true);
    try {
      const res = await http.get("/api/clinic-applications", { params: { status } });
      setItems(res.data || []);
    } catch {
      setErr("Clinic applications yüklenemedi.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, [status]);

  async function approve(id) {
    setErr("");
    try {
      await http.post(`/api/clinic-applications/${id}/approve`);
      await load();
    } catch {
      setErr("Approve başarısız.");
    }
  }

  async function reject(id) {
    setErr("");
    try {
      await http.post(`/api/clinic-applications/${id}/reject`);
      await load();
    } catch {
      setErr("Reject başarısız.");
    }
  }

  return (
      <div>
        <h3>Clinic Applications</h3>

        <div style={{ display: "flex", gap: 10, alignItems: "center", marginBottom: 12 }}>
          <label>Status:</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="PENDING">PENDING</option>
            <option value="APPROVED">APPROVED</option>
            <option value="REJECTED">REJECTED</option>
          </select>
          <button onClick={load}>Refresh</button>
        </div>

        {loading && <div>Loading...</div>}
        {err && <div style={{ color: "tomato" }}>{err}</div>}

        <table border="1" cellPadding="8" style={{ borderCollapse: "collapse", width: "100%", marginTop: 10 }}>
          <thead>
          <tr>
            <th>ID</th>
            <th>Status</th>
            <th>Email</th>
            <th>Clinic</th>
            <th>City</th>
            <th>District</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Actions</th>
          </tr>
          </thead>

          <tbody>
          {items.map((x) => (
              <tr key={x.id}>
                <td>{x.id}</td>
                <td>{x.status}</td>
                <td>{x.email}</td>
                <td>{x.clinicName}</td>
                <td>{x.city}</td>
                <td>{x.district}</td>
                <td>{x.phone}</td>
                <td>{x.address}</td>
                <td style={{ display: "flex", gap: 8 }}>
                  <button disabled={status !== "PENDING"} onClick={() => approve(x.id)}>Approve</button>
                  <button disabled={status !== "PENDING"} onClick={() => reject(x.id)}>Reject</button>
                </td>
              </tr>
          ))}

          {items.length === 0 && (
              <tr>
                <td colSpan="9" style={{ textAlign: "center" }}>No applications</td>
              </tr>
          )}
          </tbody>
        </table>
      </div>
  );
}
