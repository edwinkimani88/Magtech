<!-- ── FOOTER ────────────────────────────────────────────────── -->
<footer class="site-footer" id="about">
  <div class="footer-inner container">

    <div class="footer-brand">
      <a href="<?= APP_URL ?>/" class="logo logo--light">
        <span class="logo__mark">M</span>
        <span class="logo__text">MagTech<span class="logo__sub">Investments</span></span>
      </a>
      <p class="footer-tagline">Good Finds. Good Prices.<br>Kitengela's trusted electronics marketplace.</p>
      <div class="footer-socials">
        <a href="https://wa.me/254712345678" class="social-link" aria-label="WhatsApp" target="_blank">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 0 1-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 0 1-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 0 1 2.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0 0 12.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 0 0 5.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 0 0-3.48-8.413z"/></svg>
        </a>
        <a href="https://instagram.com/magtech_investments" class="social-link" aria-label="Instagram" target="_blank">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"/><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"/></svg>
        </a>
        <a href="https://facebook.com/magtech.investments" class="social-link" aria-label="Facebook" target="_blank">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/></svg>
        </a>
      </div>
    </div>

    <div class="footer-col">
      <h4 class="footer-heading">Marketplace</h4>
      <ul class="footer-links">
        <li><a href="<?= APP_URL ?>/shop">All Products</a></li>
        <li><a href="<?= APP_URL ?>/shop?sort=newest">New Arrivals</a></li>
        <li><a href="<?= APP_URL ?>/shop?sort=price_asc">Best Deals</a></li>
        <li><a href="<?= APP_URL ?>/shop?shop_location=Shop+1">Chairman Branch Items</a></li>
        <li><a href="<?= APP_URL ?>/shop?shop_location=Shop+2">Deliverance Rd Items</a></li>
        <li><a href="<?= APP_URL ?>/app" style="color:var(--teal-400);font-weight:600">⬇️ Download App</a></li>
      </ul>
    </div>

    <div class="footer-col">
      <h4 class="footer-heading">Categories</h4>
      <ul class="footer-links">
        <?php $footerCats = ['Phones','Laptops','TVs & Audio','Gaming','Fridges & Appliances']; ?>
        <?php foreach ($footerCats as $fc): ?>
        <li><a href="<?= APP_URL ?>/shop?category=<?= urlencode($fc) ?>"><?= htmlspecialchars($fc) ?></a></li>
        <?php endforeach; ?>
        <li><a href="<?= APP_URL ?>/shop">View all →</a></li>
      </ul>
    </div>

    <div class="footer-col">
      <h4 class="footer-heading">Our Branches</h4>
      <?php foreach (SHOPS as $key => $shop): ?>
      <div class="footer-branch">
        <strong><?= htmlspecialchars($shop['name']) ?></strong>
        <span><?= htmlspecialchars($shop['location']) ?></span>
        <a href="tel:<?= htmlspecialchars($shop['phone']) ?>"><?= htmlspecialchars($shop['phone']) ?></a>
        <span class="footer-hours"><?= htmlspecialchars($shop['hours']) ?></span>
      </div>
      <?php endforeach; ?>
    </div>

  </div>

  <div class="footer-bottom">
    <div class="container footer-bottom__inner">
      <p>&copy; <?= date('Y') ?> MagTech Investments. All rights reserved.</p>
      <div class="footer-legal">
        <a href="#">Privacy Policy</a>
        <a href="#">Terms of Use</a>
        <a href="<?= APP_URL ?>/app" class="footer-app-link">Admin App</a>
      </div>
    </div>
  </div>
</footer>

<!-- Wishlist Toast -->
<div class="toast" id="wishlist-toast" role="alert" aria-live="polite"></div>

<!-- Scripts -->
<script>
  const MAGTECH_URL = '<?= APP_URL ?>';
</script>
<script src="<?= APP_URL ?>/assets/js/main.js" defer></script>
<script src="<?= APP_URL ?>/assets/js/animations.js" defer></script>
<?= $footerExtra ?? '' ?>
</body>
</html>
