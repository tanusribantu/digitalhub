// Admin Portal & Platform Governance Controller
document.addEventListener('DOMContentLoaded', async () => {
  if (!requireRole(['ROLE_ADMIN'])) return;
  await renderNavbar();

  loadAdminStats();
  loadAllUsers();
  loadAllProducts();
  loadAllCoupons();
  loadAllReports();
});

async function loadAdminStats() {
  try {
    const stats = await api.get('/admin/stats');
    document.getElementById('adminTotalRevenue').innerText = `$${stats.totalRevenue.toFixed(2)}`;
    document.getElementById('adminTotalUsers').innerText = stats.totalUsers || 0;
    document.getElementById('adminTotalSellers').innerText = stats.totalSellers || 0;
    document.getElementById('adminTotalProducts').innerText = stats.totalProducts || 0;
  } catch (err) {
    showToast('Failed to load admin metrics: ' + err.message, 'error');
  }
}

async function loadAllUsers() {
  const container = document.getElementById('adminUsersTableBody');
  if (!container) return;

  try {
    const users = await api.get('/admin/users');
    container.innerHTML = users.map(u => `
      <tr>
        <td>
          <div style="display: flex; align-items: center; gap: 0.75rem;">
            <div class="avatar-img">${(u.fullName || 'U').charAt(0).toUpperCase()}</div>
            <div>
              <div style="color: #fff; font-weight: 600;">${u.fullName}</div>
              <span style="font-size: 0.75rem; color: var(--text-muted);">${u.email}</span>
            </div>
          </div>
        </td>
        <td>
          <span class="role-tag ${u.role === 'ROLE_ADMIN' ? 'admin' : (u.role === 'ROLE_SELLER' ? 'seller' : 'customer')}">
            ${u.role.replace('ROLE_', '')}
          </span>
        </td>
        <td>
          <span class="status-chip ${u.enabled ? 'status-active' : 'status-inactive'}">
            ${u.enabled ? 'Active' : 'Disabled'}
          </span>
        </td>
        <td>${new Date(u.createdAt).toLocaleDateString()}</td>
        <td>
          <button class="btn btn-secondary" onclick="toggleUserStatus(${u.id})" style="padding: 0.35rem 0.75rem; font-size: 0.78rem;">
            ${u.enabled ? '<i class="fa-solid fa-ban"></i> Disable' : '<i class="fa-solid fa-check"></i> Enable'}
          </button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="5" style="color: var(--accent);">Failed to load users: ${err.message}</td></tr>`;
  }
}

async function toggleUserStatus(userId) {
  try {
    const updated = await api.put(`/admin/users/${userId}/toggle-status`, {});
    showToast(`User ${updated.fullName} is now ${updated.enabled ? 'enabled' : 'disabled'}`, 'info');
    loadAllUsers();
    loadAdminStats();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function loadAllProducts() {
  const container = document.getElementById('adminProductsTableBody');
  if (!container) return;

  try {
    const products = await api.get('/admin/products');
    container.innerHTML = products.map(p => `
      <tr>
        <td>
          <div style="color: #fff; font-weight: 600;">${p.title}</div>
          <span style="font-size: 0.78rem; color: var(--text-muted);">${p.categoryName} • Seller: ${p.sellerName}</span>
        </td>
        <td style="color: #fff; font-weight: 700;">$${p.price.toFixed(2)}</td>
        <td>
          <span class="status-chip ${p.status === 'ACTIVE' ? 'status-active' : 'status-inactive'}">
            ${p.status}
          </span>
        </td>
        <td>
          <button class="icon-btn ${p.featured ? 'active' : ''}" onclick="toggleProductFeatured(${p.id})" style="${p.featured ? 'color: #f59e0b;' : ''}" title="Toggle Featured">
            <i class="fa-${p.featured ? 'solid' : 'regular'} fa-star"></i>
          </button>
        </td>
        <td>
          <select onchange="updateProductStatus(${p.id}, this.value)" style="background: rgba(255,255,255,0.05); border: 1px solid var(--border-color); color: #fff; padding: 0.35rem 0.6rem; border-radius: var(--radius-sm); font-size: 0.8rem;">
            <option value="ACTIVE" ${p.status === 'ACTIVE' ? 'selected' : ''}>Active</option>
            <option value="PENDING" ${p.status === 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="INACTIVE" ${p.status === 'INACTIVE' ? 'selected' : ''}>Inactive</option>
            <option value="REJECTED" ${p.status === 'REJECTED' ? 'selected' : ''}>Rejected</option>
          </select>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="5" style="color: var(--accent);">Failed to load products: ${err.message}</td></tr>`;
  }
}

async function updateProductStatus(productId, newStatus) {
  try {
    await api.put(`/admin/products/${productId}/status?status=${newStatus}`, {});
    showToast('Product status updated to ' + newStatus, 'success');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function toggleProductFeatured(productId) {
  try {
    await api.put(`/admin/products/${productId}/toggle-featured`, {});
    showToast('Featured status updated', 'success');
    loadAllProducts();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function loadAllCoupons() {
  const container = document.getElementById('adminCouponsTableBody');
  if (!container) return;

  try {
    const coupons = await api.get('/admin/coupons');
    container.innerHTML = coupons.map(c => `
      <tr>
        <td style="font-family: monospace; font-weight: 700; color: #a5b4fc; font-size: 0.95rem;">${c.code}</td>
        <td>${c.discountPercent}%</td>
        <td>$${c.minOrderAmount ? c.minOrderAmount.toFixed(2) : '0.00'}</td>
        <td>${c.timesUsed || 0}</td>
        <td>
          <button class="status-chip ${c.active ? 'status-active' : 'status-inactive'}" onclick="toggleCouponStatus(${c.id})" style="border: none; cursor: pointer;">
            ${c.active ? 'Active' : 'Disabled'}
          </button>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="5" style="color: var(--accent);">Failed to load coupons: ${err.message}</td></tr>`;
  }
}

async function toggleCouponStatus(couponId) {
  try {
    await api.put(`/admin/coupons/${couponId}/toggle-status`, {});
    showToast('Coupon status updated', 'info');
    loadAllCoupons();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function openCouponModal() {
  document.getElementById('couponForm').reset();
  document.getElementById('couponModal').classList.add('show');
}

function closeCouponModal() {
  document.getElementById('couponModal').classList.remove('show');
}

async function submitCoupon(e) {
  e.preventDefault();
  const payload = {
    code: document.getElementById('newCouponCode').value.trim().toUpperCase(),
    discountPercent: parseInt(document.getElementById('newCouponPercent').value),
    minOrderAmount: parseFloat(document.getElementById('newCouponMinAmount').value) || 0,
    maxDiscountAmount: parseFloat(document.getElementById('newCouponMaxDiscount').value) || null,
    usageLimit: parseInt(document.getElementById('newCouponLimit').value) || null
  };

  try {
    await api.post('/admin/coupons', payload);
    showToast('New coupon generated successfully!', 'success');
    closeCouponModal();
    loadAllCoupons();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function loadAllReports() {
  const container = document.getElementById('adminReportsTableBody');
  if (!container) return;

  try {
    const reports = await api.get('/admin/reports');
    if (!reports || reports.length === 0) {
      container.innerHTML = `<tr><td colspan="4" style="text-align: center; color: var(--text-muted); padding: 2rem;">No pending reports on file. Platform is clean.</td></tr>`;
      return;
    }

    container.innerHTML = reports.map(r => `
      <tr>
        <td>${r.product ? r.product.title : 'Product'}</td>
        <td>${r.reason}</td>
        <td><span class="status-chip status-pending">${r.status}</span></td>
        <td>${new Date(r.createdAt).toLocaleDateString()}</td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="4" style="color: var(--accent);">Failed to load reports: ${err.message}</td></tr>`;
  }
}