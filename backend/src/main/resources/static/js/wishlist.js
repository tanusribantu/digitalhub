// Wishlist Controller
document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar();
  renderFooter();

  loadWishlist();
});

async function loadWishlist() {
  const container = document.getElementById('wishlistGrid');
  if (!container) return;

  try {
    const items = await api.get('/wishlist');
    if (!items || items.length === 0) {
      container.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 4rem 2rem; background: var(--card-bg); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
          <i class="fa-regular fa-heart" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
          <h3 style="color: #fff; margin-bottom: 0.5rem;">Your wishlist is empty</h3>
          <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Save your favorite digital assets to purchase later.</p>
          <a href="products.html" class="btn btn-primary">Browse Marketplace</a>
        </div>
      `;
      return;
    }

    container.innerHTML = items.map(renderProductCard).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load wishlist: ${err.message}</p>`;
  }
}