document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(localStorage.getItem('user'));

    // Page Protection: Redirect if not a logged-in staff member
    if (!user || user.role !== 'staff') {
        logout();
        return;
    }

    // Load staff dashboard data
    loadStaffDashboard();
});

async function loadStaffDashboard() {
    try {
        // Load assigned orders
        await loadAssignedOrders();
        
        // Load pending orders that need assignment
        await loadPendingOrders();
        
        // Load inventory alerts
        await loadInventoryAlerts();
        
    } catch (error) {
        console.error('Error loading staff dashboard:', error);
        showNotification('Error loading dashboard data', 'error');
    }
}

async function loadAssignedOrders() {
    try {
        const user = JSON.parse(localStorage.getItem('user'));
        const response = await fetch(`/api/orders?assignedStaffId=${user.id}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const orders = await response.json();
        displayAssignedOrders(orders);
        
    } catch (error) {
        console.error('Error loading assigned orders:', error);
        showNotification('Error loading assigned orders', 'error');
    }
}

async function loadPendingOrders() {
    try {
        const response = await fetch('/api/orders?status=pending');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const orders = await response.json();
        displayPendingOrders(orders);
        
    } catch (error) {
        console.error('Error loading pending orders:', error);
        showNotification('Error loading pending orders', 'error');
    }
}

function displayAssignedOrders(orders) {
    // This would update a section of the staff dashboard
    // For now, we'll log the orders
    console.log('Assigned Orders:', orders);
    
    // Update UI to show assigned orders count
    updateDashboardStats('assigned-orders', orders.length);
}

function displayPendingOrders(orders) {
    // This would update a section of the staff dashboard
    console.log('Pending Orders:', orders);
    
    // Update UI to show pending orders count
    updateDashboardStats('pending-orders', orders.length);
}

async function loadInventoryAlerts() {
    try {
        // Get products with low stock
        const response = await fetch('/api/products');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const products = await response.json();
        const lowStockProducts = products.filter(product => product.stockQuantity <= 5);
        
        displayInventoryAlerts(lowStockProducts);
        
    } catch (error) {
        console.error('Error loading inventory alerts:', error);
        showNotification('Error loading inventory alerts', 'error');
    }
}

function displayInventoryAlerts(lowStockProducts) {
    console.log('Low Stock Products:', lowStockProducts);
    
    // Update UI to show inventory alerts count
    updateDashboardStats('inventory-alerts', lowStockProducts.length);
    
    if (lowStockProducts.length > 0) {
        showNotification(`${lowStockProducts.length} products are running low on stock!`, 'warning');
    }
}

async function updateOrderStatus(orderId, newStatus) {
    try {
        const response = await fetch(`/api/orders/${orderId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: newStatus }),
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        showNotification(`Order status updated to ${newStatus}`, 'success');
        
        // Reload dashboard data
        loadStaffDashboard();
        
    } catch (error) {
        console.error('Error updating order status:', error);
        showNotification('Error updating order status', 'error');
    }
}

function updateDashboardStats(statType, count) {
    // This would update dashboard statistics
    // Implementation depends on the specific UI structure
    console.log(`${statType}: ${count}`);
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
window.updateOrderStatus = updateOrderStatus;
window.logout = logout;