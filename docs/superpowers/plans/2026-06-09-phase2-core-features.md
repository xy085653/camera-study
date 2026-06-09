# Phase 2: 核心功能实现计划

> **For agentic workers:** Execute tasks sequentially. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现样图库完整功能（分类筛选 + Glide 图片加载）和基础测试系统（题库 + 答题 + 结果）。

**Architecture:** GalleryFragment 增加横向分类标签 RecyclerView 和 Glide 图片加载。考试采用 QuestionModel 数据模型，从 `assets/questions/` 的 JSON 加载题库，ExamActivity 中逐题显示，完成后跳转 ExamResultActivity。所有数据在 Phase 2 阶段从 assets 加载。

**Tech Stack:** Java 11, Glide, RecyclerView (Grid + Linear), JSON

---

### 文件变更清单

| 操作 | 文件 | 说明 |
|------|------|------|
| 新建 | `data/model/QuestionModel.java` | 题目数据模型 |
| 新建 | `data/model/ExamResultModel.java` | 考试结果模型 |
| 新建 | `data/local/AssetDataSource.java` | 从 assets 加载 JSON 数据 |
| 新建 | `exam/ExamActivity.java` | 考试答题页 |
| 新建 | `exam/ExamResultActivity.java` | 考试结果页 |
| 新建 | `exam/adapter/CategoryChipAdapter.java` | 分类标签适配器 |
| 修改 | `gallery/SampleGalleryFragment.java` | 加分类标签、搜索过滤、Glide |
| 修改 | `gallery/SampleDetailActivity.java` | 改用 Glide 加载图片 |
| 修改 | `exam/ExamHomeFragment.java` | 按钮绑定跳转 |
| 新建 | `res/layout/activity_exam.xml` | 考试布局 |
| 新建 | `res/layout/activity_exam_result.xml` | 结果布局 |
| 新建 | `res/layout/item_category_chip.xml` | 分类标签项布局 |
| 新建 | `res/drawable/chip_selected.xml` | 标签选中态背景 |
| 新建 | `assets/questions/basic.json` | 基础题库 |
| 修改 | `AndroidManifest.xml` | 注册新 Activity |

---

### Task 1: 数据模型

**Files:**
- Create: `app/src/main/java/com/example/sevenxiao/data/model/QuestionModel.java`
- Create: `app/src/main/java/com/example/sevenxiao/data/model/ExamResultModel.java`

- [ ] **Step 1: 创建 QuestionModel.java**

```java
package com.example.sevenxiao.data.model;

public class QuestionModel {
    private String id;
    private String type;           // "choice" or "judge"
    private String category;       // 关联缺陷维度
    private String difficulty;     // "basic" or "advanced"
    private String imageAsset;     // assets 路径
    private String questionText;   // 题干
    private String[] options;      // 选项
    private String correctAnswer;  // 正确答案
    private String explanation;    // 解析

    public QuestionModel() { }

    public QuestionModel(String id, String type, String category, String difficulty,
                         String imageAsset, String questionText,
                         String[] options, String correctAnswer, String explanation) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.difficulty = difficulty;
        this.imageAsset = imageAsset;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getImageAsset() { return imageAsset; }
    public void setImageAsset(String imageAsset) { this.imageAsset = imageAsset; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String[] getOptions() { return options; }
    public void setOptions(String[] options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
```

- [ ] **Step 2: 创建 ExamResultModel.java**

