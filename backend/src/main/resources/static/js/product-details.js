// Product Details & Reviews Controller
let currentProduct = null;

document.addEventListener('DOMContentLoaded', async () => {
  await renderNavbar('products');
  renderFooter();

  const urlParams = new URLSearchParams(window.location.search);
  const slug = urlParams.get('slug');
  const id = urlParams.get('id');

  if (!slug && !id) {
    window.location.href = 'products.html';
    return;
  }

  loadProductDetails(slug, id);
});

async function loadProductDetails(slug, id) {
  try {
    const endpoint = slug ? `/products/slug/${slug}` : `/products/${id}`;
    currentProduct = await api.get(endpoint);
    renderProductView(currentProduct);
    loadReviews(currentProduct.id);
  } catch (err) {
    document.getElementById('productDetailsContainer').innerHTML = `
      <div style="text-align: center; padding: 4rem 0;">
        <h2 style="color: #fff;">Product Not Found</h2>
        <p style="color: var(--text-muted); margin: 1rem 0;">${err.message}</p>
        <a href="products.html" class="btn btn-primary">Browse All Products</a>
      </div>
    `;
  }
}

function renderProductView(p) {
  document.title = `${p.title} – DigitalHub`;

  const fallbackImg = 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=1000';
  const imgUrl = p.previewImageUrl || fallbackImg;
  const discountHtml = p.discountPercent > 0 ? `<span class="badge-pill badge-discount">Save ${p.discountPercent}%</span>` : '';
  const originalPriceHtml = p.discountPercent > 0 ? `<span class="price-old" style="font-size: 1.1rem;">$${p.price.toFixed(2)}</span>` : '';

  document.getElementById('productDetailsContainer').innerHTML = `
    <div style="display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 3rem; margin-bottom: 4rem;">
      <!-- Product Left Column: Media & Specifications -->
      <div>
        <div style="border-radius: var(--radius-lg); overflow: hidden; border: 1px solid var(--border-color); background: #070a12; margin-bottom: 2rem;">
          <img src="${imgUrl}" alt="${p.title}" style="width: 100%; max-height: 480px; object-fit: cover;" onerror="this.src='${fallbackImg}'" />
        </div>

        <div style="background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 2rem; margin-bottom: 2rem;">
          <h3 style="color: #fff; font-size: 1.35rem; margin-bottom: 1.25rem;">Overview & Description</h3>
          <p style="color: var(--text-main); font-size: 1.05rem; line-height: 1.7; margin-bottom: 1.5rem;">
            ${p.shortDescription || ''}
          </p>
          <div style="color: var(--text-muted); line-height: 1.8; font-size: 0.95rem; white-space: pre-line;">
            ${p.fullDescription || 'No additional extended description provided.'}
          </div>
        </div>

        <!-- Specifications Table -->
        <div style="background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 2rem;">
          <h3 style="color: #fff; font-size: 1.25rem; margin-bottom: 1.25rem;">Product Specifications</h3>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem;">
            <div>
              <span style="color: var(--text-muted); font-size: 0.85rem;">File Format:</span>
              <p style="color: #fff; font-weight: 600; margin-top: 0.2rem;">${p.fileFormat || 'Archive (.ZIP)'}</p>
            </div>
            <div>
              <span style="color: var(--text-muted); font-size: 0.85rem;">File Size:</span>
              <p style="color: #fff; font-weight: 600; margin-top: 0.2rem;">${p.fileSize || 'Instant Delivery'}</p>
            </div>
            <div>
              <span style="color: var(--text-muted); font-size: 0.85rem;">Current Version:</span>
              <p style="color: #fff; font-weight: 600; margin-top: 0.2rem;">v${p.version || '1.0'}</p>
            </div>
            <div>
              <span style="color: var(--text-muted); font-size: 0.85rem;">Category:</span>
              <p style="color: #fff; font-weight: 600; margin-top: 0.2rem;">${p.categoryName || 'General'}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Product Right Column: Pricing & Action Box -->
      <div>
        <div style="position: sticky; top: 90px; background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 2.25rem; box-shadow: var(--shadow-lg);">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.75rem;">
            <span style="color: #38bdf8; font-weight: 600; font-size: 0.9rem;">
              <i class="fa-solid fa-circle-check"></i> Verified Creator: ${p.sellerName || 'Apex Studio'}
            </span>
            ${discountHtml}
          </div>

          <h1 style="font-size: 1.75rem; font-weight: 800; color: #fff; line-height: 1.3; margin-bottom: 1rem;">
            ${p.title}
          </h1>

          <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem; padding-bottom: 1.5rem; border-bottom: 1px solid var(--border-color);">
            <div style="display: flex; align-items: center; gap: 0.35rem; color: #f59e0b;">
              <i class="fa-solid fa-star"></i>
              <span style="font-weight: 700; color: #fff; font-size: 1.05rem;">${p.averageRating ? p.averageRating.toFixed(1) : '5.0'}</span>
            </div>
            <span style="color: var(--text-muted); font-size: 0.88rem;">(${p.reviewCount || 0} reviews)</span>
            <span style="color: var(--text-muted); font-size: 0.88rem; margin-left: auto;">
              <i class="fa-solid fa-download"></i> ${p.downloadCount || 0} sales
            </span>
          </div>

          <div style="display: flex; align-items: baseline; gap: 0.75rem; margin-bottom: 2rem;">
            <span style="font-size: 2.5rem; font-weight: 900; color: #fff;">$${p.effectivePrice.toFixed(2)}</span>
            ${originalPriceHtml}
          </div>

          <div style="display: flex; flex-direction: column; gap: 0.85rem; margin-bottom: 2rem;">
            <button class="btn btn-primary" onclick="buyNow(${p.id})" style="padding: 0.9rem 1.5rem; font-size: 1.05rem;">
              <i class="fa-solid fa-bolt"></i> Buy Now & Download
            </button>
            <button class="btn btn-secondary" onclick="addToCart(${p.id})" style="padding: 0.85rem 1.5rem;">
              <i class="fa-solid fa-cart-plus"></i> Add to Shopping Cart
            </button>
            <button class="btn btn-outline" id="detailsWishlistBtn" onclick="toggleDetailsWishlist(${p.id})" style="padding: 0.75rem 1.5rem;">
              <i class="fa-regular fa-heart"></i> Save to Wishlist
            </button>
          </div>

          <div style="border-top: 1px solid var(--border-color); padding-top: 1.5rem; font-size: 0.86rem; color: var(--text-muted); display: flex; flex-direction: column; gap: 0.65rem;">
            <div><i class="fa-solid fa-shield-halved" style="color: #10b981; margin-right: 0.5rem;"></i> Instant digital file delivery after checkout</div>
            <div><i class="fa-solid fa-infinity" style="color: var(--primary); margin-right: 0.5rem;"></i> Lifetime access & free future updates</div>
            <div><i class="fa-solid fa-rotate-left" style="color: #f59e0b; margin-right: 0.5rem;"></i> 14-day technical support guarantee</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Product Reviews Section -->
    <div id="reviewsSection" style="background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: 2.5rem; margin-bottom: 4rem;">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 2rem;">
        <div>
          <h3 style="color: #fff; font-size: 1.5rem; font-weight: 800;">Customer Reviews</h3>
          <p style="color: var(--text-muted); font-size: 0.9rem;">Verified feedback from community developers and buyers</p>
        </div>
        <button class="btn btn-primary" onclick="openReviewModal()">
          <i class="fa-solid fa-pen"></i> Write a Review
        </button>
      </div>

      <div id="reviewsList" style="display: flex; flex-direction: column; gap: 1.25rem;">
        <p style="color: var(--text-muted);">Loading reviews...</p>
      </div>
    </div>
  `;

  checkDetailsWishlistStatus(p.id);
}

