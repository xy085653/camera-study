# 影像效果工程师看图能力训练 App 设计文档

> 日期: 2026-06-09
> 状态: 设计稿

## 1. 项目概述

### 1.1 目标
培养影像效果工程师的看图能力，通过展示不同维度的影像缺陷样张（图片/视频）和分级测试系统，帮助工程师识别、分析和理解各类影像质量问题。

### 1.2 用户群体
- 刚入行的影像效果工程师（新人）
- 有一定经验的影像效果工程师

### 1.3 技术选型

| 层次 | 技术方案 |
|------|----------|
| 前端 | Android Native (Java) - 基于现有项目 |
| 后端服务 | Firebase (BaaS) |
| 用户认证 | Firebase Auth (邮箱登录) |
| 数据库 | Cloud Firestore |
| 文件存储 | Cloud Storage (图片/视频素材) |
| 版本兼容 | minSdk 24, targetSdk 36 |

### 1.4 架构策略
采用混合渐进架构：App 内置基础素材包保障离线可用，通过 Firebase 实现在线素材扩展、用户认证、数据同步和全局排行榜。可渐进式开发，先完成本地核心功能，再逐步接入云端能力。

## 2. 导航结构

### 2.1 底部导航 (4 个 Tab)

| Tab | 页面 | 说明 |
|-----|------|------|
| 📸 样图库 | SampleGalleryFragment | 影像样张浏览 |
| 📝 考试 | ExamHomeFragment | 测试入口 |
| 🏆 排行榜 | LeaderboardFragment | 用户排名 |
| 👤 我的 | ProfileFragment | 个人中心 |

### 2.2 导航流程

```
App Launch
     ↓
┌─ Bottom Navigation ──────────────────┐
│  样图库  │  考试  │  排行榜  │  我的  │
└──────────┴────────┴─────────┴───────┘
     │          │         │         │
     ↓          ↓         ↓         ↓
 样图库列表   考试首页   排行榜   个人中心
     │          │                   │
     ↓          ↓                   ↓
 样张详情   作答页面            登录/注册
     │          │                   │
     │          ↓                   │
     │      考试结果                │
```

## 3. 模块设计

### 3.1 用户模块

#### 功能
- 注册（邮箱 + 密码）
- 登录 / 自动登录（Token 持久化）
- 退出登录
- 个人资料展示（头像、昵称、邮箱、综合评分、排名）

#### 页面
- **个人中心** (ProfileFragment)：展示用户数据、快捷入口
- **登录页** (LoginActivity)：邮箱/密码输入
- **注册页** (RegisterActivity)：邮箱/密码/昵称输入
- **设置页** (SettingsActivity)：基础设置

#### 数据模型 (Firestore)
```javascript
// users/{uid}
{
  uid: string,
  email: string,
  displayName: string,
  avatarUrl: string,
  createdAt: Timestamp,
  stats: {
    totalScore: number,      // 综合评分
    totalExams: number,      // 完成考试次数
    totalQuestions: number,  // 完成题目数
    avgAccuracy: number,     // 平均正确率
    rank: number             // 当前排名
  }
}
```

### 3.2 样图库模块

#### 3.2.1 缺陷分类维度
1. ☀️ **过曝 (Overexposure)** — 高光区域细节丢失
2. 🌑 **欠曝 (Underexposure)** — 暗部细节丢失
3. 👻 **鬼影 (Ghost/Flare)** — 镜头内反射造成的虚影
4. 🌈 **炫光 (Lens Flare)** — 强光造成的条状光斑
5. 📸 **噪点 (Noise)** — 低光环境下的颗粒噪点
6. 🌫️ **模糊 (Blur)** — 对焦不准或运动模糊
7. 🎨 **偏色 (Color Cast)** — 白平衡不准导致的整体偏色
8. 📹 **视频 (Video)** — 视频相关的质量问题

#### 3.2.2 页面结构
- **样图库列表页** (SampleGalleryFragment)
  - 顶部搜索栏
  - 分类标签横向滚动（全部/各维度/视频）
  - 双列网格展示（图片/视频缩略图混排）
  - 图片标注缺陷类型和名称
  - 视频缩略图标识播放图标和"视频"角标

