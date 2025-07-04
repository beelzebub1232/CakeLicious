document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(localStorage.getItem('user'));

    // Page Protection: Redirect if not a logged-in admin
    if (!user || user.role !== 'admin') {
        logout();
        return;
    }

    // Load admin dashboard data
    loadAdminDashboard();
});

async function loadAdminDashboard() {
    try {
        // Load dashboard statistics
        await loadDashboardStats();
        
        // Load recent orders
        await loadRecentOrders();
        
        // Load inventory alerts
        await loadInventoryStatus();
        
        // Load sales analytics
        await loadSalesAnalytics();
        
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showNotification('Error loading dashboard data', 'error');
    }
}

async function loadDashboardStats() {
    try {
        // Load orders statistics
        const ordersResponse = await fetch('/api/orders');
        const orders = await ordersResponse.json();
        
        // Load products statistics
        const productsResponse = await fetch('/api/products');
        const products = await productsResponse.json();
        
        // Calculate statistics
        const totalOrders = orders.length;
        const pendingOrders = orders.filter(order => order.status === 'pending').length;
        const completedOrders = orders.filter(order => order.status === 'delivered').length;
        const totalProducts = products.length;
        const lowStockProducts = products.filter(product => product.stockQuantity <= 5).length;
        
        // Calculate total revenue
        const totalRevenue = orders
            .filter(order => order.status === 'delivered')
            .reduce((sum, order) => sum + order.finalAmount, 0);
        
        // Update dashboard UI
        updateDashboardStats({
            totalOrders,
            pendingOrders,
            completedOrders,
            totalProducts,
            lowStockProducts,
            totalRevenue
        });
        
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
        showNotification('Error loading statistics', 'error');
    }
}

async function loadRecentOrders() {
    try {
        const response = await fetch('/api/orders');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const orders = await response.json();
        
        // Get the 10 most recent orders
        const recentOrders = orders.slice(0, 10);
        
        displayRecentOrders(recentOrders);
        
    } catch (error) {
        console.error('Error loading recent orders:', error);
        showNotification('Error loading recent orders', 'error');
    }
}

async function loadInventoryStatus() {
    try {
        const response = await fetch('/api/products');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const products = await response.json();
        
        // Filter products with low stock
        const lowStockProducts = products.filter(product => product.stockQuantity <= 5);
        const outOfStockProducts = products.filter(product => product.stockQuantity === 0);
        
        displayInventoryStatus(lowStockProducts, outOfStockProducts);
        
    } catch (error) {
        console.error('Error loading inventory status:', error);
        showNotification('Error loading inventory status', 'error');
    }
}

async function loadSalesAnalytics() {
    try {
        const response = await fetch('/api/orders');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const orders = await response.json();
        
        // Calculate sales analytics
        const salesData = calculateSalesAnalytics(orders);
        
        displaySalesAnalytics(salesData);
        
    } catch (error) {
        console.error('Error loading sales analytics:', error);
        showNotification('Error loading sales analytics', 'error');
    }
}

function calculateSalesAnalytics(orders) {
    const today = new Date();
    const thisMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    
    const thisMonthOrders = orders.filter(order => {
        const orderDate = new Date(order.orderDate);
        return orderDate >= thisMonth;
    });
    
    const lastMonthOrders = orders.filter(order => {
        const orderDate = new Date(order.orderDate);
        return orderDate >= lastMonth && orderDate < thisMonth;
    });
    
    const thisMonthRevenue = thisMonthOrders
        .filter(order => order.status === 'delivered')
        .reduce((sum, order) => sum + order.finalAmount, 0);
    
    const lastMonthRevenue = lastMonthOrders
        .filter(order => order.status === 'delivered')
        .reduce((sum, order) => sum + order.finalAmount, 0);
    
    const revenueGrowth = lastMonthRevenue > 0 
        ? ((thisMonthRevenue - lastMonthRevenue) / lastMonthRevenue) * 100 
        : 0;
    
    return {
        thisMonthOrders: thisMonthOrders.length,
        lastMonthOrders: lastMonthOrders.length,
        thisMonthRevenue,
        lastMonthRevenue,
        revenueGrowth
    };
}

function updateDashboardStats(stats) {
    console.log('Dashboard Stats:', stats);
    
    // Update UI elements with statistics
    // This would update specific elements in the admin dashboard
    // Implementation depends on the specific UI structure
}

function displayRecentOrders(orders) {
    console.log('Recent Orders:', orders);
    
    // Display recent orders in the admin dashboard
    // Implementation depends on the specific UI structure
}

function displayInventoryStatus(lowStockProducts, outOfStockProducts) {
    console.log('Low Stock Products:', lowStockProducts);
    console.log('Out of Stock Products:', outOfStockProducts);
    
    if (outOfStockProducts.length > 0) {
        showNotification(`${outOfStockProducts.length} products are out of stock!`, 'error');
    } else if (lowStockProducts.length > 0) {
        showNotification(`${lowStockProducts.length} products are running low on stock`, 'warning');
    }
}

function displaySalesAnalytics(salesData) {
    console.log('Sales Analytics:', salesData);
    
    // Display sales analytics in the admin dashboard
    // Implementation depends on the specific UI structure
}

async function manageProduct(action, productData = null) {
    try {
        let url = '/api/products';
        let method = 'GET';
        let body = null;
        
        switch (action) {
            case 'create':
                method = 'POST';
                body = JSON.stringify(productData);
                break;
            case 'update':
                url += `/${productData.id}`;
                method = 'PUT';
                body = JSON.stringify(productData);
                break;
            case 'delete':
                url += `/${productData.id}`;
                method = 'DELETE';
                break;
        }
        
        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
            },
            body
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        showNotification(`Product ${action}d successfully`, 'success');
        
        // Reload dashboard data
        loadAdminDashboard();
        
    } catch (error) {
        console.error(`Error ${action}ing product:`, error);
        showNotification(`Error ${action}ing product`, 'error');
    }
}

async function manageOrder(orderId, action, data = null) {
    try {
        const response = await fetch(`/api/orders/${orderId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        showNotification(`Order ${action} successful`, 'success');
        
        // Reload dashboard data
        loadAdminDashboard();
        
    } catch (error) {
        console.error(`Error managing order:`, error);
        showNotification(`Error managing order`, 'error');
    }
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${getNotificationColor(type)};
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
    }, 4000);
}

function getNotificationColor(type) {
    switch (type) {
        case 'success': return 'var(--success-green)';
        case 'error': return 'var(--error-red)';
        case 'warning': return 'var(--warning-amber)';
        default: return 'var(--primary-pink)';
    }
}

// Global function for logout
function logout() {
    localStorage.removeItem('user');
    window.location.href = '/index.html';
}

// Export functions for global access
window.manageProduct = manageProduct;
window.manageOrder = manageOrder;
window.logout = logout;