// User Profile & Notification Center Controller
document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar();
  renderFooter();

  loadUserProfile();
  loadNotifications();
});

async function loadUserProfile() {
  try {
    const profile = await api.get('/auth/me');
    document.getElementById('profileName').value = profile.fullName || '';
    document.getElementById('profileEmail').value = profile.email || '';
    document.getElementById('profileRoleBadge').innerText = profile.role.replace('ROLE_', '');
    document.getElementById('profileCreatedDate').innerText = new Date(profile.createdAt).toLocaleDateString();
    document.getElementById('profileAvatarInitial').innerText = (profile.fullName || 'U').charAt(0).toUpperCase();
  } catch (err) {
    showToast('Failed to load profile: ' + err.message, 'error');
  }
}

async function loadNotifications() {
  const container = document.getElementById('notificationsList');
  if (!container) return;

  try {
    const notifs = await api.get('/notifications');
    if (!notifs || notifs.length === 0) {
      container.innerHTML = `<p style="color: var(--text-muted); text-align: center; padding: 2rem;">No notifications right now.</p>`;
      return;
    }

    container.innerHTML = notifs.map(n => `
      <div style="background: rgba(255, 255, 255, ${n.read ? '0.01' : '0.04'}); border: 1px solid ${n.read ? 'var(--border-color)' : 'var(--primary)'}; border-radius: var(--radius-md); padding: 1.25rem; display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.85rem;">
        <div>
          <div style="color: #fff; font-weight: 700; font-size: 0.95rem; margin-bottom: 0.2rem;">${n.title}</div>
          <p style="color: var(--text-muted); font-size: 0.85rem;">${n.message}</p>
          <span style="font-size: 0.75rem; color: var(--text-light);">${new Date(n.createdAt).toLocaleString()}</span>
        </div>
        ${!n.read ? `
          <button class="btn btn-secondary" onclick="markAsRead(${n.id})" style="padding: 0.35rem 0.75rem; font-size: 0.78rem;">
            Mark Read
          </button>
        ` : ''}
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load notifications: ${err.message}</p>`;
  }
}

async function markAsRead(id) {
  try {
    await api.put(`/notifications/${id}/read`, {});
    loadNotifications();
  } catch (e) {}
}

async function markAllNotificationsAsRead() {
  try {
    await api.put('/notifications/read-all', {});
    showToast('All notifications marked as read', 'info');
    loadNotifications();
  } catch (e) {}
}