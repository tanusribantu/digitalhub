// Landing Page Data Loader
document.addEventListener('DOMContentLoaded', async () => {
  await renderNavbar('home');
  renderFooter();

  loadCategories();
  loadTrendingProducts();
  loadBestSellers();
  loadRecentProducts();
});

async function loadCategories() {
  const container = document.getElementById('categoriesGrid');
  if (!container) return;

  try {
    const categories = await api.get('/categories');
    container.innerHTML = categories.map(cat => `
      <div class="category-card" onclick="window.location.href='products.html?category=${cat.slug}'">
        <div class="cat-icon-wrap">
          <i class="${cat.icon || 'fa-solid fa-folder'}"></i>
        </div>
        <div class="cat-name">${cat.name}</div>
        <div class="cat-desc">${cat.description || ''}</div>
      </div>
    `).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--text-muted);">Failed to load categories.</p>`;
  }
}

async function loadTrendingProducts() {
  const container = document.getElementById('trendingGrid');
  if (!container) return;

  try {
    const products = await api.get('/products/trending?limit=4');
    container.innerHTML = products.map(renderProductCard).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--text-muted);">Failed to load trending products.</p>`;
  }
}

async function loadBestSellers() {
  const container = document.getElementById('bestSellersGrid');
  if (!container) return;

  try {
    const products = await api.get('/products/top-rated?limit=4');
    container.innerHTML = products.map(renderProductCard).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--text-muted);">Failed to load best sellers.</p>`;
  }
}

async function loadRecentProducts() {
  const container = document.getElementById('recentGrid');
  if (!container) return;

  try {
    const products = await api.get('/products/recent?limit=4');
    container.innerHTML = products.map(renderProductCard).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--text-muted);">Failed to load recent products.</p>`;
  }
}

function renderProductCard(p) {
  const discountBadge = p.discountPercent > 0 ? `<span class="badge-pill badge-discount">-${p.discountPercent}%</span>` : '';
  const formatBadge = p.fileFormat ? `<span class="badge-pill badge-format">${p.fileFormat.split(' ')[0]}</span>` : '';
  const originalPriceHtml = p.discountPercent > 0 ? `<span class="price-old">$${p.price.toFixed(2)}</span>` : '';
  const fallbackImg = 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600';

  return `
    <div class="product-card">
      <div class="prod-thumb-wrap" onclick="window.location.href='product-details.html?slug=${p.slug}'" style="cursor: pointer;">
        <img src="${p.previewImageUrl || fallbackImg}" alt="${p.title}" class="prod-thumb" onerror="this.src='${fallbackImg}'" />
        <div class="prod-badges">
          ${discountBadge}
          ${formatBadge}
        </div>
        <button class="btn-wishlist" onclick="event.stopPropagation(); toggleWishlist(${p.id}, this)" title="Add to Wishlist">
          <i class="fa-regular fa-heart"></i>
        </button>
      </div>

      <div class="prod-body">
        <div class="prod-meta">
          <span class="prod-seller"><i class="fa-solid fa-circle-check"></i> ${p.sellerName || 'Verified Studio'}</span>
          <span>${p.categoryName || 'Resource'}</span>
        </div>

        <h3 class="prod-title" onclick="window.location.href='product-details.html?slug=${p.slug}'" style="cursor: pointer;">
          ${p.title}
        </h3>
        <p class="prod-desc">${p.shortDescription || ''}</p>

        <div class="prod-rating">
          <div class="stars">
            <i class="fa-solid fa-star"></i>
            <span style="font-weight: 700; color: #fff; margin-left: 0.2rem;">${p.averageRating ? p.averageRating.toFixed(1) : '5.0'}</span>
          </div>
          <span class="rating-count">(${p.reviewCount || 0} reviews)</span>
          <span style="margin-left: auto; color: var(--text-muted); font-size: 0.78rem;">
            <i class="fa-solid fa-download"></i> ${p.downloadCount || 0}
          </span>
        </div>

        <div class="prod-footer">
          <div class="prod-pricing">
            <span class="price-current">$${p.effectivePrice.toFixed(2)}</span>
            ${originalPriceHtml}
          </div>
          <button class="btn-add-cart" onclick="addToCart(${p.id})" title="Add to Cart">
            <i class="fa-solid fa-plus"></i>
          </button>
        </div>
      </div>
    </div>
  `;
}

async function addToCart(productId) {
  if (!checkAuth()) {
    showToast('Please sign in to add products to your cart', 'info');
    setTimeout(() => window.location.href = 'login.html', 1200);
    return;
  }

  try {
    await api.post(`/cart/add/${productId}`);
    showToast('Product added to your cart!', 'success');
    renderNavbar();
  } catch (err) {
    showToast(err.message, 'warning');
  }
}

async function toggleWishlist(productId, btnEl) {
  if (!checkAuth()) {
    showToast('Please sign in to save items to your wishlist', 'info');
    setTimeout(() => window.location.href = 'login.html', 1200);
    return;
  }

  try {
    const res = await api.post(`/wishlist/toggle/${productId}`);
    if (res.inWishlist) {
      btnEl.classList.add('active');
      btnEl.innerHTML = '<i class="fa-solid fa-heart"></i>';
      showToast('Saved to wishlist', 'success');
    } else {
      btnEl.classList.remove('active');
      btnEl.innerHTML = '<i class="fa-regular fa-heart"></i>';
      showToast('Removed from wishlist', 'info');
    }
    renderNavbar();
  } catch (err) {
    showToast(err.message, 'error');
  }
}