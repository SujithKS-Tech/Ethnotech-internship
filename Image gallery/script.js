const places = [
  {
    id: "forest-cabin",
    title: "Forest Cabin",
    category: "forest",
    location: "Olympic Peninsula",
    mood: "Quiet",
    light: "Soft afternoon",
    description: "A quiet path where the trees hold the afternoon light.",
    image: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1600&q=85",
    alt: "Cabin and pathway surrounded by green forest"
  },
  {
    id: "blue-shore",
    title: "Blue Shore",
    category: "coast",
    location: "Pacific Edge",
    mood: "Fresh",
    light: "Clean morning",
    description: "Foam, salt air, and the clean edge of morning.",
    image: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1600&q=85",
    alt: "Turquoise ocean waves rolling onto a pale shore"
  },
  {
    id: "alpine-pass",
    title: "Alpine Pass",
    category: "mountain",
    location: "High Alps",
    mood: "Wide",
    light: "Cloud break",
    description: "Stone peaks lifting out of a valley of cloud.",
    image: "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1600&q=85",
    alt: "Snowy mountain range above a clouded valley"
  },
  {
    id: "golden-ridge",
    title: "Golden Ridge",
    category: "mountain",
    location: "Sierra Foothills",
    mood: "Warm",
    light: "Golden hour",
    description: "Last light sliding across open country.",
    image: "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1600&q=85",
    alt: "Sunlit mountain ridge and valley at golden hour"
  },
  {
    id: "still-lake",
    title: "Still Lake",
    category: "lake",
    location: "Banff",
    mood: "Calm",
    light: "Blue dawn",
    description: "A mirrored horizon before the wind wakes up.",
    image: "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?auto=format&fit=crop&w=1600&q=85",
    alt: "Calm lake reflecting mountains and sky"
  },
  {
    id: "ember-dunes",
    title: "Ember Dunes",
    category: "desert",
    location: "Namib Desert",
    mood: "Radiant",
    light: "Low sun",
    description: "Warm sand lines moving like slow water.",
    image: "https://images.unsplash.com/photo-1509316785289-025f5b846b35?auto=format&fit=crop&w=1600&q=85",
    alt: "Rolling desert dunes glowing in sunlight"
  },
  {
    id: "rainwood",
    title: "Rainwood",
    category: "forest",
    location: "Hoh Rainforest",
    mood: "Misty",
    light: "Green shade",
    description: "Moss, mist, and a green hush under old branches.",
    image: "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1600&q=85",
    alt: "Lush rainforest trail with tall trees"
  },
  {
    id: "black-sand",
    title: "Black Sand",
    category: "coast",
    location: "Vik",
    mood: "Dramatic",
    light: "Starlit tide",
    description: "A dark shoreline where white water keeps time.",
    image: "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1600&q=85",
    alt: "Dramatic coast beneath a starry mountain sky"
  },
  {
    id: "glass-fjord",
    title: "Glass Fjord",
    category: "lake",
    location: "Norway",
    mood: "Crisp",
    light: "Clear noon",
    description: "Cold water, sharp cliffs, and a sky that feels freshly washed.",
    image: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1600&q=85",
    alt: "Clear lake below mountains and a bright sky"
  },
  {
    id: "canyon-glow",
    title: "Canyon Glow",
    category: "desert",
    location: "Arizona",
    mood: "Bold",
    light: "Reflected fire",
    description: "Red stone walls catching light in narrow, glowing ribbons.",
    image: "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?auto=format&fit=crop&w=1600&q=85",
    alt: "Warm desert canyon and rocky landscape"
  },
  {
    id: "pine-falls",
    title: "Pine Falls",
    category: "forest",
    location: "Columbia Gorge",
    mood: "Alive",
    light: "Water haze",
    description: "A white ribbon of water cutting through deep evergreen shade.",
    image: "https://images.unsplash.com/photo-1433086966358-54859d0ed716?auto=format&fit=crop&w=1600&q=85",
    alt: "Waterfall surrounded by green forest"
  },
  {
    id: "snowline",
    title: "Snowline",
    category: "mountain",
    location: "Patagonia",
    mood: "Brave",
    light: "Icy dusk",
    description: "A ridge of snow pulling the eye toward the edge of the sky.",
    image: "https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?auto=format&fit=crop&w=1600&q=85",
    alt: "Snowy mountain peaks under a pale sky"
  },
  {
    id: "coral-cove",
    title: "Coral Cove",
    category: "coast",
    location: "Amalfi",
    mood: "Bright",
    light: "Late summer",
    description: "A sheltered curve of blue where cliffs meet clear water.",
    image: "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=1600&q=85",
    alt: "Blue sea along a rocky coastline"
  },
  {
    id: "mirror-pond",
    title: "Mirror Pond",
    category: "lake",
    location: "Dolomites",
    mood: "Still",
    light: "Pale sunrise",
    description: "Peaks and pine trees doubled in water like a secret kept well.",
    image: "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1600&q=85",
    alt: "Mountain lake with trees reflected in still water"
  },
  {
    id: "saffron-flats",
    title: "Saffron Flats",
    category: "desert",
    location: "Atacama",
    mood: "Electric",
    light: "Dry heat",
    description: "Open desert stretching into color, heat, and silence.",
    image: "https://images.unsplash.com/photo-1473580044384-7ba9967e16a0?auto=format&fit=crop&w=1600&q=85",
    alt: "Wide desert road and dry mountain landscape"
  },
  {
    id: "cedar-path",
    title: "Cedar Path",
    category: "forest",
    location: "Kyoto",
    mood: "Gentle",
    light: "Filtered sun",
    description: "A narrow walk through tall trunks and soft green quiet.",
    image: "https://images.unsplash.com/photo-1493246507139-91e8fad9978e?auto=format&fit=crop&w=1600&q=85",
    alt: "Sunlit forest path through tall trees"
  },
  {
    id: "aurora-crown",
    title: "Aurora Crown",
    category: "mountain",
    location: "Iceland",
    mood: "Electric",
    light: "Northern glow",
    description: "Green light bending over dark peaks and frozen air.",
    image: "https://images.unsplash.com/photo-1483347756197-71ef80e95f73?auto=format&fit=crop&w=1600&q=85",
    alt: "Northern lights above a snowy mountain landscape"
  },
  {
    id: "turquoise-lagoon",
    title: "Turquoise Lagoon",
    category: "coast",
    location: "Maldives",
    mood: "Bright",
    light: "Clear noon",
    description: "Shallow blue water glowing around a quiet island edge.",
    image: "https://images.unsplash.com/photo-1514282401047-d79a71a590e8?auto=format&fit=crop&w=1600&q=85",
    alt: "Tropical turquoise water and white sand island"
  },
  {
    id: "lotus-lagoon",
    title: "Lotus Lagoon",
    category: "lake",
    location: "Kerala",
    mood: "Gentle",
    light: "Warm haze",
    description: "Still water carrying soft reflections through humid morning.",
    image: "https://images.unsplash.com/photo-1502082553048-f009c37129b9?auto=format&fit=crop&w=1600&q=85",
    alt: "Warm green waterside landscape with trees"
  },
  {
    id: "moon-valley",
    title: "Moon Valley",
    category: "desert",
    location: "Wadi Rum",
    mood: "Bold",
    light: "Copper dusk",
    description: "Stone towers rising from sand like another planet.",
    image: "https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1600&q=85",
    alt: "Desert valley with dramatic rock formations"
  },
  {
    id: "bamboo-light",
    title: "Bamboo Light",
    category: "forest",
    location: "Arashiyama",
    mood: "Still",
    light: "Filtered green",
    description: "Tall bamboo catching slender lines of daylight.",
    image: "https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=1600&q=85",
    alt: "Tall green bamboo forest path"
  },
  {
    id: "glacier-glass",
    title: "Glacier Glass",
    category: "lake",
    location: "Alberta",
    mood: "Crisp",
    light: "Icy blue",
    description: "A lake so clear the cold seems to shine from underneath.",
    image: "https://images.unsplash.com/photo-1482192505345-5655af888cc4?auto=format&fit=crop&w=1600&q=85",
    alt: "Blue mountain lake near snowy peaks"
  },
  {
    id: "wildflower-pass",
    title: "Wildflower Pass",
    category: "mountain",
    location: "Swiss Alps",
    mood: "Alive",
    light: "High summer",
    description: "A mountain meadow stitched with color below clean peaks.",
    image: "https://images.unsplash.com/photo-1464278533981-50106e6176b1?auto=format&fit=crop&w=1600&q=85",
    alt: "Mountain meadow and peaks in summer"
  },
  {
    id: "pink-cliffs",
    title: "Pink Cliffs",
    category: "coast",
    location: "Zakynthos",
    mood: "Radiant",
    light: "Sunlit water",
    description: "Blue water pressed against pale cliffs and hidden coves.",
    image: "https://images.unsplash.com/photo-1471922694854-ff1b63b20054?auto=format&fit=crop&w=1600&q=85",
    alt: "Bright coastal cliffs and blue water"
  }
];

