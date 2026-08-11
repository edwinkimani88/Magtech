<?php
require_once __DIR__ . '/config/config.php';
require_once __DIR__ . '/includes/functions.php';

$pageTitle       = 'MagTech Investments — Good Finds. Good Prices. | Nairobi Electronics';
$pageDescription = 'Browse quality second-hand phones, laptops, TVs, fridges & more at MagTech Investments. Two branches in Kitengela — Chairman Road & Deliverance Road. Real prices, real inventory.';
$activePage      = 'home';

// Fetch data from DB
$featuredItems  = getFeaturedItems(8);
$phones         = getItemsByCategory('Phones', 4);
$laptops        = getItemsByCategory('Laptops', 4);

require_once __DIR__ . '/includes/header.php';
?>

<!-- ═══════════════════════════════════════════════════════════
     HERO
════════════════════════════════════════════════════════════════ -->
<section class="hero" aria-label="Hero section">
  <div class="hero__inner container">

    <!-- Content -->
    <div class="hero__content">
      <div class="hero__eyebrow">
        <span>🏪</span> Two branches · Kitengela
      </div>

      <h1 class="hero__title display-title">
        Good Finds.<br>
        <em>Good Prices.</em>
      </h1>

      <p class="hero__subtitle">
        Quality second-hand electronics, straight from MagTech's shelves.
        Phones, laptops, TVs, fridges — verified, priced right.
        Uko sorted, boss.
      </p>

      <div class="hero__cta-group">
        <a href="<?= APP_URL ?>/shop" class="btn btn--light btn--lg">
          Browse All Finds →
        </a>
        <a href="https://wa.me/254712345678" class="btn btn--outline btn--lg" target="_blank"
           style="border-color:rgba(255,255,255,.3);color:rgba(255,255,255,.85);">
          💬 Ask on WhatsApp
        </a>
      </div>

      <div class="hero__stats">
        <div>
          <div class="hero__stat-num" data-count="200" data-suffix="+">200+</div>
          <div class="hero__stat-label">Items in stock</div>
        </div>
        <div>
          <div class="hero__stat-num">2</div>
          <div class="hero__stat-label">Kitengela branches</div>
        </div>
        <div>
          <div class="hero__stat-num">5★</div>
          <div class="hero__stat-label">Verified quality</div>
        </div>
      </div>
    </div>

    <!-- Visual -->
    <div class="hero__visual">
      <div class="hero__image-wrap">
        <img src="<?= APP_URL ?>/assets/img/hero.png"
             alt="MagTech premium electronics selection"
             width="800" height="600"
             loading="eager">
      </div>

      <!-- Floating chips -->
      <div class="hero__chip hero__chip--tl">
        <div class="hero__chip-icon">📍</div>
        <div>
          <div style="font-size:11px;font-weight:700">Shop 1</div>
          <div style="font-size:10px;color:#666">Chairman, Kitengela</div>
        </div>
      </div>

      <div class="hero__chip hero__chip--br">
        <div class="hero__chip-icon">✅</div>
        <div>
          <div style="font-size:11px;font-weight:700">Tested & Verified</div>
          <div style="font-size:10px;color:#666">Every item checked</div>
        </div>
      </div>
    </div>

  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     TRUST BAR
════════════════════════════════════════════════════════════════ -->
<div class="trust-bar">
  <div class="trust-bar__inner container">
    <div class="trust-item">
      <span class="trust-item__icon">🔍</span>
      <div>
        <div class="trust-item__title">Tested & Verified</div>
        <div class="trust-item__desc">Every item inspected before listing</div>
      </div>
    </div>
    <div class="trust-item">
      <span class="trust-item__icon">💬</span>
      <div>
        <div class="trust-item__title">WhatsApp First</div>
        <div class="trust-item__desc">Confirm before you come to the shop</div>
      </div>
    </div>
    <div class="trust-item">
      <span class="trust-item__icon">📍</span>
      <div>
        <div class="trust-item__title">2 Kitengela Branches</div>
        <div class="trust-item__desc">Chairman & Deliverance Rd — walk in anytime</div>
      </div>
    </div>
    <div class="trust-item">
      <span class="trust-item__icon">🏷️</span>
      <div>
        <div class="trust-item__title">Honest Pricing</div>
        <div class="trust-item__desc">Real market price, no hidden fees</div>
      </div>
    </div>
  </div>
</div>

