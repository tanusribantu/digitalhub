// Product Catalog & Filter Controller
let currentPage = 0;
let totalPages = 1;
const pageSize = 9;

document.addEventListener('DOMContentLoaded', async () => {
  await renderNavbar('products');
  renderFooter();

  // Read URL params
  const urlParams = new URLSearchParams(window.location.search);
  const searchParam = urlParams.get('search');
  const catParam = urlParams.get('category');

  if (searchParam) {
    document.getElementById('catalogSearchInput').value = searchParam;
  }

  await loadCategoriesFilter(catParam);
  fetchProducts();

  // Filter event listeners
  document.getElementById('catalogSearchInput').addEventListener('input', debounce(fetchProducts, 400));
  document.getElementById('categorySelect').addEventListener('change', () => { currentPage = 0; fetchProducts(); });
  document.getElementById('sortSelect').addEventListener('change', () => { currentPage = 0; fetchProducts(); });
  document.getElementById('ratingSelect').addEventListener('change', () => { currentPage = 0; fetchProducts(); });
  document.getElementById('priceRange').addEventListener('input', (e) => {
    document.getElementById('priceRangeVal').innerText = `$${e.target.value}`;
    debounce(fetchProducts, 300)();
  });
});

async function loadCategoriesFilter(selectedSlug) {
  const select = document.getElementById('categorySelect');
  if (!select) return;

  try {
    const categories = await api.get('/categories');
    categories.forEach(cat => {
      const opt = document.createElement('option');
      opt.value = cat.slug;
      opt.textContent = cat.name;
      if (cat.slug === selectedSlug) opt.selected = true;
      select.appendChild(opt);
    });
  } catch (e) {}
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
  document.getElementById('catalogSearchInput').value = '';
  document.getElementById('categorySelect').value = '';
  document.getElementById('priceRange').value = 150;
  document.getElementById('priceRangeVal').innerText = '$150';
  document.getElementById('ratingSelect').value = '';
  document.getElementById('sortSelect').value = 'createdAt:desc';
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