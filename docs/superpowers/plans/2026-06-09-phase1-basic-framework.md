# Phase 1: 基础框架实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 App 基础架构，包括 Firebase 接入、底部导航、用户注册/登录和基础样图展示。

**Architecture:** MainActivity 作为底部导航宿主容器，托管 4 个 Fragment（样图库/考试/排行榜/我的）。认证模块独立为两个 Activity。Firebase 通过单例 FirebaseManager 统一管理，AuthRepository 封装认证逻辑。

**Tech Stack:** Java 11, Android SDK 36, Firebase Auth, Firebase Firestore, Firebase Storage, NavigationFragment, Glide

---
### 构建文件配置

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle`

- [ ] **Step 1: 更新版本目录 (libs.versions.toml)**

编辑 `gradle/libs.versions.toml`:

```toml
[versions]
agp = "9.2.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.6.1"
material = "1.10.0"
activityKtx = "1.8.0"
constraintlayout = "2.1.4"
navigationFragment = "2.7.6"
navigationUi = "2.7.6"
glide = "4.15.1"
firebaseAuth = "22.3.1"
firebaseFirestore = "24.10.1"
firebaseStorage = "20.3.0"
playServicesAuth = "20.7.0"

[libraries]
junit = { group = "junit", name = "junit", version.ref = "junit" }
ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
activity-ktx = { group = "androidx.activity", name = "activity-ktx", version.ref = "activityKtx" }
constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
navigation-fragment = { group = "androidx.navigation", name = "navigation-fragment", version.ref = "navigationFragment" }
navigation-ui = { group = "androidx.navigation", name = "navigation-ui", version.ref = "navigationUi" }
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth", version.ref = "firebaseAuth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore", version.ref = "firebaseFirestore" }
firebase-storage = { group = "com.google.firebase", name = "firebase-storage", version.ref = "firebaseStorage" }
play-services-auth = { group = "com.google.android.gms", name = "play-services-auth", version.ref = "playServicesAuth" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
google-services = { id = "com.google.gms.google-services", version = "4.4.0" }
```

- [ ] **Step 2: 更新 app/build.gradle**

编辑 `app/build.gradle`:

```groovy
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace 'com.example.sevenxiao'
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId "com.example.sevenxiao"
        minSdk 24
        targetSdk 36
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable false
            }
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}

dependencies {
    implementation libs.appcompat
    implementation libs.material
    implementation libs.constraintlayout
    implementation libs.activity.ktx
    implementation libs.navigation.fragment
    implementation libs.navigation.ui
    implementation libs.glide
    implementation libs.firebase.auth
    implementation libs.firebase.firestore
    implementation libs.firebase.storage
    implementation libs.play.services.auth

    testImplementation libs.junit
    androidTestImplementation libs.espresso.core
    androidTestImplementation libs.ext.junit
}
```

- [ ] **Step 3: 添加 google-services.json**

从 Firebase Console (https://console.firebase.google.com) 创建项目 "sevenxiao-study"，添加 Android 应用 (包名 `com.example.sevenxiao`)，下载 `google-services.json` 放置到 `app/google-services.json`。

---

### Task 1: 数据模型

**Files:**
- Create: `app/src/main/java/com/example/sevenxiao/data/model/UserModel.java`
- Create: `app/src/main/java/com/example/sevenxiao/data/model/SampleModel.java`

- [ ] **Step 1: 创建 UserModel.java**

```java
package com.example.sevenxiao.data.model;

public class UserModel {
    private String uid;
    private String email;
    private String displayName;
    private String avatarUrl;
    private long createdAt;
    private double totalScore;
    private int totalExams;
    private int totalQuestions;
    private double avgAccuracy;
    private int rank;

    public UserModel() { }

