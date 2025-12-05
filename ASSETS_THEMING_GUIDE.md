# SafeDocs Asset & Theming Guide

## 📁 Asset Structure Overview

All assets are organized in the `res/` directory following Android best practices:

```
res/
├── drawable/           # Vector drawables & shape definitions
├── drawable-anydpi/    # Adaptive icons
├── values/             # Colors, dimensions, strings, themes
├── values-night/       # Dark theme overrides
├── mipmap-*/           # App launcher icons (multiple densities)
└── layout/             # UI layouts
```

---

## 🎨 Icon Assets (VectorDrawables)

All icons are created as **XML VectorDrawables** for scalability and small file size.

### Navigation Icons (Bottom Navigation)
- `ic_nav_documents.xml` - Documents/folder icon
- `ic_nav_shared.xml` - Sharing/broadcast icon
- `ic_nav_family.xml` - Family/people icon
- `ic_nav_profile.xml` - User/profile icon

### Action Icons (Toolbar & Menus)
- `ic_add.xml` - Create/add action
- `ic_search.xml` - Search functionality
- `ic_share.xml` - Share document
- `ic_download.xml` - Download file
- `ic_upload.xml` - Upload document
- `ic_delete.xml` - Remove/delete
- `ic_edit.xml` - Edit document
- `ic_more.xml` - More options (overflow menu)
- `ic_lock.xml` - Security/encryption indicator

### Status Icons
- `ic_success.xml` - Success/checkmark
- `ic_error.xml` - Error indicator
- `ic_warning.xml` - Warning alert
- `ic_info.xml` - Information

### File Type Icons
- `ic_file_pdf.xml` - PDF document (red)
- `ic_file_image.xml` - Image file (orange)
- `ic_file_docx.xml` - Word document (blue)

**Note:** Expand with additional file types (xlsx, pptx, txt, zip, etc.) by creating similar VectorDrawables.

---

## 🎭 Shape & Drawable Resources

### Button & Card Shapes
- `shape_button_primary.xml` - Primary button background (rounded rectangle)
- `shape_card_background.xml` - Card container (bordered rounded rectangle)
- `shape_gradient_background.xml` - Gradient background for hero sections

### State Selectors
- `selector_nav_item.xml` - Active/inactive state for bottom nav items

---

## 🎨 Color Palette (Material 3)

### Primary Brand Colors
```
primary_200:  #FFB8D8FF (Light)
primary_500:  #FF3B5EDB (Main)
primary_700:  #FF2D4AB5 (Dark)
```

### Secondary Colors
```
secondary_200: #FFFFC7D9
secondary_500: #FFFF5E8B
secondary_700: #FFCC476B
```

### Tertiary Colors
```
tertiary_200: #FFFFB8C7
tertiary_500: #FFFF7B7B
tertiary_700: #FFCC6363
```

### Surface & Background
```
surface:           #FFFBF8FE (Light) / #FF1F1B24 (Dark)
surface_container: #FFEDE9F5 (Light) / #FF2B2630 (Dark)
background:        #FFFBF8FE (Light) / #FF121212 (Dark)
```

### Status Colors
```
error:   #FFFF5252
warning: #FFFFC107
success: #FF4CAF50
info:    #FF2196F3
```

### Text Colors
```
text_primary:   #FF1D1B1F (Light) / #FFFBF8FE (Dark)
text_secondary: #FF49454F (Light) / #FFCAC4D0 (Dark)
text_tertiary:  #FF79747E (Light) / #FFA6A9AE (Dark)
```

---

## 📏 Dimension Tokens

### Corner Radii
```
corner_radius_extra_small: 4dp
corner_radius_small:       8dp
corner_radius_medium:      12dp
corner_radius_large:       16dp
corner_radius_extra_large: 28dp
```

### Spacing Scale
```
spacing_extra_small: 4dp
spacing_small:       8dp
spacing_medium:      16dp
spacing_large:       24dp
spacing_extra_large: 32dp
```

### Elevation (Shadow Depth)
```
elevation_small:       2dp
elevation_medium:      4dp
elevation_large:       8dp
elevation_extra_large: 16dp
```

### Icon Sizes
```
icon_size_small:       16dp
icon_size_medium:      24dp (default)
icon_size_large:       32dp
icon_size_extra_large: 48dp
```

### Text Sizes
```
text_size_headline: 32sp
text_size_title:    24sp
text_size_body:     16sp
text_size_label:    14sp
text_size_caption:  12sp
```

---

## 🎨 Theme Variants

### Light Theme (Default)
**File:** `values/themes.xml` - `Theme.SafeDocs`

Applied by default on all devices. Features:
- Light backgrounds
- Dark text
- Light status bar (Material 3 compliant)
- Light navigation bar