<!-- ═══════════════════════════════════════════════════════════
     CATEGORIES
════════════════════════════════════════════════════════════════ -->
<section class="section section--muted" aria-label="Browse categories">
  <div class="container">
    <div class="section-header section-header--row">
      <div>
        <span class="section-label">Shop by Type</span>
        <h2 class="section-heading">What are you looking for?</h2>
      </div>
      <a href="<?= APP_URL ?>/shop" class="btn btn--outline">View all →</a>
    </div>

    <div class="categories-grid" data-reveal>
      <a href="<?= APP_URL ?>/shop?category=Phones" class="category-card category-card--featured">
        <span class="category-card__icon">📱</span>
        <span class="category-card__name" style="font-size:1.4rem">Phones</span>
        <span class="category-card__count">iPhones, Samsung, Tecno & more</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Laptops" class="category-card">
        <span class="category-card__icon">💻</span>
        <span class="category-card__name">Laptops</span>
        <span class="category-card__count">HP, Dell, Lenovo</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=TVs+%26+Audio" class="category-card">
        <span class="category-card__icon">📺</span>
        <span class="category-card__name">TVs & Audio</span>
        <span class="category-card__count">Smart TVs, home theatre</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Gaming" class="category-card">
        <span class="category-card__icon">🎮</span>
        <span class="category-card__name">Gaming</span>
        <span class="category-card__count">PS5, Xbox, controllers</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Fridges+%26+Appliances" class="category-card">
        <span class="category-card__icon">❄️</span>
        <span class="category-card__name">Fridges</span>
        <span class="category-card__count">Double door, mini fridges</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Home+Appliances" class="category-card">
        <span class="category-card__icon">🏠</span>
        <span class="category-card__name">Appliances</span>
        <span class="category-card__count">Microwaves, irons, blenders</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Accessories" class="category-card">
        <span class="category-card__icon">🎧</span>
        <span class="category-card__name">Accessories</span>
        <span class="category-card__count">Earphones, chargers, cables</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Other+Electronics" class="category-card">
        <span class="category-card__icon">⚡</span>
        <span class="category-card__name">Other Electronics</span>
        <span class="category-card__count">Cameras, printers & more</span>
      </a>
      <a href="<?= APP_URL ?>/shop?category=Cookers" class="category-card">
        <span class="category-card__icon">🍳</span>
        <span class="category-card__name">Cookers</span>
        <span class="category-card__count">Gas, electric, induction</span>
      </a>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     FEATURED PRODUCTS