    public UserModel(String uid, String email, String displayName) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = System.currentTimeMillis();
        this.totalScore = 0;
        this.totalExams = 0;
        this.totalQuestions = 0;
        this.avgAccuracy = 0;
        this.rank = 0;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public int getTotalExams() { return totalExams; }
    public void setTotalExams(int totalExams) { this.totalExams = totalExams; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public double getAvgAccuracy() { return avgAccuracy; }
    public void setAvgAccuracy(double avgAccuracy) { this.avgAccuracy = avgAccuracy; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
```

- [ ] **Step 2: 创建 SampleModel.java**

```java
package com.example.sevenxiao.data.model;

public class SampleModel {
    private String sampleId;
    private String title;
    private String type;          // "image" or "video"
    private String category;      // 缺陷维度
    private String fileName;
    private String description;
    private String storageUrl;
    private String localAsset;    // assets 路径
    private String difficulty;    // "basic" or "advanced"
    private String[] tags;

    public SampleModel() { }

    public SampleModel(String sampleId, String title, String type, String category,
                       String fileName, String description, String localAsset) {
        this.sampleId = sampleId;
        this.title = title;
        this.type = type;
        this.category = category;
        this.fileName = fileName;
        this.description = description;
        this.localAsset = localAsset;
        this.difficulty = "basic";
    }

    // Getters and Setters
    public String getSampleId() { return sampleId; }
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStorageUrl() { return storageUrl; }
    public void setStorageUrl(String storageUrl) { this.storageUrl = storageUrl; }

    public String getLocalAsset() { return localAsset; }
    public void setLocalAsset(String localAsset) { this.localAsset = localAsset; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
}
```

---

### Task 2: Firebase 管理器

**Files:**
- Create: `app/src/main/java/com/example/sevenxiao/data/remote/FirebaseManager.java`
- Create: `app/src/main/java/com/example/sevenxiao/data/remote/AuthRepository.java`

- [ ] **Step 1: 创建 FirebaseManager.java**

```java
package com.example.sevenxiao.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() { return auth; }
    public FirebaseFirestore getFirestore() { return firestore; }
    public FirebaseStorage getStorage() { return storage; }
}
```

- [ ] **Step 2: 创建 AuthRepository.java**

```java
package com.example.sevenxiao.data.remote;

import androidx.annotation.NonNull;

import com.example.sevenxiao.data.model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public AuthRepository() {
        FirebaseManager fm = FirebaseManager.getInstance();
        this.auth = fm.getAuth();
        this.firestore = fm.getFirestore();
    }

    /** 注册新用户 */
    public Task<AuthResult> register(String email, String password, String displayName) {
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            UserModel userModel = new UserModel(user.getUid(), email, displayName);
                            firestore.collection("users").document(user.getUid()).set(userModel);
                        }
                    }
                    return task.getResult();
                });
    }

    /** 登录 */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /** 退出登录 */
    public void logout() {
        auth.signOut();
    }

    /** 获取当前用户 */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /** 从 Firestore 获取用户数据 */
    public Task<UserModel> getUserData(String uid) {
        return firestore.collection("users").document(uid).get()
                .map(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        return documentSnapshot.toObject(UserModel.class);
                    }
                    return null;
                });
    }

    /** 检查是否已登录 */
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
```

---

### Task 3: 底部导航与 MainActivity

**Files:**
- Create: `app/src/main/res/menu/bottom_nav_menu.xml`
- Modify: `app/src/main/res/layout/activity_main.xml`
- Modify: `app/src/main/java/com/example/sevenxiao/MainActivity.java`

- [ ] **Step 1: 创建底部导航菜单**

`app/src/main/res/menu/bottom_nav_menu.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_gallery"
        android:icon="@drawable/ic_gallery"
        android:title="样图库" />
    <item
        android:id="@+id/nav_exam"
        android:icon="@drawable/ic_exam"
        android:title="考试" />
    <item
        android:id="@+id/nav_leaderboard"
        android:icon="@drawable/ic_leaderboard"
        android:title="排行榜" />
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="我的" />
</menu>
```

- [ ] **Step 2: 创建底部导航图标**

`app/src/main/res/drawable/ic_gallery.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M21,19V5c0,-1.1 -0.9,-2 -2,-2H5c-1.1,0 -2,0.9 -2,2v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2zM8.5,13.5l2.5,3.01L14.5,12l4.5,6H5l3.5,-4.5z" />
</vector>
```

`app/src/main/res/drawable/ic_exam.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M4,6H2v14c0,1.1 0.9,2 2,2h14v-2H4V6zM20,2H8C6.9,2 6,2.9 6,4v12c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V4C22,2.9 21.1,2 20,2zM12,14.5v-9l6,4.5L12,14.5z" />
</vector>
```

`app/src/main/res/drawable/ic_leaderboard.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M7.5,21H2V9h5.5v12zM14.75,3h-5.5v18h5.5V3zM22,11h-5.5v10H22V11z" />
</vector>
```

`app/src/main/res/drawable/ic_profile.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z" />
</vector>
```

- [ ] **Step 3: 重写 activity_main.xml**

`app/src/main/res/layout/activity_main.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/nav_host_fragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginBottom="?attr/actionBarSize"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toTopOf="@id/bottom_navigation"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_graph" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="@drawable/bottom_nav_background"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:menu="@menu/bottom_nav_menu" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 4: 创建底部导航背景和导航图**