const elements = {
  panelTrack: document.querySelector("#panelTrack"),
  thumbStrip: document.querySelector("#thumbStrip"),
  emptyState: document.querySelector("#emptyState"),
  searchInput: document.querySelector("#searchInput"),
  sortSelect: document.querySelector("#sortSelect"),
  speedSelect: document.querySelector("#speedSelect"),
  filterButtons: Array.from(document.querySelectorAll(".filter-btn")),
  moodButtons: Array.from(document.querySelectorAll(".mood-btn")),
  paletteButtons: Array.from(document.querySelectorAll(".palette-btn")),
  viewButtons: Array.from(document.querySelectorAll(".view-btn")),
  collectionGrid: document.querySelector("#collectionGrid"),
  favoritesToggle: document.querySelector("#favoritesToggle"),
  autoplayToggle: document.querySelector("#autoplayToggle"),
  prevBtn: document.querySelector("#prevBtn"),
  nextBtn: document.querySelector("#nextBtn"),
  shuffleBtn: document.querySelector("#shuffleBtn"),
  themeToggle: document.querySelector("#themeToggle"),
  fullscreenBtn: document.querySelector("#fullscreenBtn"),
  saveBtn: document.querySelector("#saveBtn"),
  shareBtn: document.querySelector("#shareBtn"),
  captionBtn: document.querySelector("#captionBtn"),
  sourceLink: document.querySelector("#sourceLink"),
  downloadLink: document.querySelector("#downloadLink"),
  openLightbox: document.querySelector("#openLightbox"),
  showcaseMood: document.querySelector("#showcaseMood"),
  showcaseTitle: document.querySelector("#showcaseTitle"),
  showcaseDescription: document.querySelector("#showcaseDescription"),
  showcaseCategory: document.querySelector("#showcaseCategory"),
  showcaseLocation: document.querySelector("#showcaseLocation"),
  showcaseLight: document.querySelector("#showcaseLight"),
  showcaseSave: document.querySelector("#showcaseSave"),
  showcaseOpen: document.querySelector("#showcaseOpen"),
  totalCount: document.querySelector("#totalCount"),
  savedCount: document.querySelector("#savedCount"),
  visibleCount: document.querySelector("#visibleCount"),
  activeMood: document.querySelector("#activeMood"),
  heroSceneTitle: document.querySelector("#heroSceneTitle"),
  heroSceneMeta: document.querySelector("#heroSceneMeta"),
  stageLabel: document.querySelector("#stageLabel"),
  matchCount: document.querySelector("#matchCount"),
  activeCategory: document.querySelector("#activeCategory"),
  activeTitle: document.querySelector("#activeTitle"),
  activeDescription: document.querySelector("#activeDescription"),
  activeLocation: document.querySelector("#activeLocation"),
  activeLight: document.querySelector("#activeLight"),
  activeSavedStatus: document.querySelector("#activeSavedStatus"),
  activeCount: document.querySelector("#activeCount"),
  progressBar: document.querySelector("#progressBar"),
  lightbox: document.querySelector("#lightbox"),
  lightboxImage: document.querySelector("#lightboxImage"),
  lightboxCategory: document.querySelector("#lightboxCategory"),
  lightboxTitle: document.querySelector("#lightboxTitle"),
  lightboxDescription: document.querySelector("#lightboxDescription"),
  closeLightbox: document.querySelector("#closeLightbox"),
  lightboxPrev: document.querySelector("#lightboxPrev"),
  lightboxNext: document.querySelector("#lightboxNext"),
  resetBtn: document.querySelector("#resetBtn"),
  trailList: document.querySelector("#trailList"),
  dockPrev: document.querySelector("#dockPrev"),
  dockSave: document.querySelector("#dockSave"),
  dockOpen: document.querySelector("#dockOpen"),
  dockNext: document.querySelector("#dockNext"),
  toast: document.querySelector("#toast")
};

