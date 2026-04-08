const revealItems = document.querySelectorAll(".reveal");
const counterItems = document.querySelectorAll(".counter");
const parallaxItems = document.querySelectorAll(".parallax");
const themeSections = document.querySelectorAll(".scroll-theme");
const cursorGlow = document.querySelector(".cursor-glow");
const testimonialTrack = document.querySelector(".testimonial-track");
const testimonialCards = document.querySelectorAll(".testimonial-track .testimonial-card");
const carouselDots = document.querySelectorAll(".carousel-dot");
const carouselButtons = document.querySelectorAll(".carousel-btn");
const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: no-preference)").matches;

const revealObserver = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("is-visible");
        revealObserver.unobserve(entry.target);
      }
    });
  },
  {
    threshold: 0.18,
    rootMargin: "0px 0px -40px 0px",
  }
);

revealItems.forEach((item) => revealObserver.observe(item));

const heroVisual = document.querySelector(".hero-visual");

if (heroVisual && prefersReducedMotion) {
  heroVisual.addEventListener("mousemove", (event) => {
    const rect = heroVisual.getBoundingClientRect();
    const x = (event.clientX - rect.left) / rect.width - 0.5;
    const y = (event.clientY - rect.top) / rect.height - 0.5;

    heroVisual.style.setProperty("--tilt-x", `${(-y * 8).toFixed(2)}deg`);
    heroVisual.style.setProperty("--tilt-y", `${(x * 10).toFixed(2)}deg`);
  });

  heroVisual.addEventListener("mouseleave", () => {
    heroVisual.style.setProperty("--tilt-x", "0deg");
    heroVisual.style.setProperty("--tilt-y", "0deg");
  });
}

const formatCounterValue = (value, decimals, prefix, suffix) => {
  const fixedValue = decimals > 0 ? value.toFixed(decimals) : Math.round(value).toString();
  return `${prefix}${fixedValue}${suffix}`;
};

const animateCounter = (counter) => {
  const target = Number(counter.dataset.target || 0);
  const decimals = Number(counter.dataset.decimals || 0);
  const prefix = counter.dataset.prefix || "";
  const suffix = counter.dataset.suffix || "";
  const duration = 1600;
  const startTime = performance.now();

  const tick = (currentTime) => {
    const progress = Math.min((currentTime - startTime) / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3);
    const currentValue = target * eased;

    counter.textContent = formatCounterValue(currentValue, decimals, prefix, suffix);

    if (progress < 1) {
      requestAnimationFrame(tick);
    } else {
      counter.textContent = formatCounterValue(target, decimals, prefix, suffix);
    }
  };

  requestAnimationFrame(tick);
};

const counterObserver = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        animateCounter(entry.target);
        counterObserver.unobserve(entry.target);
      }
    });
  },
  {
    threshold: 0.45,
  }
);

counterItems.forEach((counter) => counterObserver.observe(counter));

if (prefersReducedMotion) {
  const applyParallax = () => {
    const scrollY = window.scrollY;

    parallaxItems.forEach((item) => {
      const speed = Number(item.dataset.parallaxSpeed || 0);
      item.style.setProperty("--parallax-y", `${scrollY * speed}px`);
    });
  };

  applyParallax();
  window.addEventListener("scroll", applyParallax, { passive: true });
}

const setTheme = (startColor, endColor) => {
  document.body.style.setProperty("--bg-a", startColor);
  document.body.style.setProperty("--bg-b", endColor);
};

const themeObserver = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        setTheme(entry.target.dataset.themeStart || "#fffaf5", entry.target.dataset.themeEnd || "#f5ede3");
      }
    });
  },
  {
    threshold: 0.45,
  }
);

themeSections.forEach((section) => themeObserver.observe(section));

if (cursorGlow && prefersReducedMotion && window.innerWidth > 960) {
  window.addEventListener("mousemove", (event) => {
    cursorGlow.style.left = `${event.clientX}px`;
    cursorGlow.style.top = `${event.clientY}px`;
    document.body.classList.add("cursor-active");
  });

  window.addEventListener("mouseleave", () => {
    document.body.classList.remove("cursor-active");
  });
}

let carouselIndex = 0;

const getVisibleSlides = () => {
  return 1;
};

const updateCarousel = (index) => {
  if (!testimonialTrack || !testimonialCards.length) {
    return;
  }

  const visibleSlides = getVisibleSlides();
  const maxIndex = Math.max(0, testimonialCards.length - visibleSlides);
  carouselIndex = Math.max(0, Math.min(index, maxIndex));

  const cardWidth = testimonialCards[0].getBoundingClientRect().width;
  const trackStyles = window.getComputedStyle(testimonialTrack);
  const gap = Number.parseFloat(trackStyles.columnGap || trackStyles.gap || 0);
  const offset = carouselIndex * (cardWidth + gap);

  testimonialTrack.style.transform = `translate3d(-${offset}px, 0, 0)`;

  testimonialCards.forEach((card, cardIndex) => {
    const isActive = cardIndex >= carouselIndex && cardIndex < carouselIndex + visibleSlides;
    card.classList.toggle("is-active", isActive);
  });

  carouselDots.forEach((dot, dotIndex) => {
    dot.classList.toggle("is-active", dotIndex === carouselIndex);
  });
};

carouselDots.forEach((dot) => {
  dot.addEventListener("click", () => {
    updateCarousel(Number(dot.dataset.carouselIndex || 0));
  });
});

carouselButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const direction = button.dataset.carouselDir === "prev" ? -1 : 1;
    updateCarousel(carouselIndex + direction);
  });
});

let carouselTimer;

const startCarousel = () => {
  if (!testimonialTrack || !prefersReducedMotion) {
    return;
  }

  clearInterval(carouselTimer);
  carouselTimer = window.setInterval(() => {
    const visibleSlides = getVisibleSlides();
    const maxIndex = Math.max(0, testimonialCards.length - visibleSlides);
    const nextIndex = carouselIndex >= maxIndex ? 0 : carouselIndex + 1;
    updateCarousel(nextIndex);
  }, 3200);
};

if (testimonialTrack) {
  updateCarousel(0);
  startCarousel();
  window.addEventListener("resize", () => updateCarousel(carouselIndex));
  testimonialTrack.addEventListener("mouseenter", () => clearInterval(carouselTimer));
  testimonialTrack.addEventListener("mouseleave", startCarousel);
}

window.addEventListener("load", () => {
  document.body.classList.remove("is-loading");
});
