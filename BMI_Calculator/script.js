const bmiForm = document.getElementById("bmiForm");
const heightInput = document.getElementById("height");
const weightInput = document.getElementById("weight");
const result = document.getElementById("result");

bmiForm.addEventListener("submit", function (event) {
  event.preventDefault();
  calculateBMI();
});

function calculateBMI() {
  const height = Number(heightInput.value);
  const weight = Number(weightInput.value);

  if (height <= 0 || weight <= 0) {
    result.textContent = "Please enter valid height and weight.";
    result.className = "result-box show";
    return;
  }

  const heightInMeters = height / 100;
  const bmi = weight / (heightInMeters * heightInMeters);
  let category = "";
  let resultClass = "";

  if (bmi < 18.5) {
    category = "Underweight";
    resultClass = "underweight";
  } else if (bmi < 25) {
    category = "Normal weight";
    resultClass = "normal";
  } else if (bmi < 30) {
    category = "Overweight";
    resultClass = "overweight";
  } else {
    category = "Obese";
    resultClass = "obese";
  }

  result.className = "result-box show " + resultClass;
  result.textContent = "Your BMI is " + bmi.toFixed(2) + " (" + category + ")";
}