- **样张详情页** (SampleDetailActivity)
  - 图片详情：全尺寸展示 + 缺陷维度标签 + 缺陷说明文字
  - 视频详情：视频播放器 + 缺陷维度标签 + 缺陷说明文字

#### 3.2.3 数据来源
- **内置资源**：assets 目录存放基础素材包，随 APK 发布
- **在线扩展**：Firebase Storage 存储额外素材，首次加载后缓存到本地

#### 数据模型 (Firestore)
```javascript
// samples/{sampleId}
{
  sampleId: string,
  title: string,
  type: "image" | "video",
  category: string,          // 缺陷维度
  fileName: string,
  description: string,       // 缺陷说明
  storageUrl: string,        // Firebase Storage URL
  localAsset: string,        // 内置资源路径（可选）
  difficulty: "basic" | "advanced",
  tags: string[],            // 关联标签
  createdAt: Timestamp
}
```

### 3.3 考试模块

#### 3.3.1 难度分层

| 维度 | 基础测试 | 进阶测试 |
|------|----------|----------|
| 题型 | 单选、判断 | 单选、判断、描述题 |
| 缺陷 | 单一缺陷 | 复合缺陷场景 |
| 素材 | 图片为主 | 图片 + 视频 |
| 题量 | 10-15 题 | 20-30 题 |
| 时限 | 不限时 | 限时 |
| 考察点 | 识别能力 | 分析能力 |

#### 3.3.2 题型设计

**题型 A: 选择题**
- 从 4 张图中选出存在目标缺陷的一张
- 或从 4 个选项中选出正确的缺陷描述

**题型 B: 判断题**
- 展示一张图，判断是否存在特定缺陷
- 选项：是 / 否

**题型 C: 描述题/填空题**
- 展示一张图片或一段视频
- 用户输入文字描述分析结果
- 列出至少 N 个缺陷类型和改进建议

#### 3.3.3 考试流程

```
 考试首页
    │  选择：基础测试 / 进阶测试
    ↓
 确认页面
    │  展示：题量、题型、时限（进阶）
    │  操作：开始考试
    ↓
 作答页面
    │  顶部：进度条（当前题号/总题数）
    │  中部：题目内容（图片/视频 + 题干）
    │  底部：选项按钮 或 输入框
    │  操作：下一题（不可回退）
    ↓
 结果页面
    │  展示：总分、正确题数、错误题数、用时、击败百分比
    │  操作：查看解析 / 重新考试 / 返回首页
```

#### 3.3.4 组卷算法
- 从题库随机抽取题目
- 确保覆盖多个缺陷维度
- 进阶测试需包含一定比例的复合缺陷题和视频题
- 同一用户连续考试避免重复出题

#### 3.3.5 评分规则
- 选择题/判断题：正确得分，错误不得分
- 描述题：关键词匹配评分（后续可优化为人工评分或 AI 辅助评分）
- 最终分数 = (正确题数 / 总题数) × 100

#### 数据模型 (Firestore)
```javascript
// questions/{questionId}
{
  questionId: string,
  type: "choice" | "judge" | "description",
  difficulty: "basic" | "advanced",
  category: string,             // 关联缺陷维度
  mediaUrl: string,             // 题目图片/视频 URL
  mediaType: "image" | "video",
  questionText: string,         // 题干
  options: string[],            // 选择题选项（仅 choice 类型）
  correctAnswer: string,        // 正确答案（choice/judge 用）
  keywords: string[],           // 关键词（description 评分用）
  explanation: string,          // 答案解析
  tags: string[]
}

// exams/{examId}
{
  examId: string,
  userId: string,
  difficulty: "basic" | "advanced",
  questions: string[],          // 题目 ID 列表
  answers: Map<string, string>, // questionId → 用户答案
  score: number,
  totalQuestions: number,
  correctCount: number,
  timeSpent: number,            // 秒
  completedAt: Timestamp
}
```

### 3.4 排行榜模块

#### 3.4.1 排名维度 Tab
- **综合** (默认) — 综合评分排序
- **正确率** — 平均正确率排序
- **题量** — 完成题数排序
- **月榜** — 本月活跃排行