`app/src/main/res/drawable/bottom_nav_background.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@android:color/white" />
</shape>
```

`app/src/main/res/navigation/nav_graph.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/nav_gallery">

    <fragment
        android:id="@+id/nav_gallery"
        android:name="com.example.sevenxiao.gallery.SampleGalleryFragment"
        android:label="样图库"
        tools:layout="@layout/fragment_gallery" />

    <fragment
        android:id="@+id/nav_exam"
        android:name="com.example.sevenxiao.exam.ExamHomeFragment"
        android:label="考试"
        tools:layout="@layout/fragment_exam_home" />

    <fragment
        android:id="@+id/nav_leaderboard"
        android:name="com.example.sevenxiao.leaderboard.LeaderboardFragment"
        android:label="排行榜"
        tools:layout="@layout/fragment_leaderboard" />

    <fragment
        android:id="@+id/nav_profile"
        android:name="com.example.sevenxiao.profile.ProfileFragment"
        android:label="我的"
        tools:layout="@layout/fragment_profile" />

</navigation>
```

- [ ] **Step 5: 重写 MainActivity.java**

```java
package com.example.sevenxiao;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp();
    }
}
```

---

### Task 4: 四个占位 Fragment

**Files:**
- Create: `app/src/main/java/com/example/sevenxiao/gallery/SampleGalleryFragment.java`
- Create: `app/src/main/java/com/example/sevenxiao/exam/ExamHomeFragment.java`
- Create: `app/src/main/java/com/example/sevenxiao/leaderboard/LeaderboardFragment.java`
- Create: `app/src/main/java/com/example/sevenxiao/profile/ProfileFragment.java`
- Create: `app/src/main/res/layout/fragment_gallery.xml`
- Create: `app/src/main/res/layout/fragment_exam_home.xml`
- Create: `app/src/main/res/layout/fragment_leaderboard.xml`
- Create: `app/src/main/res/layout/fragment_profile.xml`

- [ ] **Step 1: 创建 fragment_gallery.xml 布局**

`app/src/main/res/layout/fragment_gallery.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="12dp"
    tools:context=".gallery.SampleGalleryFragment">

    <EditText
        android:id="@+id/search_bar"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@drawable/search_bar_background"
        android:drawableStart="@android:drawable/ic_menu_search"
        android:hint="搜索样张..."
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/sample_grid"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="12dp"
        app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/search_bar"
        app:spanCount="2"
        tools:itemCount="6"
        tools:listitem="@layout/item_sample" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 2: 创建搜索栏背景**

`app/src/main/res/drawable/search_bar_background.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#F3F4F6" />
    <corners android:radius="24dp" />
</shape>
```

- [ ] **Step 3: 创建 item_sample.xml 布局**

`app/src/main/res/layout/item_sample.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="2dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <ImageView
            android:id="@+id/sample_thumbnail"
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:scaleType="centerCrop"
            android:src="@color/placeholder_gray"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/sample_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            android:ellipsize="end"
            android:maxLines="1"
            android:text="sample.jpg"
            android:textColor="@android:color/white"
            android:textSize="12sp"
            app:layout_constraintBottom_toBottomOf="@id/sample_thumbnail"
            app:layout_constraintStart_toStartOf="parent" />

        <TextView
            android:id="@+id/sample_category"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            android:background="@drawable/category_chip"
            android:paddingHorizontal="8dp"
            android:paddingVertical="2dp"
            android:text="过曝"
            android:textColor="@android:color/white"
            android:textSize="11sp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="@id/sample_thumbnail" />

    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.cardview.widget.CardView>
```

- [ ] **Step 4: 创建布局辅助资源**

`app/src/main/res/values/colors.xml` 添加占位色和 chip 背景:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="placeholder_gray">#FFE5E7EB</color>
    <color name="chip_blue">#FF2563EB</color>
</resources>
```

