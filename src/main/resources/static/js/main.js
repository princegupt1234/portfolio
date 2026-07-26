document.addEventListener('DOMContentLoaded', () => {

  /* ---------- Nav toggle (mobile) ---------- */
  const navToggle = document.querySelector('.nav-toggle');
  const navLinks = document.querySelector('.nav-links');
  if (navToggle && navLinks) {
    navToggle.addEventListener('click', () => navLinks.classList.toggle('open'));
    navLinks.querySelectorAll('a').forEach(a => a.addEventListener('click', () => navLinks.classList.remove('open')));
  }

  /* ---------- Navbar shrink on scroll + back to top ---------- */
  const navbar = document.querySelector('.navbar');
  const backToTop = document.querySelector('.back-to-top');
  window.addEventListener('scroll', () => {
    const y = window.scrollY;
    if (navbar) navbar.style.padding = y > 40 ? '10px 6vw' : '16px 6vw';
    if (backToTop) backToTop.classList.toggle('visible', y > 500);
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
    let phraseIdx = 0, charIdx = 0, deleting = false;
    function tick() {
      const phrase = phrases[phraseIdx] || '';
      if (!deleting) {
        charIdx++;
        typedEl.textContent = phrase.slice(0, charIdx);
        if (charIdx === phrase.length) { deleting = true; setTimeout(tick, 1600); return; }
      } else {
        charIdx--;
        typedEl.textContent = phrase.slice(0, charIdx);
        if (charIdx === 0) { deleting = false; phraseIdx = (phraseIdx + 1) % phrases.length; }
      }
      setTimeout(tick, deleting ? 35 : 65);
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
        card.style.display = (cat === 'all' || card.dataset.category === cat) ? '' : 'none';
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

  function showToast(text) {
    const t = document.createElement('div');
    t.className = 'toast';
    t.textContent = text;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), 3500);
  }
});
