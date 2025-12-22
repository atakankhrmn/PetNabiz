const KEY = "petnabiz_basic_auth";

export function saveCreds(email, password) {
    localStorage.setItem(KEY, JSON.stringify({ email, password }));
}

export function getCreds() {
    try {
        return JSON.parse(localStorage.getItem(KEY));
    } catch {
        return null;
    }
}

export function clearCreds() {
    localStorage.removeItem(KEY);
}

export function toBasicHeader(email, password) {
    const token = btoa(`${email}:${password}`);
    return `Basic ${token}`;
}

export function isLoggedIn() {
    const c = getCreds();
    return !!(c?.email && c?.password);
}
