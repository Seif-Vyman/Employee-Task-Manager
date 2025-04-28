// Centralized authentication helper
const AuthService = {
    // Check if user is authenticated
    isAuthenticated() {
        const token = localStorage.getItem('token');
        return !!token;
    },

    // Get current token
    getToken() {
        return localStorage.getItem('token');
    },

    // Remove token (logout)
    logout() {
        localStorage.removeItem('token');
        window.location.href = '/html/login.html';
    },

    // Decode JWT (optional, requires jwt-decode library)
    decodeToken() {
        const token = this.getToken();
        if (token) {
            // Implement JWT decoding if needed
            // return jwt_decode(token);
        }
        return null;
    }
};

// Export if using modules, otherwise it's globally available
export default AuthService;