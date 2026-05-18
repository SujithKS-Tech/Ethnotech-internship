const form = document.querySelector("#concept-form");
const answer = document.querySelector("#answer");
const emptyState = document.querySelector("#empty-state");
const submitButton = document.querySelector("#submit-button");
const conceptInput = document.querySelector("#concept");
const quickPrompts = document.querySelectorAll("[data-prompt]");
const aiToolButtons = document.querySelectorAll("[data-ai-tool]");
const isExternalStaticPreview =
  location.protocol === "file:" ||
  ["5500", "5501"].includes(location.port);
const apiBaseUrl = isExternalStaticPreview ? "http://localhost:8081" : "";

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function renderError(message) {
  emptyState.hidden = true;
  answer.hidden = false;
  answer.innerHTML = `<p class="error">${escapeHtml(message)}</p>`;
}

function renderExplanation(data) {
  const sections = data.sections
    .map(
      (section) => `
        <section class="answer-section">
          <h3>${escapeHtml(section.heading)}</h3>
          <p>${escapeHtml(section.body)}</p>
        </section>
      `
    )
    .join("");

  const followUps = data.followUps
    .map((question) => `<li>${escapeHtml(question)}</li>`)
    .join("");

  emptyState.hidden = true;
  answer.hidden = false;
  answer.innerHTML = `
    <h2>${escapeHtml(data.title)}</h2>
    <div class="meta">
      <span>${escapeHtml(data.level)}</span>
      <span>${escapeHtml(data.explanationType)}</span>
      <span>Saved ID: ${escapeHtml(data.id)}</span>
    </div>
    ${sections}
    <aside class="follow-ups">
      <h3>Better follow-up questions</h3>
      <ul>${followUps}</ul>
    </aside>
    <section class="answer-actions" aria-label="AI tools for this answer">
      <div>
        <h3>AI tools</h3>
        <p>Use the same concept to simplify, get examples, create a quiz, or make revision notes.</p>
      </div>
      <div class="answer-tool-grid">
        <button type="button" data-answer-tool="simplify">Simplify</button>
        <button type="button" data-answer-tool="examples">Examples</button>
        <button type="button" data-answer-tool="quiz">Quiz</button>
        <button type="button" data-answer-tool="study-notes">Study notes</button>
      </div>
    </section>
    <section class="answer-section">
      <h3>AI Integration</h3>
      <p>${escapeHtml(data.source)}. API keys are handled only by the backend.</p>
    </section>
  `;
}

function currentPayload(tool) {
  const formData = new FormData(form);
  return {
    concept: formData.get("concept"),
    tool,
    level: formData.get("level"),
    explanationType: formData.get("explanationType"),
    context: formData.get("context")
  };
}

function renderAiTool(data) {
  const sections = data.sections
    .map(
      (section) => `
        <section class="answer-section">
          <h3>${escapeHtml(section.heading)}</h3>
          <p>${escapeHtml(section.body)}</p>
        </section>
      `
    )
    .join("");

  emptyState.hidden = true;
  answer.hidden = false;
  answer.innerHTML = `
    <h2>${escapeHtml(data.title)}</h2>
    <div class="meta">
      <span>${escapeHtml(data.tool)}</span>
      <span>${escapeHtml(data.source)}</span>
    </div>
    ${sections}
    <section class="answer-section">
      <h3>AI Source</h3>
      <p>${escapeHtml(data.source)}. API keys are never exposed in browser code.</p>
    </section>
  `;
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const formData = new FormData(form);
  const payload = {
    concept: formData.get("concept"),
    level: formData.get("level"),
    explanationType: formData.get("explanationType"),
    context: formData.get("context")
  };

  submitButton.disabled = true;
  submitButton.textContent = "Generating...";

  try {
    const response = await fetch(`${apiBaseUrl}/api/concepts/explain`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || "Could not generate an explanation.");
    }

    renderExplanation(data);
  } catch (error) {
    renderError(error.message);
  } finally {
    submitButton.disabled = false;
    submitButton.textContent = "Generate explanation";
  }
});

quickPrompts.forEach((button) => {
  button.addEventListener("click", () => {
    conceptInput.value = button.dataset.prompt;
    conceptInput.focus();
  });
});

aiToolButtons.forEach((button) => {
  button.addEventListener("click", async () => {
    runAiTool(button, button.dataset.aiTool);
  });
});

answer.addEventListener("click", (event) => {
  const button = event.target.closest("[data-answer-tool]");
  if (!button) {
    return;
  }

  runAiTool(button, button.dataset.answerTool);
});

async function runAiTool(button, tool) {
  if (!conceptInput.value.trim()) {
    renderError("Please enter a concept before using an AI tool.");
    conceptInput.focus();
    return;
  }

  const originalText = button.textContent;
  button.disabled = true;
  button.textContent = "Thinking...";

  try {
    const response = await fetch(`${apiBaseUrl}/api/concepts/ai-tool`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(currentPayload(tool))
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || "Could not run AI tool.");
    }

    renderAiTool(data);
  } catch (error) {
    renderError(error.message);
  } finally {
    button.disabled = false;
    button.textContent = originalText;
  }
}