```java
package com.example.sevenxiao.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamResultModel {
    private String difficulty;
    private List<QuestionModel> questions;
    private Map<String, String> userAnswers;   // questionId → userAnswer
    private int correctCount;
    private int totalCount;
    private long timeSpent;     // 秒
    private long completedAt;

    public ExamResultModel() {
        this.userAnswers = new HashMap<>();
        this.questions = new ArrayList<>();
    }

    public ExamResultModel(String difficulty, List<QuestionModel> questions) {
        this.difficulty = difficulty;
        this.questions = questions;
        this.userAnswers = new HashMap<>();
        this.totalCount = questions.size();
        this.correctCount = 0;
        this.timeSpent = 0;
        this.completedAt = System.currentTimeMillis();
    }

    public void addAnswer(String questionId, String answer) {
        userAnswers.put(questionId, answer);
    }

    public boolean isCorrect(QuestionModel question) {
        String userAnswer = userAnswers.get(question.getId());
        return userAnswer != null && userAnswer.equals(question.getCorrectAnswer());
    }

    public void calculateScore() {
        correctCount = 0;
        for (QuestionModel q : questions) {
            if (isCorrect(q)) correctCount++;
        }
    }

    public int getScore() {
        if (totalCount == 0) return 0;
        return (int) Math.round((double) correctCount / totalCount * 100);
    }

    // Getters
    public String getDifficulty() { return difficulty; }
    public List<QuestionModel> getQuestions() { return questions; }
    public Map<String, String> getUserAnswers() { return userAnswers; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
    public long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(long timeSpent) { this.timeSpent = timeSpent; }
    public long getCompletedAt() { return completedAt; }
}
```

---

### Task 2: Asset 数据源

**Files:**
- Create: `app/src/main/java/com/example/sevenxiao/data/local/AssetDataSource.java`

- [ ] **Step 1: 创建 AssetDataSource.java**

```java
package com.example.sevenxiao.data.local;

import android.content.Context;

import com.example.sevenxiao.data.model.QuestionModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssetDataSource {

    private Context context;

    public AssetDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    /** 从 assets 读取 JSON 文件 */
    private String readAssetFile(String path) {
        try (InputStream is = context.getAssets().open(path)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 加载基础题库 */
    public List<QuestionModel> loadBasicQuestions() {
        String json = readAssetFile("questions/basic.json");
        return parseQuestions(json);
    }

    /** 根据难度加载题目 */
    public List<QuestionModel> loadQuestions(String difficulty) {
        String fileName = "questions/" + difficulty + ".json";
        String json = readAssetFile(fileName);
        return parseQuestions(json);
    }

    /** 解析 JSON 为题目列表 */
    private List<QuestionModel> parseQuestions(String json) {
        List<QuestionModel> questions = new ArrayList<>();
        if (json == null) return questions;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                QuestionModel q = new QuestionModel();
                q.setId(obj.getString("id"));
                q.setType(obj.getString("type"));
                q.setCategory(obj.optString("category", ""));
                q.setDifficulty(obj.optString("difficulty", "basic"));
                q.setImageAsset(obj.optString("imageAsset", ""));
                q.setQuestionText(obj.getString("questionText"));

                JSONArray optArray = obj.getJSONArray("options");
                String[] options = new String[optArray.length()];
                for (int j = 0; j < optArray.length(); j++) {
                    options[j] = optArray.getString(j);
                }
                q.setOptions(options);
                q.setCorrectAnswer(obj.getString("correctAnswer"));
                q.setExplanation(obj.optString("explanation", ""));
                questions.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    /** 从题库随机抽取指定数量的题目 */
    public List<QuestionModel> pickRandomQuestions(List<QuestionModel> all, int count) {
        List<QuestionModel> shuffled = new ArrayList<>(all);
        Collections.shuffle(shuffled);
        int n = Math.min(count, shuffled.size());
        return shuffled.subList(0, n);
    }
}
```

---

### Task 3: 基础题库 JSON

**Files:**
- Create: `app/src/main/assets/questions/basic.json`

- [ ] **Step 1: 创建基础题库**

`app/src/main/assets/questions/basic.json`:

