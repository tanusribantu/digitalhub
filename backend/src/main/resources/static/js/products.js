// Product Catalog & Filter Controller
let currentPage = 0;
let totalPages = 1;
const pageSize = 9;
let allCategories = [];

const categoryAliases = {
  'designs': 'ui-ux',
  'design': 'ui-ux',
  'guides': 'ebooks',
  'guide': 'ebooks',
  'books': 'ebooks',
  'coding': 'code'
};

function resolveCategorySlug(raw) {
  if (!raw) return '';
  const lower = raw.trim().toLowerCase();
  return categoryAliases[lower] || lower;
}

document.addEventListener('DOMContentLoaded', async () => {
  // Read URL params
  const urlParams = new URLSearchParams(window.location.search);
  const searchParam = urlParams.get('search');
  const catParam = resolveCategorySlug(urlParams.get('category'));

  if (searchParam) {
    const searchInput = document.getElementById('catalogSearchInput');
    if (searchInput) searchInput.value = searchParam;
  }

  await renderNavbar('products', catParam);
  renderFooter();

  await loadCategoriesFilter(catParam);
  updateCategoryHeader(catParam);
  fetchProducts();

  // Filter event listeners
  const searchInput = document.getElementById('catalogSearchInput');
  if (searchInput) searchInput.addEventListener('input', debounce(() => { currentPage = 0; fetchProducts(); }, 400));

  const catSelect = document.getElementById('categorySelect');
  if (catSelect) {
    catSelect.addEventListener('change', (e) => {
      currentPage = 0;
      const selected = e.target.value;
      updateCategoryUrl(selected);
      updateCategoryHeader(selected);
      fetchProducts();
    });
  }

  const sortSelect = document.getElementById('sortSelect');
  if (sortSelect) sortSelect.addEventListener('change', () => { currentPage = 0; fetchProducts(); });

  const ratingSelect = document.getElementById('ratingSelect');
  if (ratingSelect) ratingSelect.addEventListener('change', () => { currentPage = 0; fetchProducts(); });

  const priceRange = document.getElementById('priceRange');
  if (priceRange) {
    priceRange.addEventListener('input', (e) => {
      const valElem = document.getElementById('priceRangeVal');
      if (valElem) valElem.innerText = `$${e.target.value}`;
      debounce(() => { currentPage = 0; fetchProducts(); }, 300)();
    });
  }

  // Handle browser back/forward buttons
  window.addEventListener('popstate', () => {
    const params = new URLSearchParams(window.location.search);
    const popCat = resolveCategorySlug(params.get('category'));
    const popSearch = params.get('search') || '';

    if (searchInput) searchInput.value = popSearch;
    if (catSelect) catSelect.value = popCat;

    updateCategoryHeader(popCat);
    currentPage = 0;
    fetchProducts();
  });
});

async function loadCategoriesFilter(selectedSlug) {
  const select = document.getElementById('categorySelect');
  if (!select) return;

  try {
    allCategories = await api.get('/categories') || [];
    select.innerHTML = '<option value="">All Categories</option>';
    allCategories.forEach(cat => {
      const opt = document.createElement('option');
      opt.value = cat.slug;
      opt.textContent = cat.name;
      if (cat.slug === selectedSlug) opt.selected = true;
      select.appendChild(opt);
    });

    if (selectedSlug) {
      select.value = selectedSlug;
    }
  } catch (e) {
    console.error('Failed to load categories filter:', e);
  }
}

function updateCategoryHeader(slug) {
  const titleEl = document.getElementById('catalogPageTitle');
  const subtitleEl = document.getElementById('catalogPageSubtitle');
  const badgeEl = document.getElementById('catalogCategoryBadge');
  if (!titleEl) return;

  const currentCat = allCategories.find(c => c.slug === slug);

  if (currentCat) {
    titleEl.innerHTML = `<i class="${currentCat.icon || 'fa-solid fa-folder'}" style="color: var(--primary); margin-right: 0.6rem;"></i>${currentCat.name}`;
    if (subtitleEl) subtitleEl.innerText = currentCat.description || 'Curated high-quality digital assets and tools.';
    document.title = `${currentCat.name} – DigitalHub`;

    if (badgeEl) {
      badgeEl.style.display = 'block';
      badgeEl.innerHTML = `
        <span class="badge-pill badge-format" style="font-size: 0.8rem; padding: 0.35rem 0.85rem; display: inline-flex; align-items: center; gap: 0.5rem; background: rgba(99, 102, 241, 0.15); border: 1px solid rgba(99, 102, 241, 0.3); color: #a5b4fc;">
          <i class="fa-solid fa-filter"></i> Filtered by: <strong>${currentCat.name}</strong>
          <button onclick="clearCategoryFilter()" style="background: none; border: none; color: #f43f5e; cursor: pointer; margin-left: 0.35rem; font-size: 0.85rem;" title="Clear filter"><i class="fa-solid fa-xmark"></i></button>
        </span>
      `;
    }
  } else {
    titleEl.innerText = 'Explore Marketplace';
    if (subtitleEl) subtitleEl.innerText = 'Discover verified developer boilerplates, UI kits, templates, e-books, and digital design tools.';
    document.title = 'Explore Marketplace – DigitalHub';
    if (badgeEl) badgeEl.style.display = 'none';
  }

  renderNavbar('products', slug);
}

