document.addEventListener('DOMContentLoaded', () => {

  /* ---------- Nav toggle (mobile) ---------- */
  const navToggle = document.querySelector('.nav-toggle');
  const navLinks = document.querySelector('.nav-links');
  const navOverlay = document.createElement('div');
  navOverlay.style.cssText = 'position:fixed;inset:0;z-index:98;background:rgba(0,0,0,0.5);display:none;';
  document.body.appendChild(navOverlay);

  function openNav() {
    navLinks.classList.add('open');
    navOverlay.style.display = 'block';
    document.body.style.overflow = 'hidden';
  }
  function closeNav() {
    navLinks.classList.remove('open');
    navOverlay.style.display = 'none';
    document.body.style.overflow = '';
  }

  if (navToggle && navLinks) {
    navToggle.addEventListener('click', () => navLinks.classList.contains('open') ? closeNav() : openNav());
    navLinks.querySelectorAll('a').forEach(a => a.addEventListener('click', closeNav));
    navOverlay.addEventListener('click', closeNav);
  }

  /* ---------- Navbar scroll shadow + back to top ---------- */
  const navbar = document.querySelector('.navbar');
  const backToTop = document.querySelector('.back-to-top');
  window.addEventListener('scroll', () => {
    const y = window.scrollY;
    if (navbar) navbar.style.boxShadow = y > 40
      ? '0 12px 48px rgba(0,0,0,0.6), 0 0 0 1px rgba(59,130,246,0.1) inset'
      : '0 8px 40px rgba(0,0,0,0.45), 0 0 0 1px rgba(59,130,246,0.06) inset';
    if (backToTop) backToTop.classList.toggle('visible', y > 450);
  });
  if (backToTop) backToTop.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));

  /* ---------- Cursor glow ---------- */
  const glow = document.querySelector('.cursor-glow');
  if (glow && window.matchMedia('(hover: hover)').matches) {
    document.addEventListener('mousemove', e => {
      glow.style.opacity = '1';
      glow.style.left = e.clientX + 'px';
      glow.style.top = e.clientY + 'px';
    });
  }

  /* ---------- Scroll reveal ---------- */
  const revealEls = document.querySelectorAll('[data-reveal]');
  const io = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed');
        io.unobserve(entry.target);
      }
    });
  }, { threshold: 0.15 });
  revealEls.forEach(el => io.observe(el));

  /* ---------- Typing animation (hero role text) ---------- */
  const typedEl = document.querySelector('.hero-typed .typed-text');
  if (typedEl) {
    const phrases = (typedEl.dataset.phrases || '').split('|').filter(Boolean);
    const typingSpeed = Math.max(10, Math.min(200, Number(typedEl.dataset.typingSpeed) || 75));
    const deletingSpeed = Math.max(10, Math.min(200, Number(typedEl.dataset.deletingSpeed) || 35));
    const pauseDuration = Math.max(200, Math.min(5000, Number(typedEl.dataset.pauseDuration) || 1600));
    let phraseIdx = 0, charIdx = 0, deleting = false;
    function tick() {
      const phrase = phrases[phraseIdx] || '';
      if (!deleting) {
        charIdx++;
        typedEl.textContent = phrase.slice(0, charIdx);
        if (charIdx === phrase.length) { deleting = true; setTimeout(tick, pauseDuration); return; }
      } else {
        charIdx--;
        typedEl.textContent = phrase.slice(0, charIdx);
        if (charIdx === 0) { deleting = false; phraseIdx = (phraseIdx + 1) % phrases.length; }
      }
      setTimeout(tick, deleting ? deletingSpeed : typingSpeed);
    }
    if (phrases.length) tick();
  }

  /* ---------- Counter animation (stat numbers) ---------- */
  document.querySelectorAll('[data-counter]').forEach(el => {
    const target = parseFloat(el.dataset.counter) || 0;
    const counterObs = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          let current = 0;
          const step = Math.max(target / 60, 0.5);
          const isInt = Number.isInteger(target);
          const interval = setInterval(() => {
            current += step;
            if (current >= target) { current = target; clearInterval(interval); }
            el.textContent = isInt ? Math.floor(current) : current.toFixed(1);
          }, 20);
          counterObs.unobserve(el);
        }
      });
    }, { threshold: 0.4 });
    counterObs.observe(el);
  });

  /* ---------- Skill progress bars ---------- */
  document.querySelectorAll('.progress-fill').forEach(bar => {
    const val = bar.dataset.value || 0;
    const barObs = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          bar.style.width = val + '%';
          barObs.unobserve(bar);
        }
      });
    }, { threshold: 0.3 });
    barObs.observe(bar);
  });

  /* ---------- Skills category filter ---------- */
  const tabs = document.querySelectorAll('.skills-tab');
  const skillCards = document.querySelectorAll('.skill-card');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      const cat = tab.dataset.category;
      skillCards.forEach(card => {
        const show = (cat === 'all' || card.dataset.category === cat);
        card.style.display = show ? '' : 'none';
        if (show) card.classList.add('revealed');
      });
    });
  });

  /* ---------- Ripple effect on glass buttons ---------- */
  document.querySelectorAll('.glass-btn').forEach(btn => {
    btn.addEventListener('click', function (e) {
      const rect = this.getBoundingClientRect();
      const ripple = document.createElement('span');
      ripple.className = 'ripple';
      ripple.style.left = (e.clientX - rect.left) + 'px';
      ripple.style.top = (e.clientY - rect.top) + 'px';
      ripple.style.width = ripple.style.height = Math.max(rect.width, rect.height) + 'px';
      this.appendChild(ripple);
      setTimeout(() => ripple.remove(), 600);
    });
  });

  /* ---------- Project click tracking ---------- */
  document.querySelectorAll('[data-track-project]').forEach(link => {
    link.addEventListener('click', () => {
      const id = link.dataset.trackProject;
      fetch('/project/' + id + '/click').catch(() => {});
    });
  });

  /* ---------- Certificate modal preview ---------- */
  const modal = document.getElementById('certModal');
  const modalImg = document.getElementById('certModalImg');
  document.querySelectorAll('[data-cert-preview]').forEach(card => {
    card.addEventListener('click', () => {
      if (!modal) return;
      modalImg.src = card.dataset.certPreview;
      modal.classList.add('open');
    });
  });
  if (modal) {
    modal.addEventListener('click', e => { if (e.target === modal) modal.classList.remove('open'); });
    const closeBtn = modal.querySelector('.modal-close');
    if (closeBtn) closeBtn.addEventListener('click', () => modal.classList.remove('open'));
  }

  /* ---------- Contact form (progressive enhancement toast) ---------- */
  const contactForm = document.getElementById('contactForm');
  if (contactForm && contactForm.dataset.success === 'true') {
    showToast('Message sent — thanks for reaching out!');
  }

  function showToast(text, type = 'success') {
    const t = document.createElement('div');
    t.className = 'toast ' + type;
    t.innerHTML = `<div class="toast-icon">${type === 'success' ? '✓' : '!'}</div><div class="toast-content"><div class="toast-text">${text}</div></div><button class="toast-close" onclick="this.parentElement.remove()">×</button>`;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), 4000);
  }

  /* ---------- Theme Switcher ---------- */
  function initTheme() {
    const current = document.documentElement.getAttribute('data-theme') || localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', current);
    updateThemeIcon(current);
  }

  function updateThemeIcon(theme) {
    const btn = document.getElementById('themeToggle');
    if (!btn) return;
    const icon = btn.querySelector('i');
    if (!icon) return;
    if (theme === 'light') {
      icon.className = 'fa-solid fa-sun';
      btn.setAttribute('title', 'Switch to Dark Mode');
    } else {
      icon.className = 'fa-solid fa-moon';
      btn.setAttribute('title', 'Switch to Light Mode');
    }
  }

  function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') === 'light' ? 'light' : 'dark';
    const next = current === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', next);
    try { localStorage.setItem('theme', next); } catch(e) {}
    updateThemeIcon(next);
  }

  const themeBtn = document.getElementById('themeToggle');
  if (themeBtn) {
    themeBtn.addEventListener('click', toggleTheme);
  }
  initTheme();

  /* ---------- Resume Preview Modal ---------- */
  window.openResumeModal = function() {
    const modal = document.getElementById('resumeModal');
    const iframe = document.getElementById('resumeIframe');
    if (!modal) return;
    if (iframe && (!iframe.src || iframe.src === 'about:blank' || iframe.src.endsWith('/'))) {
      iframe.src = '/resume/preview';
    }
    modal.classList.add('open');
    document.body.style.overflow = 'hidden';
  };

  window.closeResumeModal = function() {
    const modal = document.getElementById('resumeModal');
    if (modal) {
      modal.classList.remove('open');
      document.body.style.overflow = '';
    }
  };

  /* ---------- Architecture Diagram Lightbox ---------- */
  window.openArchModal = function(url, title) {
    const modal = document.getElementById('archModal');
    const img = document.getElementById('archModalImg');
    const titleEl = document.getElementById('archModalTitle');
    if (!modal || !url) return;
    if (img) img.src = url;
    if (titleEl && title) titleEl.textContent = title + ' — Architecture';
    modal.classList.add('open');
    document.body.style.overflow = 'hidden';
  };

  window.closeArchModal = function() {
    const modal = document.getElementById('archModal');
    if (modal) {
      modal.classList.remove('open');
      document.body.style.overflow = '';
    }
  };

  /* ---------- Interactive Developer CLI Terminal ---------- */
  const cliHistory = [];
  let historyIndex = -1;

  window.toggleCliTerminal = function() {
    const drawer = document.getElementById('cliDrawer');
    if (!drawer) return;
    if (drawer.classList.contains('open')) {
      window.closeCliTerminal();
    } else {
      window.openCliTerminal();
    }
  };

  window.openCliTerminal = function() {
    const drawer = document.getElementById('cliDrawer');
    const input = document.getElementById('cliInput');
    if (!drawer) return;
    drawer.classList.add('open');
    if (input) setTimeout(() => input.focus(), 150);
  };

  window.closeCliTerminal = function() {
    const drawer = document.getElementById('cliDrawer');
    if (drawer) drawer.classList.remove('open');
  };

  const cliInput = document.getElementById('cliInput');
  const cliBody = document.getElementById('cliBody');

  if (cliInput && cliBody) {
    cliInput.addEventListener('keydown', function(e) {
      if (e.key === 'Enter') {
        const raw = this.value.trim();
        this.value = '';
        if (!raw) return;
        cliHistory.push(raw);
        historyIndex = cliHistory.length;
        handleCliCommand(raw);
      } else if (e.key === 'ArrowUp') {
        if (historyIndex > 0) {
          historyIndex--;
          this.value = cliHistory[historyIndex];
        }
        e.preventDefault();
      } else if (e.key === 'ArrowDown') {
        if (historyIndex < cliHistory.length - 1) {
          historyIndex++;
          this.value = cliHistory[historyIndex];
        } else {
          historyIndex = cliHistory.length;
          this.value = '';
        }
        e.preventDefault();
      }
    });
  }

  function handleCliCommand(cmd) {
    const clean = cmd.trim().toLowerCase();
    printCliLine('<span style="color:var(--primary);font-weight:700;">prince@dev:~$</span> ' + escHtml(cmd));

    switch(clean) {
      case 'help':
        printCliLine(
          'Available commands:<br>' +
          '  <strong style="color:#38bdf8;">about</strong>       - Overview &amp; career objective<br>' +
          '  <strong style="color:#38bdf8;">skills</strong>      - Core technologies &amp; tools<br>' +
          '  <strong style="color:#38bdf8;">projects</strong>    - Featured engineering projects<br>' +
          '  <strong style="color:#38bdf8;">exp</strong>         - Work experience &amp; internships<br>' +
          '  <strong style="color:#38bdf8;">education</strong>   - Degrees and college details<br>' +
          '  <strong style="color:#38bdf8;">contact</strong>     - Email, phone, socials &amp; scheduler<br>' +
          '  <strong style="color:#38bdf8;">resume</strong>      - Open in-browser resume preview<br>' +
          '  <strong style="color:#38bdf8;">theme</strong>       - Toggle between dark &amp; light mode<br>' +
          '  <strong style="color:#38bdf8;">clear</strong>       - Clear terminal screen<br>' +
          '  <strong style="color:#38bdf8;">exit</strong>        - Close terminal'
        );
        break;
      case 'about':
        const bioEl = document.querySelector('.hero-intro') || document.querySelector('#about p');
        printCliLine(bioEl ? escHtml(bioEl.textContent) : 'Full Stack Developer with expertise in Java, Spring Boot, MySQL, and React.');
        break;
      case 'skills':
        const skillNames = Array.from(document.querySelectorAll('.skill-head strong')).map(el => el.textContent);
        printCliLine(skillNames.length ? 'Skills: ' + escHtml(skillNames.join(' • ')) : 'Java, Spring Boot, REST APIs, MySQL, React, JavaScript, HTML5, CSS3, Docker, Git');
        break;
      case 'projects':
        const projTitles = Array.from(document.querySelectorAll('.project-title')).map(el => el.textContent);
        printCliLine(projTitles.length ? 'Projects:<br>' + projTitles.map(t => '  • ' + escHtml(t)).join('<br>') : 'Projects built with Java, Spring Boot, Node.js, and React.');
        break;
      case 'exp':
      case 'experience':
        const roles = Array.from(document.querySelectorAll('.exp-item h3')).map(el => el.textContent);
        printCliLine(roles.length ? 'Experience:<br>' + roles.map(r => '  • ' + escHtml(r)).join('<br>') : 'Full Stack Developer Intern @ Codveda Technologies');
        break;
      case 'education':
      case 'edu':
        const edus = Array.from(document.querySelectorAll('.timeline-item h4')).map(el => el.textContent);
        printCliLine(edus.length ? 'Education:<br>' + edus.map(e => '  • ' + escHtml(e)).join('<br>') : 'B.Tech in Computer Science & Engineering');
        break;
      case 'contact':
        const emailLink = document.querySelector('a[href^="mailto:"]');
        const emailVal = emailLink ? emailLink.getAttribute('href').replace('mailto:', '') : 'prince@example.com';
        const locItem = Array.from(document.querySelectorAll('.info-grid > div')).find(d => d.textContent.includes('Location'));
        const locVal = locItem ? (locItem.querySelector('.value') ? locItem.querySelector('.value').textContent : '') : '';
        const ghLink = document.querySelector('a[href*="github.com"]');
        const liLink = document.querySelector('a[href*="linkedin.com"]');
        printCliLine(
          'Email: ' + escHtml(emailVal) +
          (locVal ? '<br>Location: ' + escHtml(locVal) : '') +
          (ghLink ? '<br>GitHub: ' + escHtml(ghLink.getAttribute('href')) : '') +
          (liLink ? '<br>LinkedIn: ' + escHtml(liLink.getAttribute('href')) : '')
        );
        break;
      case 'resume':
        printCliLine('Opening in-browser resume preview...');
        window.openResumeModal();
        break;
      case 'theme':
        toggleTheme();
        printCliLine('Theme toggled to ' + document.documentElement.getAttribute('data-theme') + ' mode.');
        break;
      case 'clear':
        if (cliBody) cliBody.innerHTML = '';
        break;
      case 'exit':
      case 'quit':
        window.closeCliTerminal();
        break;
      case 'sudo':
        printCliLine('<span style="color:#ef4444;">prince is already a superuser. Permission granted.</span>');
        break;
      default:
        printCliLine('<span style="color:#ef4444;">Command not recognized: ' + escHtml(cmd) + '. Type <strong>help</strong> for a list of commands.</span>');
    }
    if (cliBody) cliBody.scrollTop = cliBody.scrollHeight;
  }

  function printCliLine(html) {
    if (!cliBody) return;
    const div = document.createElement('div');
    div.className = 'cli-output';
    div.innerHTML = html;
    cliBody.appendChild(div);
  }

  function escHtml(str) {
    return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }

  /* ---------- Global key shortcuts ---------- */
  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
      window.closeResumeModal();
      window.closeArchModal();
      window.closeCliTerminal();
    }
    if (e.ctrlKey && (e.key === '`' || e.key === '~')) {
      e.preventDefault();
      window.toggleCliTerminal();
    }
  });
});
