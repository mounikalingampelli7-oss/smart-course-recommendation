// Internal recommendation engine — not exposed to UI
const engine = {
  map: {},
  addNode(n) { if (!this.map[n]) this.map[n] = []; },
  link(a, b)  { this.addNode(a); this.addNode(b); if (!this.map[a].includes(b)) this.map[a].push(b); },
  find(starts) {
    const seen = new Set(), q = [], dist = {};
    starts.forEach(s => { if (this.map[s]) { q.push(s); seen.add(s); dist[s] = 0; } });
    while (q.length) {
      const cur = q.shift(), d = dist[cur];
      (this.map[cur]||[]).forEach(nb => { if (!seen.has(nb)) { seen.add(nb); dist[nb] = d+1; q.push(nb); } });
    }
    return dist;
  },
  total() { return Object.keys(this.map).length; },
  links() { return Object.values(this.map).reduce((s,v)=>s+v.length,0); }
};

const courseData = [
  { id:"dsa",     name:"Data Structures & Algorithms", level:"Intermediate", needs:["Java","OOP"],         tags:["Problem Solving","Logic","Efficiency"] },
  { id:"webdev",  name:"Full Stack Web Development",   level:"Beginner",     needs:["HTML","JavaScript"],  tags:["Frontend","Backend","Deployment"] },
  { id:"ml",      name:"Machine Learning Basics",      level:"Intermediate", needs:["Python","Math"],      tags:["AI","Data","Prediction"] },
  { id:"db",      name:"Database Design & SQL",        level:"Beginner",     needs:["SQL"],                tags:["MySQL","Design","Queries"] },
  { id:"android", name:"Android App Development",      level:"Intermediate", needs:["Java","OOP"],         tags:["Mobile","SDK","UI"] },
  { id:"spring",  name:"Spring Boot & REST APIs",      level:"Advanced",     needs:["Java","SQL"],         tags:["Backend","APIs","Microservices"] },
  { id:"devops",  name:"DevOps & Cloud Basics",        level:"Advanced",     needs:["Linux","Git"],        tags:["Docker","CI/CD","Cloud"] },
  { id:"nlp",     name:"Natural Language Processing",  level:"Advanced",     needs:["Python","Math"],      tags:["AI","Text","Models"] },
  { id:"reactjs", name:"React.js Deep Dive",           level:"Intermediate", needs:["JavaScript","HTML"],  tags:["Frontend","Components","State"] },
  { id:"sys",     name:"System Design Fundamentals",   level:"Advanced",     needs:["Java","SQL"],         tags:["Architecture","Scalability","Design"] },
];

const allSkills = ["Java","Python","JavaScript","HTML","SQL","Math","OOP","Linux","Git","DSA"];
const selected  = new Set();
let lastResults = [];

// Track which courses are in the learning path and which are completed
const pathCourses  = new Set(); // course ids added to path
const doneCourses  = new Set(); // course ids marked complete

// Activity log
const activityLog = [
  { text: "Welcome to SkillPath!", time: "Today", color: "#64748b" }
];

function addActivity(text, color) {
  activityLog.unshift({ text, time: "Just now", color: color || "#2563eb" });
  if (activityLog.length > 6) activityLog.pop();
  renderActivity();
}

function renderActivity() {
  const list = document.getElementById("activity-list");
  if (!list) return;
  list.innerHTML = activityLog.map(a => `
    <div class="activity-item">
      <div class="act-dot" style="background:${a.color}"></div>
      <div class="act-text">${a.text}</div>
      <div class="act-time">${a.time}</div>
    </div>`).join("");
}

function setupEngine() {
  courseData.forEach(c => {
    engine.addNode(c.id);
    c.needs.forEach(s => engine.link(s, c.id));
  });
  engine.link("Java","OOP");   engine.link("OOP","dsa");
  engine.link("OOP","android");engine.link("DSA","sys");
  engine.link("Python","ml");  engine.link("ml","nlp");
  engine.link("SQL","spring"); engine.link("JavaScript","reactjs");
  engine.link("HTML","webdev");engine.link("Math","ml");
  engine.link("Linux","devops");engine.link("Git","devops");
  engine.link("Java","spring");engine.link("Java","sys");
}