```json
[
  {
    "id": "b001",
    "type": "choice",
    "category": "过曝",
    "difficulty": "basic",
    "imageAsset": "samples/overexposure_01.jpg",
    "questionText": "以下哪张图片存在过曝（Overexposure）问题？",
    "options": ["正常曝光", "欠曝", "过曝", "低对比度"],
    "correctAnswer": "C",
    "explanation": "过曝是指画面亮度超过传感器可记录范围，高光区域细节丢失呈现纯白色。正确答案中的图片高光部分（天空）完全失去层次。"
  },
  {
    "id": "b002",
    "type": "judge",
    "category": "鬼影",
    "difficulty": "basic",
    "imageAsset": "samples/ghost_01.jpg",
    "questionText": "下图中是否存在鬼影（Ghost/Flare）现象？",
    "options": ["是", "否"],
    "correctAnswer": "A",
    "explanation": "图中在光源对侧出现了明显的彩色虚影，这是典型的镜头鬼影现象，由镜头内部元件间反射造成。"
  },
  {
    "id": "b003",
    "type": "choice",
    "category": "噪点",
    "difficulty": "basic",
    "imageAsset": "samples/noise_01.jpg",
    "questionText": "下图中最明显的影像问题是？",
    "options": ["过曝", "噪点", "偏色", "模糊"],
    "correctAnswer": "B",
    "explanation": "图中可以看到大量细小的颗粒状彩色/亮度噪点，尤其在暗部区域最为明显。这是高ISO感光度设置导致的噪点问题。"
  },
  {
    "id": "b004",
    "type": "judge",
    "category": "彩色",
    "difficulty": "basic",
    "imageAsset": "samples/colorcast_01.jpg",
    "questionText": "下图是否存在白平衡不准导致的偏色问题？",
    "options": ["是", "否"],
    "correctAnswer": "A",
    "explanation": "图中整体色调偏黄偏暖，中性色（如白色墙面）呈现明显黄色调，是白平衡设置不准确导致的偏色问题。"
  },
  {
    "id": "b005",
    "type": "choice",
    "category": "模糊",
    "difficulty": "basic",
    "imageAsset": "samples/blur_01.jpg",
    "questionText": "下图的模糊类型最可能是？",
    "options": ["失焦模糊", "运动模糊", "镜头污损", "景深模糊"],
    "correctAnswer": "B",
    "explanation": "图中模糊呈现方向性条纹，静态物体沿水平方向出现拖影。这是典型的运动模糊，由快门速度不足以冻结运动造成。"
  },
  {
    "id": "b006",
    "type": "judge",
    "category": "欠曝",
    "difficulty": "basic",
    "imageAsset": "samples/underexposure_01.jpg",
    "questionText": "下图是否存在欠曝（Underexposure）问题？",
    "options": ["是", "否"],
    "correctAnswer": "A",
    "explanation": "图中主体和暗部区域严重偏暗，细节完全丢失在阴影中，即使调整亮度也难以恢复。这是典型的欠曝问题。"
  },
  {
    "id": "b007",
    "type": "choice",
    "category": "炫光",
    "difficulty": "basic",
    "imageAsset": "samples/flare_01.jpg",
    "questionText": "下图中的条状光斑属于什么影像问题？",
    "options": ["鬼影", "炫光", "过曝", "镜头污损"],
    "correctAnswer": "B",
    "explanation": "图中从强光源处延伸出多条彩色条纹状光斑，这是典型的炫光（Lens Flare）现象，由强光在镜头镜片间多次反射形成。"
  },
  {
    "id": "b008",
    "type": "judge",
    "category": "过曝",
    "difficulty": "basic",
    "imageAsset": "samples/overexposure_02.jpg",
    "questionText": "下图中灯光区域的高光溢出是否属于过曝？",
    "options": ["是", "否"],
    "correctAnswer": "A",
    "explanation": "灯光区域呈现为无细节的纯白色块，周围光晕也丢失了层次。这是高光溢出导致的过曝问题。"
  },
  {
    "id": "b009",
    "type": "choice",
    "category": "综合",
    "difficulty": "basic",
    "imageAsset": "samples/ghost_01.jpg",
    "questionText": "要减少下图中的鬼影问题，最有效的措施是？",
    "options": ["降低ISO", "使用遮光罩", "提高快门速度", "调整白平衡"],
    "correctAnswer": "B",
    "explanation": "使用遮光罩可以有效阻挡非成像光线进入镜头，减少镜头内部反射，是抑制鬼影和炫光最直接有效的方法。"
  },
  {
    "id": "b010",
    "type": "judge",
    "category": "噪点",
    "difficulty": "basic",
    "imageAsset": "samples/noise_01.jpg",
    "questionText": "降低ISO设置可以有效减少下图中出现的噪点问题？",
    "options": ["是", "否"],
    "correctAnswer": "A",
    "explanation": "噪点主要源于高ISO设置放大了传感器信号中的噪声。降低ISO是减少噪点的最直接方法，但可能需要配合更长的曝光时间或更大的光圈来维持亮度。"
  }
]
```