`app/src/main/res/drawable/category_chip.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/chip_blue" />
    <corners android:radius="12dp" />
</shape>
```

- [ ] **Step 5: 创建其他三个 Fragment 布局**

`app/src/main/res/layout/fragment_exam_home.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="考试"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/subtitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="选择测试难度开始答题"
        android:textColor="#666"
        android:textSize="14sp"
        app:layout_constraintStart_toStartOf="@id/title"
        app:layout_constraintTop_toBottomOf="@id/title" />

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/basic_card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        app:cardElevation="4dp"
        app:strokeColor="#2563EB"
        app:strokeWidth="2dp">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="20dp">

            <TextView
                android:id="@+id/basic_title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="🟢 基础测试"
                android:textSize="18sp"
                android:textStyle="bold"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="10-15 题 · 单选/判断 · 不限时"
                android:textColor="#666"
                android:textSize="14sp"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/basic_title" />

        </androidx.constraintlayout.widget.ConstraintLayout>
    </com.google.android.material.card.MaterialCardView>

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/advanced_card"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:cardElevation="4dp"
        app:strokeColor="#D97706"
        app:strokeWidth="2dp">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="20dp">

            <TextView
                android:id="@+id/advanced_title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="🔴 进阶测试"
                android:textSize="18sp"
                android:textStyle="bold"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="20-30 题 · 含描述题 · 限时"
                android:textColor="#666"
                android:textSize="14sp"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/advanced_title" />

        </androidx.constraintlayout.widget.ConstraintLayout>
    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

`app/src/main/res/layout/fragment_leaderboard.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="排行榜"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="即将上线"
        android:textColor="#999"
        android:textSize="24sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

`app/src/main/res/layout/fragment_profile.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="我的"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/login_prompt"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="登录以查看个人数据"
        android:textColor="#666"
        android:textSize="16sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 6: 创建 SampleGalleryFragment.java**

```java
package com.example.sevenxiao.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;
import com.example.sevenxiao.gallery.adapter.SampleAdapter;

import java.util.ArrayList;
import java.util.List;

public class SampleGalleryFragment extends Fragment {

    private RecyclerView sampleGrid;
    private SampleAdapter adapter;
    private List<SampleModel> sampleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sampleGrid = view.findViewById(R.id.sample_grid);
        sampleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new SampleAdapter(sampleList, sample -> {
            // 跳转详情页
            SampleDetailActivity.start(requireContext(), sample);
        });
        sampleGrid.setAdapter(adapter);

        loadLocalSamples();
    }

    private void loadLocalSamples() {
        // Phase 1: 加载内置样图
        List<SampleModel> builtinSamples = getBuiltinSamples();
        sampleList.clear();
        sampleList.addAll(builtinSamples);
        adapter.notifyDataSetChanged();
    }

    private List<SampleModel> getBuiltinSamples() {
        List<SampleModel> samples = new ArrayList<>();
        samples.add(new SampleModel("1", "过曝_日景", "image", "过曝",
                "overexposure_01", "高光区域细节丢失，天空过曝成白色", "samples/overexposure_01.jpg"));
        samples.add(new SampleModel("2", "过曝_夜景", "image", "过曝",
                "overexposure_02", "灯光过曝导致高光溢出", "samples/overexposure_02.jpg"));
        samples.add(new SampleModel("3", "鬼影_逆光", "image", "鬼影",
                "ghost_01", "逆光拍摄产生镜头鬼影", "samples/ghost_01.jpg"));
        samples.add(new SampleModel("4", "炫光_夜景", "image", "炫光",
                "flare_01", "强光源引起炫光条纹", "samples/flare_01.jpg"));
        samples.add(new SampleModel("5", "噪点_暗光", "image", "噪点",
                "noise_01", "低光环境ISO过高导致噪点", "samples/noise_01.jpg"));
        samples.add(new SampleModel("6", "模糊_运动", "image", "模糊",
                "blur_01", "运动模糊导致细节丢失", "samples/blur_01.jpg"));
        samples.add(new SampleModel("7", "偏色_室内", "image", "偏色",
                "colorcast_01", "白平衡不准导致偏黄", "samples/colorcast_01.jpg"));
        samples.add(new SampleModel("8", "欠曝_逆光", "image", "欠曝",
                "underexposure_01", "逆光主体欠曝，暗部细节丢失", "samples/underexposure_01.jpg"));
        return samples;
    }
}
```

- [ ] **Step 7: 创建 SampleAdapter.java**

```java
package com.example.sevenxiao.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;

