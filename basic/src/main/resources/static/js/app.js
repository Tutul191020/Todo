// API Configuration
const API_BASE = '/api/v1';
const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'user_info';

// State
let currentFilter = 'all';
let currentUser = null;

// Utility Functions
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

function setUser(user) {
    currentUser = user;
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function getUser() {
    const userStr = localStorage.getItem(USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

function showMessage(elementId, message, type = 'info') {
    const msgEl = document.getElementById(elementId);
    msgEl.textContent = message;
    msgEl.className = `message ${type}`;
    msgEl.style.display = 'block';

    setTimeout(() => {
        msgEl.style.display = 'none';
    }, 5000);
}

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };

    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(API_BASE + endpoint, config);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        return { success: false, message: 'Network error occurred' };
    }
}

function togglePassword(inputId, toggleBtn) {
    const input = document.getElementById(inputId);
    const icon = toggleBtn.querySelector('svg');

    if (input.type === 'password') {
        input.type = 'text';
        // Change to eye-off icon
        icon.innerHTML = `
            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
            <line x1="1" y1="1" x2="23" y2="23"></line>
        `;
    } else {
        input.type = 'password';
        // Change back to eye icon
        icon.innerHTML = `
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
        `;
    }
}

// Auth Functions
function showLogin() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
}

async function login() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;

    if (!username || !password) {
        showMessage('authMessage', 'Please fill in all fields', 'error');
        return;
    }

    const response = await apiCall('/public/auth/login', 'POST', { username, password });

    if (response.success) {
        setToken(response.data.token);
        setUser({ username, role: response.data.role });
        showApp();
        showMessage('appMessage', 'Welcome back!', 'success');
    } else {
        showMessage('authMessage', response.message || 'Login failed', 'error');
    }
}

async function register() {
    const username = document.getElementById('regUsername').value.trim();
    const firstName = document.getElementById('regFirstName').value.trim();
    const lastName = document.getElementById('regLastName').value.trim();
    const password = document.getElementById('regPassword').value;

    if (!username || !firstName || !lastName || !password) {
        showMessage('authMessage', 'Please fill in all required fields', 'error');
        return;
    }

    const response = await apiCall('/public/auth/register', 'POST', {
        username,
        firstName,
        lastName,
        password
    });

    if (response.success) {
        showMessage('authMessage', 'Registration successful! Please login.', 'success');
        setTimeout(() => {
            showLogin();
            document.getElementById('loginUsername').value = username;
        }, 1500);
    } else {
        showMessage('authMessage', response.message || 'Registration failed', 'error');
    }
}

function logout() {
    removeToken();
    currentUser = null;
    document.getElementById('authSection').style.display = 'flex';
    document.getElementById('appSection').style.display = 'none';
    showLogin();

    // Clear forms
    document.getElementById('loginUsername').value = '';
    document.getElementById('loginPassword').value = '';
}

function showApp() {
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('appSection').style.display = 'block';

    const user = getUser();
    if (user) {
        document.getElementById('userDisplay').textContent = `👋 ${user.username}`;

        // Show admin section if user is admin
        if (user.role === 'ADMIN') {
            document.getElementById('adminSection').style.display = 'block';
        }
    }

    loadTodos();
    loadDashboard();
}

// Todo Functions
async function createTodo() {
    const title = document.getElementById('todoTitle').value.trim();
    const description = document.getElementById('todoDescription').value.trim();

    if (!title) {
        showMessage('appMessage', 'Please enter a title', 'error');
        return;
    }

    const response = await apiCall('/todos', 'POST', {
        title,
        description,
        status: 'TODO'
    });

    if (response.success) {
        document.getElementById('todoTitle').value = '';
        document.getElementById('todoDescription').value = '';
        showMessage('appMessage', 'Todo created successfully!', 'success');
        loadTodos();
    } else {
        showMessage('appMessage', response.message || 'Failed to create todo', 'error');
    }
}

async function loadTodos() {
    const response = await apiCall('/todos', 'GET');

    if (response.success) {
        displayTodos(response.data);
    } else {
        showMessage('appMessage', 'Failed to load todos', 'error');
    }
}

function displayTodos(todos) {
    const todoList = document.getElementById('todoList');

    // Filter todos
    let filteredTodos = todos;
    if (currentFilter !== 'all') {
        filteredTodos = todos.filter(todo => todo.status === currentFilter);
    }

    if (filteredTodos.length === 0) {
        todoList.innerHTML = '<div class="loading">No todos found</div>';
        return;
    }

    todoList.innerHTML = filteredTodos.map(todo => createTodoHTML(todo)).join('');
}

