# Blackdot EMS - Design Style Guide

**Version 2.0 - Minimal & Glossy Design System**  
**Unified CSS Architecture**

## 🎨 Design Philosophy

The Blackdot Employee Management System follows a **minimal and glossy** design approach that emphasizes:

- **Clean aesthetics** - Reduced visual clutter with ample white space
- **Glass morphism** - Subtle transparency and blur effects for depth
- **Gradient accents** - Professional Bootstrap color transitions for visual interest
- **Smooth interactions** - Micro-animations and hover effects
- **Compact density** - Efficient use of space without overwhelming users
- **Consistency** - Unified CSS architecture across all admin pages

---

## 📁 CSS Architecture

### File Structure

```
css/
├── admin-pages.css     # ⭐ THE ONLY CSS FILE - Contains EVERYTHING ⭐
└── [NO OTHER FILES]    # ❌ NEVER create additional CSS files
```

### Usage Pattern

**All admin pages MUST include ONLY this file:**

```html
<!-- Single CSS File - Everything in admin-pages.css -->
<link rel="stylesheet" th:href="@{/css/admin-pages.css}" />
```

**admin-pages.css includes EVERYTHING:**

- **Layout**: Header, Sidebar, Main content area, Navigation menu
- **Dashboard**: KPI cards, Analytics charts, Circular progress, Task breakdown
- **Components**: Cards, Tables, Buttons, Forms, Modals, Badges, Alerts
- **Responsive**: All breakpoints for mobile/tablet/desktop
- **Utilities**: Spacing, colors, shadows, animations

**Benefits:**

- ✅ Single source of truth - ONE file for ALL styles
- ✅ Perfect consistency across entire application
- ✅ No CSS conflicts or duplicates
- ✅ Easier maintenance and updates
- ✅ Faster page loads (one CSS file)
- ✅ Simpler deployment

---

## 📐 Layout & Spacing

### Grid System

```css
/* Card Grids */
grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
gap: 1rem;

/* Analytics Grids */
grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
gap: 1rem;

/* Insight Grids */
grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
gap: 1rem;
```

### Spacing Scale

```css
/* Use these consistent spacing values */
--spacing-xs: 0.25rem; /* 4px */
--spacing-sm: 0.5rem; /* 8px */
--spacing-md: 0.75rem; /* 12px */
--spacing-lg: 1rem; /* 16px */
--spacing-xl: 1.25rem; /* 20px */
--spacing-2xl: 1.5rem; /* 24px */
```

### Margins & Padding

- **Card padding**: `1rem` (standard), `0.875rem` (compact)
- **Section margins**: `1.5rem` (between major sections)
- **Component gaps**: `0.75rem` - `1rem` (between items)
- **Header padding**: `0.875rem 1rem`

---

## 🎨 Color System

### Primary Colors

```css
--primary-color: #2c3e50; /* Dark blue-grey */
--secondary-color: #2c3e50; /* Same as primary */
--success-color: #27ae60; /* Green */
--warning-color: #f39c12; /* Orange */
--danger-color: #e74c3c; /* Red */
--info-color: #17a2b8; /* Cyan */
```

### Neutral Colors

```css
--light-bg: #f8f9fa; /* Light background */
--dark-text: #2c3e50; /* Main text */
--border-color: #dee2e6; /* Borders */
--muted-text: #6c757d; /* Secondary text */
```

### Gradient Palette

#### Badge & Icon Gradients

```css
/* Primary - Dark Professional */
background: linear-gradient(145deg, #2c3e50, #1a252f);

/* Success - Professional Green */
background: linear-gradient(145deg, #27ae60, #229954);

/* Warning - Professional Orange */
background: linear-gradient(145deg, #f39c12, #e67e22);

/* Danger - Professional Red */
background: linear-gradient(145deg, #e74c3c, #c0392b);

/* Info - Professional Blue */
background: linear-gradient(145deg, #3498db, #2980b9);

/* Secondary - Neutral Purple */
background: linear-gradient(145deg, #9b59b6, #8e44ad);
```

#### Background Gradients