const storage = {
  get(key, fallback) {
    try {
      const storedValue = localStorage.getItem(key);
      return storedValue ? JSON.parse(storedValue) : fallback;
    } catch {
      return fallback;
    }
  },
  set(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch {
      return false;
    }

    return true;
  }
};

const validSorts = new Set(["curated", "title", "category", "saved"]);
const validViews = new Set(["focus", "mosaic"]);
const validThemes = new Set(["day", "night"]);
const validPalettes = new Set(["verdant", "coral", "gold"]);
const validSpeeds = new Set([2600, 4200, 6500]);

function asArray(value, fallback = []) {
  return Array.isArray(value) ? value : fallback;
}

function getStoredChoice(key, fallback, validChoices) {
  const value = storage.get(key, fallback);
  return validChoices.has(value) ? value : fallback;
}

function getStoredSpeed() {
  const value = Number(storage.get("gallerySpeed", 4200));
  return validSpeeds.has(value) ? value : 4200;
}

const hashId = decodeURIComponent(window.location.hash.replace("#", ""));
const startingPlace = places.find((place) => place.id === hashId) || places[0];
const storedTrail = asArray(storage.get("galleryTrail", []));
const storedSaved = asArray(storage.get("savedPlaces", []));

const state = {
  activeId: startingPlace.id,
  filter: "all",
  mood: "all",
  query: "",
  sort: getStoredChoice("gallerySort", "curated", validSorts),
  savedOnly: false,
  view: getStoredChoice("galleryView", "focus", validViews),
  speed: getStoredSpeed(),
  isPlaying: false,
  timerId: null,
  toastTimerId: null,
  trail: storedTrail.length > 0 ? storedTrail : [startingPlace.id],
  saved: new Set(storedSaved),
  theme: getStoredChoice("galleryTheme", "day", validThemes),
  palette: getStoredChoice("galleryPalette", "verdant", validPalettes)
};

function formatCategory(category) {
  return category.charAt(0).toUpperCase() + category.slice(1);
}

function pluralize(count, word) {
  return `${count} ${word}${count === 1 ? "" : "s"}`;
}

function sortPlaces(placeList) {
  const sortedPlaces = [...placeList];

  if (state.sort === "title") {
    return sortedPlaces.sort((first, second) => first.title.localeCompare(second.title));
  }

  if (state.sort === "category") {
    return sortedPlaces.sort((first, second) => {
      const categorySort = first.category.localeCompare(second.category);
      return categorySort || first.title.localeCompare(second.title);
    });
  }

  if (state.sort === "saved") {
    return sortedPlaces.sort((first, second) => {
      const firstSaved = state.saved.has(first.id) ? 0 : 1;
      const secondSaved = state.saved.has(second.id) ? 0 : 1;
      return firstSaved - secondSaved || first.title.localeCompare(second.title);
    });
  }

  return sortedPlaces;
}

