document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(localStorage.getItem('user'));

    // Page Protection: Redirect if not a logged-in customer
    if (!user || user.role !== 'customer') {
        logout(); // Clears any invalid session data and redirects
        return;
    }

    document.getElementById('user-name').textContent = user.fullName;

    // Load products when page loads
    loadProducts();
});

async function loadProducts() {
    const productListDiv = document.getElementById('product-list');
    
    // Show loading state
    productListDiv.innerHTML = `
        <div class="loading">
            <div class="loading-spinner"></div>
            <span>Loading our delicious cakes...</span>
        </div>
    `;

    try {
        const response = await fetch('/api/products');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const products = await response.json();
        productListDiv.innerHTML = ''; // Clear loading text

        if (products.length === 0) {
            productListDiv.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🎂</div>
                    <h3>No cakes available</h3>
                    <p>We're baking something special! Check back soon.</p>
                </div>
            `;
            return;
        }

        // Add staggered animation delay for cards
        products.forEach((product, index) => {
            const productCard = document.createElement('div');
            productCard.className = 'product-card';
            productCard.style.animationDelay = `${index * 0.1}s`;
            
            // Use placeholder image if no image URL provided
            const imageUrl = product.imageUrl || getRandomCakeImage();
            
            // Determine stock status
            const stockStatus = getStockStatus(product.stockQuantity);
            const stockIndicatorClass = stockStatus.level;
            
            productCard.innerHTML = `
                <div class="product-image-container">
                    <img src="${imageUrl}" alt="${product.name}" class="product-image" 
                         onerror="this.src='${getRandomCakeImage()}'">
                    ${product.stockQuantity > 0 ? '<div class="product-badge">Fresh</div>' : '<div class="product-badge" style="background: var(--error-red);">Sold Out</div>'}
                </div>
                <div class="product-info">
                    <div class="product-category">${product.categoryName || 'Artisan Cakes'}</div>
                    <h3 class="product-name">${product.name}</h3>
                    <p class="product-description">${product.description}</p>
                    <div class="product-details">
                        <div class="product-price">$${product.price.toFixed(2)}</div>
                        <div class="product-stock">
                            <span class="stock-indicator ${stockIndicatorClass}"></span>
                            <span>${stockStatus.text}</span>
                        </div>
                    </div>
                    <button class="add-to-cart-btn" onclick="addToCart(${product.id}, '${product.name}')" 
                            ${product.stockQuantity === 0 ? 'disabled' : ''}>
                        ${product.stockQuantity === 0 ? '😔 Out of Stock' : '🛒 Add to Cart'}
                    </button>
                </div>
            `;
            productListDiv.appendChild(productCard);
        });

    } catch (error) {
        productListDiv.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">😕</div>
                <h3>Oops! Something went wrong</h3>
                <p>We couldn't load our cakes right now. Please try again later.</p>
                <button class="btn btn-primary" onclick="loadProducts()" style="margin-top: 1rem; width: auto; padding: 0.75rem 1.5rem;">
                    🔄 Try Again
                </button>
            </div>
        `;
        console.error('Error fetching products:', error);
    }
}

function getStockStatus(quantity) {
    if (quantity === 0) {
        return { level: 'out', text: 'Out of Stock' };
    } else if (quantity <= 5) {
        return { level: 'low', text: `Only ${quantity} left` };
    } else {
        return { level: '', text: `${quantity} available` };
    }
}

function getRandomCakeImage() {
    const cakeImages = [
        'https://images.pexels.com/photos/1721932/pexels-photo-1721932.jpeg?auto=compress&cs=tinysrgb&w=400',
        'https://images.pexels.com/photos/1126359/pexels-photo-1126359.jpeg?auto=compress&cs=tinysrgb&w=400',
        'https://images.pexels.com/photos/1702373/pexels-photo-1702373.jpeg?auto=compress&cs=tinysrgb&w=400',
        'https://images.pexels.com/photos/1055272/pexels-photo-1055272.jpeg?auto=compress&cs=tinysrgb&w=400',
        'https://images.pexels.com/photos/291528/pexels-photo-291528.jpeg?auto=compress&cs=tinysrgb&w=400',
        'https://images.pexels.com/photos/1998635/pexels-photo-1998635.jpeg?auto=compress&cs=tinysrgb&w=400'
    ];
    return cakeImages[Math.floor(Math.random() * cakeImages.length)];
}

function addToCart(productId, productName) {
    // Create a more engaging feedback animation
    const button = event.target;
    const originalText = button.innerHTML;
    
    // Disable button and show loading state
    button.disabled = true;
    button.innerHTML = '✨ Adding...';
    button.style.transform = 'scale(0.95)';
    
    // Simulate API call delay
    setTimeout(() => {
        button.innerHTML = '✅ Added!';
        button.style.background = 'var(--success-green)';
        
        // Show success notification
        showNotification(`${productName} added to your cart! 🎉`, 'success');
        
        // Reset button after delay
        setTimeout(() => {
            button.disabled = false;
            button.innerHTML = originalText;
            button.style.background = '';
            button.style.transform = 'scale(1)';
        }, 2000);
    }, 800);
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? 'var(--success-green)' : 'var(--primary-pink)'};
        color: white;
        padding: 1rem 1.5rem;
        border-radius: var(--radius-md);
        box-shadow: var(--shadow-lg);
        z-index: 1000;
        animation: slideInRight 0.3s ease-out;
        max-width: 300px;
        font-weight: 600;
    `;
    notification.textContent = message;
    
    // Add animation keyframes if not already added
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(notification);
    
    // Remove notification after delay
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// This function needs to be available in the global scope for the button's onclick
function logout() {
    localStorage.removeItem('user');
    window.location.href = '/index.html';
}