#### 3.4.2 综合评分算法
```
综合分 = 正确率得分 × 40% + 完成题量得分 × 25% + 用时效率得分 × 20% + 进阶加成 × 15%
```

#### 3.4.3 页面结构
- **排行榜列表** (LeaderboardFragment)
  - 顶部 Tab 切换
  - 当前用户高亮展示（置顶或特殊标记）
  - 榜单列表：排名序号 + 头像 + 昵称 + 综合分 + 完成测试数
  - 前三名展示金银铜奖牌
  - 下拉刷新

#### 数据模型 (Firestore)
```javascript
// leaderboard/{leaderboardId}
{
  userId: string,
  displayName: string,
  avatarUrl: string,
  compositeScore: number,     // 综合分
  avgAccuracy: number,        // 平均正确率
  totalQuestions: number,     // 完成题数
  monthlyScore: number,       // 月榜得分
  updatedAt: Timestamp
}
```

## 4. 包结构设计

```java
com.example.sevenxiao/
│
├── MainActivity.java              // 主 Activity（含底部导航）
│
├── auth/
│   ├── LoginActivity.java         // 登录
│   └── RegisterActivity.java      // 注册
│
├── gallery/
│   ├── SampleGalleryFragment.java // 样图库列表
│   ├── SampleDetailActivity.java  // 样张详情
│   └── adapter/
│       └── SampleAdapter.java     // 样图网格适配器
│
├── exam/
│   ├── ExamHomeFragment.java      // 考试首页
│   ├── ExamActivity.java          // 考试作答页
│   ├── ExamResultActivity.java    // 考试结果页
│   ├── adapter/
│   │   └── QuestionAdapter.java   // 题目适配器
│   └── model/
│       └── Question.java          // 题目模型
│
├── leaderboard/
│   └── LeaderboardFragment.java   // 排行榜
│
├── profile/
│   ├── ProfileFragment.java       // 个人中心
│   └── SettingsActivity.java      // 设置
│
├── data/
│   ├── local/
│   │   ├── AssetManager.java      // 内置资源管理
│   │   └── LocalCacheManager.java // 本地缓存
│   ├── remote/
│   │   ├── FirebaseManager.java   // Firebase 初始化与操作
│   │   ├── AuthRepository.java    // 用户认证仓库
│   │   ├── SampleRepository.java  // 样图数据仓库
│   │   ├── ExamRepository.java    // 考试数据仓库
│   │   └── LeaderboardRepository.java // 排行榜仓库
│   └── model/
│       ├── User.java              // 用户模型
│       ├── Sample.java            // 样图模型
│       ├── Question.java          // 题目模型
│       └── ExamResult.java        // 考试结果模型
│
└── util/
    ├── ImageUtils.java            // 图片加载工具
    └── ScoreUtils.java            // 评分计算工具
```

## 5. 开发路线图（建议分期）

### Phase 1: 基础框架
- [ ] Firebase 项目配置与接入
- [ ] 底部导航架构搭建
- [ ] 用户注册/登录（Firebase Auth）
- [ ] 内置样图资源加载与展示

### Phase 2: 核心功能
- [ ] 样图库完整功能（分类筛选、详情页）
- [ ] 基础测试题库 + 答题流程
- [ ] 选择题/判断题支持
- [ ] 考试结果展示

### Phase 3: 进阶功能
- [ ] 进阶测试（含描述题、复合缺陷）
- [ ] 视频播放与展示
- [ ] 在线素材更新机制

### Phase 4: 社交与完善
- [ ] 排行榜（多维度排名）
- [ ] 考试记录与错题本
- [ ] 性能优化与离线策略
- [ ] 测试与发布

## 6. 关键设计决策

1. **描述题评分**：初期采用关键词匹配评分，后续可引入人工评分或 AI 辅助评分
2. **图片加载**：使用 Coil/Picasso/Glide 实现图片的懒加载和缓存
3. **视频播放**：使用 ExoPlayer 实现视频播放
4. **离线策略**：核心素材内置，在线素材首次加载后缓存至本地 Room 数据库
5. **安全**：Firestore Security Rules 控制数据访问权限，用户只能读写自己的数据