════════════════════════════════════════════════════════════════ -->
<section class="section" aria-label="Featured products">
  <div class="container">
    <div class="section-header section-header--row">
      <div>
        <span class="section-label">Just Listed</span>
        <h2 class="section-heading">Fresh Finds</h2>
        <p class="section-subheading" style="margin-top:.75rem">
          Check hii — real inventory, updated from our shops in real time.
        </p>
      </div>
      <a href="<?= APP_URL ?>/shop" class="btn btn--outline">See all products →</a>
    </div>

    <?php if (!empty($featuredItems)): ?>
    <div class="product-grid">
      <?php foreach ($featuredItems as $item):
        $photo = primaryPhoto($item);
        $shopNum = str_contains($item['shop_location'], '2') ? '2' : '1';
        $badgeClass = "badge--shop{$shopNum}";
        $condClass  = conditionClass($item['condition_grade']);
        $url = APP_URL . '/product?id=' . $item['remote_item_id'];
        $wl  = whatsappLink($item);
      ?>
      <article class="product-card <?= $condClass ?>"
               data-url="<?= htmlspecialchars($url) ?>"
               itemscope itemtype="https://schema.org/Product">

        <div class="product-card__image">
          <img src="<?= htmlspecialchars($photo) ?>"
               alt="<?= htmlspecialchars($item['item_name']) ?>"
               loading="lazy"
               itemprop="image">

          <div class="product-card__badge-row">
            <span class="badge <?= $badgeClass ?>">
              <?= htmlspecialchars($item['shop_branch_name'] ?? $item['shop_location']) ?>
            </span>
            <button class="product-card__save"
                    data-save-btn
                    data-id="<?= $item['remote_item_id'] ?>"
                    data-name="<?= htmlspecialchars($item['item_name']) ?>"
                    aria-label="Save to wishlist">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="product-card__body">
          <div class="product-card__meta">
            <span class="badge badge--condition"><?= htmlspecialchars($item['condition_grade']) ?></span>
            <?php if ($item['brand']): ?>
            <span style="font-size:11px;color:var(--text-muted);font-weight:500"><?= htmlspecialchars($item['brand']) ?></span>
            <?php endif; ?>
          </div>

          <h3 class="product-card__name" itemprop="name">
            <?= htmlspecialchars($item['item_name']) ?>
          </h3>

          <div class="product-card__price-row">
            <span class="product-card__price" itemprop="price">
              <?= formatPrice($item['marketplace_price']) ?>
            </span>
            <?php if ($item['estimated_market_value'] && $item['estimated_market_value'] > $item['marketplace_price']): ?>
            <span class="product-card__old-price">
              <?= formatPrice($item['estimated_market_value']) ?>
            </span>
            <?php endif; ?>
          </div>

          <div class="product-card__actions">
            <a href="<?= htmlspecialchars($wl) ?>" target="_blank" class="btn btn--primary btn--sm">
              WhatsApp
            </a>
            <a href="<?= htmlspecialchars($url) ?>" class="btn btn--outline btn--sm">
              Details
            </a>
          </div>
        </div>
      </article>
      <?php endforeach; ?>
    </div>

    <?php else: ?>
    <!-- No items yet — show placeholder grid -->
    <div class="product-grid">
      <?php for ($i = 0; $i < 8; $i++): ?>
      <div class="product-card" style="pointer-events:none">
        <div class="product-card__image skeleton" style="aspect-ratio:1;border-radius:0"></div>
        <div class="product-card__body">
          <div class="skeleton" style="height:12px;width:50%;margin-bottom:8px;border-radius:6px"></div>
          <div class="skeleton" style="height:16px;width:80%;margin-bottom:12px;border-radius:6px"></div>
          <div class="skeleton" style="height:20px;width:40%;border-radius:6px"></div>
        </div>
      </div>
      <?php endfor; ?>
    </div>
    <p style="text-align:center;margin-top:2rem;color:var(--text-secondary)">
      Products will appear here once synced from the MagTech app.
      <a href="<?= APP_URL ?>/config/schema.sql" style="color:var(--teal-600)">Setup DB first →</a>
    </p>
    <?php endif; ?>

    <div style="text-align:center;margin-top:3rem">
      <a href="<?= APP_URL ?>/shop" class="btn btn--primary btn--lg">
        Browse All Products →
      </a>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     EDITORIAL: PHONES SPOTLIGHT
════════════════════════════════════════════════════════════════ -->
<section class="section section--muted" aria-label="Phones">
  <div class="container">
    <div class="editorial-block" data-reveal>
      <div class="editorial-block__image">
        <img src="<?= APP_URL ?>/assets/img/product-phone.png"
             alt="Premium phones at MagTech"
             loading="lazy">
      </div>
      <div class="editorial-block__content">
        <span class="section-label editorial-block__label">Phones</span>
        <h2 class="editorial-block__title display-title">
          The phone you want.<br>
          Bei iko poa.
        </h2>
        <p class="editorial-block__body">
          From flagship iPhones to Samsung Galaxy — our phone inventory rotates constantly.
          Every handset is IMEI checked, network confirmed, and described honestly.
          No sugarcoating. Hii ndio itakusaidia.
        </p>
        <div style="display:flex;gap:1rem;flex-wrap:wrap">
          <a href="<?= APP_URL ?>/shop?category=Phones" class="btn btn--primary">Browse Phones →</a>
          <a href="https://wa.me/254712345678?text=Natafuta+phone" class="btn btn--ghost" target="_blank">Ask on WhatsApp</a>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- PHONES GRID -->
