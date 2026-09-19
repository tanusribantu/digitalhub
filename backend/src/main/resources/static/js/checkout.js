// Checkout & Payment Simulation Controller
let singleProductId = null;
let checkoutItems = [];
let appliedCouponCode = null;

document.addEventListener('DOMContentLoaded', async () => {
  if (!requireAuth()) return;
  await renderNavbar();
  renderFooter();

  const urlParams = new URLSearchParams(window.location.search);
  singleProductId = urlParams.get('single');

  // Check stored coupon
  const storedCoupon = sessionStorage.getItem('digitalhub_applied_coupon');
  if (storedCoupon) {
    try {
      const c = JSON.parse(storedCoupon);
      appliedCouponCode = c.code;
      document.getElementById('checkoutCouponInput').value = c.code;
    } catch(e) {}
  }

  loadCheckoutSummary();
});

async function loadCheckoutSummary() {
  const user = getCurrentUser();
  if (user) {
    document.getElementById('billingName').value = user.fullName || '';
    document.getElementById('billingEmail').value = user.email || '';
  }

  try {
    if (singleProductId) {
      const p = await api.get(`/products/${singleProductId}`);
      checkoutItems = [{
        productId: p.id,
        productTitle: p.title,
        previewImageUrl: p.previewImageUrl,
        effectivePrice: p.effectivePrice,
        fileFormat: p.fileFormat
      }];
    } else {
      const cart = await api.get('/cart');
      if (!cart || cart.length === 0) {
        showToast('Your cart is empty', 'warning');
        setTimeout(() => window.location.href = 'products.html', 1500);
        return;
      }
      checkoutItems = cart;
    }

    renderSummaryList();
  } catch (err) {
    showToast('Failed to load items: ' + err.message, 'error');
  }
}

function renderSummaryList() {
  const container = document.getElementById('summaryItemsList');
  if (!container) return;

  container.innerHTML = checkoutItems.map(item => `
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.85rem; font-size: 0.9rem;">
      <div style="color: #fff; max-width: 260px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
        ${item.productTitle}
      </div>
      <div style="font-weight: 700; color: #fff;">
        $${item.effectivePrice.toFixed(2)}
      </div>
    </div>
  `).join('');

  calculateTotals();
}

async function calculateTotals() {
  const subtotal = checkoutItems.reduce((sum, i) => sum + i.effectivePrice, 0);
  let discount = 0;

  if (appliedCouponCode) {
    try {
      const res = await api.get(`/coupons/validate?code=${encodeURIComponent(appliedCouponCode)}&amount=${subtotal}`);
      if (res.valid) {
        discount = res.discountAmount;
      } else {
        appliedCouponCode = null;
      }
    } catch (e) {
      appliedCouponCode = null;
    }
  }

  const finalAmount = Math.max(0, subtotal - discount);

  document.getElementById('summarySubtotal').innerText = `$${subtotal.toFixed(2)}`;
  document.getElementById('summaryDiscount').innerText = `-$${discount.toFixed(2)}`;
  document.getElementById('summaryTotal').innerText = `$${finalAmount.toFixed(2)}`;
  document.getElementById('payBtnAmount').innerText = `$${finalAmount.toFixed(2)}`;
}

async function applyCheckoutCoupon() {
  const code = document.getElementById('checkoutCouponInput').value.trim();
  if (!code) {
    appliedCouponCode = null;
    calculateTotals();
    return;
  }
  appliedCouponCode = code;
  await calculateTotals();
  showToast('Coupon verified', 'info');
}

async function processOrder(e) {
  e.preventDefault();

  const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
  const payBtn = document.getElementById('submitOrderBtn');
  payBtn.disabled = true;
  payBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Authorizing Simulated Payment...';

  try {
    // Artificial 1 second delay for realistic payment gateway simulation
    await new Promise(resolve => setTimeout(resolve, 1000));

    const payload = {
      paymentMethod: paymentMethod,
      couponCode: appliedCouponCode,
      singleProductId: singleProductId ? Number(singleProductId) : null
    };

    const order = await api.post('/orders/checkout', payload);
    sessionStorage.removeItem('digitalhub_applied_coupon');

    // Show instant success modal with direct downloads button
    document.getElementById('orderConfirmationModal').classList.add('show');
    document.getElementById('confirmedOrderNumber').innerText = order.orderNumber;
    document.getElementById('confirmedTotal').innerText = `$${order.finalAmount.toFixed(2)}`;
    renderNavbar();
  } catch (err) {
    showToast(err.message, 'error');
    payBtn.disabled = false;
    payBtn.innerHTML = '<i class="fa-solid fa-lock"></i> Complete Order & Download';
  }
}