// Shared Dynamic Navigation Bar & Footer
async function renderNavbar(activePage = '') {
  const headerContainer = document.getElementById('navbar-container');
  if (!headerContainer) return;

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
          <li><a href="index.html" class="nav-link ${activePage === 'home' ? 'active' : ''}">Home</a></li>
          <li><a href="products.html" class="nav-link ${activePage === 'products' ? 'active' : ''}">Explore</a></li>
          <li><a href="products.html?category=code" class="nav-link">Code</a></li>
          <li><a href="products.html?category=ui-ux" class="nav-link">Design</a></li>
          <li><a href="products.html?category=ebooks" class="nav-link">Guides</a></li>
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