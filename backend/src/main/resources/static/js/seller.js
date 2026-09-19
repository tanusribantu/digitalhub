// Seller Studio & Product Management Controller
let sellerProducts = [];
let editingProductId = null;

document.addEventListener('DOMContentLoaded', async () => {
  if (!requireRole(['ROLE_SELLER', 'ROLE_ADMIN'])) return;
  await renderNavbar();

  loadSellerStats();
  loadSellerProducts();
  loadSellerSales();
  loadCategoryDropdown();
});

async function loadCategoryDropdown() {
  const select = document.getElementById('prodCategorySelect');
  if (!select) return;
  try {
    const categories = await api.get('/categories');
    select.innerHTML = categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
  } catch (e) {}
}

async function loadSellerStats() {
  try {
    const stats = await api.get('/seller/stats');
    document.getElementById('statTotalRevenue').innerText = `$${stats.totalRevenue.toFixed(2)}`;
    document.getElementById('statTotalSales').innerText = stats.totalSales || 0;
    document.getElementById('statTotalProducts').innerText = stats.totalProducts || 0;
  } catch (err) {
    showToast('Failed to load seller stats: ' + err.message, 'error');
  }
}

async function loadSellerProducts() {
  const container = document.getElementById('sellerProductsTableBody');
  if (!container) return;

  try {
    sellerProducts = await api.get('/seller/products');
    if (!sellerProducts || sellerProducts.length === 0) {
      container.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">No products listed yet. Click "+ Add New Product" to get started.</td></tr>`;
      return;
    }

    container.innerHTML = sellerProducts.map(p => `
      <tr>
        <td>
          <div style="display: flex; align-items: center; gap: 0.75rem;">
            <img src="${p.previewImageUrl || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=100'}" alt="${p.title}" style="width: 44px; height: 34px; object-fit: cover; border-radius: var(--radius-sm);" />
            <div>
              <div style="color: #fff; font-weight: 600;">${p.title}</div>
              <span style="font-size: 0.75rem; color: var(--text-muted);">${p.categoryName || 'General'} • v${p.version || '1.0'}</span>
            </div>
          </div>
        </td>
        <td style="color: #fff; font-weight: 700;">$${p.price.toFixed(2)}</td>
        <td>${p.discountPercent}%</td>
        <td><i class="fa-solid fa-download"></i> ${p.downloadCount || 0}</td>
        <td>
          <span class="status-chip ${p.status === 'ACTIVE' ? 'status-active' : 'status-inactive'}">
            ${p.status}
          </span>
        </td>
        <td>
          <div style="display: flex; gap: 0.4rem;">
            <button class="icon-btn" onclick="openUploadFileModal(${p.id}, '${p.title}')" title="Upload Product File (.ZIP/.PDF)"><i class="fa-solid fa-upload"></i></button>
            <button class="icon-btn" onclick="openEditProductModal(${p.id})" title="Edit Product"><i class="fa-solid fa-pen"></i></button>
            <button class="icon-btn" onclick="deleteProduct(${p.id})" style="color: var(--accent);" title="Deactivate"><i class="fa-solid fa-trash"></i></button>
          </div>
        </td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="6" style="color: var(--accent); padding: 2rem;">Error: ${err.message}</td></tr>`;
  }
}

async function loadSellerSales() {
  const container = document.getElementById('sellerSalesTableBody');
  if (!container) return;

  try {
    const sales = await api.get('/seller/sales');
    if (!sales || sales.length === 0) {
      container.innerHTML = `<tr><td colspan="4" style="text-align: center; color: var(--text-muted); padding: 2rem;">No sales recorded yet.</td></tr>`;
      return;
    }

    container.innerHTML = sales.map(s => `
      <tr>
        <td style="color: #fff; font-weight: 600;">${s.productTitle}</td>
        <td>$${s.priceAtPurchase.toFixed(2)}</td>
        <td><span class="status-chip status-completed">Completed</span></td>
        <td><span style="color: #10b981; font-weight: 700;">+$${s.priceAtPurchase.toFixed(2)}</span></td>
      </tr>
    `).join('');
  } catch (err) {
    container.innerHTML = `<tr><td colspan="4" style="color: var(--accent);">Failed to load sales: ${err.message}</td></tr>`;
  }
}

function openAddProductModal() {
  editingProductId = null;
  document.getElementById('productModalTitle').innerText = 'Add New Digital Product';
  document.getElementById('productForm').reset();
  document.getElementById('productModal').classList.add('show');
}

function openEditProductModal(productId) {
  const p = sellerProducts.find(item => item.id === productId);
  if (!p) return;

  editingProductId = productId;
  document.getElementById('productModalTitle').innerText = 'Edit Digital Product';
  document.getElementById('prodTitleInput').value = p.title || '';
  document.getElementById('prodShortDescInput').value = p.shortDescription || '';
  document.getElementById('prodFullDescInput').value = p.fullDescription || '';
  document.getElementById('prodPriceInput').value = p.price || '';
  document.getElementById('prodDiscountInput').value = p.discountPercent || 0;
  document.getElementById('prodCategorySelect').value = p.categoryId || '';
  document.getElementById('prodFormatInput').value = p.fileFormat || '';
  document.getElementById('prodSizeInput').value = p.fileSize || '';
  document.getElementById('prodVersionInput').value = p.version || '1.0';
  document.getElementById('prodImageInput').value = p.previewImageUrl || '';
  document.getElementById('productModal').classList.add('show');
}

function closeProductModal() {
  document.getElementById('productModal').classList.remove('show');
}

async function saveProduct(e) {
  e.preventDefault();

  const payload = {
    title: document.getElementById('prodTitleInput').value.trim(),
    shortDescription: document.getElementById('prodShortDescInput').value.trim(),
    fullDescription: document.getElementById('prodFullDescInput').value.trim(),
    price: parseFloat(document.getElementById('prodPriceInput').value),
    discountPercent: parseInt(document.getElementById('prodDiscountInput').value) || 0,
    categoryId: parseInt(document.getElementById('prodCategorySelect').value),
    fileFormat: document.getElementById('prodFormatInput').value.trim(),
    fileSize: document.getElementById('prodSizeInput').value.trim(),
    version: document.getElementById('prodVersionInput').value.trim() || '1.0',
    previewImageUrl: document.getElementById('prodImageInput').value.trim()
  };

  try {
    if (editingProductId) {
      await api.put(`/seller/products/${editingProductId}`, payload);
      showToast('Product updated successfully!', 'success');
    } else {
      await api.post('/seller/products', payload);
      showToast('New digital product published!', 'success');
    }

    closeProductModal();
    loadSellerProducts();
    loadSellerStats();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteProduct(productId) {
  if (!confirm('Are you sure you want to deactivate this product?')) return;

  try {
    await api.delete(`/seller/products/${productId}`);
    showToast('Product deactivated', 'info');
    loadSellerProducts();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

let targetUploadProductId = null;
function openUploadFileModal(productId, productTitle) {
  targetUploadProductId = productId;
  document.getElementById('uploadTargetTitle').innerText = productTitle;
  document.getElementById('uploadFileModal').classList.add('show');
}

function closeUploadFileModal() {
  document.getElementById('uploadFileModal').classList.remove('show');
}

async function submitFileUpload(e) {
  e.preventDefault();
  const fileInput = document.getElementById('productFileInput');
  if (!fileInput.files || fileInput.files.length === 0) {
    showToast('Please select a file to upload', 'warning');
    return;
  }

  const formData = new FormData();
  formData.append('file', fileInput.files[0]);

  const submitBtn = document.getElementById('uploadSubmitBtn');
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Uploading asset...';

  try {
    await api.upload(`/seller/products/${targetUploadProductId}/upload-file`, formData);
    showToast('Asset uploaded successfully!', 'success');
    closeUploadFileModal();
    loadSellerProducts();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = 'Upload Asset';
  }
}