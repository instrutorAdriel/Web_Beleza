document.addEventListener('DOMContentLoaded', () => {
    const btnToggle = document.getElementById('btnToggleSidebar');
    const btnClose = document.getElementById('btnCloseSidebar');
    const sidebar = document.getElementById('sidebarMenu');
    const overlay = document.getElementById('sidebarOverlay');

    if (!sidebar) return;

    function setSidebarOpen(isOpen) {
        sidebar.classList.toggle('open', isOpen);
        if (overlay) overlay.classList.toggle('active', isOpen);
        document.body.classList.toggle('overflow-hidden', isOpen);
        if (btnToggle) btnToggle.setAttribute('aria-expanded', String(isOpen));
    }

    function toggleSidebar() {
        setSidebarOpen(!sidebar.classList.contains('open'));
    }

    if (btnToggle) btnToggle.addEventListener('click', toggleSidebar);
    if (btnClose) btnClose.addEventListener('click', () => setSidebarOpen(false));
    if (overlay) overlay.addEventListener('click', () => setSidebarOpen(false));
    sidebar.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', () => setSidebarOpen(false));
    });
    document.addEventListener('keydown', event => {
        if (event.key === 'Escape' && sidebar.classList.contains('open')) {
            setSidebarOpen(false);
        }
    });
});
