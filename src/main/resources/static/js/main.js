/* Immediate global exports to guarantee availability before and after DOM load */
window.openRecruiterModal = function() {
  const m = document.getElementById('recruiterModal');
  if (m) {
    m.style.display = 'flex';
    m.classList.add('open');
    document.body.style.overflow = 'hidden';
  }
};
window.closeRecruiterModal = function() {
  const m = document.getElementById('recruiterModal');
  if (m) {
    m.classList.remove('open');
    m.style.display = 'none';
    document.body.style.overflow = '';
  }
};
window.copyRecruiterPitch = function() {
  const modal = document.getElementById('recruiterModal');
  const customPitch = modal ? modal.getAttribute('data-pitch') : null;
  const pitchText = (customPitch && customPitch.trim())
    ? customPitch.trim()
    : ("Prince Gupt | Full Stack Software Engineer (Java, Spring Boot, MySQL, React). Ready for immediate hire (0-day notice) for SDE-1 / Software Engineer roles. Email: princegupt3052@gmail.com | Portfolio: " + window.location.origin);
  navigator.clipboard.writeText(pitchText).then(() => {
    const btn = document.getElementById('pitchCopyBtn');
    if (btn) {
      const orig = btn.innerHTML;
      btn.innerHTML = '<i class="fa-solid fa-check" style="color:#10b981;"></i> <span>Copied!</span>';
      setTimeout(() => { btn.innerHTML = orig; }, 2200);
    }
  });
};

window.toggleAiChat = function() {
  const d = document.getElementById('aiChatDrawer');
  if (!d) return;
  if (d.classList.contains('open') || d.style.display === 'flex') {
    window.closeAiChat();
  } else {
    window.openAiChat();
  }
};
window.openAiChat = function() {
  const d = document.getElementById('aiChatDrawer');
  if (!d) return;
  d.style.display = 'flex';
  d.classList.add('open');
  const inp = document.getElementById('aiChatInput');
  if (inp) setTimeout(() => inp.focus(), 100);
};
window.closeAiChat = function() {
  const d = document.getElementById('aiChatDrawer');
  if (d) {
    d.classList.remove('open');
    d.style.display = 'none';
  }
};

window.openResumeModal = function() {
  const modal = document.getElementById('resumeModal');
  const iframe = document.getElementById('resumeIframe');
  if (!modal) return;
  if (iframe && (!iframe.src || iframe.src === 'about:blank' || iframe.src.endsWith('/'))) {
    iframe.src = '/resume/preview';
  }
  modal.style.display = 'flex';
  modal.classList.add('open');
  document.body.style.overflow = 'hidden';
};
window.closeResumeModal = function() {
  const modal = document.getElementById('resumeModal');
  if (modal) {
    modal.classList.remove('open');
    modal.style.display = 'none';
    document.body.style.overflow = '';
  }
};

