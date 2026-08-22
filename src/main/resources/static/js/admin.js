document.addEventListener('DOMContentLoaded', () => {

  const sidebar = document.querySelector('.admin-sidebar');
  const toggle = document.querySelector('.sidebar-toggle');
  const sidebarOverlay = document.createElement('div');
  sidebarOverlay.style.cssText = 'position:fixed;inset:0;z-index:49;background:rgba(0,0,0,0.6);display:none;';
  document.body.appendChild(sidebarOverlay);

  function openSidebar() {
    if (sidebar) sidebar.classList.add('open');
    sidebarOverlay.style.display = 'block';
    document.body.style.overflow = 'hidden';
  }
  function closeSidebar() {
    if (sidebar) sidebar.classList.remove('open');
    sidebarOverlay.style.display = 'none';
    document.body.style.overflow = '';
  }

  if (toggle && sidebar) {
    toggle.addEventListener('click', () => sidebar.classList.contains('open') ? closeSidebar() : openSidebar());
    sidebarOverlay.addEventListener('click', closeSidebar);
    sidebar.querySelectorAll('a').forEach(a => a.addEventListener('click', () => {
      if (window.innerWidth <= 1000) closeSidebar();
    }));
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

  /* Hero phrase preview, validation, and reorder helpers */
  const heroPhrasesInput = document.getElementById('heroPhrasesInput');
  const heroPhraseValidation = document.getElementById('heroPhraseValidation');
  const heroPhraseList = document.getElementById('heroPhraseList');
  const heroPreview = document.getElementById('heroPreview');

  function getHeroPhrases(raw) {
    return (raw || '').split('|').map(p => p.trim()).filter(Boolean);
  }

  function setHeroPhrases(phrases) {
    if (!heroPhrasesInput) return;
    heroPhrasesInput.value = phrases.join(' | ');
  }

  function renderHeroPhraseList(phrases) {
    if (!heroPhraseList) return;
    if (!phrases.length) {
      heroPhraseList.innerHTML = '<div style="color:#94a3b8; font-size:0.92rem;">No typed phrases yet. Add phrases above, separated with <code>|</code>.</div>';
      return;
    }
    heroPhraseList.innerHTML = phrases.map((phrase, index) => `
      <div style="display:flex; align-items:center; gap:8px; padding:10px 12px; border-radius:12px; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.08);">
        <div style="flex:1; font-family:var(--font-mono); font-size:0.95rem; color:#f8fafc;">${index + 1}. ${phrase}</div>
        <div style="display:flex; gap:6px;">
          <button type="button" data-phrase-action="up" data-phrase-index="${index}" style="border:none; background:rgba(255,255,255,0.08); color:#cbd5e1; padding:8px 10px; border-radius:10px; cursor:pointer;">▲</button>
          <button type="button" data-phrase-action="down" data-phrase-index="${index}" style="border:none; background:rgba(255,255,255,0.08); color:#cbd5e1; padding:8px 10px; border-radius:10px; cursor:pointer;">▼</button>
          <button type="button" data-phrase-action="remove" data-phrase-index="${index}" style="border:none; background:rgba(251,113,133,0.14); color:#fecaca; padding:8px 10px; border-radius:10px; cursor:pointer;">✕</button>
        </div>
      </div>`).join('');
  }

  function updateHeroPreview() {
    if (!heroPhrasesInput || !heroPreview || !heroPhraseValidation || !heroPhraseList) return;
    const phrases = getHeroPhrases(heroPhrasesInput.value);
    const errors = [];
    if (!phrases.length) {
      errors.push('Enter at least one phrase.');
    }
    phrases.forEach((phrase, index) => {
      if (phrase.length > 60) {
        errors.push(`Phrase ${index + 1} is too long (${phrase.length} chars). Use 60 or fewer.`);
      }
    });
    heroPhraseValidation.textContent = errors.join(' ');
    heroPreview.textContent = phrases.length ? 'Preview: ' + phrases.slice(0, 3).join(' • ') : 'Preview: no phrases entered yet.';
    renderHeroPhraseList(phrases);
  }

  function reorderHeroPhrase(index, direction) {
    const phrases = getHeroPhrases(heroPhrasesInput.value);
    if (index < 0 || index >= phrases.length) return;
    const target = index + direction;
    if (target < 0 || target >= phrases.length) return;
    [phrases[index], phrases[target]] = [phrases[target], phrases[index]];
    setHeroPhrases(phrases);
    updateHeroPreview();
  }

  function removeHeroPhrase(index) {
    const phrases = getHeroPhrases(heroPhrasesInput.value);
    if (index < 0 || index >= phrases.length) return;
    phrases.splice(index, 1);
    setHeroPhrases(phrases);
    updateHeroPreview();
  }

  if (heroPhrasesInput) {
    heroPhrasesInput.addEventListener('input', updateHeroPreview);
    updateHeroPreview();
  }

  if (heroPhraseList) {
    heroPhraseList.addEventListener('click', e => {
      const button = e.target.closest('button[data-phrase-action]');
      if (!button) return;
      const action = button.dataset.phraseAction;
      const index = Number(button.dataset.phraseIndex);
      if (action === 'up') reorderHeroPhrase(index, -1);
      if (action === 'down') reorderHeroPhrase(index, 1);
      if (action === 'remove') removeHeroPhrase(index);
    });
  }

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

  function showToast(text, type = 'success') {
    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `
      <div class="toast-icon">${type === 'error' ? '✕' : '✓'}</div>
      <div class="toast-content">
        <strong class="toast-title">${type === 'error' ? 'Reply failed' : 'Reply sent'}</strong>
        <span class="toast-text">${text}</span>
      </div>
      <button type="button" class="toast-close" aria-label="Close notification">×</button>
    `;

    const closeButton = t.querySelector('.toast-close');
    closeButton.addEventListener('click', () => t.remove());

    document.body.appendChild(t);
    setTimeout(() => t.remove(), 4500);
  }

  function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
  }

  const sentParam = getQueryParam('sent');
  if (sentParam === 'true') {
    showToast('Reply sent successfully.');
  } else if (sentParam === 'false') {
    showToast('Reply failed to send. Check SMTP settings.', 'error');
  }
});