async function buyNow(productId) {
  if (!checkAuth()) {
    showToast('Please sign in to proceed with direct checkout', 'info');
    setTimeout(() => window.location.href = `login.html?redirect=checkout.html?single=${productId}`, 1000);
    return;
  }
  window.location.href = `checkout.html?single=${productId}`;
}

async function checkDetailsWishlistStatus(productId) {
  if (!checkAuth()) return;
  try {
    const res = await api.get(`/wishlist/check/${productId}`);
    const btn = document.getElementById('detailsWishlistBtn');
    if (res && res.inWishlist && btn) {
      btn.innerHTML = '<i class="fa-solid fa-heart" style="color: var(--accent);"></i> In Wishlist';
      btn.classList.add('active');
    }
  } catch (e) {}
}

async function toggleDetailsWishlist(productId) {
  if (!checkAuth()) {
    showToast('Please sign in to save items to wishlist', 'info');
    setTimeout(() => window.location.href = 'login.html', 1000);
    return;
  }
  try {
    const res = await api.post(`/wishlist/toggle/${productId}`);
    const btn = document.getElementById('detailsWishlistBtn');
    if (res.inWishlist) {
      btn.innerHTML = '<i class="fa-solid fa-heart" style="color: var(--accent);"></i> In Wishlist';
      btn.classList.add('active');
      showToast('Saved to wishlist', 'success');
    } else {
      btn.innerHTML = '<i class="fa-regular fa-heart"></i> Save to Wishlist';
      btn.classList.remove('active');
      showToast('Removed from wishlist', 'info');
    }
    renderNavbar();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function loadReviews(productId) {
  const container = document.getElementById('reviewsList');
  if (!container) return;

  try {
    const reviews = await api.get(`/products/${productId}/reviews`);
    if (!reviews || reviews.length === 0) {
      container.innerHTML = `
        <div style="text-align: center; padding: 2rem; color: var(--text-muted);">
          <i class="fa-regular fa-comment-dots" style="font-size: 2rem; margin-bottom: 0.75rem;"></i>
          <p>No reviews yet for this product. Be the first verified buyer to share your feedback!</p>
        </div>
      `;
      return;
    }

    const currentUser = getCurrentUser();

    container.innerHTML = reviews.map(r => {
      const isAuthor = currentUser && currentUser.id === r.customerId;
      const actionsHtml = isAuthor ? `
        <div style="margin-left: auto; display: flex; gap: 0.5rem;">
          <button class="icon-btn" onclick="openEditReviewModal(${r.id}, ${r.rating}, '${escapeHtml(r.comment)}')" title="Edit Review"><i class="fa-solid fa-pen-to-square"></i></button>
          <button class="icon-btn" onclick="deleteReview(${r.id})" style="color: var(--accent);" title="Delete Review"><i class="fa-solid fa-trash"></i></button>
        </div>
      ` : '';

      return `
        <div style="background: rgba(255, 255, 255, 0.02); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.5rem;">
          <div style="display: flex; align-items: center; gap: 0.85rem; margin-bottom: 0.85rem;">
            <div class="avatar-img">${(r.customerName || 'U').charAt(0).toUpperCase()}</div>
            <div>
              <div style="color: #fff; font-weight: 600; font-size: 0.95rem;">${r.customerName}</div>
              <div style="color: var(--text-light); font-size: 0.78rem;">Verified Buyer • ${new Date(r.createdAt).toLocaleDateString()}</div>
            </div>
            <div style="color: #f59e0b; margin-left: 1.5rem;">
              ${Array.from({ length: 5 }, (_, i) => `<i class="fa-star ${i < r.rating ? 'fa-solid' : 'fa-regular'}"></i>`).join('')}
            </div>
            ${actionsHtml}
          </div>
          <p style="color: var(--text-main); font-size: 0.92rem; line-height: 1.6;">${escapeHtml(r.comment)}</p>
        </div>
      `;
    }).join('');
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load reviews.</p>`;
  }
}

function openReviewModal() {
  if (!checkAuth()) {
    showToast('Please sign in to write a review', 'info');
    setTimeout(() => window.location.href = 'login.html', 1000);
    return;
  }
  document.getElementById('reviewModal').classList.add('show');
}

function closeReviewModal() {
  document.getElementById('reviewModal').classList.remove('show');
}

async function submitReview(e) {
  e.preventDefault();
  const rating = document.getElementById('reviewRatingSelect').value;
  const comment = document.getElementById('reviewCommentInput').value.trim();

  if (!comment) {
    showToast('Please write a review comment', 'warning');
    return;
  }

  try {
    await api.post(`/products/${currentProduct.id}/reviews`, {
      rating: Number(rating),
      comment: comment
    });
    showToast('Review published successfully!', 'success');
    closeReviewModal();
    loadProductDetails(null, currentProduct.id);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteReview(reviewId) {
  if (!confirm('Are you sure you want to delete your review?')) return;
  try {
    await api.delete(`/products/${currentProduct.id}/reviews/${reviewId}`);
    showToast('Review deleted', 'info');
    loadProductDetails(null, currentProduct.id);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function escapeHtml(str) {
  return (str || '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}