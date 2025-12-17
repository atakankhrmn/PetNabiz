import { useEffect, useState } from "react";
import Login from "./pages/Login";
import RegisterOwner from "./pages/RegisterOwner";
import ClinicApplication from "./pages/ClinicApplication";
import Dashboard from "./pages/Dashboard";
import { http } from "./api/http";
import { clearCreds, isLoggedIn } from "./auth/authStore";

export default function App() {
    const [me, setMe] = useState(null);
    const [screen, setScreen] = useState("login"); // login | register | clinic-application
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!isLoggedIn()) {
            setLoading(false);
            return;
        }

        http
            .get("/api/users/me")
            .then((res) => setMe(res.data))
            .catch(() => clearCreds())
            .finally(() => setLoading(false));
    }, []);

    function logout() {
        clearCreds();
        setMe(null);
        setScreen("login");
    }

    if (loading) return <div>Loading...</div>;

    if (!me) {
        if (screen === "register") {
            return <RegisterOwner onRegistered={(data) => setMe(data)} goLogin={() => setScreen("login")} />;
        }

        if (screen === "clinic-application") {
            return <ClinicApplication goBack={() => setScreen("login")} />;
        }

        return (
            <Login
                onLoggedIn={(data) => setMe(data)}
                goRegister={() => setScreen("register")}
                goClinicApplication={() => setScreen("clinic-application")}
            />
        );
    }

    return <Dashboard me={me} onLogout={logout} />;
}