function getVisiblePlaces() {
  const query = state.query.trim().toLowerCase();

  const visiblePlaces = places.filter((place) => {
    const matchesFilter = state.filter === "all" || place.category === state.filter;
    const matchesMood = state.mood === "all" || place.mood === state.mood;
    const searchableText = `${place.title} ${place.category} ${place.location} ${place.mood} ${place.light} ${place.description}`.toLowerCase();
    const matchesQuery = !query || searchableText.includes(query);
    const matchesSaved = !state.savedOnly || state.saved.has(place.id);

    return matchesFilter && matchesMood && matchesQuery && matchesSaved;
  });

  return sortPlaces(visiblePlaces);
}

function getActivePlace() {
  return places.find((place) => place.id === state.activeId) || places[0];
}

function setImageVariable(element, image) {
  element.style.setProperty("--image", `url("${image}")`);
}

function movePanelSpotlight(event, panel) {
  const bounds = panel.getBoundingClientRect();
  const x = ((event.clientX - bounds.left) / bounds.width) * 100;
  const y = ((event.clientY - bounds.top) / bounds.height) * 100;

  panel.style.setProperty("--pointer-x", `${x}%`);
  panel.style.setProperty("--pointer-y", `${y}%`);
}

function createPanel(place, index) {
  const panel = document.createElement("button");
  panel.className = "panel";
  panel.type = "button";
  panel.dataset.id = place.id;
  panel.dataset.number = String(index + 1).padStart(2, "0");
  panel.setAttribute("aria-pressed", "false");
  panel.setAttribute("aria-label", `${place.title}, ${formatCategory(place.category)}, ${place.location}`);
  setImageVariable(panel, place.image);

  if (state.saved.has(place.id)) {
    panel.classList.add("saved");
  }

  const saveMark = document.createElement("span");
  saveMark.className = "save-mark";
  saveMark.textContent = "Saved";

  const spotlight = document.createElement("span");
  spotlight.className = "panel-spotlight";

  const content = document.createElement("span");
  content.className = "panel-content";

  const category = document.createElement("span");
  category.className = "panel-category";
  category.textContent = formatCategory(place.category);

  const title = document.createElement("span");
  title.className = "panel-title";
  title.textContent = place.title;

  const location = document.createElement("span");
  location.className = "panel-location";
  location.textContent = `${place.location} / ${place.mood}`;

  const copy = document.createElement("span");
  copy.className = "panel-copy";
  copy.textContent = place.description;

  content.append(category, title, location, copy);
  panel.append(saveMark, spotlight, content);

  panel.addEventListener("click", () => {
    if (state.activeId === place.id) {
      openLightbox();
      return;
    }

    setActivePlace(place.id);
  });
  panel.addEventListener("dblclick", () => {
    setActivePlace(place.id);
    openLightbox();
  });
  panel.addEventListener("pointermove", (event) => movePanelSpotlight(event, panel));
  panel.addEventListener("keydown", handlePanelKeydown);

  return panel;
}

function createThumb(place) {
  const thumb = document.createElement("button");
  thumb.className = "thumb";
  thumb.type = "button";
  thumb.dataset.id = place.id;
  thumb.setAttribute("aria-label", `Open ${place.title}`);
  setImageVariable(thumb, place.image);

  const label = document.createElement("span");
  label.textContent = place.title;
  thumb.append(label);

  thumb.addEventListener("click", () => setActivePlace(place.id, { focusPanel: true }));

  return thumb;
}

function createCollectionCard(category) {
  const categoryPlaces = places.filter((place) => place.category === category);
  const coverPlace = categoryPlaces[0];
  const card = document.createElement("button");
  card.className = "collection-card";
  card.type = "button";
  card.dataset.filter = category;
  card.setAttribute("aria-pressed", String(state.filter === category));
  setImageVariable(card, coverPlace.image);

  if (state.filter === category) {
    card.classList.add("active");
  }

  const count = document.createElement("span");
  count.textContent = pluralize(categoryPlaces.length, "scene");

  const label = document.createElement("strong");
  label.textContent = formatCategory(category);

  card.append(count, label);
  card.addEventListener("click", () => setFilter(category));

  return card;
}

function renderCollections() {
  const categories = ["forest", "coast", "mountain", "desert", "lake"];
  elements.collectionGrid.innerHTML = "";

  categories.forEach((category) => {
    elements.collectionGrid.append(createCollectionCard(category));
  });
}

function createTrailButton(place) {
  const trailButton = document.createElement("button");
  trailButton.className = "trail-btn";
  trailButton.type = "button";
  trailButton.dataset.id = place.id;
  trailButton.setAttribute("aria-label", `Return to ${place.title}`);
  setImageVariable(trailButton, place.image);

  const label = document.createElement("span");
  label.textContent = place.title;
  trailButton.append(label);
  trailButton.addEventListener("click", () => setActivePlace(place.id, { focusPanel: true }));

  return trailButton;
}

function addToTrail(place) {
  state.trail = [place.id, ...state.trail.filter((id) => id !== place.id)].slice(0, 7);
  storage.set("galleryTrail", state.trail);
}

function renderTrail() {
  elements.trailList.innerHTML = "";

  const trailPlaces = state.trail
    .map((id) => places.find((place) => place.id === id))
    .filter(Boolean);

  if (trailPlaces.length === 0) {
    const emptyTrail = document.createElement("div");
    emptyTrail.className = "trail-empty";
    emptyTrail.textContent = "Start exploring to build your trail.";
    elements.trailList.append(emptyTrail);
    return;
  }

  trailPlaces.forEach((place) => {
    elements.trailList.append(createTrailButton(place));
  });
}

