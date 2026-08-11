/* ================================================================
   MAGTECH — GSAP ANIMATIONS
   Premium, purposeful motion
   ================================================================ */

(function () {
  'use strict';

  // Wait for GSAP to load
  const waitForGSAP = (cb, attempts = 0) => {
    if (window.gsap && window.ScrollTrigger) {
      cb();
    } else if (attempts < 30) {
      setTimeout(() => waitForGSAP(cb, attempts + 1), 100);
    }
  };

  waitForGSAP(() => {
    const { gsap, ScrollTrigger } = window;
    gsap.registerPlugin(ScrollTrigger);

    // ── Hero entrance ──────────────────────────────────────
    const heroContent = document.querySelector('.hero__content');
    const heroVisual  = document.querySelector('.hero__visual');

    if (heroContent) {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      tl.from('.hero__eyebrow', { opacity: 0, y: 20, duration: 0.6 })
        .from('.hero__title',   { opacity: 0, y: 40, duration: 0.8 }, '-=0.3')
        .from('.hero__subtitle',{ opacity: 0, y: 30, duration: 0.6 }, '-=0.5')
        .from('.hero__cta-group .btn', {
          opacity: 0, y: 20, duration: 0.5,
          stagger: 0.15
        }, '-=0.4')
        .from('.hero__stats > *', {
          opacity: 0, y: 20, duration: 0.5,
          stagger: 0.12
        }, '-=0.3');
    }

    if (heroVisual) {
      gsap.from(heroVisual, {
        opacity: 0,
        x: 60,
        duration: 1.1,
        ease: 'power3.out',
        delay: 0.2,
      });

      // Floating chips
      gsap.from('.hero__chip', {
        opacity: 0,
        scale: 0.85,
        duration: 0.5,
        stagger: 0.15,
        ease: 'back.out(1.4)',
        delay: 0.7,
      });
    }

    // ── Scroll-triggered section reveals ───────────────────
    const revealElements = document.querySelectorAll(
      '.trust-item, .category-card, .product-card, [data-reveal]'
    );

    revealElements.forEach((el, i) => {
      gsap.from(el, {
        scrollTrigger: {
          trigger: el,
          start: 'top 88%',
          toggleActions: 'play none none none',
        },
        opacity: 0,
        y: 30,
        duration: 0.65,
        delay: (i % 6) * 0.07,
        ease: 'power2.out',
      });
    });

    // ── Section headings ───────────────────────────────────
    document.querySelectorAll('.section-heading').forEach((el) => {
      gsap.from(el, {
        scrollTrigger: { trigger: el, start: 'top 85%' },
        opacity: 0,
        y: 24,
        duration: 0.7,
        ease: 'power2.out',
      });
    });

    // ── Product card hover (GSAP supplements CSS) ──────────
    document.querySelectorAll('.product-card').forEach((card) => {
      const img = card.querySelector('.product-card__image img');

      card.addEventListener('mouseenter', () => {
        if (img) gsap.to(img, { scale: 1.06, duration: 0.5, ease: 'power2.out' });
        gsap.to(card, { y: -5, duration: 0.4, ease: 'power2.out' });
      });

      card.addEventListener('mouseleave', () => {
        if (img) gsap.to(img, { scale: 1, duration: 0.5, ease: 'power2.out' });
        gsap.to(card, { y: 0, duration: 0.4, ease: 'power2.out' });
      });
    });

    // ── Category cards ─────────────────────────────────────
    document.querySelectorAll('.category-card').forEach((card) => {
      card.addEventListener('mouseenter', () => {
        gsap.to(card, { y: -4, duration: 0.3, ease: 'power2.out' });
      });
      card.addEventListener('mouseleave', () => {
        gsap.to(card, { y: 0, duration: 0.3, ease: 'power2.out' });
      });
    });

    // ── Editorial block image parallax ─────────────────────
    document.querySelectorAll('.editorial-block__image').forEach((block) => {
      const img = block.querySelector('img');
      if (!img) return;

      gsap.to(img, {
        scrollTrigger: {
          trigger: block,
          start: 'top bottom',
          end: 'bottom top',
          scrub: true,
        },
        scale: 1.1,
        yPercent: -8,
        ease: 'none',
      });
    });

    // ── Promo band ─────────────────────────────────────────
    const promoBand = document.querySelector('.promo-band');
    if (promoBand) {
      gsap.from(promoBand, {
        scrollTrigger: { trigger: promoBand, start: 'top 85%' },
        opacity: 0,
        y: 40,
        duration: 0.8,
        ease: 'power2.out',
      });
    }

    // ── Stats counter ──────────────────────────────────────
    document.querySelectorAll('.hero__stat-num[data-count]').forEach((el) => {
      const target = parseFloat(el.dataset.count);
      gsap.from({ val: 0 }, {
        val: target,
        duration: 1.8,
        ease: 'power2.out',
        delay: 1,
        onUpdate: function () {
          el.textContent = Math.round(this.targets()[0].val) + (el.dataset.suffix || '');
        }
      });
    });

    // ── Hero image floating animation ──────────────────────
    const heroImageWrap = document.querySelector('.hero__image-wrap');
    if (heroImageWrap) {
      gsap.to(heroImageWrap, {
        y: -10,
        duration: 3.5,
        ease: 'sine.inOut',
        yoyo: true,
        repeat: -1,
      });
    }

    // ── Gallery main on product page ───────────────────────
    const galleryMain = document.querySelector('.gallery-main');
    if (galleryMain) {
      gsap.from(galleryMain, {
        opacity: 0,
        scale: 0.97,
        duration: 0.7,
        ease: 'power2.out',
      });

      gsap.from('.product-info__title', {
        opacity: 0, x: 20, duration: 0.7, ease: 'power2.out', delay: 0.2,
      });

      gsap.from('.product-info__price-block', {
        opacity: 0, y: 20, duration: 0.6, ease: 'power2.out', delay: 0.3,
      });

      gsap.from('.contact-actions .btn', {
        opacity: 0, y: 16, stagger: 0.12, duration: 0.5, ease: 'power2.out', delay: 0.4,
      });
    }

  });

})();
