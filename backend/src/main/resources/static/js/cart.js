// Shopping Cart Controller
let cartItems = [];
let appliedCoupon = null;

document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar('cart');
  renderFooter();

  loadCart();
});

async function loadCart() {
  const container = document.getElementById('cartItemsList');
  if (!container) return;

  try {
    cartItems = await api.get('/cart');
    renderCart();
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent);">Failed to load cart: ${err.message}</p>`;
  }
}

function renderCart() {
  const container = document.getElementById('cartItemsList');
  const summaryContainer = document.getElementById('cartSummary');

  if (!cartItems || cartItems.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 4rem 2rem; background: var(--card-bg); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
        <i class="fa-solid fa-cart-arrow-down" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
        <h3 style="color: #fff; margin-bottom: 0.5rem;">Your shopping cart is empty</h3>
        <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Browse our marketplace to find premium digital products and templates.</p>
        <a href="products.html" class="btn btn-primary">Discover Digital Products</a>
      </div>
    `;
    if (summaryContainer) summaryContainer.style.display = 'none';
    return;
  }

  if (summaryContainer) summaryContainer.style.display = 'block';

  container.innerHTML = cartItems.map(item => `
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 1.25rem; background: var(--card-bg); border: 1px solid var(--border-color); border-radius: var(--radius-md); margin-bottom: 1rem;">
      <div style="display: flex; align-items: center; gap: 1.25rem;">
        <img src="${item.previewImageUrl || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=150'}" alt="${item.productTitle}" style="width: 80px; height: 60px; object-fit: cover; border-radius: var(--radius-sm);" />
        <div>
          <h4 style="color: #fff; font-size: 1.05rem; margin-bottom: 0.25rem;">
            <a href="product-details.html?slug=${item.productSlug}">${item.productTitle}</a>
          </h4>
          <span style="font-size: 0.8rem; color: var(--text-muted);">${item.categoryName || 'Digital Good'} • Format: ${item.fileFormat || 'Instant Download'}</span>
        </div>
      </div>

      <div style="display: flex; align-items: center; gap: 2rem;">
        <div style="font-size: 1.35rem; font-weight: 800; color: #fff;">
          $${item.effectivePrice.toFixed(2)}
        </div>
        <button class="icon-btn" onclick="removeFromCart(${item.productId})" style="color: var(--accent);" title="Remove Item">
          <i class="fa-solid fa-trash"></i>
        </button>
      </div>
    </div>
  `).join('');

  updateCartTotals();
}

async function removeFromCart(productId) {
  try {
    await api.delete(`/cart/remove/${productId}`);
    showToast('Product removed from cart', 'info');
    cartItems = cartItems.filter(i => i.productId !== productId);
    renderCart();
    renderNavbar();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function updateCartTotals() {
  const subtotal = cartItems.reduce((sum, item) => sum + item.effectivePrice, 0);
  let discount = 0;

  if (appliedCoupon) {
    discount = appliedCoupon.discountAmount;
  }

  const finalAmount = Math.max(0, subtotal - discount);

  document.getElementById('cartSubtotal').innerText = `$${subtotal.toFixed(2)}`;
  document.getElementById('cartDiscount').innerText = `-$${discount.toFixed(2)}`;
  document.getElementById('cartTotal').innerText = `$${finalAmount.toFixed(2)}`;
}

async function applyCoupon() {
  const code = document.getElementById('couponInput').value.trim();
  if (!code) {
    showToast('Please enter a coupon code', 'warning');
    return;
  }

  const subtotal = cartItems.reduce((sum, item) => sum + item.effectivePrice, 0);

  try {
    const res = await api.get(`/coupons/validate?code=${encodeURIComponent(code)}&amount=${subtotal}`);
    if (res.valid) {
      appliedCoupon = res;
      sessionStorage.setItem('digitalhub_applied_coupon', JSON.stringify(res));
      showToast(res.message, 'success');
      updateCartTotals();
    } else {
      appliedCoupon = null;
      sessionStorage.removeItem('digitalhub_applied_coupon');
      showToast(res.message, 'error');
      updateCartTotals();
    }
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function proceedToCheckout() {
  if (cartItems.length === 0) {
    showToast('Your cart is empty', 'warning');
    return;
  }
  window.location.href = 'checkout.html';
}