import java.util.List;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {

    private List<SampleModel> samples;
    private OnSampleClickListener listener;

    public interface OnSampleClickListener {
        void onSampleClick(SampleModel sample);
    }

    public SampleAdapter(List<SampleModel> samples, OnSampleClickListener listener) {
        this.samples = samples;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SampleModel sample = samples.get(position);
        holder.title.setText(sample.getFileName());
        holder.category.setText(sample.getCategory());
        holder.category.setVisibility(View.VISIBLE);

        // 加载缩略图 - Phase 1 使用 placeholder
        holder.thumbnail.setImageResource(android.R.color.darker_gray);

        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSampleClick(sample);
            }
        });
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView thumbnail;
        TextView title;
        TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;
            thumbnail = itemView.findViewById(R.id.sample_thumbnail);
            title = itemView.findViewById(R.id.sample_title);
            category = itemView.findViewById(R.id.sample_category);
        }
    }
}
```

- [ ] **Step 8: 创建其他三个 Fragment**

`app/src/main/java/com/example/sevenxiao/exam/ExamHomeFragment.java`:

```java
package com.example.sevenxiao.exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sevenxiao.R;

public class ExamHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_home, container, false);
    }
}
```

`app/src/main/java/com/example/sevenxiao/leaderboard/LeaderboardFragment.java`:

```java
package com.example.sevenxiao.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sevenxiao.R;

public class LeaderboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }
}
```

`app/src/main/java/com/example/sevenxiao/profile/ProfileFragment.java`:

```java
package com.example.sevenxiao.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.remote.AuthRepository;

public class ProfileFragment extends Fragment {

    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authRepository = new AuthRepository();

