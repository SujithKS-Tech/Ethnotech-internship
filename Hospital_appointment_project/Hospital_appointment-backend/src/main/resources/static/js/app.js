const runtimeOrigin = window.location.origin;
const BASE_URL =
  (window.API_BASE_URL && window.API_BASE_URL.replace(/\/$/, "")) ||
  (runtimeOrigin && runtimeOrigin.startsWith("http")
    ? runtimeOrigin
    : "http://localhost:8080");

// ================= AUTH HEADER =================
function authHeaders() {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Please login first");
    window.location.href = "index.html";
    return null;
  }

  return {
    "Content-Type": "application/json",
    Authorization: "Bearer " + token,
  };
}

// ================= DECODE ROLE =================
function getRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.role;
  } catch (e) {
    console.error("Invalid token");
    return null;
  }
}

// ================= LOGIN =================
function login() {
  fetch(`${BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: document.getElementById("email").value,
      password: document.getElementById("password").value,
    }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Login failed");
      return res.json();
    })
    .then((data) => {
      const token = data.token;

      localStorage.setItem("token", token);

      // ✅ Get role from JWT
      const role = getRoleFromToken(token);
      localStorage.setItem("role", role);

      alert("Login Successful");

      // ✅ Redirect based on role
      if (role === "ADMIN") {
        window.location.href = "admin.html";
      } else if (role === "DOCTOR") {
        window.location.href = "doctor.html";
      } else if (role === "PATIENT") {
        window.location.href = "patient.html";
      } else {
        alert("Unknown role");
      }
    })
    .catch((err) => {
      console.error(err);
      alert("Login Error");
    });
}

// ================= REGISTER =================
function register() {
  fetch(`${BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: document.getElementById("name").value,
      email: document.getElementById("email").value,
      password: document.getElementById("password").value,
      phoneNumber: document.getElementById("phoneNumber").value,
      role: document.getElementById("role").value,
    }),
  })
    .then((res) => res.text())
    .then((data) => {
      alert(data);
      window.location.href = "index.html";
    })
    .catch((err) => alert("Registration Failed"));
}

// ================= LOGOUT =================
function logout() {
  localStorage.clear();
  window.location.href = "index.html";
}

// ================= ADMIN =================
function createDoctor() {
  const headers = authHeaders();
  if (!headers) return;

  const experience = document.getElementById("dexperience").value;
  const fee = document.getElementById("dfee").value;

  fetch(`${BASE_URL}/admin/doctors`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({
      name: document.getElementById("dname").value,
      email: document.getElementById("demail").value,
      password: document.getElementById("dpassword").value,
      phoneNumber: document.getElementById("dphone").value,
      specialization: document.getElementById("dspecialization").value,

      // ✅ FIX: ensure numbers
      experienceYears: experience ? parseInt(experience) : 0,
      consultationFee: fee ? parseInt(fee) : 0,
    }),
  })
    .then((res) => res.text())
    .then(alert)
    .catch(() => alert("Error creating doctor"));
}

function loadDoctors() {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/admin/doctors`, { headers })
    .then((res) => res.json())
    .then((data) => {
      const table = document.getElementById("doctorTable");
      table.innerHTML = "";

      data.forEach((d) => {
        const id = d.doctorId ?? d.id; // ✅ FIX

        table.innerHTML += `
          <tr>
            <td>${id}</td>
            <td>${d.name}</td>
            <td>${d.specialization || ""}</td>
            <td>
              <button onclick="editDoctor(${id}, '${d.name}', '${d.specialization || ""}')">Edit</button>
              <button onclick="deleteDoctor(${id})">Delete</button>
            </td>
          </tr>
        `;
      });
    })
    .catch(() => alert("Error loading doctors"));
}

function editDoctor(id, name, specialization) {
  if (!id) {
    alert("Invalid doctor ID");
    return;
  }

  const newName = prompt("Enter new name", name);
  const newSpec = prompt("Enter specialization", specialization);

  if (!newName) return;

  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/admin/doctors/${id}`, {
    method: "PUT",
    headers: headers,
    body: JSON.stringify({
      name: newName,
      specialization: newSpec,
    }),
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadDoctors();
    })
    .catch(() => alert("Error updating doctor"));
}

function deleteDoctor(id) {
  if (!confirm("Are you sure you want to delete this doctor?")) return;

  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/admin/doctors/${id}`, {
    method: "DELETE",
    headers: headers,
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadDoctors(); // refresh
    })
    .catch(() => alert("Error deleting doctor"));
}

// ================= DOCTOR =================
function createSchedule() {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/doctor/schedules`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({
      date: document.getElementById("date").value,
      startTime: document.getElementById("startTime").value,
      endTime: document.getElementById("endTime").value,
      maxAppointments: document.getElementById("maxAppointments").value,
    }),
  })
    .then((res) => res.text())
    .then(alert)
    .catch(() => alert("Error creating schedule"));
}