function createTodoHTML(todo) {
    const isCompleted = todo.status === 'COMPLETED';
    const statusBadge = getStatusBadge(todo.status);
    const createdDate = new Date(todo.createdAt).toLocaleDateString();

    return `
        <div class="todo-item ${isCompleted ? 'completed' : ''}">
            <div class="todo-header">
                <div class="todo-title-section">
                    <select class="status-selector" onchange="updateTodoStatus(${todo.id}, this.value)">
                        <option value="TODO" ${todo.status === 'TODO' ? 'selected' : ''}>📋 To Do</option>
                        <option value="IN_PROGRESS" ${todo.status === 'IN_PROGRESS' ? 'selected' : ''}>⏳ In Progress</option>
                        <option value="COMPLETED" ${todo.status === 'COMPLETED' ? 'selected' : ''}>✅ Completed</option>
                        <option value="DONE" ${todo.status === 'DONE' ? 'selected' : ''}>✔️ Done</option>
                    </select>
                    <h4 class="todo-title">${escapeHtml(todo.title)}</h4>
                </div>
                <div class="todo-actions">
                    <button onclick="openEditModal(${todo.id}, '${escapeHtml(todo.title).replace(/'/g, "\\'")}', '${escapeHtml(todo.description || '').replace(/'/g, "\\'")}', '${todo.status}')" class="btn btn-success btn-sm">Edit</button>
                    <button onclick="deleteTodo(${todo.id})" class="btn btn-danger btn-sm">Delete</button>
                </div>
            </div>
            ${todo.description ? `<div class="todo-description">${escapeHtml(todo.description)}</div>` : ''}
            <div class="todo-meta">
                <span>${statusBadge}</span>
                <span>📅 ${createdDate}</span>
            </div>
        </div>
    `;
}

function getStatusBadge(status) {
    const badges = {
        'TODO': '<span class="todo-badge badge-todo">To Do</span>',
        'COMPLETED': '<span class="todo-badge badge-completed">Completed</span>',
        'IN_PROGRESS': '<span class="todo-badge badge-in-progress">In Progress</span>',
        'DONE': '<span class="todo-badge badge-completed">Done</span>'
    };
    return badges[status] || badges['TODO'];
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

async function updateTodoStatus(id, newStatus) {
    const response = await apiCall(`/todos/${id}`, 'PUT', {
        status: newStatus
    });

    if (response.success) {
        loadTodos();
        loadDashboard(); // Refresh dashboard stats
    } else {
        showMessage('appMessage', 'Failed to update status', 'error');
        loadTodos(); // Reload to reset dropdown
    }
}

async function deleteTodo(id) {
    if (!confirm('Are you sure you want to delete this todo?')) {
        return;
    }

    const response = await apiCall(`/todos/${id}`, 'DELETE');

    if (response.success) {
        showMessage('appMessage', 'Todo deleted successfully', 'success');
        loadTodos();
    } else {
        showMessage('appMessage', response.message || 'Failed to delete todo', 'error');
    }
}

function setFilter(filter) {
    currentFilter = filter;

    // Update active button
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.filter === filter) {
            btn.classList.add('active');
        }
    });

    loadTodos();
}

// Edit Todo Functions
let currentEditId = null;

function openEditModal(id, title, description, status) {
    currentEditId = id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editDescription').value = description;
    document.getElementById('editStatus').value = status;
    document.getElementById('editModal').style.display = 'flex';
}

function closeEditModal() {
    currentEditId = null;
    document.getElementById('editModal').style.display = 'none';
}

async function saveEdit() {
    if (!currentEditId) return;

    const title = document.getElementById('editTitle').value.trim();
    const description = document.getElementById('editDescription').value.trim();
    const status = document.getElementById('editStatus').value;

    if (!title) {
        showMessage('appMessage', 'Title cannot be empty', 'error');
        return;
    }

    const response = await apiCall(`/todos/${currentEditId}`, 'PUT', {
        title,
        description,
        status
    });

    if (response.success) {
        showMessage('appMessage', 'Todo updated successfully!', 'success');
        closeEditModal();
        loadTodos();
    } else {
        showMessage('appMessage', response.message || 'Failed to update todo', 'error');
    }
}

// Admin Functions
async function loadAllTodos() {
    const response = await apiCall('/todos/admin/all', 'GET');

    if (response.success) {
        displayAdminTodos(response.data);
    } else {
        showMessage('appMessage', 'Failed to load admin todos', 'error');
    }
}

