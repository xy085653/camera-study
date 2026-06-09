# 📷 影像工坊 - ImageLab

> **影像效果工程师看图能力训练系统**
> *Image Defect Recognition Training for Imaging Engineers*

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

---

## 📖 简介 | Overview

**影像工坊 (ImageLab)** 是一款专为影像效果工程师设计的看图能力训练 App。通过浏览不同维度的影像缺陷样张、参与分级测试、查阅影像知识库，帮助工程师系统性地提升对影像质量问题的识别与分析能力。

### 适用人群
- 刚入行的影像效果工程师
- 有一定经验但希望系统化提升的影像工程师
- 摄像头模组测试、图像质量评测相关从业人员

---

## ✨ 功能特性 | Features

### 📸 样图库 | Sample Gallery
- 8 大缺陷维度分类：**过曝、欠曝、鬼影、炫光、噪点、模糊、偏色、视频**
- 分类标签快速筛选 + 关键词搜索
- 样张详情页，附带缺陷说明文字
- 支持内置素材包（assets）加载

### 📝 考试系统 | Exam System
- **基础测试**：选择题 + 判断题，覆盖各类影像缺陷识别
- **进阶测试**（开发中）：含描述题、复合缺陷场景
- 随机组卷，每场考试 10 题
- 即时评分 + 逐题解析

### 📖 知识库 | Knowledge Base
- 8 大影像缺陷的详细图文解释
- 每个缺陷包含：**成因分析、识别方法、改善建议**
- 展开式卡片浏览，交互友好

### 👤 个人中心 | Profile
- 本地账号注册/登录（无需网络）
- 学习数据统计
- 检查更新（GitHub Release）
- 应用信息

---

## 🛠️ 技术栈 | Tech Stack

| 技术 | 用途 |
|------|------|
| **Java 11** | Android 开发语言 |
| **Android SDK 36** | 目标平台版本 (minSdk 24) |
| **Jetpack Navigation** | 底部导航 + Fragment 切换 |
| **ConstraintLayout** | 页面布局 |
| **RecyclerView** | 列表/网格展示 |
| **Glide** | 图片加载 |
| **Material Design** | UI 组件库 |
| **SharedPreferences** | 本地数据持久化（账号、考试记录） |

> 🔒 **零云端依赖**：注册登录、题库、知识库全部本地化存储，无需网络即可使用。

---

## 📂 项目结构 | Project Structure

```
com.example.sevenxiao/
├── MainActivity.java               # 主 Activity（底部导航）
├── auth/                           # 注册登录
│   ├── LoginActivity.java
│   └── RegisterActivity.java
├── gallery/                        # 样图库
│   ├── SampleGalleryFragment.java
│   ├── SampleDetailActivity.java
│   └── adapter/
│       ├── SampleAdapter.java
│       └── CategoryChipAdapter.java
├── exam/                           # 考试系统
│   ├── ExamHomeFragment.java
│   ├── ExamActivity.java
│   └── ExamResultActivity.java
├── knowledge/                      # 知识库
│   ├── KnowledgeFragment.java
│   └── adapter/
│       └── KnowledgeAdapter.java
├── profile/                        # 个人中心
│   └── ProfileFragment.java
└── data/
    ├── model/                      # 数据模型
    │   ├── UserModel.java
    │   ├── SampleModel.java
    │   ├── QuestionModel.java
    │   ├── ExamResultModel.java
    │   └── KnowledgeEntry.java
    ├── local/                      # 本地数据源
    │   └── AssetDataSource.java
    └── remote/                     # 数据仓库
        └── AuthRepository.java
```

---

## 🚀 开始使用 | Getting Started

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 11+
- Android SDK 36

### 构建步骤

```bash
# 1. 克隆项目
git clone https://github.com/xy085653/camera-study.git

# 2. 用 Android Studio 打开项目目录
# 3. 等待 Gradle Sync 完成
# 4. 连接设备或启动模拟器
# 5. Run → Run 'app'
```

> **注意**：首次打开需要 Gradle Sync 下载依赖，请保持网络畅通。

---

## 📦 更新题库 | Updating Questions

题库存储在 `app/src/main/assets/questions/basic.json`，采用 JSON 格式：

```json
{
  "id": "b001",
  "type": "choice",
  "category": "过曝",
  "difficulty": "basic",
  "imageAsset": "samples/overexposure_01.jpg",
  "questionText": "以下哪张图片存在过曝问题？",
  "options": ["选项A", "选项B", "选项C", "选项D"],
  "correctAnswer": "C",
  "explanation": "答案解析..."
}
```

样图放置在 `app/src/main/assets/samples/` 目录。

---

## 🧩 开发路线图 | Roadmap

- [x] **Phase 1**: 基础框架（导航、注册登录、样图库）
- [x] **Phase 2**: 核心功能（分类筛选、考试系统、知识库）
- [ ] **Phase 3**: 进阶测试（描述题、复合缺陷、视频分析）
- [ ] **Phase 4**: 在线素材更新、检查更新机制完善

---

## 📄 许可证 | License

[MIT License](LICENSE)

---

## 👨‍💻 作者 | Author

[xy085653](https://github.com/xy085653)

---

> **影像工坊** — 看得见，才专业。
