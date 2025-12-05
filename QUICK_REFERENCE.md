# SafeDocs Design System - Quick Reference Card

## 🎨 Color Palette

### Primary Brand
- **Primary 500** `#3B5EDB` - Main brand color (buttons, highlights)
- **Primary 700** `#2D4AB5` - Darker variant (pressed states)
- **Primary 200** `#B8D8FF` - Light variant (backgrounds)

### Secondary Accent
- **Secondary 500** `#FF5E8B` - Accent color
- **Secondary 700** `#CC476B` - Darker accent

### Status Colors
- **Success** `#4CAF50` - ✓ Positive actions, confirmations
- **Error** `#FF5252` - ✗ Errors, destructive actions
- **Warning** `#FFC107` - ⚠ Alerts, cautions
- **Info** `#2196F3` - ℹ Informational messages

### Neutrals
- **Text Primary** `#1D1B1F` - Main text (light) / `#FFFBF8FE` (dark)
- **Text Secondary** `#49454F` - Secondary text (light) / `#FFCAC4D0` (dark)
- **Surface** `#FFFBF8FE` (light) / `#FF1F1B24` (dark)
- **Background** `#FFFBF8FE` (light) / `#FF121212` (dark)
- **Outline** `#79747E` - Borders, dividers

---

## 📏 Spacing Scale (dp)

```
4dp  ← xs (minimal gaps)
8dp  ← s  (small spacing)
16dp ← m  (standard padding)
24dp ← l  (large spacing)
32dp ← xl (extra large)
```

**Common Usage:**
- Card padding: 16dp
- Button margins: 8dp
- Screen margins: 16-24dp
- Divider thickness: 1dp

---

## 🔘 Corner Radii (dp)

```
4dp  ← xs (small elements, chips)
8dp  ← s  (buttons, text fields)
12dp ← m  (cards, surfaces)
16dp ← l  (large cards)
28dp ← xl (FAB, badges)
```

---

## 📝 Typography

| Style | Size | Weight | Usage |
|-------|------|--------|-------|
| **Headline** | 32sp | Bold | Page titles |
| **Title** | 24sp | Bold | Section headers |
| **Body** | 16sp | Regular | Content text |
| **Label** | 14sp | Regular | UI labels, captions |
| **Caption** | 12sp | Regular | Fine print |

---

## 🎯 Icon Sizing

```
16dp ← small (badges, indicators)
24dp ← medium (standard, nav)
32dp ← large (featured icons)
48dp ← extra large (hero icons)
```

**Icon Library:** 24 custom VectorDrawable icons
- Bottom nav (4): documents, shared, family, profile
- Actions (9): add, search, share, download, upload, delete, edit, more, lock
- Status (4): success, error, warning, info
- File types (3): PDF, image, Word

---

## 🎭 Component Elevation (dp)

```
2dp  ← small (subtle cards)
4dp  ← medium (standard cards)
8dp  ← large (important cards, dialogs)
16dp ← extra large (FABs, toasts)
```

---

## 🔧 Design Tokens Files

| File | Purpose |
|------|---------|
| `values/colors.xml` | Light theme colors |
| `values-night/colors.xml` | Dark theme colors |
| `values/dimensions.xml` | All size tokens |
| `values/design_tokens.xml` | Quick reference |
| `values/themes.xml` | Light & dark theme definitions |
| `values-night/themes.xml` | Auto dark mode |
| `values/strings.xml` | Localizable text |

---

## 📱 Drawable Resources by Category

### Navigation Icons
```
ic_nav_documents.xml    📄
ic_nav_shared.xml       👥
ic_nav_family.xml       👨‍👩‍👧‍👦
ic_nav_profile.xml      👤
```

### Action Icons
```
ic_add.xml              ➕
ic_search.xml           🔍
ic_share.xml            📤
ic_download.xml         ⬇️
ic_upload.xml           ⬆️
ic_delete.xml           🗑️
ic_edit.xml             ✏️
ic_more.xml             ⋮
ic_lock.xml             🔒
```

### Status Icons
```
ic_success.xml          ✓
ic_error.xml            ✗
ic_warning.xml          ⚠
ic_info.xml             ℹ
```

### File Types
```
ic_file_pdf.xml         PDF 🔴
ic_file_image.xml       IMG 🟠
ic_file_docx.xml        DOC 🔵
```

### Shapes
```
shape_button_primary.xml      (rounded rect)
shape_card_background.xml     (bordered rect)
shape_gradient_background.xml (gradient rect)
```

---

## 🎯 Component Styles

### Pre-styled Components

```kotlin
// Button
style="@style/Widget.SafeDocs.Button.Primary"

// Card
style="@style/Widget.SafeDocs.CardView"

// Toolbar
style="@style/Widget.SafeDocs.Toolbar"

// Bottom Navigation
style="@style/Widget.SafeDocs.BottomNavigationView"
```

### Pre-styled Text

```kotlin
// Headline
style="@style/TextStyle.SafeDocs.Headline"

// Title
style="@style/TextStyle.SafeDocs.Title"

// Body
style="@style/TextStyle.SafeDocs.Body"

// Label
style="@style/TextStyle.SafeDocs.Label"
```

---

## 💡 Usage Examples

### Layout with Icon
```xml
<ImageView
    android:layout_width="@dimen/icon_size_large"
    android:layout_height="@dimen/icon_size_large"
    android:src="@drawable/ic_add"
    android:tint="@color/primary_500" />
```

### Styled Button
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="@string/action_download"
    app:icon="@drawable/ic_download"
    style="@style/Widget.SafeDocs.Button.Primary" />
```

### Styled Card
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="@dimen/spacing_large"
    style="@style/Widget.SafeDocs.CardView">
    <!-- content -->
</com.google.android.material.card.MaterialCardView>
```

### Styled Text
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    style="@style/TextStyle.SafeDocs.Title"
    android:text="@string/nav_documents" />
```

---

## 🌓 Theme Behavior

**Automatic:** Light/dark theme switches based on system setting
- Light Mode: Uses `values/` resources
- Dark Mode: Uses `values-night/` resources
- No code changes needed—Material3 handles it!

---

## ✅ Accessibility Checklist

- [ ] Text contrast ≥ 4.5:1 (WCAG AA)
- [ ] Icon tinting for visibility
- [ ] Meaningful content descriptions
- [ ] Touch targets ≥ 48dp × 48dp
- [ ] No color-only information
- [ ] Sufficient spacing between elements

---

## 📚 Quick Links

- **Full Guide:** `ASSETS_THEMING_GUIDE.md`
- **Setup Summary:** `SETUP_SUMMARY.md`
- **Example Layout:** `example_layout_usage.xml`
- **Design Tokens:** `values/design_tokens.xml`

---

**Material Design 3 Compliant** ✓
**Dark Mode Ready** ✓
**Localization Ready** ✓
**Accessibility Ready** ✓

Last Updated: 2025-12-04