---

### Task 4: 样图库分类标签 + Glide 加载

**Files:**
- Modify: `app/src/main/res/layout/fragment_gallery.xml` — 添加分类标签 RecyclerView
- Create: `app/src/main/java/com/example/sevenxiao/gallery/adapter/CategoryChipAdapter.java`
- Create: `app/src/main/res/layout/item_category_chip.xml`
- Create: `app/src/main/res/drawable/chip_selected.xml`
- Create: `app/src/main/res/drawable/chip_unselected.xml`
- Modify: `app/src/main/java/com/example/sevenxiao/gallery/SampleGalleryFragment.java`
- Modify: `app/src/main/java/com/example/sevenxiao/gallery/SampleDetailActivity.java`
- Modify: `app/src/main/java/com/example/sevenxiao/gallery/adapter/SampleAdapter.java`

- [ ] **Step 1: 创建分类标签的 drawable 背景**

`app/src/main/res/drawable/chip_selected.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#2563EB" />
    <corners android:radius="16dp" />
</shape>
```

`app/src/main/res/drawable/chip_unselected.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#F3F4F6" />
    <corners android:radius="16dp" />
</shape>
```

- [ ] **Step 2: 创建分类标签项布局**

`app/src/main/res/layout/item_category_chip.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/chip_text"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:paddingHorizontal="16dp"
    android:paddingVertical="8dp"
    android:text="全部"
    android:textSize="14sp" />
```

- [ ] **Step 3: 创建 CategoryChipAdapter.java**

```java
package com.example.sevenxiao.gallery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;

import java.util.List;

public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.ViewHolder> {

    private List<String> categories;
    private int selectedPosition = 0;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category, int position);
    }

    public CategoryChipAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.text.setText(category);

        if (position == selectedPosition) {
            holder.text.setBackgroundResource(R.drawable.chip_selected);
            holder.text.setTextColor(0xFFFFFFFF);
        } else {
            holder.text.setBackgroundResource(R.drawable.chip_unselected);
            holder.text.setTextColor(0xFF374151);
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(prev);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.chip_text);
        }
    }
}
```

- [ ] **Step 4: 更新 fragment_gallery.xml 添加分类标签栏**

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
        android:imeOptions="actionSearch"
        android:inputType="text"
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        android:importantForAutofill="no"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/category_chips"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:orientation="horizontal"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        app:layout_constraintTop_toBottomOf="@id/search_bar" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/sample_grid"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="12dp"
        app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/category_chips"
        app:spanCount="2"
        tools:itemCount="6"
        tools:listitem="@layout/item_sample" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 5: 重写 SampleGalleryFragment.java**