```css
/* Card backgrounds (subtle) */
background: linear-gradient(145deg, #ffffff, #f8f9fa);

/* Header backgrounds (very subtle) */
background: linear-gradient(
  145deg,
  rgba(44, 62, 80, 0.03),
  rgba(26, 37, 47, 0.05)
);

/* Hover states */
background: linear-gradient(
  90deg,
  rgba(44, 62, 80, 0.02),
  rgba(26, 37, 47, 0.03)
);
```

---

## 📝 Typography

### Font Families

```css
font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
```

### Font Sizes (Minimal Scale)

```css
/* Headings */
--heading-xl: 1.5rem; /* 24px - Page titles */
--heading-lg: 1.25rem; /* 20px - Section titles */
--heading-md: 1.1rem; /* 17.6px - Card titles */
--heading-sm: 0.9rem; /* 14.4px - Sub-headers */

/* Body Text */
--text-base: 0.875rem; /* 14px - Normal text */
--text-sm: 0.8rem; /* 12.8px - Labels */
--text-xs: 0.75rem; /* 12px - Captions */
--text-2xs: 0.7rem; /* 11.2px - Small labels */
--text-3xs: 0.65rem; /* 10.4px - Tiny text */

/* Display Numbers */
--display-lg: 1.5rem; /* 24px - Large KPI values */
--display-md: 1.25rem; /* 20px - Medium values */
--display-sm: 1.1rem; /* 17.6px - Small values */
```

### Font Weights

```css
--weight-normal: 400;
--weight-medium: 500;
--weight-semibold: 600;
--weight-bold: 700;
```

### Line Heights

```css
--line-tight: 1; /* For numbers/headings */
--line-normal: 1.6; /* Body text */
--line-relaxed: 1.8; /* Readable paragraphs */
```

### Letter Spacing

```css
letter-spacing: 0.3px; /* Labels and small text */
letter-spacing: 0.5px; /* Uppercase text */
```

---

## 🎴 Card Components

### Standard Card

```css
.card {
  background: linear-gradient(145deg, #ffffff, #f8f9fa);
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 4px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 8px 20px rgba(0, 0, 0, 0.06);
}
```

### Card Header

```css
.card-header {
  padding: 0.875rem 1rem;
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.05),
    rgba(118, 75, 162, 0.05)
  );
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}
```

### Card Title

```css
.card-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--dark-text);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.card-title i {
  color: var(--primary-color);
  font-size: 0.85rem;
}
```

---

## 🏷️ Badges & Pills

### Icon Badges (Professional)

```css
.badge {
  width: 35px;
  height: 35px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Color variants - Professional Theme */
.badge-blue {
  background: linear-gradient(145deg, #3498db, #2980b9);
}
.badge-green {
  background: linear-gradient(145deg, #27ae60, #229954);
}
.badge-purple {
  background: linear-gradient(145deg, #9b59b6, #8e44ad);
}
.badge-orange {
  background: linear-gradient(145deg, #f39c12, #e67e22);
}
.badge-primary {
  background: linear-gradient(145deg, #2c3e50, #1a252f);
}
.badge-danger {
  background: linear-gradient(145deg, #e74c3c, #c0392b);
}
```

### Status Pills

```css
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.3rem 0.65rem;
  border-radius: 16px;
  font-size: 0.7rem;
  font-weight: 600;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.status-pill.success {
  background: linear-gradient(
    135deg,
    rgba(17, 153, 142, 0.15),
    rgba(56, 239, 125, 0.15)
  );
  color: #0d6e5f;
}

.status-pill.warning {
  background: linear-gradient(
    135deg,
    rgba(240, 147, 251, 0.15),
    rgba(245, 87, 108, 0.15)
  );
  color: #c41e3a;
}
```

### Value Badges

```css
.value-badge {
  padding: 0.3rem 0.65rem;
  background: linear-gradient(145deg, #f8f9fa, #e9ecef);
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--dark-text);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
```

---

## 📊 Progress Indicators

### Progress Bars

