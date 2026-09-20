// Shared Dynamic Navigation Bar & Footer
async function renderNavbar(activePage = '', activeCategory = '') {
  const headerContainer = document.getElementById('navbar-container');
  if (!headerContainer) return;

  // Auto-detect category from URL if on products page and not specified
  if (!activeCategory && typeof window !== 'undefined' && window.location.search) {
    const urlParams = new URLSearchParams(window.location.search);
    const cat = urlParams.get('category');
    if (cat) {
      const lower = cat.toLowerCase();
      if (lower === 'designs' || lower === 'design') activeCategory = 'ui-ux';
      else if (lower === 'guides' || lower === 'guide') activeCategory = 'ebooks';
      else activeCategory = lower;
    }
  }

  const user = getCurrentUser();
  let cartCount = 0;
  let wishlistCount = 0;

  if (user) {
    try {
      const cart = await api.get('/cart');
      cartCount = cart ? cart.length : 0;
      const wishlist = await api.get('/wishlist');
      wishlistCount = wishlist ? wishlist.length : 0;
    } catch (e) {}
  }

  let roleDashboardLink = '';
  if (user && user.role === 'ROLE_ADMIN') {
    roleDashboardLink = `
      <a href="admin-dashboard.html" class="dropdown-item">
        <i class="fa-solid fa-shield-halved"></i> Admin Control Room
      </a>
    `;
  } else if (user && (user.role === 'ROLE_SELLER' || user.role === 'ROLE_ADMIN')) {
    roleDashboardLink = `
      <a href="seller-dashboard.html" class="dropdown-item">
        <i class="fa-solid fa-store"></i> Seller Studio
      </a>
    `;
  }

  let userSectionHtml = '';
  if (user) {
    const roleTagClass = user.role === 'ROLE_ADMIN' ? 'admin' : (user.role === 'ROLE_SELLER' ? 'seller' : 'customer');
    const roleLabel = user.role.replace('ROLE_', '');
    const initial = user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U';

    userSectionHtml = `
      <div class="user-menu">
        <button class="user-avatar-btn" id="userMenuBtn" onclick="toggleUserDropdown(event)">
          <div class="avatar-img">${initial}</div>
          <span style="font-size: 0.85rem; font-weight: 600; max-width: 110px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${user.fullName.split(' ')[0]}</span>
          <i class="fa-solid fa-chevron-down" style="font-size: 0.7rem; color: var(--text-muted);"></i>
        </button>
        <div class="user-dropdown" id="userDropdown">
          <div class="dropdown-header">
            <div class="name">${user.fullName}</div>
            <span class="role-tag ${roleTagClass}">${roleLabel}</span>
          </div>
          ${roleDashboardLink}
          <a href="downloads.html" class="dropdown-item">
            <i class="fa-solid fa-cloud-arrow-down"></i> My Digital Library
          </a>
          <a href="orders.html" class="dropdown-item">
            <i class="fa-solid fa-receipt"></i> Purchase History
          </a>
          <a href="wishlist.html" class="dropdown-item">
            <i class="fa-solid fa-heart"></i> Saved Items
          </a>
          <a href="profile.html" class="dropdown-item">
            <i class="fa-solid fa-gear"></i> Account Settings
          </a>
          <div class="dropdown-divider"></div>
          <a href="javascript:void(0)" onclick="logout()" class="dropdown-item" style="color: var(--accent);">
            <i class="fa-solid fa-arrow-right-from-bracket"></i> Sign Out
          </a>
        </div>
      </div>
    `;
  } else {
    userSectionHtml = `
      <a href="login.html" class="btn btn-secondary" style="padding: 0.45rem 1rem;">Sign In</a>
      <a href="register.html" class="btn btn-primary" style="padding: 0.45rem 1rem;">Get Started</a>
    `;
  }

  const isHome = activePage === 'home';
  const isExplore = (activePage === 'products' || !activePage) && !activeCategory;
  const isCode = activeCategory === 'code';
  const isDesigns = activeCategory === 'ui-ux' || activeCategory === 'designs';
  const isGuides = activeCategory === 'ebooks' || activeCategory === 'guides';

  headerContainer.innerHTML = `
    <nav class="navbar">
      <div class="container nav-inner">
        <a href="index.html" class="nav-brand">
          <div class="brand-icon"><i class="fa-solid fa-cube"></i></div>
          <span>Digital<span class="brand-gradient">Hub</span></span>
        </a>

        <div class="nav-search">
          <i class="fa-solid fa-magnifying-glass"></i>
          <input type="text" id="globalNavSearch" placeholder="Search templates, code, e-books..." onkeydown="handleGlobalSearch(event)" />
        </div>

        <ul class="nav-links">
          <li><a href="index.html" class="nav-link ${isHome ? 'active' : ''}">Home</a></li>
          <li><a href="products.html" class="nav-link ${isExplore ? 'active' : ''}">Explore</a></li>
          <li><a href="products.html?category=code" class="nav-link ${isCode ? 'active' : ''}">Code</a></li>
          <li><a href="products.html?category=ui-ux" class="nav-link ${isDesigns ? 'active' : ''}">Designs</a></li>
          <li><a href="products.html?category=ebooks" class="nav-link ${isGuides ? 'active' : ''}">Guides</a></li>
        </ul>

        <div class="nav-actions">
          <a href="wishlist.html" class="icon-btn" title="Wishlist">
            <i class="fa-regular fa-heart"></i>
            ${wishlistCount > 0 ? `<span class="badge">${wishlistCount}</span>` : ''}
          </a>
          <a href="cart.html" class="icon-btn" title="Shopping Cart">
            <i class="fa-solid fa-cart-shopping"></i>
            ${cartCount > 0 ? `<span class="badge" id="navCartBadge">${cartCount}</span>` : ''}
          </a>
          ${userSectionHtml}
        </div>
      </div>
    </nav>
  `;

  // Close dropdown on click outside
  document.addEventListener('click', (e) => {
    const dropdown = document.getElementById('userDropdown');
    const btn = document.getElementById('userMenuBtn');
    if (dropdown && btn && !btn.contains(e.target) && !dropdown.contains(e.target)) {
      dropdown.classList.remove('show');
    }
  });
}

