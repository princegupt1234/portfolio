document.addEventListener('DOMContentLoaded', () => {

  const sidebar = document.querySelector('.admin-sidebar');
  const toggle = document.querySelector('.sidebar-toggle');
  if (toggle && sidebar) {
    toggle.addEventListener('click', () => sidebar.classList.toggle('open'));
  }

  /* Confirm before delete */
  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!confirm(form.dataset.confirm || 'Are you sure?')) e.preventDefault();
    });
  });

  /* File input preview + drop zone label */
  document.querySelectorAll('.file-drop input[type=file]').forEach(input => {
    input.addEventListener('change', () => {
      const label = input.closest('.file-drop').querySelector('.file-drop-label');
      if (label && input.files.length) label.textContent = input.files[0].name;
    });
  });

  /* Simple client-side table search */
  document.querySelectorAll('[data-table-search]').forEach(input => {
    input.addEventListener('input', () => {
      const table = document.querySelector(input.dataset.tableSearch);
      if (!table) return;
      const term = input.value.toLowerCase();
      table.querySelectorAll('tbody tr').forEach(row => {
        row.style.display = row.textContent.toLowerCase().includes(term) ? '' : 'none';
      });
    });
  });

  /* Visitors chart (Chart.js, loaded from CDN in dashboard.html) */
  const chartCanvas = document.getElementById('visitorsChart');
  if (chartCanvas && window.Chart && window.chartLabels) {
    new Chart(chartCanvas, {
      type: 'line',
      data: {
        labels: window.chartLabels,
        datasets: [{
          label: 'Portfolio Views',
          data: window.chartViews,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59,130,246,0.15)',
          tension: 0.35,
          fill: true,
          pointRadius: 0,
        }]
      },
      options: {
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { color: 'rgba(255,255,255,0.06)' }, ticks: { color: '#94a3b8' } },
          y: { grid: { color: 'rgba(255,255,255,0.06)' }, ticks: { color: '#94a3b8' } }
        }
      }
    });
  }

  const projectChartCanvas = document.getElementById('projectClicksChart');
  if (projectChartCanvas && window.Chart && window.projectLabels) {
    new Chart(projectChartCanvas, {
      type: 'doughnut',
      data: {
        labels: window.projectLabels,
        datasets: [{
          data: window.projectViews,
          backgroundColor: ['#3b82f6', '#06b6d4', '#8b5cf6', '#22c55e', '#f59e0b', '#ef4444']
        }]
      },
      options: { plugins: { legend: { position: 'bottom', labels: { color: '#94a3b8' } } } }
    });
  }
});
