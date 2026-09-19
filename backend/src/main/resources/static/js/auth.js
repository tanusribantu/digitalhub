// Authentication & Page Access Guards
function checkAuth() {
  const user = getCurrentUser();
  return !!(user && getAuthToken());
}

function requireAuth(redirectUrl = 'login.html') {
  if (!checkAuth()) {
    window.location.href = `${redirectUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
    return false;
  }
  return true;
}

function requireRole(allowedRoles) {
  if (!requireAuth()) return false;
  const user = getCurrentUser();
  if (!allowedRoles.includes(user.role)) {
    showToast('Access denied: Unauthorized role', 'error');
    setTimeout(() => window.location.href = 'index.html', 1500);
    return false;
  }
  return true;
}

function logout() {
  clearAuthSession();
  showToast('Logged out successfully', 'info');
  setTimeout(() => {
    window.location.href = 'index.html';
  }, 500);
}