import axios from "axios";
import { getCreds, toBasicHeader, clearCreds } from "../auth/authStore";

export const http = axios.create({
    baseURL: "", // vite proxy varsa /api -> 8080, yoksa direkt aynı origin
});

http.interceptors.request.use((config) => {
    const creds = getCreds();
    if (creds?.email && creds?.password) {
        config.headers = config.headers || {};
        config.headers.Authorization = toBasicHeader(creds.email, creds.password);
    }
    return config;
});

http.interceptors.response.use(
    (res) => res,
    (err) => {
        if (err?.response?.status === 401) clearCreds();
        return Promise.reject(err);
    }
);