function loadQueue() {
  const headers = authHeaders();
  if (!headers) return;

  const id = document.getElementById("queueScheduleId").value;

  fetch(`${BASE_URL}/doctor/appointments/queue/${id}`, { headers })
    .then((res) => res.json())
    .then((data) => {
      console.log("QUEUE DATA:", data);

      const table = document.getElementById("queueTable");
      table.innerHTML = "";

      data.forEach((q) => {
        table.innerHTML += `
          <tr>
            <td>${q.queueNumber}</td>
            <td>${q.patientName}</td>
            <td>${q.status}</td>
          </tr>
        `;
      });
    })
    .catch(() => alert("Error loading queue"));
}

function startAppointment() {
  const headers = authHeaders();
  if (!headers) return;

  const id = document.getElementById("appointmentId").value;

  fetch(`${BASE_URL}/doctor/appointments/${id}/start`, {
    method: "PUT",
    headers: headers,
  })
    .then((res) => res.text())
    .then(alert);
}

function completeAppointment() {
  const headers = authHeaders();
  if (!headers) return;

  const id = document.getElementById("appointmentId").value;

  fetch(`${BASE_URL}/doctor/appointments/${id}/complete`, {
    method: "PUT",
    headers: headers,
  })
    .then((res) => res.text())
    .then(alert);
}

function loadSchedules() {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/doctor/schedules`, { headers })
    .then((res) => res.json())
    .then((data) => {
      const table = document.getElementById("scheduleTable");
      table.innerHTML = "";

      data.forEach((s) => {
        const id = s.scheduleId ?? s.id;

        table.innerHTML += `
          <tr>
            <td>${id}</td>
            <td>${s.date}</td>
            <td>${s.startTime}</td>
            <td>${s.endTime || "N/A"}</td>
            <td>${s.maxAppointments}</td>
            <td>
              <button onclick="deleteSchedule(${id})">Delete</button>
              <button onclick="updateSchedule(${id})">Update</button>
            </td>
          </tr>
        `;
      });
    });
}

function deleteSchedule(id) {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/doctor/schedules/${id}`, {
    method: "DELETE",
    headers: headers,
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadSchedules();
    });
}

function updateSchedule(id) {
  let date = prompt("Enter date (YYYY-MM-DD)");
  let startTime = prompt("Enter start time (HH:mm) e.g. 10:00");
  let endTime = prompt("Enter end time (HH:mm) e.g. 11:00");
  let max = prompt("Enter max appointments");

  // 🔥 REMOVE AM/PM if user types it
  if (startTime.includes("am") || startTime.includes("pm")) {
    alert("Please use 24-hour format (HH:mm). Example: 14:00");
    return;
  }

  if (endTime.includes("am") || endTime.includes("pm")) {
    alert("Please use 24-hour format (HH:mm). Example: 15:00");
    return;
  }

  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/doctor/schedules/${id}`, {
    method: "PUT",
    headers: headers,
    body: JSON.stringify({
      date: date,
      startTime: startTime,
      endTime: endTime,
      maxAppointments: parseInt(max),
    }),
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadSchedules();
    })
    .catch(() => alert("Error updating schedule"));
}
// ================= PATIENT =================
function bookAppointment() {
  const headers = authHeaders();
  if (!headers) return;

  const doctorId = document.getElementById("doctorId").value;
  const scheduleId = document.getElementById("scheduleId").value;

  if (!doctorId || !scheduleId) {
    alert("Enter doctorId and scheduleId");
    return;
  }

  fetch(`${BASE_URL}/patient/appointments`, {
    method: "POST",
    headers: headers,
    body: JSON.stringify({
      doctorId: parseInt(doctorId),
      scheduleId: parseInt(scheduleId),
    }),
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadAppointments();
    })
    .catch(() => alert("Error booking appointment"));
}

function loadAppointments() {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/patient/appointments`, { headers })
    .then((res) => res.json())
    .then((data) => {
      const table = document.getElementById("appointmentTable");
      table.innerHTML = "";

      data.content.forEach((a) => {
        const id = a.appointmentId ?? a.id;

        table.innerHTML += `
          <tr>
            <td>${id}</td>
            <td>${a.status}</td>
            <td>
              <button onclick="cancelAppointment(${id})">Cancel</button>
            </td>
          </tr>
        `;
      });
    })
    .catch(() => alert("Error loading appointments"));
}

function checkQueueStatus() {
  const headers = authHeaders();
  if (!headers) return;

  const id = document.getElementById("queueId").value;

  if (!id) {
    alert("Enter appointment ID");
    return;
  }

  fetch(`${BASE_URL}/patient/appointments/queue-status/${id}`, { headers })
    .then((res) => res.json())
    .then((data) => {
      alert(
        "Queue Number: " +
          data.queueNumber +
          "\nPatients Ahead: " +
          data.patientsAhead +
          "\nCurrently Serving: " +
          (data.currentServing ?? "Not Started"),
      );
    })
    .catch(() => alert("Error fetching queue status"));
}

function cancelAppointment(id) {
  const headers = authHeaders();
  if (!headers) return;

  fetch(`${BASE_URL}/patient/appointments/${id}`, {
    method: "DELETE",
    headers: headers,
  })
    .then((res) => res.text())
    .then((msg) => {
      alert(msg);
      loadAppointments();
    });
}