```java
package com.example.sevenxiao.gallery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.SampleModel;
import com.example.sevenxiao.gallery.adapter.CategoryChipAdapter;
import com.example.sevenxiao.gallery.adapter.SampleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SampleGalleryFragment extends Fragment {

    private RecyclerView sampleGrid;
    private SampleAdapter adapter;
    private List<SampleModel> allSamples = new ArrayList<>();
    private List<SampleModel> filteredSamples = new ArrayList<>();
    private String currentCategory = "全部";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allSamples = getBuiltinSamples();
        filteredSamples = new ArrayList<>(allSamples);

        // 搜索框
        EditText searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim().toLowerCase();
                applyFilters();
            }
        });

        // 分类标签
        List<String> categories = new ArrayList<>(Arrays.asList("全部", "过曝", "欠曝", "鬼影", "炫光", "噪点", "模糊", "偏色", "视频"));
        RecyclerView chipRecycler = view.findViewById(R.id.category_chips);
        chipRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        CategoryChipAdapter chipAdapter = new CategoryChipAdapter(categories, (category, position) -> {
            currentCategory = category;
            applyFilters();
        });
        chipRecycler.setAdapter(chipAdapter);

        // 样图网格
        sampleGrid = view.findViewById(R.id.sample_grid);
        sampleGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new SampleAdapter(filteredSamples, sample ->
                SampleDetailActivity.start(requireContext(), sample));
        sampleGrid.setAdapter(adapter);
    }

    private void applyFilters() {
        filteredSamples.clear();

        // 按分类筛选
        List<SampleModel> categoryFiltered;
        if ("全部".equals(currentCategory)) {
            categoryFiltered = new ArrayList<>(allSamples);
        } else {
            categoryFiltered = allSamples.stream()
                    .filter(s -> s.getCategory().equals(currentCategory))
                    .collect(Collectors.toList());
        }

        // 按搜索关键词筛选
        if (searchQuery.isEmpty()) {
            filteredSamples.addAll(categoryFiltered);
        } else {
            for (SampleModel s : categoryFiltered) {
                if (s.getTitle().toLowerCase().contains(searchQuery)
                        || s.getFileName().toLowerCase().contains(searchQuery)
                        || s.getDescription().toLowerCase().contains(searchQuery)) {
                    filteredSamples.add(s);
                }
            }
        }

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

- [ ] **Step 6: 更新 SampleDetailActivity 使用 Glide 加载图片**

修改 `onCreate` 中的图片加载部分：

```java
    // 替换原先的占位图片设置
    Glide.with(this)
            .load("file:///android_asset/" + "samples/placeholder.jpg") // Phase 2: 使用 assets 图片
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .into(imageView);
```

同时添加 Glide import 到 import 区：

```java
import com.bumptech.glide.Glide;
```

- [ ] **Step 7: 更新 SampleAdapter 使用 Glide 加载缩略图**

修改 `SampleAdapter.java` 中 `onBindViewHolder` 的缩略图设置：

```java
    // 替换：holder.thumbnail.setImageResource(android.R.color.darker_gray);
    Glide.with(holder.thumbnail.getContext())
            .load("file:///android_asset/" + sample.getLocalAsset())
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .centerCrop()
            .into(holder.thumbnail);