        if (!authRepository.isLoggedIn()) {
            // 未登录，提示登录
            view.findViewById(R.id.login_prompt).setOnClickListener(v -> {
                startActivity(LoginActivity.createIntent(requireContext()));
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (authRepository != null && authRepository.isLoggedIn()) {
            // 已登录，更新 UI
            updateLoggedInUI();
        }
    }

    private void updateLoggedInUI() {
        // Phase 2: 显示用户数据
    }
}
```

- [ ] **Step 9: 修改 LoginActivity 引用（先在下一步创建）。更新 ProfileFragment 中的 import**

在 `ProfileFragment.java` 中添加 import:

```java
// 在上方添加
import com.example.sevenxiao.auth.LoginActivity;
```

---

### Task 5: 登录/注册模块

**Files:**
- Create: `app/src/main/res/layout/activity_login.xml`
- Create: `app/src/main/res/layout/activity_register.xml`
- Create: `app/src/main/java/com/example/sevenxiao/auth/LoginActivity.java`
- Create: `app/src/main/java/com/example/sevenxiao/auth/RegisterActivity.java`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 创建 activity_login.xml**

`app/src/main/res/layout/activity_login.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp">

    <TextView
        android:id="@+id/login_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="60dp"
        android:text="登录"
        android:textSize="28sp"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/email_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="40dp"
        android:hint="邮箱"
        app:layout_constraintTop_toBottomOf="@id/login_title"
        app:startIconDrawable="@android:drawable/ic_dialog_email">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/email_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/password_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:hint="密码"
        app:layout_constraintTop_toBottomOf="@id/email_layout"
        app:passwordToggleEnabled="true"
        app:startIconDrawable="@android:drawable/ic_lock_idle_lock">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/password_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textPassword" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/login_btn"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_marginTop="32dp"
        android:text="登录"
        app:layout_constraintTop_toBottomOf="@id/password_layout" />

    <TextView
        android:id="@+id/register_link"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:padding="8dp"
        android:text="还没有账号？立即注册"
        android:textColor="#2563EB"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/login_btn" />

    <TextView
        android:id="@+id/error_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="#EF4444"
        android:visibility="gone"
        app:layout_constraintTop_toBottomOf="@id/register_link" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 2: 创建 activity_register.xml**

`app/src/main/res/layout/activity_register.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp">

    <TextView
        android:id="@+id/register_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="60dp"
        android:text="注册"
        android:textSize="28sp"
        android:textStyle="bold"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/name_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="40dp"
        android:hint="昵称"
        app:layout_constraintTop_toBottomOf="@id/register_title"
        app:startIconDrawable="@android:drawable/ic_menu_myplaces">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/name_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textPersonName" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/email_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:hint="邮箱"
        app:layout_constraintTop_toBottomOf="@id/name_layout"
        app:startIconDrawable="@android:drawable/ic_dialog_email">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/email_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/password_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:hint="密码（至少6位）"
        app:layout_constraintTop_toBottomOf="@id/email_layout"
        app:passwordToggleEnabled="true"
        app:startIconDrawable="@android:drawable/ic_lock_idle_lock">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/password_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textPassword" />

    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/register_btn"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_marginTop="32dp"
        android:text="注册"
        app:layout_constraintTop_toBottomOf="@id/password_layout" />

    <TextView
        android:id="@+id/login_link"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:padding="8dp"
        android:text="已有账号？去登录"
        android:textColor="#2563EB"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/register_btn" />

    <TextView
        android:id="@+id/error_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="#EF4444"
        android:visibility="gone"
        app:layout_constraintTop_toBottomOf="@id/login_link" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 3: 创建 LoginActivity.java**

```java
package com.example.sevenxiao.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.remote.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginBtn;
    private TextView registerLink;
    private TextView errorText;

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        registerLink = findViewById(R.id.register_link);
        errorText = findViewById(R.id.error_text);

        loginBtn.setOnClickListener(v -> login());
        registerLink.setOnClickListener(v -> {
            startActivity(RegisterActivity.createIntent(this));
        });
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("请填写邮箱和密码");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("登录中...");
        errorText.setVisibility(View.GONE);

        authRepository.login(email, password)
                .addOnCompleteListener(task -> {
                    loginBtn.setEnabled(true);
                    loginBtn.setText("登录");
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = task.getException() != null ?
                                task.getException().getLocalizedMessage() : "登录失败";
                        showError(msg);
                    }
                });
    }

    private void showError(String msg) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
    }
}
```

- [ ] **Step 4: 创建 RegisterActivity.java**

```java
package com.example.sevenxiao.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.remote.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerBtn;
    private TextView loginLink;
    private TextView errorText;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();

        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerBtn = findViewById(R.id.register_btn);
        loginLink = findViewById(R.id.login_link);
        errorText = findViewById(R.id.error_text);

        registerBtn.setOnClickListener(v -> register());
        loginLink.setOnClickListener(v -> finish());
    }

    private void register() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("请填写所有字段");
            return;
        }
        if (password.length() < 6) {
            showError("密码至少6位");
            return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("注册中...");
        errorText.setVisibility(View.GONE);

        authRepository.register(email, password, name)
                .addOnCompleteListener(task -> {
                    registerBtn.setEnabled(true);
                    registerBtn.setText("注册");
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish(); // 返回登录页
                    } else {
                        String msg = task.getException() != null ?
                                task.getException().getLocalizedMessage() : "注册失败";
                        showError(msg);
                    }
                });
    }

    private void showError(String msg) {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(msg);
    }
}
```

- [ ] **Step 5: 更新 AndroidManifest.xml**

添加 LoginActivity 和 RegisterActivity:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Sevenxiao">
        <activity
            android:name=".auth.LoginActivity"
            android:exported="false"
            android:windowSoftInputMode="adjustResize" />
        <activity
            android:name=".auth.RegisterActivity"
            android:exported="false"
            android:windowSoftInputMode="adjustResize" />
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

### Task 6: 样张详情页

**Files:**
- Create: `app/src/main/res/layout/activity_sample_detail.xml`
- Create: `app/src/main/java/com/example/sevenxiao/gallery/SampleDetailActivity.java`

- [ ] **Step 1: 创建 activity_sample_detail.xml**

`app/src/main/res/layout/activity_sample_detail.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <ImageView
        android:id="@+id/sample_image"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:importantForAccessibility="no"
        android:scaleType="fitCenter"
        android:src="@android:color/darker_gray"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/category_chip"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="12dp"
        android:background="@drawable/category_chip"
        android:paddingHorizontal="12dp"
        android:paddingVertical="4dp"
        android:text="过曝"
        android:textColor="@android:color/white"
        android:textSize="14sp"
        app:layout_constraintStart_toStartOf="@id/sample_image"
        app:layout_constraintTop_toTopOf="@id/sample_image" />

    <TextView
        android:id="@+id/sample_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="样张名称"
        android:textSize="20sp"
        android:textStyle="bold"
        app:layout_constraintTop_toBottomOf="@id/sample_image" />

    <TextView
        android:id="@+id/sample_description"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="12dp"
        android:lineSpacingExtra="4dp"
        android:text="缺陷说明"
        android:textColor="#555"
        android:textSize="15sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/sample_title" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 2: 创建 SampleDetailActivity.java**