function matchColor(score) {
  if (score >= 80) return "#16a34a";
  if (score >= 60) return "#2563eb";
  if (score >= 40) return "#d97706";
  return "#94a3b8";
}
function matchLabel(score) {
  if (score >= 80) return "Excellent Match";
  if (score >= 60) return "Good Match";
  if (score >= 40) return "Partial Match";
  return "Low Match";
}
function levelColor(level) {
  if (level === "Beginner")     return "#16a34a";
  if (level === "Intermediate") return "#2563eb";
  return "#7c3aed";
}

// ── Skill filter ──
function filterSkills(query) {
  document.querySelectorAll(".skill-pill").forEach(el => {
    el.style.display = el.dataset.skill.toLowerCase().includes(query.toLowerCase()) ? "" : "none";
  });
}

// ── Toggle skill ──
function toggleSkill(skill, el) {
  selected.has(skill) ? selected.delete(skill) : selected.add(skill);
  el.classList.toggle("selected");
  const btn = document.getElementById("find-btn");
  btn.disabled = selected.size === 0;

  const row = document.getElementById("selected-row");
  row.textContent = selected.size === 0
    ? "No skills selected yet"
    : [...selected].join("  ·  ");

  document.getElementById("d-skills").textContent = selected.size;
}

// ── Toggle course in learning path ──
function togglePath(courseId, checkbox) {
  const course = courseData.find(c => c.id === courseId);
  if (!course) return;

  if (checkbox.checked) {
    pathCourses.add(courseId);
    addActivity(`Added "${course.name}" to learning path`, "#0d9488");
  } else {
    pathCourses.delete(courseId);
    doneCourses.delete(courseId); // remove from done too if removed from path
    addActivity(`Removed "${course.name}" from path`, "#94a3b8");
  }

  // Update card highlight
  const card = checkbox.closest(".course-card");
  if (card) card.classList.toggle("in-path", checkbox.checked);

  updatePathPage();
  updateProgressPage();
  updateDashboardStats();
}

// ── Toggle course done ──
function toggleDone(courseId, btn) {
  const course = courseData.find(c => c.id === courseId);
  if (!course) return;

  if (doneCourses.has(courseId)) {
    doneCourses.delete(courseId);
    btn.textContent = "Mark done";
    btn.classList.remove("is-done");
    addActivity(`Resumed "${course.name}"`, "#d97706");
  } else {
    doneCourses.add(courseId);
    btn.textContent = "✓ Done";
    btn.classList.add("is-done");
    addActivity(`Completed "${course.name}"`, "#16a34a");
  }

  updatePathPage();
  updateProgressPage();
  updateDashboardStats();
}