window.toggleCliTerminal = function() {
  const d = document.getElementById('cliDrawer');
  if (d) d.classList.toggle('open');
};
window.openCliTerminal = function() {
  const d = document.getElementById('cliDrawer');
  if (d) d.classList.add('open');
};
window.closeCliTerminal = function() {
  const d = document.getElementById('cliDrawer');
  if (d) d.classList.remove('open');
};
window.execCliCommand = function(c) {
  window.openCliTerminal();
};
window.toggleCliMaximize = function() {
  const d = document.getElementById('cliDrawer');
  if (d) d.classList.toggle('maximized');
};
window.handleCliDrawerClick = function(e) {};

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

  /* ---------- Navbar scroll shadow & floating back-to-top ---------- */
  const navbar = document.querySelector('.navbar');
  const floatingBackToTop = document.getElementById('floatingBackToTop');
  const aiLauncher = document.getElementById('aiChatLaunchBtn');

  if (floatingBackToTop && !aiLauncher) {
    floatingBackToTop.style.bottom = '24px';
  }

  const updateScrollShadowAndTopBtn = () => {
    const y = window.scrollY;
    if (navbar) navbar.style.boxShadow = y > 40
      ? '0 12px 48px rgba(0,0,0,0.6), 0 0 0 1px rgba(59,130,246,0.1) inset'
      : '0 8px 40px rgba(0,0,0,0.45), 0 0 0 1px rgba(59,130,246,0.06) inset';
    if (floatingBackToTop) {
      floatingBackToTop.classList.toggle('visible', y > 60);
    }
  };

  window.addEventListener('scroll', updateScrollShadowAndTopBtn, { passive: true });
  updateScrollShadowAndTopBtn();

  if (floatingBackToTop) {
    floatingBackToTop.addEventListener('click', (e) => {
      e.preventDefault();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

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

  /* ---------- Recruiter 30-Second Pitch Modal ---------- */
  window.openRecruiterModal = function() {
    const modal = document.getElementById('recruiterModal');
    if (modal) {
      modal.style.display = 'flex';
      modal.classList.add('open');
      document.body.style.overflow = 'hidden';
    }
  };

  window.closeRecruiterModal = function() {
    const modal = document.getElementById('recruiterModal');
    if (modal) {
      modal.classList.remove('open');
      modal.style.display = 'none';
      document.body.style.overflow = '';
    }
  };

  window.copyRecruiterPitch = function() {
    const modal = document.getElementById('recruiterModal');
    const customPitch = modal ? modal.getAttribute('data-pitch') : null;
    const pitchText = (customPitch && customPitch.trim())
      ? customPitch.trim()
      : ("Prince Gupt | Full Stack Software Engineer (Java, Spring Boot, MySQL, React). Ready for immediate hire (0-day notice) for SDE-1 / Software Engineer roles. Email: princegupt3052@gmail.com | Portfolio: " + window.location.origin);
    
    function showCopiedFeedback() {
      const btn = document.getElementById('pitchCopyBtn');
      if (btn) {
        const orig = btn.innerHTML;
        btn.innerHTML = '<i class="fa-solid fa-check" style="color:#10b981;"></i> <span>Copied!</span>';
        setTimeout(() => { btn.innerHTML = orig; }, 2200);
      }
    }

    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(pitchText)
        .then(showCopiedFeedback)
        .catch(() => {
          // Fallback if clipboard API blocked
          try {
            const ta = document.createElement('textarea');
            ta.value = pitchText;
            ta.style.position = 'fixed';
            ta.style.opacity = '0';
            document.body.appendChild(ta);
            ta.select();
            document.execCommand('copy');
            document.body.removeChild(ta);
            showCopiedFeedback();
          } catch(e) {}
        });
    } else {
      try {
        const ta = document.createElement('textarea');
        ta.value = pitchText;
        ta.style.position = 'fixed';
        ta.style.opacity = '0';
        document.body.appendChild(ta);
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
        showCopiedFeedback();
      } catch(e) {}
    }
  };

  /* ---------- Interactive Developer CLI Terminal ---------- */
  const cliHistory = [];
  let historyIndex = -1;

  const ALL_CLI_COMMANDS = [
    'help', 'about', 'skills', 'projects', 'exp', 'edu', 'certs',
    'contact', 'resume', 'theme', 'socials', 'github', 'linkedin',
    'whatsapp', 'email', 'stats', 'currently', 'ls', 'cat', 'pwd',
    'whoami', 'date', 'echo', 'clear', 'history', 'sudo', 'exit'
  ];

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
    if (input) {
      setTimeout(() => {
        input.focus();
        input.select();
      }, 120);
    }
  };

  window.closeCliTerminal = function() {
    const drawer = document.getElementById('cliDrawer');
    if (drawer) {
      drawer.classList.remove('open');
      drawer.classList.remove('maximized');
    }
  };

  window.toggleCliMaximize = function() {
    const drawer = document.getElementById('cliDrawer');
    const maxBtn = document.getElementById('cliMaxBtn');
    if (!drawer) return;
    drawer.classList.toggle('maximized');
    if (maxBtn) {
      const isMax = drawer.classList.contains('maximized');
      maxBtn.innerHTML = isMax ? '<i class="fa-solid fa-down-left-and-up-right-to-center"></i>' : '<i class="fa-solid fa-up-right-and-down-left-from-center"></i>';
    }
    const input = document.getElementById('cliInput');
    if (input) input.focus();
  };

  window.handleCliDrawerClick = function(e) {
    if (e.target.closest('button') || e.target.closest('a') || e.target.closest('input')) return;
    const input = document.getElementById('cliInput');
    if (input) input.focus();
  };

  window.execCliCommand = function(cmd) {
    window.openCliTerminal();
    const input = document.getElementById('cliInput');
    if (input) input.value = '';
    if (cmd) {
      cliHistory.push(cmd);
      historyIndex = cliHistory.length;
      handleCliCommand(cmd);
    }
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
      } else if (e.key === 'Tab') {
        e.preventDefault();
        const current = this.value.trim().toLowerCase();
        if (!current) return;
        const match = ALL_CLI_COMMANDS.find(c => c.startsWith(current));
        if (match) {
          this.value = match;
        }
      }
    });
  }

  function handleCliCommand(cmd) {
    const raw = cmd.trim();
    const parts = raw.split(/\s+/);
    const clean = parts[0].toLowerCase();
    const arg = parts.slice(1).join(' ').trim();

    printCliLine('<span style="color:var(--primary);font-weight:700;">prince@dev:~$</span> ' + escHtml(raw));

    switch(clean) {
      case 'help':
      case '?':
      case 'commands':
        printCliLine(
          '<div style="margin:4px 0 8px; color:var(--text); font-weight:600;">⚡ Available Commands (click to execute):</div>' +
          '<div style="display:grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap:6px 16px; font-size:0.8rem;">' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'about\')">about</span> - Career narrative &amp; background</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'skills\')">skills</span> - Stack &amp; technologies</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'projects\')">projects</span> - Engineering projects</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'exp\')">exp</span> - Experience &amp; internships</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'edu\')">edu</span> - College &amp; degrees</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'certs\')">certs</span> - Verified certifications</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'contact\')">contact</span> - Email, phone, socials &amp; scheduler</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'resume\')">resume</span> - Open live resume viewer</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'stats\')">stats</span> - Project &amp; coding metrics</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'socials\')">socials</span> - GitHub, LinkedIn, WhatsApp</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'theme\')">theme</span> - Toggle dark / light mode</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'ls\')">ls</span> - List directory files</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'whoami\')">whoami</span> - Display current user</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'date\')">date</span> - Print current timestamp</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'history\')">history</span> - Previous commands</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'clear\')">clear</span> - Clear terminal window</div>' +
          '  <div><span class="cli-cmd-tag" onclick="execCliCommand(\'exit\')">exit</span> - Close terminal drawer</div>' +
          '</div>'
        );
        break;

      case 'about':
      case 'bio':
      case 'whois':
        const bioEl = document.querySelector('#about p') || document.querySelector('.hero-intro');
        const availEl = document.querySelector('#about [style*="color:var(--success)"]');
        const prefEl = document.querySelector('#about .info-grid');
        let aboutOut = '<div style="color:var(--primary); font-weight:700; margin-bottom:4px;">👤 About Prince Gupt:</div>';
        if (bioEl) aboutOut += '<div>' + escHtml(bioEl.textContent.trim()) + '</div>';
        if (availEl) aboutOut += '<div style="margin-top:6px; color:var(--success);">🟢 ' + escHtml(availEl.textContent.trim()) + '</div>';
        printCliLine(aboutOut);
        break;

      case 'skills':
      case 'tech':
      case 'stack':
        const skillCards = document.querySelectorAll('.skill-card');
        if (skillCards.length) {
          const categorized = {};
          skillCards.forEach(c => {
            const cat = c.dataset.category || 'General';
            const nameEl = c.querySelector('strong');
            if (nameEl) {
              if (!categorized[cat]) categorized[cat] = [];
              categorized[cat].push(nameEl.textContent.trim());
            }
          });
          let skillHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">🛠️ Technical Skills &amp; Stack:</div>';
          for (const [cat, list] of Object.entries(categorized)) {
            skillHtml += '<div style="margin-bottom:4px;"><span style="color:#f59e0b; font-weight:600;">[' + escHtml(cat.toUpperCase()) + ']:</span> ' + escHtml(list.join(' • ')) + '</div>';
          }
          printCliLine(skillHtml);
        } else {
          printCliLine('Java, Spring Boot, Microservices, REST APIs, MySQL, Hibernate/JPA, React, Next.js, Docker, Git, Linux');
        }
        break;

      case 'projects':
      case 'work':
      case 'portfolio':
        const projElements = document.querySelectorAll('.project-card');
        if (projElements.length) {
          let projHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">🚀 Featured &amp; Engineering Projects:</div>';
          projElements.forEach(card => {
            const titleEl = card.querySelector('.project-title');
            const descEl = card.querySelector('.project-desc');
            const demoLink = card.querySelector('a[href*="http"]');
            if (titleEl) {
              const isFeat = card.querySelector('.project-featured-badge') ? ' ⭐' : '';
              projHtml += '<div style="margin-bottom:8px; padding-left:10px; border-left:2px solid var(--primary);">';
              projHtml += '<strong>' + escHtml(titleEl.textContent.trim()) + isFeat + '</strong>';
              if (descEl) projHtml += '<div style="color:var(--text-secondary); font-size:0.78rem;">' + escHtml(descEl.textContent.trim().substring(0, 140)) + '...</div>';
              if (demoLink) projHtml += '<a href="' + escHtml(demoLink.href) + '" target="_blank" rel="noopener" class="cli-link-btn"><i class="fa-solid fa-arrow-up-right-from-square"></i> Open</a>';
              projHtml += '</div>';
            }
          });
          printCliLine(projHtml);
        } else {
          printCliLine('Full Stack E-Commerce Platform (Java + Spring Boot), Smart Portfolio System, Microservices Architecture Demo.');
        }
        break;

      case 'exp':
      case 'experience':
      case 'jobs':
        const expItems = document.querySelectorAll('.exp-item');
        if (expItems.length) {
          let expHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">💼 Experience Timeline:</div>';
          expItems.forEach(item => {
            const role = item.querySelector('h3');
            const company = item.querySelector('p');
            if (role) {
              expHtml += '<div style="margin-bottom:8px; padding-left:10px; border-left:2px solid var(--secondary);">';
              expHtml += '<strong>' + escHtml(role.textContent.trim()) + '</strong>';
              if (company) expHtml += '<div style="color:#38bdf8; font-size:0.78rem;">' + escHtml(company.textContent.trim()) + '</div>';
              expHtml += '</div>';
            }
          });
          printCliLine(expHtml);
        } else {
          printCliLine('Full Stack Developer Intern @ Codveda Technologies (Java, Spring Boot, REST APIs, MySQL)');
        }
        break;

      case 'edu':
      case 'education':
        const eduItems = document.querySelectorAll('.timeline-item');
        if (eduItems.length) {
          let eduHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">🎓 Education History:</div>';
          eduItems.forEach(item => {
            const deg = item.querySelector('h4');
            const inst = item.querySelector('.meta');
            const dur = item.querySelector('p');
            if (deg) {
              eduHtml += '<div style="margin-bottom:8px; padding-left:10px; border-left:2px solid #10b981;">';
              eduHtml += '<strong>' + escHtml(deg.textContent.trim()) + '</strong>';
              if (inst) eduHtml += '<div style="color:var(--text-secondary); font-size:0.78rem;">' + escHtml(inst.textContent.trim()) + '</div>';
              if (dur) eduHtml += '<div style="color:#94a3b8; font-size:0.75rem;">' + escHtml(dur.textContent.trim()) + '</div>';
              eduHtml += '</div>';
            }
          });
          printCliLine(eduHtml);
        } else {
          printCliLine('B.Tech in Computer Science & Engineering (2021-2025) @ BIET Lucknow (AKTU)');
        }
        break;

      case 'certs':
      case 'certificates':
      case 'cert':
        const certCards = document.querySelectorAll('.cert-card');
        if (certCards.length) {
          let certHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">📜 Verified Certifications:</div>';
          certCards.forEach(c => {
            const title = c.querySelector('h3');
            const issuer = c.querySelector('.cert-issuer');
            const link = c.querySelector('a[href*="http"]');
            if (title) {
              certHtml += '<div style="margin-bottom:6px; padding-left:8px; border-left:2px solid #f59e0b;">';
              certHtml += '<strong>' + escHtml(title.textContent.trim()) + '</strong>';
              if (issuer) certHtml += '<span style="color:#94a3b8; font-size:0.78rem;"> · ' + escHtml(issuer.textContent.trim()) + '</span>';
              if (link) certHtml += '<a href="' + escHtml(link.href) + '" target="_blank" class="cli-link-btn"><i class="fa-solid fa-arrow-up-right-from-square"></i> Verify</a>';
              certHtml += '</div>';
            }
          });
          printCliLine(certHtml);
        } else {
          printCliLine('Certifications: Java Full Stack Development (Wipro/Coursera/Udemy)');
        }
        break;

      case 'contact':
      case 'reach':
        const emailLink = document.querySelector('a[href^="mailto:"]');
        const emailVal = emailLink ? emailLink.getAttribute('href').replace('mailto:', '') : 'princegupt3052@gmail.com';
        const calLink = document.querySelector('a[href*="calendly"]');
        let contactHtml = '<div style="color:var(--primary); font-weight:700; margin-bottom:6px;">📬 Contact Information:</div>';
        contactHtml += '<div><strong>Email:</strong> <a href="mailto:' + escHtml(emailVal) + '" style="color:#38bdf8;">' + escHtml(emailVal) + '</a></div>';
        contactHtml += '<div><strong>Location:</strong> Lucknow, Uttar Pradesh, India</div>';
        contactHtml += '<div><strong>Phone:</strong> +91-7275807576</div>';
        contactHtml += '<div><strong>GitHub:</strong> <a href="https://github.com/princegupt1234" target="_blank" style="color:#38bdf8;">github.com/princegupt1234</a></div>';
        contactHtml += '<div><strong>LinkedIn:</strong> <a href="https://linkedin.com/in/prince-gupt-175289322" target="_blank" style="color:#38bdf8;">linkedin.com/in/prince-gupt</a></div>';
        if (calLink) contactHtml += '<div style="margin-top:6px;"><a href="' + escHtml(calLink.href) + '" target="_blank" class="cli-link-btn" style="background:#10b981; border-color:#10b981; color:#fff;"><i class="fa-solid fa-calendar"></i> Book 1:1 Call</a></div>';
        printCliLine(contactHtml);
        break;

      case 'resume':
      case 'cv':
        printCliLine('📄 Opening in-browser resume viewer modal...');
        if (typeof window.openResumeModal === 'function') {
          window.openResumeModal();
        }
        break;

      case 'theme':
      case 'mode':
        if (typeof toggleTheme === 'function') {
          toggleTheme();
          const cur = document.documentElement.getAttribute('data-theme') || 'dark';
          printCliLine('🌓 Theme toggled to <strong style="color:var(--primary);">' + escHtml(cur.toUpperCase()) + '</strong> mode.');
        }
        break;

      case 'socials':
      case 'social':
        printCliLine(
          '<div style="margin-bottom:4px; color:var(--primary); font-weight:700;">🌐 Social Profiles:</div>' +
          '  • <strong>GitHub:</strong> <a href="https://github.com/princegupt1234" target="_blank" style="color:#38bdf8;">github.com/princegupt1234</a><br>' +
          '  • <strong>LinkedIn:</strong> <a href="https://linkedin.com/in/prince-gupt-175289322" target="_blank" style="color:#38bdf8;">linkedin.com/in/prince-gupt-175289322</a><br>' +
          '  • <strong>WhatsApp:</strong> <a href="https://wa.me/917275807576" target="_blank" style="color:#38bdf8;">wa.me/917275807576</a>'
        );
        break;

      case 'github':
      case 'gh':
        printCliLine('Opening GitHub profile in a new tab: <a href="https://github.com/princegupt1234" target="_blank" style="color:#38bdf8;">github.com/princegupt1234</a>');
        window.open('https://github.com/princegupt1234', '_blank');
        break;

      case 'linkedin':
      case 'li':
        printCliLine('Opening LinkedIn profile in a new tab: <a href="https://linkedin.com/in/prince-gupt-175289322" target="_blank" style="color:#38bdf8;">linkedin.com/in/prince-gupt-175289322</a>');
        window.open('https://linkedin.com/in/prince-gupt-175289322', '_blank');
        break;

      case 'whatsapp':
      case 'wa':
        printCliLine('Opening WhatsApp chat: <a href="https://wa.me/917275807576" target="_blank" style="color:#38bdf8;">wa.me/917275807576</a>');
        window.open('https://wa.me/917275807576', '_blank');
        break;

      case 'email':
      case 'mail':
        printCliLine('Opening default mail client for princegupt3052@gmail.com...');
        window.location.href = 'mailto:princegupt3052@gmail.com';
        break;

      case 'stats':
      case 'metrics':
        const statEls = document.querySelectorAll('#quick-stats .stat-item, .stat-card');
        let statText = '<div style="color:var(--primary); font-weight:700; margin-bottom:4px;">📊 Portfolio Metrics &amp; Stats:</div>';
        if (statEls.length) {
          statEls.forEach(s => {
            const num = s.querySelector('.stat-num') || s.querySelector('h3');
            const lbl = s.querySelector('.stat-label') || s.querySelector('p');
            if (num && lbl) {
              statText += '  • <strong>' + escHtml(num.textContent.trim()) + '</strong> ' + escHtml(lbl.textContent.trim()) + '<br>';
            }
          });
        } else {
          statText += '  • <strong>10+</strong> Projects Built<br>  • <strong>LeetCode &amp; DSA</strong> Active Problem Solving<br>  • <strong>1+ Yrs</strong> Hands-on Development';
        }
        printCliLine(statText);
        break;

      case 'currently':
      case 'now':
        printCliLine('🔨 <strong>Currently Building:</strong> Production-ready Spring Boot Microservices, Cloud Integrations &amp; System Design studies.');
        break;

      case 'ls':
      case 'dir':
        printCliLine(
          '<div style="font-family:var(--font-mono); color:#93c5fd; display:flex; flex-wrap:wrap; gap:16px;">' +
          '  <span style="color:#a7f3d0;">about.txt</span>' +
          '  <span style="color:#a7f3d0;">skills.json</span>' +
          '  <span style="color:#fcd34d;">projects/</span>' +
          '  <span style="color:#a7f3d0;">experience.md</span>' +
          '  <span style="color:#a7f3d0;">education.txt</span>' +
          '  <span style="color:#fcd34d;">certificates/</span>' +
          '  <span style="color:#f472b6;">resume.pdf</span>' +
          '  <span style="color:#67e8f9;">contact.sh</span>' +
          '</div>' +
          '<div style="color:var(--text-muted); font-size:0.75rem; margin-top:4px;">Type <code style="color:#38bdf8;">cat &lt;filename&gt;</code> to inspect file contents.</div>'
        );
        break;

      case 'cat':
        if (!arg) {
          printCliLine('<span style="color:#fca5a5;">Usage: cat &lt;filename&gt; (e.g. cat about.txt, cat skills.json, cat resume.pdf)</span>');
        } else if (arg.includes('about')) {
          handleCliCommand('about');
        } else if (arg.includes('skill')) {
          handleCliCommand('skills');
        } else if (arg.includes('project')) {
          handleCliCommand('projects');
        } else if (arg.includes('exp')) {
          handleCliCommand('exp');
        } else if (arg.includes('edu')) {
          handleCliCommand('edu');
        } else if (arg.includes('cert')) {
          handleCliCommand('certs');
        } else if (arg.includes('resume')) {
          handleCliCommand('resume');
        } else if (arg.includes('contact')) {
          handleCliCommand('contact');
        } else {
          printCliLine('<span style="color:#fca5a5;">cat: ' + escHtml(arg) + ': No such file. Try typing <span class="cli-cmd-tag" onclick="execCliCommand(\'ls\')">ls</span> to view files.</span>');
        }
        break;

      case 'whoami':
        printCliLine('visitor@prince-portfolio (Role: Guest Recruiter / Developer inspecting Prince Gupta\'s portfolio)');
        break;

      case 'pwd':
        printCliLine('/home/prince/portfolio');
        break;

      case 'date':
        printCliLine(new Date().toString());
        break;

      case 'echo':
        printCliLine(escHtml(arg || ''));
        break;

      case 'history':
        if (cliHistory.length === 0) {
          printCliLine('No commands in history yet.');
        } else {
          let hist = '<div style="color:var(--primary); font-weight:700;">Command History:</div>';
          cliHistory.forEach((h, i) => {
            hist += '  ' + (i + 1) + '. <span class="cli-cmd-tag" onclick="execCliCommand(\'' + escHtml(h) + '\')">' + escHtml(h) + '</span><br>';
          });
          printCliLine(hist);
        }
        break;

      case 'clear':
      case 'cls':
        if (cliBody) {
          cliBody.innerHTML = '';
          printCliLine('<div style="color:var(--text-muted); font-size:0.78rem;">Terminal screen cleared. Type <span class="cli-cmd-tag" onclick="execCliCommand(\'help\')">help</span> for commands.</div>');
        }
        break;

      case 'exit':
      case 'quit':
        window.closeCliTerminal();
        break;

      case 'sudo':
        printCliLine('<span style="color:#22c55e;">✔ prince is already root superuser. All privileges granted.</span>');
        break;

      default:
        printCliLine(
          '<span style="color:#fca5a5;">zsh: command not found: ' + escHtml(raw) + '.</span> ' +
          'Type <span class="cli-cmd-tag" onclick="execCliCommand(\'help\')">help</span> to view supported commands, or click any chip above.'
        );
    }

    if (cliBody) {
      setTimeout(() => {
        cliBody.scrollTop = cliBody.scrollHeight;
      }, 20);
    }
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
      window.closeRecruiterModal();
      window.closeAiChat();
      window.closeCliTerminal();
    }
    if ((e.ctrlKey || e.metaKey) && (e.key === '`' || e.key === '~')) {
      e.preventDefault();
      window.toggleCliTerminal();
    }
    if (e.key === 'F2') {
      e.preventDefault();
      window.toggleCliTerminal();
    }
  });

  /* ===================== "Ask Prince AI" Chatbot Widget ===================== */
  window.toggleAiChat = function() {
    const drawer = document.getElementById('aiChatDrawer');
    if (!drawer) return;
    if (drawer.classList.contains('open')) {
      window.closeAiChat();
    } else {
      window.openAiChat();
    }
  };

  window.openAiChat = function() {
    const drawer = document.getElementById('aiChatDrawer');
    if (!drawer) return;
    drawer.classList.add('open');
    const input = document.getElementById('aiChatInput');
    if (input) setTimeout(() => input.focus(), 100);
  };

  window.closeAiChat = function() {
    const drawer = document.getElementById('aiChatDrawer');
    if (drawer) drawer.classList.remove('open');
  };

  window.clearAiChat = function() {
    const messages = document.getElementById('aiChatMessages');
    if (messages) {
      messages.innerHTML = `
        <div class="ai-msg bot">
          <div class="ai-msg-avatar"><i class="fa-solid fa-robot"></i></div>
          <div class="ai-msg-bubble">
            👋 <strong>Hello!</strong> I am Prince's interactive AI assistant.<br><br>
            Ask me anything about Prince's <strong>Java &amp; Spring Boot</strong> mastery, featured projects, <strong>LeetCode &amp; DSA problem solving</strong>, or hiring availability!
          </div>
        </div>
      `;
    }
  };

  window.sendQuickAiQuery = function(text) {
    const input = document.getElementById('aiChatInput');
    if (input) {
      input.value = text;
      const form = document.getElementById('aiChatForm');
      if (form) form.dispatchEvent(new Event('submit', { cancelable: true }));
    }
  };

  window.handleAiChatSubmit = function(e) {
    if (e) e.preventDefault();
    const input = document.getElementById('aiChatInput');
    const messages = document.getElementById('aiChatMessages');
    const sendBtn = document.getElementById('aiChatSendBtn');
    if (!input || !messages) return;

    const query = input.value.trim();
    if (!query) return;

    // Append User Message
    const userMsg = document.createElement('div');
    userMsg.className = 'ai-msg user';
    userMsg.innerHTML = `
      <div class="ai-msg-avatar"><i class="fa-solid fa-user"></i></div>
      <div class="ai-msg-bubble">${escHtml(query)}</div>
    `;
    messages.appendChild(userMsg);
    input.value = '';
    messages.scrollTop = messages.scrollHeight;

    // Show Typing Indicator
    const typingMsg = document.createElement('div');
    typingMsg.className = 'ai-msg bot';
    typingMsg.id = 'aiTypingIndicator';
    typingMsg.innerHTML = `
      <div class="ai-msg-avatar"><i class="fa-solid fa-robot"></i></div>
      <div class="ai-msg-bubble">
        <div class="ai-typing-indicator">
          <span class="ai-typing-dot"></span>
          <span class="ai-typing-dot"></span>
          <span class="ai-typing-dot"></span>
        </div>
      </div>
    `;
    messages.appendChild(typingMsg);
    messages.scrollTop = messages.scrollHeight;

    if (sendBtn) sendBtn.disabled = true;

    // Fetch API
    fetch('/api/ai/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ message: query })
    })
      .then(res => res.json())
      .then(data => {
        const ind = document.getElementById('aiTypingIndicator');
        if (ind) ind.remove();
        if (sendBtn) sendBtn.disabled = false;

        const replyText = (data && data.reply) ? data.reply : "I'm sorry, I couldn't process that. Please try asking about Prince's skills, projects, or contact info!";
        const botMsg = document.createElement('div');
        botMsg.className = 'ai-msg bot';
        botMsg.innerHTML = `
          <div class="ai-msg-avatar"><i class="fa-solid fa-robot"></i></div>
          <div class="ai-msg-bubble">${formatAiMarkdown(replyText)}</div>
        `;
        messages.appendChild(botMsg);
        messages.scrollTop = messages.scrollHeight;
      })
      .catch(err => {
        const ind = document.getElementById('aiTypingIndicator');
        if (ind) ind.remove();
        if (sendBtn) sendBtn.disabled = false;

        const botMsg = document.createElement('div');
        botMsg.className = 'ai-msg bot';
        botMsg.innerHTML = `
          <div class="ai-msg-avatar"><i class="fa-solid fa-triangle-exclamation" style="color:#ef4444;"></i></div>
          <div class="ai-msg-bubble" style="border-color:rgba(239,68,68,0.3);">
            Could not connect right now. You can reach Prince directly via email at <a href="mailto:princegupt3052@gmail.com">princegupt3052@gmail.com</a>.
          </div>
        `;
        messages.appendChild(botMsg);
        messages.scrollTop = messages.scrollHeight;
      });
  };

  function formatAiMarkdown(text) {
    if (!text) return '';
    let h = escHtml(text);
    // Bold: **text**
    h = h.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    // Italic: _text_
    h = h.replace(/_(.*?)_/g, '<em>$1</em>');
    // Markdown link: [text](url)
    h = h.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank">$1</a>');
    // Bullet points starting with • or - or *
    const lines = h.split('\n');
    let inList = false;
    let out = [];
    for (let line of lines) {
      let trimmed = line.trim();
      if (trimmed.startsWith('• ') || trimmed.startsWith('- ') || trimmed.startsWith('* ')) {
        if (!inList) {
          out.push('<ul>');
          inList = true;
        }
        out.push('<li>' + trimmed.replace(/^[•\-*]\s*/, '') + '</li>');
      } else {
        if (inList) {
          out.push('</ul>');
          inList = false;
        }
        if (trimmed) {
          out.push('<p>' + trimmed + '</p>');
        }
      }
    }
    if (inList) out.push('</ul>');
    return out.join('');
  }
});

