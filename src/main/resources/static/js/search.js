document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchInput");
    const productContainer = document.getElementById("productContainer");
    const searchMessage = document.getElementById("searchMessage");

    if (!searchInput || !productContainer) {
        console.error("ShopEase search elements were not found.");
        return;
    }

    let debounceTimer;
    let currentController;

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, character => {
            const entities = {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                '"': "&quot;",
                "'": "&#39;"
            };

            return entities[character];
        });
    }

    function getCsrfField() {
        const token = document.querySelector(
            'meta[name="_csrf"]'
        )?.content;

        if (!token) {
            console.error(
                "CSRF token missing. Check the CSRF meta tags in index.html."
            );
            return "";
        }

        return `<input type="hidden" name="_csrf" value="${escapeHtml(token)}">`;
    }

    function renderProducts(products) {
        if (!Array.isArray(products) || products.length === 0) {
            productContainer.innerHTML = `
                <div class="empty-state">
                    <h3>No products found</h3>
                    <p>Try searching with another keyword.</p>
                </div>
            `;
            return;
        }

        const csrfField = getCsrfField();

        productContainer.innerHTML = products.map(product => {
            const id = Number(product.id);
            const price = Number(product.price);
            const stock = Number(product.stock);

            if (
                !Number.isSafeInteger(id) ||
                id <= 0 ||
                !Number.isFinite(price) ||
                price < 0 ||
                !Number.isFinite(stock) ||
                stock < 0
            ) {
                return "";
            }

            const name = escapeHtml(product.name);
            const description = escapeHtml(product.description);
            const imageUrl = escapeHtml(product.imageUrl || "");

            const image = imageUrl
                ? `<img
                       src="${imageUrl}"
                       alt="${name}"
                       class="product-image"
                       onerror="this.style.display='none'">`
                : `<div class="product-image-placeholder">No image</div>`;

            let cartAction;

            if (stock > 0 && csrfField) {
                cartAction = `
                    <form action="/cart/add/${id}" method="post">
                        ${csrfField}
                        <button type="submit" class="add-to-cart-btn">
                            Add to Cart
                        </button>
                    </form>
                `;
            } else if (stock <= 0) {
                cartAction = `
                    <button class="add-to-cart-btn" disabled>
                        Out of Stock
                    </button>
                `;
            } else {
                cartAction = `
                    <p>Please refresh the page to enable adding to cart.</p>
                `;
            }

            return `
                <article class="product-card">
                    ${image}

                    <div class="product-info">
                        <h3>${name}</h3>
                        <p class="product-description">${description}</p>
                        <p class="product-price">₹${price.toFixed(2)}</p>
                        <p class="product-stock">
                            ${stock > 0 ? `${stock} available` : "Out of stock"}
                        </p>
                        ${cartAction}
                    </div>
                </article>
            `;
        }).join("");
    }

    async function searchProducts(keyword = "") {
        if (currentController) {
            currentController.abort();
        }

        currentController = new AbortController();

        if (searchMessage) {
            searchMessage.textContent = "Searching...";
        }

        try {
            const response = await fetch(
                `/api/products/search?keyword=${encodeURIComponent(keyword)}`,
                {
                    method: "GET",
                    headers: {
                        "Accept": "application/json"
                    },
                    signal: currentController.signal
                }
            );

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const products = await response.json();

            renderProducts(products);

            if (searchMessage) {
                searchMessage.textContent =
                    `${products.length} product(s) found`;
            }
        } catch (error) {
            if (error.name === "AbortError") {
                return;
            }

            console.error("ShopEase search error:", error);

            if (searchMessage) {
                searchMessage.textContent =
                    "Unable to load products. Please try again.";
            }

            productContainer.innerHTML = `
                <div class="empty-state">
                    <h3>Unable to load products</h3>
                    <p>Refresh the page or try again later.</p>
                </div>
            `;
        }
    }

    searchInput.addEventListener("input", () => {
        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(() => {
            searchProducts(searchInput.value.trim());
        }, 300);
    });

    searchProducts();
});