```

添加 import：

```java
import com.bumptech.glide.Glide;
```

---

### Task 5: 考试系统 — 答题 Activity

**Files:**
- Create: `app/src/main/res/layout/activity_exam.xml`
- Create: `app/src/main/java/com/example/sevenxiao/exam/ExamActivity.java`
- Modify: `app/src/main/java/com/example/sevenxiao/exam/ExamHomeFragment.java`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 创建 activity_exam.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/exam_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/progress_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="第 1/10 题"
        android:textColor="#666"
        android:textSize="14sp"
        app:layout_constraintTop_toTopOf="parent" />

    <ProgressBar
        android:id="@+id/progress_bar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="4dp"
        android:layout_marginTop="8dp"
        android:max="100"
        android:progress="10"
        app:layout_constraintTop_toBottomOf="@id/progress_text" />

    <ImageView
        android:id="@+id/question_image"
        android:layout_width="match_parent"
        android:layout_height="240dp"
        android:layout_marginTop="16dp"
        android:importantForAccessibility="no"
        android:scaleType="fitCenter"
        android:src="@android:color/darker_gray"
        app:layout_constraintTop_toBottomOf="@id/progress_bar" />

    <TextView
        android:id="@+id/question_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="题干"
        android:textSize="16sp"
        android:textStyle="bold"
        app:layout_constraintTop_toBottomOf="@id/question_image" />

    <LinearLayout
        android:id="@+id/options_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:orientation="vertical"
        app:layout_constraintTop_toBottomOf="@id/question_text"
        />

    <com.google.android.material.button.MaterialButton
        android:id="@+id/next_btn"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_marginTop="24dp"
        android:text="下一题"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/options_container" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 2: 创建 ExamActivity.java**

```java
package com.example.sevenxiao.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.sevenxiao.R;
import com.example.sevenxiao.data.local.AssetDataSource;
import com.example.sevenxiao.data.model.ExamResultModel;
import com.example.sevenxiao.data.model.QuestionModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExamActivity extends AppCompatActivity {

    private static final String EXTRA_DIFFICULTY = "extra_difficulty";

    private AssetDataSource dataSource;
    private List<QuestionModel> questions;
    private ExamResultModel result;
    private int currentIndex = 0;
    private long startTime;

    private TextView progressText;
    private ProgressBar progressBar;
    private ImageView questionImage;
    private TextView questionText;
    private LinearLayout optionsContainer;
    private MaterialButton nextBtn;
    private View[] optionViews;

    public static Intent createIntent(Context context, String difficulty) {
        Intent intent = new Intent(context, ExamActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.exam_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        String difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);
        if (difficulty == null) difficulty = "basic";

        dataSource = new AssetDataSource(this);
        List<QuestionModel> all = dataSource.loadQuestions(difficulty);
        questions = dataSource.pickRandomQuestions(all, 10);
        result = new ExamResultModel(difficulty, questions);
        startTime = SystemClock.elapsedRealtime();

        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        questionImage = findViewById(R.id.question_image);
        questionText = findViewById(R.id.question_text);
        optionsContainer = findViewById(R.id.options_container);
        nextBtn = findViewById(R.id.next_btn);

        nextBtn.setOnClickListener(v -> goToNext());
        showQuestion(0);
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) return;
        currentIndex = index;
        QuestionModel q = questions.get(index);

        // 更新进度
        progressText.setText("第 " + (index + 1) + "/" + questions.size() + " 题");
        progressBar.setProgress((index + 1) * 100 / questions.size());

        // 加载图片
        String assetPath = q.getImageAsset();
        if (!assetPath.isEmpty()) {
            Glide.with(this)
                    .load("file:///android_asset/" + assetPath)
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.color.darker_gray)
                    .into(questionImage);
            questionImage.setVisibility(View.VISIBLE);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        // 显示题干
        questionText.setText(q.getQuestionText());

        // 显示选项
        optionsContainer.removeAllViews();
        String[] options = q.getOptions();
        optionViews = new View[options.length];
        for (int i = 0; i < options.length; i++) {
            final int optionIndex = i;
            Button btn = new Button(this);
            btn.setText((char)('A' + i) + ". " + options[i]);
            btn.setTextSize(15);
            btn.setBackgroundResource(R.drawable.chip_unselected);
            btn.setTextColor(0xFF374151);
            btn.setPadding(16, 12, 16, 12);
            btn.setAllCaps(false);
            btn.setOnClickListener(v -> selectOption(optionIndex));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = 12;
            optionsContainer.addView(btn, lp);
            optionViews[i] = btn;
        }

        // 检查是否已有答案 (旋转恢复等场景)
        String existingAnswer = result.getUserAnswers().get(q.getId());
        if (existingAnswer != null) {
            String[] labels = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < options.length; i++) {
                if (labels[i].equals(existingAnswer)) {
                    highlightOption(i);
                    break;
                }
            }
        }

        // 最后一题切换按钮文字
        if (index == questions.size() - 1) {
            nextBtn.setText("提交");
        } else {
            nextBtn.setText("下一题");
        }
    }

    private void selectOption(int optionIndex) {
        QuestionModel q = questions.get(currentIndex);
        String[] labels = {"A", "B", "C", "D", "E"};
        String answer = labels[optionIndex];
        result.addAnswer(q.getId(), answer);
        highlightOption(optionIndex);
    }

    private void highlightOption(int optionIndex) {
        for (int i = 0; i < optionViews.length; i++) {
            Button btn = (Button) optionViews[i];
            if (i == optionIndex) {
                btn.setBackgroundResource(R.drawable.chip_selected);
                btn.setTextColor(0xFFFFFFFF);
            } else {
                btn.setBackgroundResource(R.drawable.chip_unselected);
                btn.setTextColor(0xFF374151);
            }
        }
    }

    private void goToNext() {
        // 检查当前题是否已作答
        QuestionModel q = questions.get(currentIndex);
        if (!result.getUserAnswers().containsKey(q.getId())) {
            return; // 未作答不能继续
        }

        if (currentIndex >= questions.size() - 1) {
            // 最后一道题，提交
            result.setTimeSpent((SystemClock.elapsedRealtime() - startTime) / 1000);
            result.calculateScore();
            startActivity(ExamResultActivity.createIntent(this, result));
            finish();
        } else {
            showQuestion(currentIndex + 1);
        }
    }
}
```

- [ ] **Step 3: 更新 ExamHomeFragment 绑定点击事件**

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.basic_card).setOnClickListener(v -> {
            startActivity(ExamActivity.createIntent(requireContext(), "basic"));
        });

        view.findViewById(R.id.advanced_card).setOnClickListener(v -> {
            // Phase 2: 进阶测试暂不可用
        });
    }
}
```