function toggleUserDropdown(e) {
  e.stopPropagation();
  const dropdown = document.getElementById('userDropdown');
  if (dropdown) dropdown.classList.toggle('show');
}

function handleGlobalSearch(e) {
  if (e.key === 'Enter') {
    const query = e.target.value.trim();
    if (query) {
      window.location.href = `products.html?search=${encodeURIComponent(query)}`;
    }
  }
}

function renderFooter() {
  const footerContainer = document.getElementById('footer-container');
  if (!footerContainer) return;

  footerContainer.innerHTML = `
    <footer class="footer">
      <div class="container footer-grid">
        <div class="footer-brand">
          <div class="nav-brand" style="margin-bottom: 0.5rem;">
            <div class="brand-icon"><i class="fa-solid fa-cube"></i></div>
            <span>Digital<span class="brand-gradient">Hub</span></span>
          </div>
          <p>The premier global marketplace for verified digital assets, developer boilerplates, UI kits, design systems, and educational guides.</p>
          <div style="margin-top: 1.25rem; display: flex; gap: 1rem; color: var(--text-muted); font-size: 1.1rem;">
            <a href="#"><i class="fa-brands fa-github"></i></a>
            <a href="#"><i class="fa-brands fa-x-twitter"></i></a>
            <a href="#"><i class="fa-brands fa-discord"></i></a>
            <a href="#"><i class="fa-brands fa-linkedin"></i></a>
          </div>
        </div>

        <div class="footer-col">
          <h4>Marketplace</h4>
          <ul>
            <li><a href="products.html">All Products</a></li>
            <li><a href="products.html?category=code">Developer Code</a></li>
            <li><a href="products.html?category=ui-ux">UI/UX Kits</a></li>
            <li><a href="products.html?category=ebooks">E-Books & Manuals</a></li>
            <li><a href="products.html?category=templates">Web Templates</a></li>
          </ul>
        </div>

        <div class="footer-col">
          <h4>For Creators</h4>
          <ul>
            <li><a href="seller-dashboard.html">Seller Dashboard</a></li>
            <li><a href="register.html">Become a Creator</a></li>
            <li><a href="#">Creator Guidelines</a></li>
            <li><a href="#">Earnings & Payouts</a></li>
            <li><a href="#">License Agreement</a></li>
          </ul>
        </div>

        <div class="footer-col">
          <h4>Support & Legal</h4>
          <ul>
            <li><a href="#">Help Center</a></li>
            <li><a href="#">Terms of Service</a></li>
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Security</a></li>
            <li><a href="#">Contact Us</a></li>
          </ul>
        </div>
      </div>

      <div class="container footer-bottom">
        <div>© 2026 DigitalHub Marketplace Inc. All rights reserved. Academic FSD Project.</div>
        <div style="display: flex; gap: 1.5rem;">
          <a href="#">Terms</a>
          <a href="#">Privacy</a>
          <a href="#">Cookies</a>
        </div>
      </div>
    </footer>
  `;
}

// Shared Product Card Renderer & Cart/Wishlist Actions
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