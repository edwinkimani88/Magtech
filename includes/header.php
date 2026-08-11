<?php
$pageTitle       = $pageTitle ?? 'MagTech Investments — Good Finds. Good Prices.';
$pageDescription = $pageDescription ?? 'Nairobi\'s premium second-hand electronics marketplace. Quality phones, laptops, TVs, fridges and more from Shop 1 (Westlands) and Shop 2 (CBD).';
$pageUrl         = $pageUrl ?? (isset($_SERVER['HTTPS']) ? 'https' : 'http') . '://' . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];
$pageImage       = $pageImage ?? APP_URL . '/assets/img/hero.png';
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">

  <title><?= htmlspecialchars($pageTitle) ?></title>
  <meta name="description" content="<?= htmlspecialchars($pageDescription) ?>">
  <meta name="robots" content="index, follow">

  <!-- Open Graph -->
  <meta property="og:type"        content="website">
  <meta property="og:title"       content="<?= htmlspecialchars($pageTitle) ?>">
  <meta property="og:description" content="<?= htmlspecialchars($pageDescription) ?>">
  <meta property="og:url"         content="<?= htmlspecialchars($pageUrl) ?>">
  <meta property="og:image"       content="<?= htmlspecialchars($pageImage) ?>">
  <meta property="og:site_name"   content="MagTech Investments">

  <!-- Twitter Card -->
  <meta name="twitter:card"        content="summary_large_image">
  <meta name="twitter:title"       content="<?= htmlspecialchars($pageTitle) ?>">
  <meta name="twitter:description" content="<?= htmlspecialchars($pageDescription) ?>">
  <meta name="twitter:image"       content="<?= htmlspecialchars($pageImage) ?>">

  <!-- Canonical -->
  <link rel="canonical" href="<?= htmlspecialchars($pageUrl) ?>">

  <!-- Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Fraunces:ital,opsz,wght@0,9..144,300;0,9..144,700;1,9..144,400&display=swap" rel="stylesheet">

  <!-- CSS -->
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/main.css">
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/components.css">
  <link rel="stylesheet" href="<?= APP_URL ?>/assets/css/responsive.css">

  <!-- Favicon (inline SVG data URI) -->
  <link rel="icon" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' rx='20' fill='%2306403F'/><text y='.9em' font-size='70' x='10'>⚡</text></svg>">

  <!-- GSAP CDN -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js" defer></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js" defer></script>

  <?= $headExtra ?? '' ?>
</head>
<body class="<?= $bodyClass ?? '' ?>">