```css
.progress-bar {
  height: 5px;
  background: linear-gradient(145deg, #e9ecef, #f8f9fa);
  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

.progress-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Gradient fills */
.progress-fill.success {
  background: linear-gradient(90deg, #11998e 0%, #38ef7d 100%);
}

.progress-fill.warning {
  background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
}

.progress-fill.danger {
  background: linear-gradient(90deg, #fa709a 0%, #fee140 100%);
}
```

### Circular Progress

```css
.circular-progress {
  position: relative;
  width: 65px;
  height: 65px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.circular-percentage {
  font-size: 1.1rem;
  font-weight: 700;
  line-height: 1;
}
```

---

## 🔘 Buttons

### Button Sizing

**Standard Size (Primary Actions):**
All buttons should use the standard size without `btn-sm` modifier:

```html
<button class="btn btn-primary"><i class="fas fa-plus"></i> Create</button>
```

**Small Size (Table Actions Only):**
Use `btn-sm` only for action buttons within tables (edit, delete, view):

```html
<button class="btn btn-sm" title="View Details">
  <i class="fas fa-eye"></i>
</button>
```

### Primary Button (Professional Dark)

```css
.btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Primary - Dark Professional (Main Actions) */
.btn-primary {
  background: linear-gradient(145deg, #2c3e50, #1a252f);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(145deg, #1a252f, #0d1318);
  box-shadow: 0 4px 12px rgba(44, 62, 80, 0.4);
}

/* Success - Professional Green */
.btn-success {
  background: linear-gradient(145deg, #27ae60, #229954);
  color: white;
}

.btn-success:hover {
  background: linear-gradient(145deg, #229954, #1d7f43);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.4);
}

/* Danger - Professional Red */
.btn-danger {
  background: linear-gradient(145deg, #e74c3c, #c0392b);
  color: white;
}

.btn-danger:hover {
  background: linear-gradient(145deg, #c0392b, #a93226);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
}

/* Warning - Professional Orange */
.btn-warning {
  background: linear-gradient(145deg, #f39c12, #e67e22);
  color: white;
}

.btn-warning:hover {
  background: linear-gradient(145deg, #e67e22, #d35400);
  box-shadow: 0 4px 12px rgba(243, 156, 18, 0.4);
}

/* Info - Professional Blue */
.btn-info {
  background: linear-gradient(145deg, #3498db, #2980b9);
  color: white;
}

.btn-info:hover {
  background: linear-gradient(145deg, #2980b9, #21618c);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

/* Secondary - Neutral Gray */
.btn-secondary {
  background: linear-gradient(145deg, #6c757d, #5a6268);
  color: white;
}

.btn-secondary:hover {
  background: linear-gradient(145deg, #5a6268, #495057);
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.4);
}
```

### Button Groups

For action button groups, use card wrapper with flex layout:

```html
<div class="card mb-3">
  <div class="card-body">
    <div style="display: flex; flex-wrap: wrap; gap: 1rem;">
      <button class="btn btn-primary">Primary Action</button>
      <button class="btn btn-secondary">Secondary Action</button>
      <button class="btn btn-success">Success Action</button>
    </div>
  </div>
</div>
```

---

## 📋 Tables

### Modern Table

```css
.modern-table {
  width: 100%;
  border-collapse: collapse;
}

.modern-table thead {
  background: linear-gradient(
    135deg,
    rgba(102, 126, 234, 0.08),
    rgba(118, 75, 162, 0.08)
  );
}

.modern-table th {
  padding: 0.75rem 1rem;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.3px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.modern-table td {
  padding: 0.875rem 1rem;
  font-size: 0.8rem;
}

.modern-table tbody tr {
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
  transition: all 0.3s;
}

.modern-table tbody tr:hover {
  background: linear-gradient(
    90deg,
    rgba(102, 126, 234, 0.03),
    rgba(118, 75, 162, 0.03)
  );
  transform: scale(1.002);
}
```

---

## 🎯 Icons

### Icon Sizing

```css
/* Small icons (labels, inline) */
font-size: 0.7rem - 0.85rem;

/* Medium icons (badges, cards) */
font-size: 0.9rem - 1rem;

/* Large icons (featured) */
font-size: 1.1rem - 1.2rem;
```