function updateStageLabel(visiblePlaces) {
  const filterName = state.filter === "all" ? "all places" : formatCategory(state.filter);
  const moodName = state.mood === "all" ? "" : ` in a ${state.mood.toLowerCase()} mood`;
  const savedText = state.savedOnly ? "saved " : "";
  const searchText = state.query.trim() ? ` matching "${state.query.trim()}"` : "";

  elements.stageLabel.textContent = `Showing ${savedText}${filterName}${moodName}${searchText}`;
  elements.matchCount.textContent = pluralize(visiblePlaces.length, "place");
}

function renderGallery() {
  const visiblePlaces = getVisiblePlaces();
  elements.panelTrack.innerHTML = "";
  elements.thumbStrip.innerHTML = "";
  renderCollections();
  elements.emptyState.hidden = visiblePlaces.length > 0;
  elements.panelTrack.hidden = visiblePlaces.length === 0;
  elements.thumbStrip.hidden = visiblePlaces.length === 0;
  elements.emptyState.textContent = state.savedOnly ? "No saved places match this view yet." : "No matching places yet.";
  updateStageLabel(visiblePlaces);

  if (visiblePlaces.length === 0) {
    stopAutoplay();
    updateStory(null, visiblePlaces);
    updateStats(null, visiblePlaces);
    renderTrail();
    return;
  }

  if (!visiblePlaces.some((place) => place.id === state.activeId)) {
    state.activeId = visiblePlaces[0].id;
  }

  visiblePlaces.forEach((place, index) => {
    elements.panelTrack.append(createPanel(place, index));
    elements.thumbStrip.append(createThumb(place));
  });

  syncActiveState();
}

function updateStory(place, visiblePlaces) {
  const hasPlace = Boolean(place);
  const activeIndex = hasPlace ? visiblePlaces.findIndex((item) => item.id === place.id) : -1;

  elements.activeCategory.textContent = hasPlace ? `${formatCategory(place.category)} / ${place.location}` : "No Match";
  elements.activeTitle.textContent = hasPlace ? place.title : "Try another search";
  elements.activeDescription.textContent = hasPlace ? place.description : "Clear the search or choose a different landscape.";
  elements.activeLocation.textContent = hasPlace ? place.location : "No location";
  elements.activeLight.textContent = hasPlace ? place.light : "No light";
  elements.activeSavedStatus.textContent = hasPlace && state.saved.has(place.id) ? "Saved frame" : "Not saved";
  elements.activeCount.textContent = hasPlace
    ? `${String(activeIndex + 1).padStart(2, "0")} / ${String(visiblePlaces.length).padStart(2, "0")}`
    : "00 / 00";

  elements.saveBtn.disabled = !hasPlace;
  elements.shareBtn.disabled = !hasPlace;
  elements.captionBtn.disabled = !hasPlace;
  elements.openLightbox.disabled = !hasPlace;
  elements.showcaseSave.disabled = !hasPlace;
  elements.showcaseOpen.disabled = !hasPlace;
  elements.dockPrev.disabled = !hasPlace;
  elements.dockSave.disabled = !hasPlace;
  elements.dockOpen.disabled = !hasPlace;
  elements.dockNext.disabled = !hasPlace;
  elements.sourceLink.setAttribute("aria-disabled", String(!hasPlace));
  elements.downloadLink.setAttribute("aria-disabled", String(!hasPlace));
  elements.sourceLink.tabIndex = hasPlace ? 0 : -1;
  elements.downloadLink.tabIndex = hasPlace ? 0 : -1;

  if (hasPlace) {
    const isSaved = state.saved.has(place.id);
    elements.saveBtn.textContent = isSaved ? "Saved" : "Save";
    elements.showcaseSave.textContent = isSaved ? "Saved" : "Save Frame";
    elements.dockSave.textContent = isSaved ? "Saved" : "Save";
    elements.saveBtn.setAttribute("aria-pressed", String(isSaved));
    elements.sourceLink.href = place.image;
    elements.downloadLink.href = `${place.image}&download=1`;
    elements.downloadLink.download = `${place.id}.jpg`;
  } else {
    elements.saveBtn.textContent = "Save";
    elements.showcaseSave.textContent = "Save Frame";
    elements.dockSave.textContent = "Save";
    elements.saveBtn.setAttribute("aria-pressed", "false");
    elements.sourceLink.href = "#";
    elements.downloadLink.href = "#";
  }
}

function updateShowcase(place) {
  if (!place) {
    elements.showcaseMood.textContent = "No match";
    elements.showcaseTitle.textContent = "Try another filter";
    elements.showcaseDescription.textContent = "Clear the search or choose another collection.";
    elements.showcaseCategory.textContent = "No category";
    elements.showcaseLocation.textContent = "No location";
    elements.showcaseLight.textContent = "No light";
    return;
  }

  elements.showcaseMood.textContent = place.mood;
  elements.showcaseTitle.textContent = place.title;
  elements.showcaseDescription.textContent = place.description;
  elements.showcaseCategory.textContent = formatCategory(place.category);
  elements.showcaseLocation.textContent = place.location;
  elements.showcaseLight.textContent = place.light;
}