<!-- ── HEADER ───────────────────────────────────────────────── -->
<header class="site-header" id="site-header">
  <div class="header-inner container">

    <!-- Logo -->
    <a href="<?= APP_URL ?>/" class="logo" aria-label="MagTech Investments Home">
      <span class="logo__mark">M</span>
      <span class="logo__text">MagTech<span class="logo__sub">Investments</span></span>
    </a>

    <!-- Desktop Nav -->
    <nav class="nav-primary" id="nav-primary" aria-label="Primary navigation">
      <ul class="nav-list">
        <li><a href="<?= APP_URL ?>/"          class="nav-link <?= ($activePage ?? '') === 'home'     ? 'is-active' : '' ?>">Home</a></li>
        <li class="has-dropdown">
          <a href="<?= APP_URL ?>/shop"        class="nav-link <?= ($activePage ?? '') === 'shop'     ? 'is-active' : '' ?>">Shop</a>
          <div class="nav-dropdown">
            <div class="dropdown-grid">
              <?php foreach (CATEGORIES as $cat): ?>
              <a href="<?= APP_URL ?>/shop?category=<?= urlencode($cat) ?>" class="dropdown-item">
                <span class="dropdown-icon"><?= categoryIcon($cat) ?></span>
                <span><?= htmlspecialchars($cat) ?></span>
              </a>
              <?php endforeach; ?>
            </div>
          </div>
        </li>
        <li><a href="<?= APP_URL ?>/shop?sort=newest"  class="nav-link">New Arrivals</a></li>
        <li><a href="<?= APP_URL ?>/shop?sort=price_asc" class="nav-link">Deals</a></li>
        <li><a href="#about" class="nav-link">About</a></li>
      </ul>
    </nav>

    <!-- Header Actions -->
    <div class="header-actions">
      <!-- Search toggle -->
      <button class="icon-btn" id="search-toggle" aria-label="Search" aria-expanded="false">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
        </svg>
      </button>

      <!-- Wishlist -->
      <button class="icon-btn" id="wishlist-btn" aria-label="Saved items">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
        </svg>
        <span class="icon-btn__badge" id="wishlist-count" style="display:none">0</span>
      </button>

      <!-- WhatsApp quick contact -->
      <a href="https://wa.me/254712345678" class="btn btn--primary btn--sm" target="_blank" rel="noopener">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 0 1-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 0 1-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 0 1 2.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0 0 12.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 0 0 5.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 0 0-3.48-8.413z"/></svg>
        Chat
      </a>

      <!-- Mobile hamburger -->
      <button class="hamburger" id="hamburger" aria-label="Open menu" aria-expanded="false">
        <span></span><span></span><span></span>
      </button>
    </div>
  </div>

  <!-- Search Overlay -->
  <div class="search-overlay" id="search-overlay" role="search" aria-hidden="true">
    <div class="search-overlay__inner">
      <form class="search-form" action="<?= APP_URL ?>/shop" method="GET">
        <input type="text" name="search" id="search-input" class="search-input"
               placeholder="Tafuta — phones, laptops, TVs, fridges…"
               autocomplete="off" aria-label="Search products">
        <button type="submit" class="search-submit" aria-label="Submit search">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/>
          </svg>
        </button>
        <button type="button" class="search-close" id="search-close" aria-label="Close search">✕</button>
      </form>
      <div class="search-hints">
        <span class="hint-label">Quick searches:</span>
        <a href="<?= APP_URL ?>/shop?category=Phones"   class="hint-tag">Phones</a>
        <a href="<?= APP_URL ?>/shop?category=Laptops"  class="hint-tag">Laptops</a>
        <a href="<?= APP_URL ?>/shop?category=TVs+%26+Audio" class="hint-tag">TVs</a>
        <a href="<?= APP_URL ?>/shop?search=iphone"     class="hint-tag">iPhone</a>
        <a href="<?= APP_URL ?>/shop?search=samsung"    class="hint-tag">Samsung</a>
        <a href="<?= APP_URL ?>/shop?sort=price_asc"    class="hint-tag">Best deals</a>
      </div>
    </div>
  </div>

  <!-- Mobile Nav Drawer -->
  <div class="mobile-nav" id="mobile-nav" aria-hidden="true">
    <div class="mobile-nav__inner">
      <div class="mobile-nav__header">
        <span class="logo"><span class="logo__mark">M</span><span class="logo__text">MagTech</span></span>
        <button class="mobile-nav__close" id="mobile-nav-close" aria-label="Close menu">✕</button>
      </div>
      <nav>
        <ul class="mobile-nav__list">
          <li><a href="<?= APP_URL ?>/">Home</a></li>
          <li><a href="<?= APP_URL ?>/shop">All Products</a></li>
          <li class="mobile-nav__divider">Categories</li>
          <?php foreach (CATEGORIES as $cat): ?>
          <li><a href="<?= APP_URL ?>/shop?category=<?= urlencode($cat) ?>"><?= categoryIcon($cat) ?> <?= htmlspecialchars($cat) ?></a></li>
          <?php endforeach; ?>
          <li class="mobile-nav__divider">Branches</li>
          <li><a href="<?= APP_URL ?>/shop?shop_location=Shop+1">📍 Shop 1 — Westlands</a></li>
          <li><a href="<?= APP_URL ?>/shop?shop_location=Shop+2">📍 Shop 2 — CBD</a></li>
        </ul>
      </nav>
      <div class="mobile-nav__footer">
        <a href="https://wa.me/254712345678" class="btn btn--primary btn--full" target="_blank">
          💬 WhatsApp us
        </a>
      </div>
    </div>
  </div>
  <div class="mobile-nav__backdrop" id="mobile-nav-backdrop"></div>
</header>

<!-- Push header height -->
<div class="header-spacer"></div>