- [ ] **Step 4: 在 AndroidManifest 注册 ExamActivity**

```xml
<activity
    android:name=".exam.ExamActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustNothing" />
```

---

### Task 6: 考试结果页

**Files:**
- Create: `app/src/main/res/layout/activity_exam_result.xml`
- Create: `app/src/main/java/com/example/sevenxiao/exam/ExamResultActivity.java`

- [ ] **Step 1: 创建 activity_exam_result.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/result_root"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="24dp">

        <TextView
            android:id="@+id/score_label"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="得分"
            android:textColor="#666"
            android:textSize="16sp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/score_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="0"
            android:textColor="#2563EB"
            android:textSize="64sp"
            android:textStyle="bold"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/score_label" />

        <TextView
            android:id="@+id/stats_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:text="✅ 正确 0/0"
            android:textColor="#666"
            android:textSize="15sp"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/score_text" />

        <View
            android:id="@+id/divider"
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:layout_marginTop="24dp"
            android:background="#E5E7EB"
            app:layout_constraintTop_toBottomOf="@id/stats_text" />

        <TextView
            android:id="@+id/review_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:text="题目解析"
            android:textSize="18sp"
            android:textStyle="bold"
            app:layout_constraintTop_toBottomOf="@id/divider" />

        <LinearLayout
            android:id="@+id/review_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:orientation="vertical"
            app:layout_constraintTop_toBottomOf="@id/review_title" />

        <LinearLayout
            android:id="@+id/button_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:orientation="horizontal"
            app:layout_constraintTop_toBottomOf="@id/review_container">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/retry_btn"
                android:layout_width="0dp"
                android:layout_height="48dp"
                android:layout_marginEnd="8dp"
                android:layout_weight="1"
                android:text="重新考试" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/home_btn"
                android:layout_width="0dp"
                android:layout_height="48dp"
                android:layout_weight="1"
                android:text="返回首页" />

        </LinearLayout>

    </androidx.constraintlayout.widget.ConstraintLayout>
</ScrollView>
```

- [ ] **Step 2: 创建 ExamResultActivity.java**

```java
package com.example.sevenxiao.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sevenxiao.R;
import com.example.sevenxiao.data.model.ExamResultModel;
import com.example.sevenxiao.data.model.QuestionModel;

public class ExamResultActivity extends AppCompatActivity {

    private static final String EXTRA_RESULT = "extra_result";