function updateStats(place, visiblePlaces) {
  elements.totalCount.textContent = String(places.length).padStart(2, "0");
  elements.savedCount.textContent = String(state.saved.size).padStart(2, "0");
  elements.visibleCount.textContent = String(visiblePlaces.length).padStart(2, "0");
  elements.activeMood.textContent = place ? place.mood : "None";
  elements.heroSceneTitle.textContent = place ? place.title : "No match";
  elements.heroSceneMeta.textContent = place ? `${formatCategory(place.category)} / ${place.location}` : "Try a new filter";
  updateShowcase(place);
}

function updateAtmosphere(place) {
  if (!place) {
    return;
  }

  document.body.style.setProperty("--ambient-image", `url("${place.image}")`);
}

function syncActiveState() {
  const visiblePlaces = getVisiblePlaces();
  const activePlace = getActivePlace();

  document.querySelectorAll(".panel").forEach((panel) => {
    const isActive = panel.dataset.id === state.activeId;
    panel.classList.toggle("active", isActive);
    panel.setAttribute("aria-pressed", String(isActive));
  });

  document.querySelectorAll(".thumb").forEach((thumb) => {
    thumb.classList.toggle("active", thumb.dataset.id === state.activeId);
  });

  updateStory(activePlace, visiblePlaces);
  updateStats(activePlace, visiblePlaces);
  updateAtmosphere(activePlace);
  addToTrail(activePlace);
  renderTrail();
  updateLightbox();
  updateHash(activePlace);
  restartProgress();
}

function setActivePlace(id, options = {}) {
  if (!places.some((place) => place.id === id)) {
    return;
  }

  state.activeId = id;
  syncActiveState();

  if (options.focusPanel) {
    document.querySelector(`.panel[data-id="${id}"]`)?.focus();
  }
}

function moveActivePlace(direction, options = {}) {
  const visiblePlaces = getVisiblePlaces();

  if (visiblePlaces.length === 0) {
    showToast("No visible places to move through.");
    return;
  }

  const activeIndex = Math.max(visiblePlaces.findIndex((place) => place.id === state.activeId), 0);
  const nextIndex = (activeIndex + direction + visiblePlaces.length) % visiblePlaces.length;
  const focusPanel = options.focusPanel ?? elements.lightbox.hidden;
  setActivePlace(visiblePlaces[nextIndex].id, { focusPanel });
}

function handlePanelKeydown(event) {
  const keyActions = {
    ArrowRight: () => moveActivePlace(1, { focusPanel: true }),
    ArrowDown: () => moveActivePlace(1, { focusPanel: true }),
    ArrowLeft: () => moveActivePlace(-1, { focusPanel: true }),
    ArrowUp: () => moveActivePlace(-1, { focusPanel: true }),
    Enter: () => openLightbox(),
    Home: () => {
      const firstPlace = getVisiblePlaces()[0];
      if (firstPlace) {
        setActivePlace(firstPlace.id, { focusPanel: true });
      }
    },
    End: () => {
      const visiblePlaces = getVisiblePlaces();
      const lastPlace = visiblePlaces[visiblePlaces.length - 1];
      if (lastPlace) {
        setActivePlace(lastPlace.id, { focusPanel: true });
      }
    }
  };

  const action = keyActions[event.key];

  if (action) {
    event.preventDefault();
    action();
  }
}

function restartProgress() {
  elements.progressBar.style.animation = "none";
  elements.progressBar.offsetHeight;
  elements.progressBar.style.animation = "";
}

function startAutoplay() {
  if (state.isPlaying) {
    return;
  }

  state.isPlaying = true;
  document.body.classList.add("is-playing");
  elements.autoplayToggle.textContent = "Pause";
  elements.autoplayToggle.setAttribute("aria-pressed", "true");
  state.timerId = window.setInterval(() => moveActivePlace(1, { focusPanel: false }), state.speed);
  restartProgress();
}

function stopAutoplay() {
  state.isPlaying = false;
  document.body.classList.remove("is-playing");
  elements.autoplayToggle.textContent = "Auto Play";
  elements.autoplayToggle.setAttribute("aria-pressed", "false");
  window.clearInterval(state.timerId);
  state.timerId = null;
}

function shufflePlace() {
  const visiblePlaces = getVisiblePlaces();

  if (visiblePlaces.length <= 1) {
    showToast("Need at least two visible places to shuffle.");
    return;
  }

  const candidates = visiblePlaces.filter((place) => place.id !== state.activeId);
  const randomPlace = candidates[Math.floor(Math.random() * candidates.length)];
  setActivePlace(randomPlace.id, { focusPanel: true });
  showToast(`Jumped to ${randomPlace.title}.`);
}

function toggleSave() {
  const activePlace = getActivePlace();
  const wasSaved = state.saved.has(activePlace.id);

  if (wasSaved) {
    state.saved.delete(activePlace.id);
  } else {
    state.saved.add(activePlace.id);
  }

  storage.set("savedPlaces", Array.from(state.saved));
  renderGallery();
  showToast(wasSaved ? `${activePlace.title} removed from saved.` : `${activePlace.title} saved.`);
}

function applyTheme() {
  const isNight = state.theme === "night";
  document.body.dataset.theme = state.theme;
  elements.themeToggle.textContent = isNight ? "Day" : "Night";
  elements.themeToggle.setAttribute("aria-pressed", String(isNight));
  storage.set("galleryTheme", state.theme);
}

