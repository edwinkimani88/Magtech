/* ================================================================
   MAGTECH — MAIN JAVASCRIPT
   Navigation, search, wishlist, header scroll, UI interactions
   ================================================================ */

(function () {
  'use strict';

  // ── DOM ready helper ───────────────────────────────────────
  const ready = (fn) => {
    if (document.readyState !== 'loading') fn();
    else document.addEventListener('DOMContentLoaded', fn);
  };

  // ── Selectors ──────────────────────────────────────────────
  const $ = (sel, ctx = document) => ctx.querySelector(sel);
  const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

  // ── Header scroll behaviour ────────────────────────────────
  const initHeader = () => {
    const header = $('#site-header');
    if (!header) return;

    const onScroll = () => {
      header.classList.toggle('is-scrolled', window.scrollY > 20);
    };

    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();
  };

  // ── Search overlay ─────────────────────────────────────────
  const initSearch = () => {
    const toggle  = $('#search-toggle');
    const overlay = $('#search-overlay');
    const closBtn = $('#search-close');
    const input   = $('#search-input');

    if (!toggle || !overlay) return;

    const open = () => {
      overlay.classList.add('is-open');
      overlay.setAttribute('aria-hidden', 'false');
      toggle.setAttribute('aria-expanded', 'true');
      document.body.style.overflow = 'hidden';
      setTimeout(() => input?.focus(), 100);
    };

    const close = () => {
      overlay.classList.remove('is-open');
      overlay.setAttribute('aria-hidden', 'true');
      toggle.setAttribute('aria-expanded', 'false');
      document.body.style.overflow = '';
    };

    toggle.addEventListener('click', open);
    closBtn?.addEventListener('click', close);

    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) close();
    });

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') close();
    });
  };

  // ── Mobile nav ─────────────────────────────────────────────
  const initMobileNav = () => {
    const hamburger = $('#hamburger');
    const nav       = $('#mobile-nav');
    const backdrop  = $('#mobile-nav-backdrop');
    const closeBtn  = $('#mobile-nav-close');

    if (!hamburger || !nav) return;

    const open = () => {
      nav.classList.add('is-open');
      nav.setAttribute('aria-hidden', 'false');
      backdrop?.classList.add('is-visible');
      hamburger.classList.add('is-open');
      hamburger.setAttribute('aria-expanded', 'true');
      document.body.style.overflow = 'hidden';
    };

    const close = () => {
      nav.classList.remove('is-open');
      nav.setAttribute('aria-hidden', 'true');
      backdrop?.classList.remove('is-visible');
      hamburger.classList.remove('is-open');
      hamburger.setAttribute('aria-expanded', 'false');
      document.body.style.overflow = '';
    };

    hamburger.addEventListener('click', open);
    closeBtn?.addEventListener('click', close);
    backdrop?.addEventListener('click', close);
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') close();
    });
  };

  // ── Wishlist (localStorage) ────────────────────────────────
  const WISHLIST_KEY = 'magtech_wishlist';

  const getWishlist = () => {
    try { return JSON.parse(localStorage.getItem(WISHLIST_KEY) || '[]'); }
    catch { return []; }
  };

  const saveWishlist = (list) => {
    localStorage.setItem(WISHLIST_KEY, JSON.stringify(list));
  };

  const toggleWishlistItem = (id, name) => {
    let list = getWishlist();
    const idx = list.indexOf(String(id));
    if (idx === -1) {
      list.push(String(id));
      showToast(`Saved: ${name}`);
    } else {
      list.splice(idx, 1);
      showToast(`Removed from saved`);
    }
    saveWishlist(list);
    updateWishlistUI();
    return idx === -1;
  };

  const updateWishlistUI = () => {
    const list  = getWishlist();
    const badge = $('#wishlist-count');
    if (badge) {
      badge.textContent = list.length;
      badge.style.display = list.length > 0 ? 'flex' : 'none';
    }

    // Update all save buttons on page
    $$('[data-save-btn]').forEach((btn) => {
      const id = btn.dataset.id;
      btn.classList.toggle('is-saved', list.includes(String(id)));
    });
  };

  const initWishlist = () => {
    updateWishlistUI();

    // Delegate click on save buttons
    document.addEventListener('click', (e) => {
      const btn = e.target.closest('[data-save-btn]');
      if (!btn) return;
      e.preventDefault();
      e.stopPropagation();
      const { id, name } = btn.dataset;
      const saved = toggleWishlistItem(id, name);
      btn.classList.toggle('is-saved', saved);
    });
  };

  // ── Toast notifications ─────────────────────────────────────
  const showToast = (msg, duration = 3000) => {
    const toast = $('#wishlist-toast');
    if (!toast) return;
    toast.textContent = msg;
    toast.classList.add('is-visible');
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => toast.classList.remove('is-visible'), duration);
  };

  // Expose globally
  window.magtechShowToast = showToast;

  // ── Scroll reveal ──────────────────────────────────────────
  const initReveal = () => {
    if (!('IntersectionObserver' in window)) {
      $$('[data-reveal]').forEach((el) => el.classList.add('is-visible'));
      return;
    }

    const obs = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible');
          obs.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

    $$('[data-reveal]').forEach((el) => obs.observe(el));
  };

  // ── Product card link wrap ─────────────────────────────────
  // Allow clicking the whole card to go to product page
  const initCardLinks = () => {
    $$('.product-card[data-url]').forEach((card) => {
      card.addEventListener('click', (e) => {
        if (e.target.closest('[data-save-btn]')) return;
        if (e.target.closest('.btn')) return;
        window.location.href = card.dataset.url;
      });
    });
  };

  // ── Gallery thumbnails (product page) ──────────────────────
  const initGallery = () => {
    const mainImg = $('#gallery-main-img');
    if (!mainImg) return;

    $$('.gallery-thumb').forEach((thumb) => {
      thumb.addEventListener('click', () => {
        const src = thumb.dataset.src;
        mainImg.src = src;
        $$('.gallery-thumb').forEach((t) => t.classList.remove('is-active'));
        thumb.classList.add('is-active');
      });
    });

    // Lightbox
    const galleryMain = $('.gallery-main');
    const lightbox    = $('#lightbox');
    const lightboxImg = $('#lightbox-img');
    const lightboxClose = $('#lightbox-close');

    if (galleryMain && lightbox) {
      galleryMain.addEventListener('click', () => {
        lightboxImg.src = mainImg.src;
        lightbox.classList.add('is-open');
        document.body.style.overflow = 'hidden';
      });

      const closeLightbox = () => {
        lightbox.classList.remove('is-open');
        document.body.style.overflow = '';
      };

      lightboxClose?.addEventListener('click', closeLightbox);
      lightbox.addEventListener('click', (e) => { if (e.target === lightbox) closeLightbox(); });
      document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeLightbox(); });
    }
  };

  // ── Filter chips (shop page) ───────────────────────────────
  const initFilterChips = () => {
    $$('.filter-chip[data-param]').forEach((chip) => {
      chip.addEventListener('click', () => {
        const params = new URLSearchParams(window.location.search);
        const { param, value } = chip.dataset;

        if (chip.classList.contains('is-active') && value !== 'All') {
          params.delete(param);
        } else {
          if (value === 'All' || value === '') params.delete(param);
          else params.set(param, value);
          params.delete('page');
        }

        window.location.href = window.location.pathname + '?' + params.toString();
      });
    });
  };

  // ── Sidebar options (shop page) ────────────────────────────
  const initSidebarOptions = () => {
    $$('.sidebar-option[data-param]').forEach((opt) => {
      opt.addEventListener('click', () => {
        const params = new URLSearchParams(window.location.search);
        const { param, value } = opt.dataset;
        if (value === 'All' || value === '') params.delete(param);
        else params.set(param, value);
        params.delete('page');
        window.location.href = window.location.pathname + '?' + params.toString();
      });
    });
  };

  // ── Sort select ────────────────────────────────────────────
  const initSortSelect = () => {
    const sel = $('#sort-select');
    if (!sel) return;
    sel.addEventListener('change', () => {
      const params = new URLSearchParams(window.location.search);
      params.set('sort', sel.value);
      params.delete('page');
      window.location.href = window.location.pathname + '?' + params.toString();
    });
  };

  // ── Init all ───────────────────────────────────────────────
  ready(() => {
    initHeader();
    initSearch();
    initMobileNav();
    initWishlist();
    initReveal();
    initCardLinks();
    initGallery();
    initFilterChips();
    initSidebarOptions();
    initSortSelect();
  });

})();
