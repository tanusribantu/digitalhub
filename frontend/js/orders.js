// Orders History Controller
document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar();
  renderFooter();

  loadOrders();
});

async function loadOrders() {
  const container = document.getElementById('ordersListContainer');
  if (!container) return;

  try {
    const orders = await api.get('/orders');
    if (!orders || orders.length === 0) {
      container.innerHTML = `
        <div style="text-align: center; padding: 4rem 2rem; background: var(--card-bg); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
          <i class="fa-solid fa-receipt" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
          <h3 style="color: #fff; margin-bottom: 0.5rem;">No orders yet</h3>
          <p style="color: var(--text-muted); margin-bottom: 1.5rem;">When you complete a purchase, your invoices and licenses will appear here.</p>
          <a href="products.html" class="btn btn-primary">Browse Marketplace</a>
        </div>
      `;
      return;
    }

    container.innerHTML = orders.map(order => `
      <div style="background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 1.75rem; margin-bottom: 1.5rem;">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; padding-bottom: 1.25rem; border-bottom: 1px solid var(--border-color); margin-bottom: 1.25rem; flex-wrap: wrap; gap: 1rem;">
          <div>
            <div style="font-size: 0.82rem; color: var(--text-muted); text-transform: uppercase;">Order Number</div>
            <div style="color: #fff; font-weight: 700; font-size: 1.1rem;">${order.orderNumber}</div>
            <div style="font-size: 0.8rem; color: var(--text-light); margin-top: 0.2rem;">Purchased on ${new Date(order.createdAt).toLocaleDateString()}</div>
          </div>
          <div style="text-align: right;">
            <span class="status-chip status-completed"><i class="fa-solid fa-circle-check"></i> ${order.status}</span>
            <div style="font-size: 1.25rem; font-weight: 800; color: #fff; margin-top: 0.35rem;">$${order.finalAmount.toFixed(2)}</div>
            <div style="font-size: 0.78rem; color: var(--text-muted);">${order.paymentMethod}</div>
          </div>
        </div>

        <div style="display: flex; flex-direction: column; gap: 0.85rem;">
          ${(order.items || []).map(item => `
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <div style="color: #fff; font-weight: 600; font-size: 0.95rem;">${item.productTitle}</div>
                <span style="font-size: 0.8rem; color: var(--text-muted);">Format: ${item.fileFormat || 'Instant File'}</span>
              </div>
              <div style="display: flex; align-items: center; gap: 1.5rem;">
                <span style="color: #fff; font-weight: 700;">$${item.priceAtPurchase.toFixed(2)}</span>
                <a href="downloads.html" class="btn btn-secondary" style="padding: 0.35rem 0.8rem; font-size: 0.82rem;">
                  <i class="fa-solid fa-download"></i> Access Files
                </a>
              </div>
            </div>
          `).join('')}
        </div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load orders: ${err.message}</p>`;
  }
}