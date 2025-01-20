### **IndicatorBottomNavigationView**  
The **IndicatorBottomNavigationView** library allows you to create a custom **BottomNavigationView** with an indicator that highlights the selected item.

[![](https://jitpack.io/v/MehdiSekoba/IndicatorBottomNavigationView.svg)](https://jitpack.io/#MehdiSekoba/IndicatorBottomNavigationView)
---
### **Features**  
- Support for **Material Design 3**.  
- Highlight indicator for active navigation items.  
- Fully customizable design.  
- Perfect for modern UI designs with smooth animations.  
---

### **Design Inspiration**  
The design for **IndicatorBottomNavigationView** was inspired by modern UI design trends, particularly focusing on smooth animations and a clean, intuitive user interface. The following Pinterest link showcases the inspiration behind the layout and overall style:

[Design Inspiration on Pinterest](https://www.pinterest.com/pin/276689970851178936/)

---

### **Add to Your Project**
#### 1. Add the JitPack repository  
Add the following line to your root `settings.gradle` or `build.gradle` file:  
```kotlin
maven { url = uri("https://jitpack.io") }
```

#### 2. Add the library dependency  
In your module `build.gradle` file (usually `app`):  
```kotlin
implementation("com.github.MehdiSekoba:IndicatorBottomNavigationView:v1.0.1")
```

---

### **How to Use**

#### 1. Add to XML  
In your `activity_main.xml` (or any layout file), add the following:  
```xml
    <ir.mehdisekoba.indicatorbottomnavigationview.IndicatorBottomNavigationView
        android:id="@+id/bottomNav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:menu="@menu/main_menu"
        android:layout_marginBottom="38dp"
        android:background="@drawable/bg_navigation"
        android:layout_marginHorizontal="12dp"
        android:clipToPadding="true"
        app:indicatorShadowVisible="true"
        app:indicatorHeaderColor="@color/Vivid_Red"
        app:indicatorHeaderHeight="4dp"
        app:labelVisibilityMode="unlabeled"
        app:itemIconTint="@color/item_bottom_nav_tint"
        app:indicatorShadowColor="@color/Vivid_Red"
        app:itemTextColor="@color/white"
        />
```

#### 2. Handle Item Selection in Code  
In your `MainActivity` or any relevant Activity/Fragment:  
```kotlin
class MainActivity : AppCompatActivity() {
    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Other
    private lateinit var navHost: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Init nav host
        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        //Bottom nav menu
        binding.bottomNav.apply {
            itemRippleColor= ColorStateList.valueOf(Color.TRANSPARENT)
            setupWithNavController(navHost.navController)
        }
        //Gone bottom menu
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.apply {
                when (destination.id) {
                    R.id.homeFragment -> bottomNav.isVisible = true
                    R.id.shopFragment -> bottomNav.isVisible = true
                    R.id.favoriteFragment -> bottomNav.isVisible = true
                    R.id.profileFragment -> bottomNav.isVisible = true
                    else -> bottomNav.isVisible = false
                }
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
```

---

### **Requirements**
- Minimum Android version: **Android 7.0 (API Level 24)**.  
- Gradle version: 8.0 or above.  
- Kotlin DSL support.

---

### **Preview**
Here’s a preview of how the library looks in action:
![Sample GIF](https://github.com/MehdiSekoba/IndicatorBottomNavigationView/blob/master/app/art/sample.gif?raw=true)

---

### **Contributing**  
We welcome contributions to improve this library!  
1. Fork the repository.  
2. Make your changes.  
3. Submit a **Pull Request**.  

---

### **License**  
This project is released under the [MIT License](https://opensource.org/licenses/MIT).  