### Dark Theme
**File:** `values/themes.xml` - `Theme.SafeDocs.Dark`

Can be used explicitly or automatically on devices in dark mode.

### Night Mode (Auto Dark Theme)
**File:** `values-night/themes.xml`

Automatically applied when device is in dark mode or battery saver is enabled.
- Overrides colors from `values-night/colors.xml`
- Inherits dark theme styles

---

## 🎨 Text Styles

Pre-defined typography styles for consistent UI:

- `TextStyle.SafeDocs.Headline` - Large titles (32sp, bold)
- `TextStyle.SafeDocs.Title` - Section headers (24sp, bold)
- `TextStyle.SafeDocs.Body` - Body text (16sp, regular)
- `TextStyle.SafeDocs.Label` - UI labels (14sp, regular)

Usage in layouts:
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    style="@style/TextStyle.SafeDocs.Title"
    android:text="My Documents" />
```

---

## 🎛️ Component Styles

### Button Style
**Style:** `Widget.SafeDocs.Button.Primary`

```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    style="@style/Widget.SafeDocs.Button.Primary"
    android:text="Upload Document" />
```

### Card Style
**Style:** `Widget.SafeDocs.CardView`

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    style="@style/Widget.SafeDocs.CardView">
    <!-- Card content -->
</com.google.android.material.card.MaterialCardView>
```

### BottomNavigationView Style
**Style:** `Widget.SafeDocs.BottomNavigationView`

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_nav"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    style="@style/Widget.SafeDocs.BottomNavigationView"
    app:menu="@menu/menu_bottom_nav" />
```

### Toolbar Style
**Style:** `Widget.SafeDocs.Toolbar`

```xml
<com.google.android.material.appbar.MaterialToolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    style="@style/Widget.SafeDocs.Toolbar"
    android:title="@string/app_name" />
```

---

## 📱 Menu Resources

### Bottom Navigation Menu
**File:** `menu/menu_bottom_nav.xml`

Defines the four main navigation destinations:
1. Documents
2. Shared with Me
3. Family
4. Profile

Each menu item includes a custom icon and localized label string.

---

## 🌍 String Resources

**File:** `values/strings.xml`

Includes:
- Navigation labels
- Action labels (add, search, share, delete, etc.)
- Common UI labels (loading, error, success, etc.)
- Empty state messages
- Login screen text

All strings are localization-ready for multi-language support.

---

## 🚀 Using Assets in Code

### Reference Icons in Java/Kotlin
```kotlin
// Set icon programmatically
button.setIcon(R.drawable.ic_add)
menuItem.setIcon(R.drawable.ic_share)
imageView.setImageResource(R.drawable.ic_nav_documents)
```

### Use Dimensions in Layouts
```xml
<ImageView
    android:layout_width="@dimen/icon_size_large"
    android:layout_height="@dimen/icon_size_large"
    android:src="@drawable/ic_add" />

<View
    android:layout_width="match_parent"
    android:layout_height="@dimen/spacing_medium" />
```

### Apply Text Styles
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    style="@style/TextStyle.SafeDocs.Title"
    android:text="@string/nav_documents" />
```

### Use Colors Programmatically
```kotlin
val primaryColor = ContextCompat.getColor(this, R.color.primary_500)
val backgroundColor = ContextCompat.getColor(this, R.color.background)
```

---

## 📝 For Frontend Developers

### Integration Checklist

- [ ] Review all icon VectorDrawables (may need refinement for branding)
- [ ] Test color palette on both light and dark themes
- [ ] Validate text styles against design specifications
- [ ] Create additional file type icons as needed
- [ ] Add placeholder/empty state illustrations
- [ ] Create app launcher icon adaptive layer (if needed)
- [ ] Generate Lottie animations for loading states (optional)
- [ ] Test accessibility contrast ratios (WCAG AA minimum)

### Extending Assets

To add new icons:
1. Create a new VectorDrawable XML in `drawable/`
2. Name it following the pattern: `ic_[category]_[name].xml`
3. Use 24dp viewportSize for consistency
4. Reference in layouts using `@drawable/ic_[name]`

To customize colors:
1. Modify hex values in `values/colors.xml` (light) and `values-night/colors.xml` (dark)
2. Maintain Material 3 contrast compliance
3. Test on both themes

---

## 🔗 References

- [Material Design 3 Guidelines](https://m3.material.io/)
- [Android VectorDrawable Docs](https://developer.android.com/guide/topics/graphics/vector-drawable-resource)
- [Android Theming](https://developer.android.com/guide/topics/ui/look-and-feel/themes)

---

Generated: 2025-12-04 | SafeDocs Project