<?php if (!empty($phones)): ?>
<section class="section section--tight" aria-label="Phone listings">
  <div class="container">
    <div class="section-header section-header--row">
      <h3 class="section-heading" style="font-size:var(--text-2xl)">Phones In Stock</h3>
      <a href="<?= APP_URL ?>/shop?category=Phones" class="btn btn--outline btn--sm">See all</a>
    </div>
    <div class="product-grid">
      <?php foreach ($phones as $item):
        $photo = primaryPhoto($item);
        $shopNum = str_contains($item['shop_location'], '2') ? '2' : '1';
        $url = APP_URL . '/product?id=' . $item['remote_item_id'];
        $wl  = whatsappLink($item);
      ?>
      <article class="product-card <?= conditionClass($item['condition_grade']) ?>"
               data-url="<?= htmlspecialchars($url) ?>">
        <div class="product-card__image">
          <img src="<?= htmlspecialchars($photo) ?>" alt="<?= htmlspecialchars($item['item_name']) ?>" loading="lazy">
          <div class="product-card__badge-row">
            <span class="badge badge--shop<?= $shopNum ?>"><?= htmlspecialchars($item['shop_location']) ?></span>
            <button class="product-card__save" data-save-btn
                    data-id="<?= $item['remote_item_id'] ?>"
                    data-name="<?= htmlspecialchars($item['item_name']) ?>"
                    aria-label="Save">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="product-card__body">
          <div class="product-card__meta">
            <span class="badge badge--condition"><?= htmlspecialchars($item['condition_grade']) ?></span>
          </div>
          <h3 class="product-card__name"><?= htmlspecialchars($item['item_name']) ?></h3>
          <div class="product-card__price-row">
            <span class="product-card__price"><?= formatPrice($item['marketplace_price']) ?></span>
          </div>
          <div class="product-card__actions">
            <a href="<?= htmlspecialchars($wl) ?>" target="_blank" class="btn btn--primary btn--sm">WhatsApp</a>
            <a href="<?= htmlspecialchars($url) ?>" class="btn btn--outline btn--sm">Details</a>
          </div>
        </div>
      </article>
      <?php endforeach; ?>
    </div>
  </div>
</section>
<?php endif; ?>

<!-- ═══════════════════════════════════════════════════════════
     EDITORIAL: LAPTOPS
════════════════════════════════════════════════════════════════ -->
<section class="section section--muted" aria-label="Laptops">
  <div class="container">
    <div class="editorial-block editorial-block--reverse" data-reveal>
      <div class="editorial-block__image">
        <img src="<?= APP_URL ?>/assets/img/product-laptop.png"
             alt="Laptops at MagTech"
             loading="lazy">
      </div>
      <div class="editorial-block__content">
        <span class="section-label editorial-block__label">Laptops</span>
        <h2 class="editorial-block__title display-title">
          Power to work.<br>
          Priced to move.
        </h2>
        <p class="editorial-block__body">
          HP EliteBook, Dell Latitude, Lenovo ThinkPad — business-grade machines without the premium showroom price.
          Ideal for students, freelancers and office setups. Maisha inaendelea, work lazima ifanyike.
        </p>
        <div style="display:flex;gap:1rem;flex-wrap:wrap">
          <a href="<?= APP_URL ?>/shop?category=Laptops" class="btn btn--primary">Browse Laptops →</a>
          <a href="https://wa.me/254712345678?text=Natafuta+laptop" class="btn btn--ghost" target="_blank">Ask on WhatsApp</a>
        </div>
      </div>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     PROMO BAND
════════════════════════════════════════════════════════════════ -->
<section class="section section--tight" aria-label="Promotions">
  <div class="container">
    <div class="promo-band">
      <div class="promo-band__content">
        <div class="promo-band__label">Unatafuta nini, boss?</div>
        <h2 class="promo-band__title">
          Can't find it?<br>
          Message us.
        </h2>
        <p class="promo-band__sub" style="margin-top:.5rem">
          Our stock changes daily. If you don't see it, ask — sisi tutakuambia.
        </p>
      </div>
      <div style="display:flex;flex-direction:column;gap:1rem;flex-shrink:0">
        <a href="https://wa.me/254712345678" class="btn btn--light btn--lg" target="_blank">
          💬 WhatsApp Shop 1 (Chairman)
        </a>
        <a href="https://wa.me/254798765432" class="btn btn--light btn--lg" target="_blank">
          💬 WhatsApp Shop 2 (Deliverance Rd)
        </a>
      </div>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     SHOP LOCATIONS
