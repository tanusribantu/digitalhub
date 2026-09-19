// Digital Library & File Download Controller
document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar();
  renderFooter();

  loadDownloads();
});

async function loadDownloads() {
  const container = document.getElementById('downloadsGrid');
  if (!container) return;

  try {
    const items = await api.get('/downloads');
    if (!items || items.length === 0) {
      container.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 4rem 2rem; background: var(--card-bg); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
          <i class="fa-solid fa-cloud-arrow-down" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
          <h3 style="color: #fff; margin-bottom: 0.5rem;">Your digital library is currently empty</h3>
          <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Browse products on DigitalHub and start downloading instantly after purchase.</p>
          <a href="products.html" class="btn btn-primary">Browse Marketplace</a>
        </div>
      `;
      return;
    }

    container.innerHTML = items.map(item => `
      <div style="background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.5rem; display: flex; flex-direction: column;">
        <div style="display: flex; gap: 1rem; margin-bottom: 1.25rem;">
          <img src="${item.previewImageUrl || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=150'}" alt="${item.productTitle}" style="width: 70px; height: 70px; border-radius: var(--radius-sm); object-fit: cover;" />
          <div>
            <span class="badge-pill badge-format" style="margin-bottom: 0.35rem; display: inline-block;">${item.fileFormat || 'ZIP'}</span>
            <h4 style="color: #fff; font-size: 1rem; line-height: 1.35; margin-bottom: 0.25rem;">
              <a href="product-details.html?slug=${item.productSlug}">${item.productTitle}</a>
            </h4>
            <span style="font-size: 0.78rem; color: var(--text-muted);">Size: ${item.fileSize || 'Standard Asset'}</span>
          </div>
        </div>

        <div style="margin-top: auto; padding-top: 1rem; border-top: 1px solid var(--border-color); display: flex; align-items: center; justify-content: space-between;">
          <span style="font-size: 0.8rem; color: #10b981;">
            <i class="fa-solid fa-circle-check"></i> Licensed & Verified
          </span>
          <button class="btn btn-primary" onclick="downloadItem(${item.id}, this)" style="padding: 0.45rem 1rem; font-size: 0.85rem;">
            <i class="fa-solid fa-download"></i> Download
          </button>
        </div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load downloads: ${err.message}</p>`;
  }
}

async function downloadItem(orderItemId, btn) {
  const token = getAuthToken();
  const url = `${API_BASE_URL}/downloads/${orderItemId}`;
  
  const originalHtml = btn.innerHTML;
  btn.disabled = true;
  btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Preparing...';

  try {
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to download asset');
    }

    // Extract filename from Content-Disposition if present
    const disposition = response.headers.get('Content-Disposition');
    let fileName = 'digitalhub-product.zip';
    if (disposition && disposition.indexOf('filename=') !== -1) {
      const matches = /filename="([^"]*)"/.exec(disposition);
      if (matches != null && matches[1]) fileName = matches[1];
    }

    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(downloadUrl);

    showToast('Download started: ' + fileName, 'success');
  } catch (err) {
    showToast('Download error: ' + err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.innerHTML = originalHtml;
  }
}