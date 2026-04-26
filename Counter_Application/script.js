const counterValue = document.getElementById("counterValue");
const counterStatus = document.getElementById("counterStatus");
const increaseBtn = document.getElementById("increaseBtn");
const decreaseBtn = document.getElementById("decreaseBtn");
const resetBtn = document.getElementById("resetBtn");

let count = 0;

function getStatusText(value) {
  if (value > 0) {
    return "Positive number.";
  }

  if (value < 0) {
    return "Negative number.";
  }

  return "Back to zero.";
}

function renderCounter() {
  counterValue.textContent = count;
  counterStatus.textContent = getStatusText(count);

  counterValue.classList.remove("positive", "negative", "neutral", "changed");

  if (count > 0) {
    counterValue.classList.add("positive");
  } else if (count < 0) {
    counterValue.classList.add("negative");
  } else {
    counterValue.classList.add("neutral");
  }

  window.requestAnimationFrame(() => {
    counterValue.classList.add("changed");
  });
}

counterValue.addEventListener("animationend", () => {
  counterValue.classList.remove("changed");
});

increaseBtn.addEventListener("click", () => {
  count += 1;
  renderCounter();
});

decreaseBtn.addEventListener("click", () => {
  count -= 1;
  renderCounter();
});

resetBtn.addEventListener("click", () => {
  count = 0;
  renderCounter();
});

renderCounter();