    public static Intent createIntent(Context context, ExamResultModel result) {
        Intent intent = new Intent(context, ExamResultActivity.class);
        // 由于 ExamResultModel 不可序列化，传递基本数据
        intent.putExtra("score", result.getScore());
        intent.putExtra("correctCount", result.getCorrectCount());
        intent.putExtra("totalCount", result.getTotalCount());
        intent.putExtra("difficulty", result.getDifficulty());
        intent.putExtra("timeSpent", result.getTimeSpent());

        // 传递题目 ID 列表用于标识
        String[] questionIds = new String[result.getQuestions().size()];
        String[] questionTexts = new String[result.getQuestions().size()];
        String[] correctAnswers = new String[result.getQuestions().size()];
        String[] explanations = new String[result.getQuestions().size()];
        String[] userAnswers = new String[result.getQuestions().size()];

        for (int i = 0; i < result.getQuestions().size(); i++) {
            QuestionModel q = result.getQuestions().get(i);
            questionIds[i] = q.getId();
            questionTexts[i] = q.getQuestionText();
            correctAnswers[i] = q.getCorrectAnswer();
            explanations[i] = q.getExplanation();
            String ua = result.getUserAnswers().get(q.getId());
            userAnswers[i] = ua != null ? ua : "";
        }

        intent.putExtra("questionIds", questionIds);
        intent.putExtra("questionTexts", questionTexts);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("explanations", explanations);
        intent.putExtra("userAnswers", userAnswers);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.result_root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        int score = getIntent().getIntExtra("score", 0);
        int correctCount = getIntent().getIntExtra("correctCount", 0);
        int totalCount = getIntent().getIntExtra("totalCount", 0);
        long timeSpent = getIntent().getLongExtra("timeSpent", 0);
        String difficulty = getIntent().getStringExtra("difficulty");

        String[] questionTexts = getIntent().getStringArrayExtra("questionTexts");
        String[] correctAnswers = getIntent().getStringArrayExtra("correctAnswers");
        String[] explanations = getIntent().getStringArrayExtra("explanations");
        String[] userAnswers = getIntent().getStringArrayExtra("userAnswers");

        // 显示分数
        TextView scoreText = findViewById(R.id.score_text);
        scoreText.setText(String.valueOf(score));

        // 显示统计
        TextView statsText = findViewById(R.id.stats_text);
        String timeStr = String.format("%d分%d秒", timeSpent / 60, timeSpent % 60);
        statsText.setText("✅ 正确 " + correctCount + "/" + totalCount + "    ⏱ " + timeStr);

        // 显示解析
        LinearLayout reviewContainer = findViewById(R.id.review_container);
        if (questionTexts != null && correctAnswers != null) {
            String[] labels = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < questionTexts.length; i++) {
                String userAns = userAnswers != null && i < userAnswers.length ? userAnswers[i] : "";
                boolean isCorrect = userAns.equals(correctAnswers[i]);

                String correctText = "";
                if (correctAnswers[i] != null) {
                    int idx = correctAnswers[i].charAt(0) - 'A';
                    correctText = idx >= 0 && idx < labels.length ? labels[idx] : correctAnswers[i];
                }

                String resultIcon = isCorrect ? "✅" : "❌";
                String reviewText = resultIcon + " " + questionTexts[i] + "\n"
                        + "你的答案: " + userAns + "    正确答案: " + correctText + "\n"
                        + "解析: " + (explanations != null && i < explanations.length ? explanations[i] : "");

                TextView tv = new TextView(this);
                tv.setText(reviewText);
                tv.setTextSize(14);
                tv.setLineSpacing(4, 1);
                tv.setPadding(16, 16, 16, 16);
                tv.setBackgroundResource(isCorrect ? R.drawable.chip_unselected : R.drawable.chip_selected);
                if (!isCorrect) tv.setTextColor(0xFFFFFFFF);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.bottomMargin = 12;
                reviewContainer.addView(tv, lp);
            }
        }

        // 按钮
        findViewById(R.id.retry_btn).setOnClickListener(v -> {
            startActivity(ExamActivity.createIntent(this, difficulty));
            finish();
        });

        findViewById(R.id.home_btn).setOnClickListener(v -> {
            finish();
        });
    }
}
```

- [ ] **Step 3: 在 AndroidManifest 注册 ExamResultActivity**

```xml
<activity
    android:name=".exam.ExamResultActivity"
    android:exported="false" />
```

---

### Phase 2 完成验收标准

- [ ] 样图库顶部显示分类标签栏，点击筛选样图
- [ ] 搜索框输入文字后实时过滤样图
- [ ] 样图缩略图使用 Glide 加载
- [ ] 考试首页点击"基础测试"进入答题
- [ ] 答题页显示图片 + 题干 + 选项，选择后高亮
- [ ] 每道题必须作答后才能进入下一题
- [ ] 最后一题显示"提交"
- [ ] 提交后显示得分、正确题数、用时
- [ ] 结果页可查看每道题的解析
- [ ] 结果页可重新考试或返回首页