function applyPalette() {
  document.body.dataset.palette = state.palette;
  elements.paletteButtons.forEach((button) => {
    const isActive = button.dataset.palette === state.palette;
    button.classList.toggle("active", isActive);
    button.setAttribute("aria-pressed", String(isActive));
  });
  storage.set("galleryPalette", state.palette);
}

function setPalette(palette) {
  state.palette = palette;
  applyPalette();
  showToast(`${palette.charAt(0).toUpperCase() + palette.slice(1)} palette applied.`);
}

function toggleTheme() {
  state.theme = state.theme === "night" ? "day" : "night";
  applyTheme();
  showToast(`${state.theme === "night" ? "Night" : "Day"} mode is on.`);
}

function applyView() {
  document.body.dataset.view = state.view;
  elements.viewButtons.forEach((button) => {
    const isActive = button.dataset.view === state.view;
    button.classList.toggle("active", isActive);
    button.setAttribute("aria-pressed", String(isActive));
  });
  storage.set("galleryView", state.view);
}

function setView(view) {
  state.view = view;
  applyView();
  showToast(`${view === "mosaic" ? "Mosaic" : "Focus"} view is ready.`);
}

function applySpeed() {
  document.body.style.setProperty("--play-speed", `${state.speed}ms`);
  elements.speedSelect.value = String(state.speed);
  storage.set("gallerySpeed", state.speed);

  if (state.isPlaying) {
    stopAutoplay();
    startAutoplay();
  }
}

function applySort() {
  elements.sortSelect.value = state.sort;
  storage.set("gallerySort", state.sort);
}

function toggleSavedOnly() {
  state.savedOnly = !state.savedOnly;
  elements.favoritesToggle.setAttribute("aria-pressed", String(state.savedOnly));
  renderGallery();
  showToast(state.savedOnly ? "Showing saved frames only." : "Showing every matching place.");
}

function setFilter(filter) {
  state.filter = filter;

  elements.filterButtons.forEach((filterButton) => {
    const isActive = filterButton.dataset.filter === filter;
    filterButton.classList.toggle("active", isActive);
    filterButton.setAttribute("aria-pressed", String(isActive));
  });

  renderGallery();
}

function openLightbox() {
  if (getVisiblePlaces().length === 0) {
    showToast("Pick a visible place first.");
    return;
  }

  elements.lightbox.hidden = false;
  document.body.classList.add("has-lightbox");
  updateLightbox();
  elements.closeLightbox.focus();
  showToast("Image view opened.");
}

function closeLightbox() {
  elements.lightbox.hidden = true;
  document.body.classList.remove("has-lightbox");
  elements.openLightbox.focus({ preventScroll: true });
}

function updateLightbox() {
  if (elements.lightbox.hidden) {
    return;
  }

  const activePlace = getActivePlace();
  elements.lightboxImage.src = activePlace.image;
  elements.lightboxImage.alt = activePlace.alt;
  elements.lightboxCategory.textContent = `${formatCategory(activePlace.category)} / ${activePlace.location}`;
  elements.lightboxTitle.textContent = activePlace.title;
  elements.lightboxDescription.textContent = activePlace.description;
}

function updateHash(place) {
  if (!place || !history.replaceState) {
    return;
  }

  history.replaceState(null, "", `#${place.id}`);
}

function showToast(message) {
  window.clearTimeout(state.toastTimerId);
  elements.toast.textContent = message;
  elements.toast.classList.add("visible");
  state.toastTimerId = window.setTimeout(() => {
    elements.toast.classList.remove("visible");
  }, 2400);
}

function fallbackCopy(text) {
  const textarea = document.createElement("textarea");
  textarea.value = text;
  textarea.setAttribute("readonly", "");
  textarea.style.position = "fixed";
  textarea.style.opacity = "0";
  document.body.append(textarea);
  textarea.select();
  const copied = document.execCommand("copy");
  textarea.remove();
  return copied;
}

async function copyShareLink() {
  const activePlace = getActivePlace();
  const shareUrl = `${window.location.origin}${window.location.pathname}#${activePlace.id}`;

  try {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(shareUrl);
    } else {
      fallbackCopy(shareUrl);
    }

    showToast(`Link copied for ${activePlace.title}.`);
  } catch {
    showToast("Copy did not work in this browser.");
  }
}

async function copyCaption() {
  const place = getActivePlace();
  const caption = `${place.title} - ${place.location}. ${place.description}`;

  try {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(caption);
    } else {
      fallbackCopy(caption);
    }

    showToast(`Caption copied for ${place.title}.`);
  } catch {
    showToast("Caption copy did not work in this browser.");
  }
}

async function toggleFullscreen() {
  try {
    if (!document.fullscreenElement) {
      await document.documentElement.requestFullscreen();
      elements.fullscreenBtn.textContent = "Exit Full";
      showToast("Full-screen gallery is on.");
    } else {
      await document.exitFullscreen();
      elements.fullscreenBtn.textContent = "Full Screen";
      showToast("Full-screen gallery is off.");
    }
  } catch {
    showToast("Full screen is not available here.");
  }
}