function displayAdminTodos(todos) {
    const adminTodoList = document.getElementById('adminTodoList');

    if (todos.length === 0) {
        adminTodoList.innerHTML = '<div class="loading">No todos found</div>';
        return;
    }

    adminTodoList.innerHTML = todos.map(todo => createAdminTodoHTML(todo)).join('');
}

function createAdminTodoHTML(todo) {
    const isCompleted = todo.status === 'COMPLETED';
    const statusBadge = getStatusBadge(todo.status);
    const createdDate = new Date(todo.createdAt).toLocaleDateString();

    return `
        <div class="todo-item ${isCompleted ? 'completed' : ''}">
            <div class="todo-header">
                <div class="todo-title-section">
                    <h4 class="todo-title">${escapeHtml(todo.title)}</h4>
                </div>
            </div>
            ${todo.description ? `<div class="todo-description">${escapeHtml(todo.description)}</div>` : ''}
            <div class="todo-meta">
                <span>${statusBadge}</span>
                <span>👤 ${escapeHtml(todo.username)}</span>
                <span>📅 ${createdDate}</span>
            </div>
        </div>
    `;
}

// Dashboard Functions
let todoChart = null;

async function loadDashboard() {
    const monthInput = document.getElementById('monthSelect').value;

    if (!monthInput) {
        // Set to current month if not selected
        const now = new Date();
        const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
        document.getElementById('monthSelect').value = currentMonth;
        return loadDashboard();
    }

    const [year, month] = monthInput.split('-').map(Number);

    const response = await apiCall(`/todos/statistics?year=${year}&month=${month}`, 'GET');

    if (response.success) {
        displayStatistics(response.data);
    } else {
        showMessage('appMessage', 'Failed to load statistics', 'error');
    }
}

function displayStatistics(stats) {
    // Update stat cards
    document.getElementById('totalTodos').textContent = stats.totalTodos;
    document.getElementById('completedTodos').textContent = stats.completedTodos;
    document.getElementById('inProgressTodos').textContent = stats.inProgressTodos;
    document.getElementById('todoTodos').textContent = stats.todoTodos;

    // Create/update pie chart
    const ctx = document.getElementById('todoChart').getContext('2d');

    // Destroy existing chart if it exists
    if (todoChart) {
        todoChart.destroy();
    }

    todoChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['Completed', 'In Progress', 'To Do'],
            datasets: [{
                data: [stats.completedTodos, stats.inProgressTodos, stats.todoTodos],
                backgroundColor: [
                    'rgba(34, 197, 94, 0.8)',   // Green for completed
                    'rgba(251, 191, 36, 0.8)',  // Yellow for in progress
                    'rgba(99, 102, 241, 0.8)'   // Blue for todo
                ],
                borderColor: [
                    'rgba(34, 197, 94, 1)',
                    'rgba(251, 191, 36, 1)',
                    'rgba(99, 102, 241, 1)'
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#e5e7eb',
                        font: {
                            size: 14
                        },
                        padding: 15
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(17, 24, 39, 0.9)',
                    titleColor: '#e5e7eb',
                    bodyColor: '#e5e7eb',
                    borderColor: 'rgba(99, 102, 241, 0.5)',
                    borderWidth: 1,
                    padding: 12,
                    displayColors: true,
                    callbacks: {
                        label: function (context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

function filterByStatus(status) {
    // Scroll to todo list
    const todoListElement = document.getElementById('todoList');
    todoListElement.scrollIntoView({ behavior: 'smooth', block: 'start' });

    // Update current filter
    currentFilter = status;

    // Update filter button states
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.filter === status) {
            btn.classList.add('active');
        }
    });

    // Reload todos with filter
    loadTodos();

    // Show message
    const filterNames = {
        'all': 'All Tasks',
        'TODO': 'To Do Tasks',
        'IN_PROGRESS': 'In Progress Tasks',
        'COMPLETED': 'Completed Tasks'
    };
    showMessage('appMessage', `Showing ${filterNames[status] || 'tasks'}`, 'info');
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is already logged in
    const token = getToken();
    const user = getUser();

    if (token && user) {
        currentUser = user;
        showApp();
    } else {
        showLogin();
    }

    // Add enter key support for forms
    document.getElementById('loginPassword').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') login();
    });

    document.getElementById('regPassword').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') register();
    });

    document.getElementById('todoTitle').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') createTodo();
    });
});
