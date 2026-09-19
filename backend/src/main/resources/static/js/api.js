// DigitalHub Universal API Client
const API_BASE_URL = window.location.port === '8080' ? '/api' : 'http://localhost:8080/api';

function getAuthToken() {
  return localStorage.getItem('digitalhub_token');
}

function setAuthSession(authData) {
  localStorage.setItem('digitalhub_token', authData.token);
  localStorage.setItem('digitalhub_user', JSON.stringify({
    id: authData.id,
    fullName: authData.fullName,
    email: authData.email,
    role: authData.role,
    storeName: authData.storeName
  }));
}

function clearAuthSession() {
  localStorage.removeItem('digitalhub_token');
  localStorage.removeItem('digitalhub_user');
}

function getCurrentUser() {
  const data = localStorage.getItem('digitalhub_user');
  return data ? JSON.parse(data) : null;
}

async function request(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
  const headers = options.headers || {};
  
  const token = getAuthToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  options.headers = headers;

  try {
    const response = await fetch(url, options);
    
    // Check if unauthorized
    if (response.status === 401) {
      if (token) {
        clearAuthSession();
        showToast('Session expired. Please log in again.', 'warning');
        setTimeout(() => window.location.href = 'login.html', 1500);
      }
    }

    const json = await response.json();
    if (!response.ok || (json.success === false)) {
      const errMsg = json.message || 'An error occurred during request';
      throw new Error(errMsg);
    }
    return json.data;
  } catch (err) {
    throw err;
  }
}

const api = {
  get: (endpoint) => request(endpoint, { method: 'GET' }),
  post: (endpoint, body) => request(endpoint, { method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint, body) => request(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (endpoint) => request(endpoint, { method: 'DELETE' }),
  upload: (endpoint, formData) => request(endpoint, { method: 'POST', body: formData })
};

// Toast notification helper
function showToast(message, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const icons = {
    success: 'fa-check-circle',
    error: 'fa-exclamation-circle',
    warning: 'fa-exclamation-triangle',
    info: 'fa-info-circle'
  };

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <i class="fa-solid ${icons[type] || icons.info}" style="font-size: 1.1rem;"></i>
    <span>${message}</span>
  `;

  container.appendChild(toast);
  setTimeout(() => toast.classList.add('show'), 10);

  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}