function updateCategoryUrl(slug) {
  const url = new URL(window.location.href);
  if (slug) {
    url.searchParams.set('category', slug);
  } else {
    url.searchParams.delete('category');
  }
  window.history.pushState({ category: slug }, '', url.toString());
}

function clearCategoryFilter() {
  const catSelect = document.getElementById('categorySelect');
  if (catSelect) catSelect.value = '';
  updateCategoryUrl('');
  updateCategoryHeader('');
  currentPage = 0;
  fetchProducts();
}

async function fetchProducts() {
  const container = document.getElementById('catalogProductsGrid');
  const paginationContainer = document.getElementById('pagination');
  if (!container) return;

  container.innerHTML = `
    <div style="grid-column: 1/-1; text-align: center; padding: 4rem 0;">
      <i class="fa-solid fa-spinner fa-spin" style="font-size: 2rem; color: var(--primary);"></i>
      <p style="color: var(--text-muted); margin-top: 1rem;">Loading digital assets...</p>
    </div>
  `;

  const search = document.getElementById('catalogSearchInput').value.trim();
  const category = document.getElementById('categorySelect').value;
  const maxPrice = document.getElementById('priceRange').value;
  const minRating = document.getElementById('ratingSelect').value;
  const sortVal = document.getElementById('sortSelect').value;

  let [sortBy, direction] = sortVal.split(':');
  if (!sortBy) { sortBy = 'createdAt'; direction = 'desc'; }

  let query = `?page=${currentPage}&size=${pageSize}&sortBy=${sortBy}&direction=${direction}`;
  if (search) query += `&search=${encodeURIComponent(search)}`;
  if (category) query += `&category=${encodeURIComponent(category)}`;
  if (maxPrice && Number(maxPrice) < 150) query += `&maxPrice=${maxPrice}`;
  if (minRating) query += `&minRating=${minRating}`;

  try {
    const pageData = await api.get(`/products${query}`);
    const products = pageData.content;
    totalPages = pageData.totalPages;

    document.getElementById('resultCount').innerText = `${pageData.totalElements} products found`;

    if (!products || products.length === 0) {
      container.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 4rem 0; background: var(--card-bg); border-radius: var(--radius-lg); border: 1px dashed var(--border-color);">
          <i class="fa-solid fa-box-open" style="font-size: 2.5rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
          <h3 style="color: #fff; margin-bottom: 0.5rem;">No products match your criteria</h3>
          <p style="color: var(--text-muted);">Try adjusting your search terms, price filters, or category.</p>
          <button class="btn btn-secondary" onclick="resetFilters()" style="margin-top: 1.25rem;">Reset All Filters</button>
        </div>
      `;
      paginationContainer.innerHTML = '';
      return;
    }

    container.innerHTML = products.map(renderProductCard).join('');
    renderPagination();
  } catch (err) {
    container.innerHTML = `<p style="color: var(--accent); grid-column: 1/-1;">Error loading products: ${err.message}</p>`;
  }
}

function renderPagination() {
  const container = document.getElementById('pagination');
  if (!container || totalPages <= 1) {
    if (container) container.innerHTML = '';
    return;
  }

  let html = `
    <button class="btn btn-secondary" style="padding: 0.4rem 0.8rem;" ${currentPage === 0 ? 'disabled' : ''} onclick="goToPage(${currentPage - 1})">
      <i class="fa-solid fa-chevron-left"></i> Previous
    </button>
  `;

  for (let i = 0; i < totalPages; i++) {
    html += `
      <button class="btn ${i === currentPage ? 'btn-primary' : 'btn-secondary'}" style="padding: 0.4rem 0.8rem; min-width: 38px;" onclick="goToPage(${i})">
        ${i + 1}
      </button>
    `;
  }

  html += `
    <button class="btn btn-secondary" style="padding: 0.4rem 0.8rem;" ${currentPage >= totalPages - 1 ? 'disabled' : ''} onclick="goToPage(${currentPage + 1})">
      Next <i class="fa-solid fa-chevron-right"></i>
    </button>
  `;

  container.innerHTML = html;
}

function goToPage(page) {
  currentPage = page;
  fetchProducts();
  window.scrollTo({ top: 120, behavior: 'smooth' });
}

function resetFilters() {
  const searchInput = document.getElementById('catalogSearchInput');
  const catSelect = document.getElementById('categorySelect');
  const priceRange = document.getElementById('priceRange');
  const priceRangeVal = document.getElementById('priceRangeVal');
  const ratingSelect = document.getElementById('ratingSelect');
  const sortSelect = document.getElementById('sortSelect');

  if (searchInput) searchInput.value = '';
  if (catSelect) catSelect.value = '';
  if (priceRange) priceRange.value = 150;
  if (priceRangeVal) priceRangeVal.innerText = '$150';
  if (ratingSelect) ratingSelect.value = '';
  if (sortSelect) sortSelect.value = 'createdAt:desc';

  updateCategoryUrl('');
  updateCategoryHeader('');
  currentPage = 0;
  fetchProducts();
}

function debounce(func, delay) {
  let timeout;
  return function(...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), delay);
  };
}