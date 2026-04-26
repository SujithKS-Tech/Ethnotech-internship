const timeElement = document.getElementById("time");
const periodElement = document.getElementById("period");
const dateElement = document.getElementById("date");

function updateClock() {
  const now = new Date();
  let hours = now.getHours();
  const minutes = now.getMinutes();
  const seconds = now.getSeconds();
  const period = hours >= 12 ? "PM" : "AM";

  hours %= 12;
  hours = hours || 12;

  timeElement.textContent = [
    hours,
    minutes,
    seconds
  ].map((value) => String(value).padStart(2, "0")).join(":");

  periodElement.textContent = period;
  dateElement.textContent = now.toLocaleDateString(undefined, {
    weekday: "long",
    month: "long",
    day: "numeric",
    year: "numeric"
  });
}

updateClock();
setInterval(updateClock, 1000);