════════════════════════════════════════════════════════════════ -->
<section class="section section--muted" aria-label="Our branches">
  <div class="container">
    <div class="section-header text-center" style="max-width:560px;margin-inline:auto;text-align:center">
      <span class="section-label">Visit Us in Kitengela</span>
      <h2 class="section-heading">Two Shops, One MagTech</h2>
      <p class="section-subheading" style="margin:1rem auto 0;text-align:center">
        Both branches are fully stocked and open six days a week in Kitengela.
        Walk in, browse, and negotiate directly with our team.
      </p>
    </div>

    <div class="shop-cards" style="margin-top:3rem" data-reveal>
      <?php foreach (SHOPS as $key => $shop):
        $num = str_contains($key, '1') ? '1' : '2';
      ?>
      <div class="shop-card shop-card--<?= $num ?>">
        <div>
          <span class="shop-card__badge"><?= htmlspecialchars($shop['name']) ?></span>
        </div>
        <h3 class="shop-card__name"><?= htmlspecialchars($shop['location']) ?></h3>
        <p class="shop-card__address">Kitengela, Kenya</p>
        <p class="shop-card__hours" style="margin-top:.5rem">⏰ <?= htmlspecialchars($shop['hours']) ?></p>

        <div class="shop-card__contacts">
          <a href="https://wa.me/<?= preg_replace('/\D/','',$shop['whatsapp']) ?>"
             class="shop-card__contact-btn btn" target="_blank">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 0 1-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 0 1-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 0 1 2.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0 0 12.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 0 0 5.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 0 0-3.48-8.413z"/></svg>
            WhatsApp this branch
          </a>
          <a href="tel:<?= htmlspecialchars($shop['phone']) ?>"
             class="shop-card__contact-btn btn">
            📞 <?= htmlspecialchars($shop['phone']) ?>
          </a>
          <a href="<?= APP_URL ?>/shop?shop_location=<?= urlencode($key) ?>"
             class="shop-card__contact-btn btn">
            🛒 Browse <?= htmlspecialchars($shop['name']) ?> inventory
          </a>
        </div>
      </div>
      <?php endforeach; ?>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════
     TVs + FRIDGES EDITORIAL
════════════════════════════════════════════════════════════════ -->
<section class="section" aria-label="Home electronics">
  <div class="container">
    <div class="section-header" style="max-width:640px">
      <span class="section-label">Home Electronics</span>
      <h2 class="section-heading">Furnish the whole crib</h2>
      <p class="section-subheading" style="margin-top:.75rem">
        Smart TVs, fridges, cookers — the home appliances section is stocked regularly.
        Quality pieces, tested, at prices that make sense.
      </p>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:1.5rem;margin-top:3rem" data-reveal>
      <!-- TV card -->
      <div style="background:var(--teal-900);border-radius:var(--radius-2xl);overflow:hidden;position:relative;min-height:300px;display:flex;flex-direction:column;justify-content:flex-end;padding:2rem">
        <img src="<?= APP_URL ?>/assets/img/product-tv.png"
             alt="Smart TVs at MagTech"
             loading="lazy"
             style="position:absolute;inset:0;width:100%;height:100%;object-fit:contain;padding:2rem;opacity:.55">
        <div style="position:relative;z-index:1">
          <p style="font-size:11px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--teal-200);margin-bottom:.5rem">Smart TVs</p>
          <h3 style="font-family:var(--font-display);font-size:var(--text-2xl);font-weight:700;color:var(--white);line-height:1.1;margin-bottom:1rem">Sony. LG. Samsung.<br>All sizes.</h3>
          <a href="<?= APP_URL ?>/shop?category=TVs+%26+Audio" class="btn btn--light btn--sm">Browse TVs →</a>
        </div>
      </div>

      <!-- Fridge card -->
      <div style="background:var(--grey-50);border:1.5px solid var(--grey-100);border-radius:var(--radius-2xl);overflow:hidden;position:relative;min-height:300px;display:flex;flex-direction:column;justify-content:flex-end;padding:2rem">
        <img src="<?= APP_URL ?>/assets/img/product-fridge.png"
             alt="Fridges at MagTech"
             loading="lazy"
             style="position:absolute;inset:0;width:100%;height:100%;object-fit:contain;padding:2rem;opacity:.7">
        <div style="position:relative;z-index:1">
          <p style="font-size:11px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--teal-600);margin-bottom:.5rem">Fridges & Appliances</p>
          <h3 style="font-family:var(--font-display);font-size:var(--text-2xl);font-weight:700;color:var(--text-primary);line-height:1.1;margin-bottom:1rem">Keep it cold,<br>keep it fresh.</h3>
          <a href="<?= APP_URL ?>/shop?category=Fridges+%26+Appliances" class="btn btn--primary btn--sm">Browse Fridges →</a>
        </div>
      </div>
    </div>
  </div>
</section>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