// ── Recommend ──
function recommend() {
  const dist   = engine.find([...selected]);
  const ranked = courseData
    .map(c => ({ ...c, score: Math.max(0, 100 - (dist[c.id]??999)*20) }))
    .filter(c => c.score > 0)
    .sort((a,b) => b.score - a.score);

  lastResults = ranked;

  document.getElementById("d-reco").textContent = ranked.length;
  document.getElementById("result-count").textContent = ranked.length + " courses found";

  // Show path hint
  const hint = document.getElementById("path-hint");
  if (ranked.length > 0) hint.classList.add("visible");

  const area = document.getElementById("results-area");
  if (ranked.length === 0) {
    area.innerHTML = `<div class="empty-state"><div class="empty-icon">🔍</div>No matching courses found.<br>Try selecting different skills.</div>`;
    return;
  }

  area.innerHTML = `<div class="course-list">${ranked.map((c,i) => `
    <div class="course-card ${pathCourses.has(c.id)?'in-path':''}" id="card-${c.id}" style="animation-delay:${i*50}ms">
      <div style="display:flex;flex-direction:column;align-items:center;gap:3px;flex-shrink:0;">
        <input type="checkbox" class="path-checkbox" ${pathCourses.has(c.id)?'checked':''} onchange="togglePath('${c.id}', this)" title="Add to Learning Path"/>
        <span class="add-path-label">${pathCourses.has(c.id)?'In path':'Add'}</span>
      </div>
      <div class="course-rank ${i<3?'top':''}">#${i+1}</div>
      <div class="course-info">
        <div class="course-name">${c.name}</div>
        <div class="course-meta" style="color:${levelColor(c.level)};font-weight:600;">${c.level}</div>
        <div class="course-tags">${c.tags.map(t=>`<span class="tag">${t}</span>`).join("")}</div>
      </div>
      <div class="match-block">
        <div class="match-pct" style="color:${matchColor(c.score)}">${c.score}%</div>
        <div class="match-lbl">${matchLabel(c.score)}</div>
        <div class="mini-bar"><div class="mini-fill" style="width:${c.score}%;background:${matchColor(c.score)}"></div></div>
      </div>
    </div>`).join("")}
  </div>`;

  // Dashboard strip
  const strip = document.getElementById("dash-strip");
  const hint2 = document.getElementById("dash-strip-hint");
  if (hint2) hint2.style.display = "none";
  strip.innerHTML = ranked.slice(0,4).map(c => `
    <div class="strip-card">
      <div class="strip-level" style="color:${levelColor(c.level)}">${c.level}</div>
      <div class="strip-name">${c.name}</div>
      <div class="strip-match">${c.score}% match</div>
    </div>`).join("");

  addActivity(`Got ${ranked.length} course recommendations`, "#2563eb");
  updateDashboardStats();
}

// ── Build learning path page ──
function updatePathPage() {
  const container = document.getElementById("path-steps");
  const pathArr = [...pathCourses];

  if (pathArr.length === 0) {
    container.innerHTML = `<div style="color:var(--muted);font-size:0.82rem;padding:20px 0;">
      Go to <strong>Find Courses</strong>, get recommendations, then tick the checkbox on any course to add it here.
    </div>`;
    document.getElementById("path-pct").textContent  = "0%";
    document.getElementById("path-bar").style.width  = "0%";
    document.getElementById("path-steps-done").textContent = "0/0";
    return;
  }

  // Get course objects for path
  const pathCourseObjs = pathArr.map(id => courseData.find(c => c.id === id)).filter(Boolean);

  container.innerHTML = pathCourseObjs.map((c, i) => {
    const isDone   = doneCourses.has(c.id);
    const isActive = !isDone && pathCourseObjs.slice(0, i).every(prev => doneCourses.has(prev.id));
    const state    = isDone ? "done" : isActive ? "active" : "locked";
    const label    = isDone ? "Completed" : isActive ? "In Progress" : "Upcoming";
    const isLast   = i === pathCourseObjs.length - 1;
    const btnText  = isDone ? "✓ Done" : "Mark done";

    return `
      <div class="path-step">
        <div class="step-line-wrap">
          <div class="step-dot ${state}">${isDone ? '✓' : (i+1)}</div>
          ${!isLast ? `<div class="step-connector ${isDone?'done':''}"></div>` : ''}
        </div>
        <div class="step-body">
          <div class="step-body-left">
            <div class="step-title">${c.name}</div>
            <div class="step-detail">Level: ${c.level} &nbsp;·&nbsp; ${c.tags[0]}, ${c.tags[1]||''}</div>
            <span class="step-badge ${state}">${label}</span>
          </div>
          <button class="toggle-done-btn ${isDone?'is-done':''}" onclick="toggleDone('${c.id}', this)">${btnText}</button>
        </div>
      </div>`;
  }).join("");

  // Recalculate progress
  const total    = pathCourseObjs.length;
  const doneCount = pathCourseObjs.filter(c => doneCourses.has(c.id)).length;
  const pct      = total > 0 ? Math.round((doneCount / total) * 100) : 0;

  document.getElementById("path-pct").textContent  = pct + "%";
  document.getElementById("path-bar").style.width  = pct + "%";
  document.getElementById("path-steps-done").textContent = `${doneCount}/${total}`;
}