```java
package com.example.sevenxiao.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;

public class SampleDetailActivity extends AppCompatActivity {

    private static final String EXTRA_SAMPLE = "extra_sample";

    public static void start(Context context, SampleModel sample) {
        Intent intent = new Intent(context, SampleDetailActivity.class);
        intent.putExtra(EXTRA_SAMPLE, sample.getSampleId()); // Phase 1: 简单传递
        intent.putExtra("title", sample.getTitle());
        intent.putExtra("category", sample.getCategory());
        intent.putExtra("description", sample.getDescription());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_detail);

        ImageView imageView = findViewById(R.id.sample_image);
        TextView titleView = findViewById(R.id.sample_title);
        TextView categoryView = findViewById(R.id.category_chip);
        TextView descView = findViewById(R.id.sample_description);

        String title = getIntent().getStringExtra("title");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");

        titleView.setText(title);
        categoryView.setText(category);
        descView.setText(description);

        // Phase 1: placeholder 图片，后续用 Glide 加载
        imageView.setImageResource(android.R.color.darker_gray);
    }
}
```

---

### Task 7: 内置样图资源

**Files:**
- Create: `app/src/main/assets/samples/` (目录)
- Create: `app/src/main/assets/samples/overexposure_01.jpg` (占位)
- 注意：实际样图需要由用户后续放入

- [ ] **Step 1: 创建 assets 目录**

```bash
mkdir -p app/src/main/assets/samples
```

- [ ] **Step 2: 创建样本清单文件**

`app/src/main/assets/samples/manifest.json`:

```json
[
  {"id":"1","title":"过曝_日景","type":"image","category":"过曝","file":"samples/overexposure_01.jpg","desc":"高光区域细节丢失"},
  {"id":"2","title":"过曝_夜景","type":"image","category":"过曝","file":"samples/overexposure_02.jpg","desc":"灯光高光溢出"},
  {"id":"3","title":"鬼影_逆光","type":"image","category":"鬼影","file":"samples/ghost_01.jpg","desc":"逆光镜头鬼影"},
  {"id":"4","title":"炫光_夜景","type":"image","category":"炫光","file":"samples/flare_01.jpg","desc":"强光炫光条纹"},
  {"id":"5","title":"噪点_暗光","type":"image","category":"噪点","file":"samples/noise_01.jpg","desc":"低光ISO噪点"},
  {"id":"6","title":"模糊_运动","type":"image","category":"模糊","file":"samples/blur_01.jpg","desc":"运动模糊"},
  {"id":"7","title":"偏色_室内","type":"image","category":"偏色","file":"samples/colorcast_01.jpg","desc":"白平衡偏黄"},
  {"id":"8","title":"欠曝_逆光","type":"image","category":"欠曝","file":"samples/underexposure_01.jpg","desc":"逆光主体欠曝"}
]
```

---

### Task 8: 验证构建

- [ ] **Step 1: Sync Gradle 并构建**

```bash
cd e:/AndroidProject/sevenxiao-study-project
./gradlew assembleDebug
```

预期: BUILD SUCCESSFUL

- [ ] **Step 2: 修复编译错误**

如果构建失败，根据错误信息修复：
- 检查所有 import 语句是否正确
- 检查资源引用（R.id.xxx, R.layout.xxx）是否匹配
- 检查 AndroidManifest 中 Activity 注册
- 确认 google-services.json 已放置

---

### Phase 1 完成验收标准

- [x] App 可以正常编译
- [x] 底部 4 个 Tab 导航正常工作
- [x] 样图库显示 8 个内置样图卡片
- [x] 点击样图可跳转详情页查看说明
- [x] "我的"页面提示登录
- [x] 点击登录可跳转登录页
- [x] 可从登录页跳转注册页
- [ ] 实际样图图片放入 assets/samples/ 目录
- [ ] Firebase 项目创建成功且 google-services.json 配置完毕
