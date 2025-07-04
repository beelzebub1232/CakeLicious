document.addEventListener('DOMContentLoaded', () => {
    // If already logged in on a non-login page, do nothing.
    // If on login page, redirect away.
    if (window.location.pathname === '/' || window.location.pathname === '/index.html') {
        if (localStorage.getItem('user')) {
            const user = JSON.parse(localStorage.getItem('user'));
            window.location.href = `/${user.role}.html`;
        }
    }

    const loginForm = document.getElementById('login-form');
    if(loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');
    const submitButton = e.target.querySelector('button[type="submit"]');
    
    // Clear previous error
    errorMessage.style.display = 'none';
    errorMessage.textContent = '';

    // Show loading state
    const originalButtonText = submitButton.innerHTML;
    submitButton.disabled = true;
    submitButton.innerHTML = '<span>Signing In...</span><span>⏳</span>';

    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            const userData = await response.json();
            localStorage.setItem('user', JSON.stringify(userData));
            
            // Show success state
            submitButton.innerHTML = '<span>Success!</span><span>✅</span>';
            
            // Add success animation to form
            const loginContainer = document.querySelector('.login-container');
            loginContainer.style.transform = 'scale(1.02)';
            loginContainer.style.boxShadow = '0 25px 50px -12px rgba(236, 72, 153, 0.25)';
            
            // Redirect after brief delay
            setTimeout(() => {
                window.location.href = `/${userData.role}.html`;
            }, 1000);
        } else {
            const error = await response.json();
            showError(error.error || 'Login failed. Please check your credentials.');
        }
    } catch (error) {
        showError('Unable to connect to server. Please try again later.');
        console.error('Login Error:', error);
    } finally {
        // Reset button if there was an error
        if (!localStorage.getItem('user')) {
            setTimeout(() => {
                submitButton.disabled = false;
                submitButton.innerHTML = originalButtonText;
            }, 1000);
        }
    }
    
    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
        
        // Add shake animation to form
        const loginContainer = document.querySelector('.login-container');
        loginContainer.style.animation = 'shake 0.5s ease-in-out';
        
        // Reset animation
        setTimeout(() => {
            loginContainer.style.animation = '';
        }, 500);
    }
}

function logout() {
    // Add logout animation
    const body = document.body;
    body.style.transition = 'opacity 0.3s ease-out';
    body.style.opacity = '0';
    
    setTimeout(() => {
        localStorage.removeItem('user');
        window.location.href = '/index.html';
    }, 300);
}