// ── Build progress tracker page ──
function updateProgressPage() {
  const pathArr = [...pathCourses];
  const total   = pathArr.length;
  const doneCount = pathArr.filter(id => doneCourses.has(id)).length;
  const pct     = total > 0 ? Math.round((doneCount / total) * 100) : 0;
  const remaining = total - doneCount;

  document.getElementById("prog-overall").textContent    = pct + "%";
  document.getElementById("prog-done-count").textContent = doneCount;
  document.getElementById("prog-remaining").textContent  = remaining;
  document.getElementById("prog-total-label").textContent = `of ${total} in path`;

  const trackList = document.getElementById("track-list");

  if (total === 0) {
    trackList.innerHTML = `<div style="color:var(--muted);font-size:0.82rem;padding:20px 0;text-align:center;">
      Add courses to your learning path first, then mark them complete to track progress here.
    </div>`;
    return;
  }

  trackList.innerHTML = pathArr.map((id, i) => {
    const c = courseData.find(co => co.id === id);
    if (!c) return "";
    const isDone   = doneCourses.has(id);
    const isActive = !isDone && pathArr.slice(0, i).every(prev => doneCourses.has(prev));
    const iconClass = isDone ? "done" : isActive ? "active" : "locked";
    const icon      = isDone ? "✅" : isActive ? "▶️" : "🔒";
    const statusText = isDone ? "Completed · 100%" : isActive ? "In Progress · 0%" : "Not started";
    const fillPct    = isDone ? 100 : 0;
    const fillColor  = isDone ? "var(--green)" : "var(--blue)";

    return `
      <div class="track-item">
        <div class="track-icon ${iconClass}">${icon}</div>
        <div class="track-info">
          <div class="track-name">${c.name}</div>
          <div class="track-status">${statusText}</div>
        </div>
        <div class="track-bar"><div class="track-fill" style="width:${fillPct}%;background:${fillColor}"></div></div>
      </div>`;
  }).join("");
}

// ── Update dashboard stats ──
function updateDashboardStats() {
  const total     = pathCourses.size;
  const doneCount = [...pathCourses].filter(id => doneCourses.has(id)).length;
  const pct       = total > 0 ? Math.round((doneCount / total) * 100) : 0;

  document.getElementById("d-path-count").textContent = total;
  document.getElementById("d-prog").textContent       = pct + "%";
  document.getElementById("d-prog-sub").textContent   =
    total === 0 ? "No courses added yet" : `${doneCount} of ${total} done`;
}

// ── Page navigation ──
const pageMeta = {
  dashboard: { title:"Dashboard",        sub:"Welcome back — here's your learning overview" },
  recommend: { title:"Find Courses",     sub:"Select your skills and get personalised recommendations" },
  path:      { title:"Learning Path",   sub:"Your chosen courses — mark them done as you complete them" },
  progress:  { title:"Progress Tracker", sub:"Track your learning journey and course completion" },
};

function showPage(id, navEl) {
  document.querySelectorAll(".page").forEach(p => p.classList.remove("active"));
  document.querySelectorAll(".nav-item").forEach(n => n.classList.remove("active"));
  document.getElementById("page-"+id).classList.add("active");
  if (navEl) navEl.classList.add("active");
  const m = pageMeta[id];
  document.getElementById("page-title").textContent = m.title;
  document.getElementById("page-sub").textContent   = m.sub;

  // Re-render path/progress when navigating to them
  if (id === "path")     updatePathPage();
  if (id === "progress") updateProgressPage();
}

// ── Init ──
function init() {
  setupEngine();

  const grid = document.getElementById("skills-grid");
  allSkills.forEach(skill => {
    const el = document.createElement("div");
    el.className = "skill-pill";
    el.textContent = skill;
    el.dataset.skill = skill;
    el.onclick = () => toggleSkill(skill, el);
    grid.appendChild(el);
  });

  renderActivity();
}

init();