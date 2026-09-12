document.addEventListener('DOMContentLoaded', () => {

  /* ── Mobile Sidebar Drawer ── */
  const sidebar = document.querySelector('.admin-sidebar');
  const toggle = document.querySelector('.sidebar-toggle');
  const sidebarOverlay = document.createElement('div');
  sidebarOverlay.style.cssText = 'position:fixed;inset:0;z-index:49;background:rgba(0,0,0,0.6);display:none;backdrop-filter:blur(4px);';
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

  /* ── Confirm destructive actions ── */
  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!confirm(form.dataset.confirm || 'Are you sure you want to perform this action?')) {
        e.preventDefault();
      }
    });
  });

  /* ── File input preview + drop zone label ── */
  document.querySelectorAll('.file-drop input[type=file]').forEach(input => {
    input.addEventListener('change', () => {
      const label = input.closest('.file-drop').querySelector('.file-drop-label');
      if (label && input.files.length) {
        label.innerHTML = `<i class="fa-solid fa-check" style="color:var(--success);"></i> Selected: <strong>${input.files[0].name}</strong>`;
      }
    });
  });

  /* ── CSRF Token Helpers ── */
  function getCookie(name) {
    const value = document.cookie.split('; ').find(row => row.startsWith(`${name}=`));
    return value ? decodeURIComponent(value.split('=')[1]) : null;
  }

  function getCsrfToken(form) {
    const meta = document.querySelector('meta[name="_csrf"]');
    if (meta && meta.content) return meta.content;
    if (form) {
      const input = form.querySelector('input[name="_csrf"]');
      if (input && input.value) return input.value;
    }
    return getCookie('XSRF-TOKEN') || '';
  }

  function getCsrfHeader() {
    const meta = document.querySelector('meta[name="_csrf_header"]');
    if (meta && meta.content) return meta.content;
    return 'X-XSRF-TOKEN';
  }

  /* ── Form Toggle Switches (Active Sync & Label Click Delegation) ── */
  function syncToggleSwitch(cb) {
    const parentSwitch = cb.closest('.toggle-switch');
    if (parentSwitch) {
      parentSwitch.classList.toggle('checked', cb.checked);
    }
  }

  // Synchronize on load and when state changes
  document.querySelectorAll('.toggle-switch input[type="checkbox"]').forEach(cb => {
    syncToggleSwitch(cb);
    cb.addEventListener('change', () => syncToggleSwitch(cb));
  });

  // Clicking label text or the toggle group row toggles the checkbox
  document.querySelectorAll('.toggle-group, .avail-row').forEach(group => {
    group.style.cursor = 'pointer';
    group.addEventListener('click', (e) => {
      if (e.target.closest('.toggle-switch')) return;
      if (e.target.closest('a, button, select, input, textarea')) return;
      const cb = group.querySelector('.toggle-switch input[type="checkbox"]');
      if (cb) {
        cb.checked = !cb.checked;
        syncToggleSwitch(cb);
        cb.dispatchEvent(new Event('change', { bubbles: true }));
      }
    });
  });

  /* ── Featured Toggle Button (AJAX with Form Fallback) ── */
  function setFeaturedButtonState(button, featured) {
    button.dataset.featured = String(featured);
    button.classList.toggle('on', featured);
    button.classList.toggle('off', !featured);
    button.setAttribute('aria-pressed', String(featured));
    button.textContent = featured ? 'Yes' : 'No';
  }

  document.querySelectorAll('[data-featured-toggle]').forEach(button => {
    button.addEventListener('click', async (e) => {
      e.preventDefault();
      if (button.disabled) return;

      const previousValue = button.dataset.featured === 'true';
      const requestedValue = !previousValue;
      const projectId = button.dataset.projectId;
      const form = button.closest('form');

      button.disabled = true;
      button.setAttribute('aria-busy', 'true');
      setFeaturedButtonState(button, requestedValue);

      const csrfToken = getCsrfToken(form);
      const csrfHeader = getCsrfHeader();

      try {
        const headers = { 'Content-Type': 'application/json' };
        if (csrfToken) {
          headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(`/admin/projects/${projectId}/featured`, {
          method: 'PATCH',
          headers: headers,
          body: JSON.stringify({ featured: requestedValue })
        });
        const result = await response.json().catch(() => ({}));
        if (!response.ok || typeof result.featured !== 'boolean') {
          throw new Error(result.error || 'Featured status could not be updated.');
        }
        setFeaturedButtonState(button, result.featured);
        showToast(`Project is ${result.featured ? 'now featured' : 'no longer featured'}.`);
      } catch (error) {
        if (form) {
          // Native POST form fallback guarantees state update even if fetch fails
          form.submit();
        } else {
          setFeaturedButtonState(button, previousValue);
          showToast(error.message || 'Featured status could not be updated.', 'error');
        }
      } finally {
        button.disabled = false;
        button.removeAttribute('aria-busy');
      }
    });
  });

  /* ── Universal Table Toggle Buttons (AJAX with Form Fallback) ── */
  document.querySelectorAll('form[action*="/toggle-"]').forEach(form => {
    // Skip if already handled by data-featured-toggle
    if (form.querySelector('[data-featured-toggle]')) return;

    form.addEventListener('submit', async (e) => {
      if (e.metaKey || e.ctrlKey) return;
      e.preventDefault();

      const button = form.querySelector('button[type="submit"]');
      if (!button || button.disabled) return;

      button.disabled = true;
      button.setAttribute('aria-busy', 'true');

      const isStatusPill = button.classList.contains('status-pill');
      const wasOn = button.classList.contains('on');
      const wasUnread = button.classList.contains('unread');
      const previousText = button.innerText.trim();

      const csrfToken = getCsrfToken(form);
      const csrfHeader = getCsrfHeader();

      try {
        const headers = { 'X-Requested-With': 'XMLHttpRequest' };
        if (csrfToken) headers[csrfHeader] = csrfToken;

        const response = await fetch(form.action, {
          method: 'POST',
          headers: headers,
          body: new FormData(form)
        });

        if (!response.ok) {
          throw new Error('Server returned ' + response.status);
        }

        if (isStatusPill) {
          if (wasUnread) {
            button.classList.remove('unread');
            button.classList.add('off');
            button.innerHTML = '<span class="status-dot"></span><span>Read</span>';
            showToast('Message marked as Read.');
          } else if (button.classList.contains('off') && previousText.toLowerCase().includes('read')) {
            button.classList.remove('off');
            button.classList.add('unread');
            button.innerHTML = '<span class="status-dot"></span><span>Unread</span>';
            showToast('Message marked as Unread.');
          } else {
            const nowOn = !wasOn;
            button.classList.toggle('on', nowOn);
            button.classList.toggle('off', !nowOn);

            let newLabel = nowOn ? 'Visible' : 'Hidden';
            if (previousText.toLowerCase().includes('published')) {
              newLabel = nowOn ? 'Published' : 'Hidden';
            } else if (previousText.toLowerCase().includes('yes') || previousText.toLowerCase().includes('no')) {
              newLabel = nowOn ? 'Yes' : 'No';
            }
            button.innerHTML = `<span class="status-dot"></span><span>${newLabel}</span>`;
            showToast(`Status updated to ${newLabel}.`);
          }
        } else {
          window.location.reload();
        }
      } catch (err) {
        // Fallback to native form submission
        form.submit();
      } finally {
        button.disabled = false;
        button.removeAttribute('aria-busy');
      }
    });
  });

  /* ── Enhanced Client-Side Table Search ── */
  document.querySelectorAll('[data-table-search]').forEach(input => {
    input.addEventListener('input', () => {
      const table = document.querySelector(input.dataset.tableSearch);
      if (!table) return;
      const tbody = table.querySelector('tbody');
      if (!tbody) return;

      const term = input.value.trim().toLowerCase();
      let matchCount = 0;
      const rows = tbody.querySelectorAll('tr:not(.table-no-matches):not(.empty-initial-row)');

      rows.forEach(row => {
        const matches = row.textContent.toLowerCase().includes(term);
        row.style.display = matches ? '' : 'none';
        if (matches) matchCount++;
      });

      // Show/remove "no matches" placeholder row
      let noMatchRow = tbody.querySelector('.table-no-matches');
      if (term && matchCount === 0) {
        if (!noMatchRow) {
          noMatchRow = document.createElement('tr');
          noMatchRow.className = 'table-no-matches';
          noMatchRow.innerHTML = `<td colspan="100%" style="text-align:center; padding:32px; color:var(--text-muted); font-size:0.88rem;">
            <i class="fa-solid fa-magnifying-glass" style="margin-right:8px; opacity:0.6;"></i>
            No records found matching "<strong>${escapeHtml(input.value)}</strong>"
          </td>`;
          tbody.appendChild(noMatchRow);
        }
      } else if (noMatchRow) {
        noMatchRow.remove();
      }
    });
  });

  function escapeHtml(str) {
    return (str || '').replace(/[&<>"']/g, m => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[m]);
  }

  /* ── Hero phrase preview, validation, and reorder helpers ── */
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
      heroPhraseList.innerHTML = '<div style="color:var(--text-muted); font-size:0.88rem;">No typed phrases yet. Add phrases above, separated with <code>|</code>.</div>';
      return;
    }
    heroPhraseList.innerHTML = phrases.map((phrase, index) => `
      <div style="display:flex; align-items:center; gap:8px; padding:10px 14px; border-radius:var(--radius-sm); background:rgba(255,255,255,0.04); border:1px solid var(--glass-border);">
        <div style="flex:1; font-family:var(--font-mono); font-size:0.88rem; color:var(--text);">${index + 1}. ${escapeHtml(phrase)}</div>
        <div style="display:flex; gap:6px;">
          <button type="button" data-phrase-action="up" data-phrase-index="${index}" style="border:none; background:rgba(255,255,255,0.08); color:var(--text-secondary); padding:6px 9px; border-radius:6px; cursor:pointer;">▲</button>
          <button type="button" data-phrase-action="down" data-phrase-index="${index}" style="border:none; background:rgba(255,255,255,0.08); color:var(--text-secondary); padding:6px 9px; border-radius:6px; cursor:pointer;">▼</button>
          <button type="button" data-phrase-action="remove" data-phrase-index="${index}" style="border:none; background:rgba(239,68,68,0.15); color:#fca5a5; padding:6px 9px; border-radius:6px; cursor:pointer;">✕</button>
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
        errors.push(`Phrase ${index + 1} is too long (${phrase.length} chars). Max 60.`);
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

  /* ── Charts Initialization ── */
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
          backgroundColor: 'rgba(59,130,246,0.12)',
          tension: 0.35,
          fill: true,
          pointRadius: 0,
        }]
      },
      options: {
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } },
          y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } }
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
      options: {
        plugins: { legend: { position: 'bottom', labels: { color: '#94a3b8' } } }
      }
    });
  }

  /* ── Dismissible Alerts ── */
  document.querySelectorAll('.alert-close').forEach(btn => {
    btn.addEventListener('click', () => {
      const alert = btn.closest('.alert');
      if (alert) alert.remove();
    });
  });

  /* ── Toast Notifications ── */
  function showToast(text, type = 'success') {
    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `
      <div class="toast-icon">${type === 'error' ? '✕' : '✓'}</div>
      <div class="toast-content">
        <strong class="toast-title">${type === 'error' ? 'Notice' : 'Success'}</strong>
        <span class="toast-text">${escapeHtml(text)}</span>
      </div>
      <button type="button" class="toast-close" aria-label="Close notification">×</button>
    `;

    const closeButton = t.querySelector('.toast-close');
    closeButton.addEventListener('click', () => t.remove());

    document.body.appendChild(t);
    setTimeout(() => {
      t.style.opacity = '0';
      t.style.transform = 'translateY(-8px)';
      setTimeout(() => t.remove(), 300);
    }, 4500);
  }

  window.showToast = showToast;

  /* ── URL Parameter Feedback ── */
  function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
  }

  if (getQueryParam('saved') === 'true') {
    showToast('Changes saved successfully.');
  }
  if (getQueryParam('sent') === 'true') {
    showToast('Reply sent successfully.');
  } else if (getQueryParam('sent') === 'false') {
    showToast('Reply saved in database, but outbound email delivery failed.', 'error');
  }
});