### Icon Containers

```css
.icon-container {
  width: 28px - 35px;
  height: 28px - 35px;
  border-radius: 8px - 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}
```

---

## 🌊 Shadows & Effects

### Shadow System

```css
/* Subtle shadow (cards) */
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 4px 8px rgba(0, 0, 0, 0.04);

/* Medium shadow (elevated cards) */
box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08), 0 6px 12px rgba(0, 0, 0, 0.06);

/* Strong shadow (hover states) */
box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 8px 20px rgba(0, 0, 0, 0.06);

/* Inset shadow (progress bars) */
box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);

/* Icon shadows */
box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);

/* Drop shadow filter */
filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
```

### Glass Morphism

```css
backdrop-filter: blur(10px);
background: rgba(255, 255, 255, 0.8);
border: 1px solid rgba(255, 255, 255, 0.8);
```

---

## 🎬 Animations & Transitions

### Standard Transitions

```css
transition: all 0.3s ease; /* General purpose */
transition: transform 0.3s ease; /* Movement only */
transition: width 0.5s ease; /* Progress bars */
transition: background 0.2s ease; /* Color changes */
```

### Hover Effects

```css
/* Card lift */
.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* Button lift */
.btn:hover {
  transform: translateY(-2px);
}

/* Subtle lift */
.item:hover {
  transform: translateY(-2px);
}

/* Scale effect */
.item:hover {
  transform: scale(1.002);
}
```

### Loading States

```css
@keyframes shimmer {
  0% {
    background-position: -1000px 0;
  }
  100% {
    background-position: 1000px 0;
  }
}

.loading {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 1000px 100%;
  animation: shimmer 2s infinite;
}
```

---

## 🎨 Border Radius System

```css
--radius-sm: 6px; /* Small components */
--radius-md: 8px; /* Default */
--radius-lg: 10px; /* Badges, icons */
--radius-xl: 12px; /* Cards */
--radius-pill: 16px; /* Status pills */
--radius-circle: 50%; /* Avatars */
```

---

## 📱 Responsive Breakpoints

```css
/* Extra small devices (phones) */
@media (max-width: 576px) {
  /* Single column layouts */
  /* Font sizes: -10% */
}

/* Small devices (tablets) */
@media (max-width: 768px) {
  /* 2-column grids */
  /* Reduced spacing */
}

/* Medium devices (landscape tablets) */
@media (max-width: 992px) {
  /* Sidebar collapse */
  /* Adjusted grid columns */
}

/* Large devices (desktops) */
@media (max-width: 1200px) {
  /* Full featured layout */
}
```

---

## ⚡ Performance Guidelines

### Optimize for Performance

```css
/* Use transform instead of position changes */
transform: translateY(-3px); /* ✅ GPU accelerated */
top: -3px; /* ❌ Causes reflow */

/* Prefer opacity over visibility */
opacity: 0; /* ✅ Smooth transitions */
visibility: hidden; /* ❌ No transitions */

/* Use will-change for animations */
will-change: transform; /* Only when animating */

/* Avoid expensive properties in transitions */
transition: all 0.3s; /* ❌ Heavy */
transition: transform 0.3s; /* ✅ Lightweight */
```

---

## 🎯 Accessibility

### Color Contrast

- **Body text**: Minimum 4.5:1 contrast ratio
- **Large text (18px+)**: Minimum 3:1 contrast ratio
- **Icons**: Always pair with text labels

### Focus States

```css
.interactive:focus {
  outline: 2px solid #667eea;
  outline-offset: 2px;
  border-radius: 4px;
}
```

### Interactive Elements

```css
/* Minimum touch target size */
min-width: 44px;
min-height: 44px;

/* Keyboard navigation */
cursor: pointer;
user-select: none;
```

---

## 📦 Component Checklist

When creating new components, ensure:

- ✅ Uses glossy gradient backgrounds
- ✅ Has subtle shadows (multi-layered)
- ✅ Includes backdrop-filter blur
- ✅ Border radius: 8px - 12px
- ✅ Hover state with lift effect
- ✅ Smooth 0.3s transitions
- ✅ Compact padding (0.875rem - 1rem)
- ✅ Font sizes from minimal scale
- ✅ Color from gradient palette
- ✅ Proper spacing with design tokens

---

## 🎨 Quick Copy-Paste Templates

### Glossy Card

```html
<div class="glossy-card">
  <div class="card-header-minimal">
    <h3 class="card-title-minimal"><i class="fas fa-icon"></i> Title</h3>
  </div>
  <div class="card-body-minimal">
    <!-- Content -->
  </div>
</div>
```

### KPI Stat

```html
<div class="kpi-stat">
  <div class="kpi-header">
    <span class="kpi-label">Metric Name</span>
    <span class="kpi-badge badge-gradient-blue">
      <i class="fas fa-icon"></i>
    </span>
  </div>
  <div class="kpi-value">1,234</div>
  <div class="kpi-footer">
    <span class="kpi-trend success">
      <i class="fas fa-arrow-up"></i> 12% increase
    </span>
  </div>
</div>
```

### Gradient Button

```html
<button class="btn-gradient-primary">
  <i class="fas fa-plus"></i> Add New
</button>
```

---

## 📝 Notes

- **Consistency is key**: Use the same spacing, colors, and effects across all pages
- **Less is more**: Reduce visual clutter, let the glossy effects shine
- **Gradients sparingly**: Use on accents, not as primary backgrounds
- **Performance**: Test animations on lower-end devices
- **Accessibility**: Always maintain proper contrast and keyboard navigation

---

## 🚀 Quick Start for New Pages

### 1. HTML Template Structure

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Page Title - Blackdot EMS</title>
    <!-- Single CSS File - Everything in admin-pages.css -->
    <link rel="stylesheet" th:href="@{/css/admin-pages.css}" />
    <link
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <!-- Header -->
    <div th:replace="~{layout/admin-layout :: header}"></div>

    <!-- Sidebar -->
    <div th:replace="~{layout/admin-layout :: sidebar('menu-item')}"></div>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Your content here -->
    </main>

    <!-- Scripts -->
    <div th:replace="~{layout/admin-layout :: common-scripts}"></div>
  </body>
</html>
```

### 2. Page Header Pattern

```html
<div class="page-header">
  <div class="page-header-content">
    <div class="page-icon">
      <i class="fas fa-icon-name"></i>
    </div>
    <div>
      <h2 class="page-title">Page Title</h2>
      <p class="page-subtitle">Description</p>
    </div>
  </div>
  <button class="btn btn-primary"><i class="fas fa-plus"></i> Add New</button>
</div>
```

### 3. Common Patterns

**Filter Card:**

```html
<div class="card">
  <div class="card-body">
    <div class="filters-grid">
      <div class="filter-group">
        <label class="filter-label">
          <i class="fas fa-search"></i> Search
        </label>
        <input type="text" class="form-control" placeholder="Search..." />
      </div>
    </div>
  </div>
</div>
```

**Data Table:**

```html
<div class="card">
  <div class="table-responsive">
    <table class="table">
      <thead>
        <tr>
          <th>Column 1</th>
          <th>Column 2</th>
          <th class="text-center">Actions</th>
        </tr>
      </thead>
      <tbody>
        <!-- Table rows -->
      </tbody>
    </table>
  </div>
</div>
```

**Modal:**

```html
<div id="myModal" class="modal" style="display: none;">
  <div class="modal-content">
    <div class="modal-header">
      <h3><i class="fas fa-icon"></i> Modal Title</h3>
      <button class="modal-close" onclick="closeModal()">
        <i class="fas fa-times"></i>
      </button>
    </div>
    <div class="modal-body">
      <!-- Modal content -->
    </div>
    <div class="modal-footer">
      <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
      <button class="btn btn-primary">Save</button>
    </div>
  </div>
</div>
```

---

**Last Updated**: November 6, 2025  
**Design Version**: 2.0 - Minimal & Glossy (Unified Architecture)  
**Author**: Blackdot EMS Design Team