function resetView() {
  stopAutoplay();
  state.filter = "all";
  state.mood = "all";
  state.query = "";
  state.savedOnly = false;
  state.sort = "curated";
  state.view = "focus";
  state.speed = 4200;
  state.palette = "verdant";
  state.activeId = places[0].id;

  elements.searchInput.value = "";
  elements.favoritesToggle.setAttribute("aria-pressed", "false");
  elements.filterButtons.forEach((button) => {
    const isActive = button.dataset.filter === "all";
    button.classList.toggle("active", isActive);
    button.setAttribute("aria-pressed", String(isActive));
  });
  elements.moodButtons.forEach((button) => {
    const isActive = button.dataset.mood === "all";
    button.classList.toggle("active", isActive);
    button.setAttribute("aria-pressed", String(isActive));
  });

  applySort();
  applySpeed();
  applyPalette();
  applyView();
  renderGallery();
  showToast("Gallery view reset.");
}

function handleLightboxBackdrop(event) {
  if (event.target === elements.lightbox) {
    closeLightbox();
  }
}

function handleDocumentKeydown(event) {
  const isTyping = ["INPUT", "SELECT", "TEXTAREA"].includes(document.activeElement?.tagName);

  if (!isTyping && elements.lightbox.hidden) {
    if (event.key === "/") {
      event.preventDefault();
      elements.searchInput.focus();
    }

    if (event.key.toLowerCase() === "s") {
      event.preventDefault();
      toggleSave();
    }

    if (event.key.toLowerCase() === "m") {
      event.preventDefault();
      setView(state.view === "mosaic" ? "focus" : "mosaic");
    }

    if (event.key.toLowerCase() === "f") {
      event.preventDefault();
      toggleSavedOnly();
    }
  }

  if (!elements.lightbox.hidden) {
    if (event.key === "Escape") {
      closeLightbox();
    }

    if (event.key === "ArrowRight") {
      moveActivePlace(1);
    }

    if (event.key === "ArrowLeft") {
      moveActivePlace(-1);
    }
  }
}

elements.searchInput.addEventListener("input", (event) => {
  state.query = event.target.value;
  renderGallery();
});

elements.filterButtons.forEach((button) => {
  button.addEventListener("click", () => setFilter(button.dataset.filter));
});

elements.moodButtons.forEach((button) => {
  button.addEventListener("click", () => {
    state.mood = button.dataset.mood;

    elements.moodButtons.forEach((moodButton) => {
      const isActive = moodButton === button;
      moodButton.classList.toggle("active", isActive);
      moodButton.setAttribute("aria-pressed", String(isActive));
    });

    renderGallery();
  });
});

elements.viewButtons.forEach((button) => {
  button.addEventListener("click", () => setView(button.dataset.view));
});

elements.paletteButtons.forEach((button) => {
  button.addEventListener("click", () => setPalette(button.dataset.palette));
});

elements.sortSelect.addEventListener("change", (event) => {
  state.sort = event.target.value;
  applySort();
  renderGallery();
});

elements.speedSelect.addEventListener("change", (event) => {
  state.speed = Number(event.target.value);
  applySpeed();
  showToast("Autoplay speed updated.");
});

elements.favoritesToggle.addEventListener("click", toggleSavedOnly);
elements.autoplayToggle.addEventListener("click", () => {
  if (state.isPlaying) {
    stopAutoplay();
  } else {
    startAutoplay();
  }
});
elements.prevBtn.addEventListener("click", () => moveActivePlace(-1, { focusPanel: true }));
elements.nextBtn.addEventListener("click", () => moveActivePlace(1, { focusPanel: true }));
elements.shuffleBtn.addEventListener("click", shufflePlace);
elements.themeToggle.addEventListener("click", toggleTheme);
elements.fullscreenBtn.addEventListener("click", toggleFullscreen);
elements.saveBtn.addEventListener("click", toggleSave);
elements.showcaseSave.addEventListener("click", toggleSave);
elements.shareBtn.addEventListener("click", copyShareLink);
elements.captionBtn.addEventListener("click", copyCaption);
elements.openLightbox.addEventListener("click", openLightbox);
elements.showcaseOpen.addEventListener("click", openLightbox);
elements.closeLightbox.addEventListener("click", closeLightbox);
elements.lightbox.addEventListener("click", handleLightboxBackdrop);
elements.lightboxPrev.addEventListener("click", () => moveActivePlace(-1, { focusPanel: false }));
elements.lightboxNext.addEventListener("click", () => moveActivePlace(1, { focusPanel: false }));
elements.resetBtn.addEventListener("click", resetView);
elements.dockPrev.addEventListener("click", () => moveActivePlace(-1, { focusPanel: false }));
elements.dockSave.addEventListener("click", toggleSave);
elements.dockOpen.addEventListener("click", openLightbox);
elements.dockNext.addEventListener("click", () => moveActivePlace(1, { focusPanel: false }));
elements.sourceLink.addEventListener("click", (event) => {
  if (elements.sourceLink.getAttribute("aria-disabled") === "true") {
    event.preventDefault();
  }
});
elements.downloadLink.addEventListener("click", (event) => {
  if (elements.downloadLink.getAttribute("aria-disabled") === "true") {
    event.preventDefault();
  }
});
document.addEventListener("fullscreenchange", () => {
  elements.fullscreenBtn.textContent = document.fullscreenElement ? "Exit Full" : "Full Screen";
});
window.addEventListener("error", () => {
  showToast("Something paused the gallery. Reset View can recover it.");
});
document.addEventListener("keydown", handleDocumentKeydown);

applyTheme();
applyPalette();
applySort();
applySpeed();
applyView();
renderGallery();
