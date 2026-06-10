from django.db import models


class Question(models.Model):
    TYPE_CHOICES = [("choice", "选择题"), ("judge", "判断题"), ("description", "描述题")]
    DIFFICULTY_CHOICES = [("basic", "基础"), ("advanced", "进阶")]

    qid = models.CharField("编号", max_length=20, unique=True)
    type = models.CharField("题型", max_length=20, choices=TYPE_CHOICES)
    category = models.CharField("缺陷维度", max_length=50, blank=True)
    difficulty = models.CharField("难度", max_length=20, choices=DIFFICULTY_CHOICES, default="basic")
    image = models.ImageField("图片", upload_to="questions/", blank=True, null=True)
    question_text = models.TextField("题干")
    options = models.JSONField("选项", default=list, blank=True)
    correct_answer = models.CharField("正确答案", max_length=10, blank=True)
    explanation = models.TextField("解析", blank=True)
    is_active = models.BooleanField("启用", default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "题目"
        verbose_name_plural = "题目"
        ordering = ["qid"]

    def __str__(self):
        return f"[{self.get_difficulty_display()}] {self.qid}"


class Sample(models.Model):
    TYPE_CHOICES = [("image", "图片"), ("video", "视频")]
    CATEGORY_CHOICES = [
        ("过曝", "过曝"), ("欠曝", "欠曝"), ("鬼影", "鬼影"), ("炫光", "炫光"),
        ("噪点", "噪点"), ("模糊", "模糊"), ("偏色", "偏色"), ("视频", "视频"),
    ]

    sid = models.CharField("编号", max_length=20, unique=True)
    title = models.CharField("名称", max_length=100)
    type = models.CharField("类型", max_length=10, choices=TYPE_CHOICES)
    category = models.CharField("缺陷维度", max_length=50, choices=CATEGORY_CHOICES)
    file = models.FileField("文件", upload_to="samples/")
    description = models.TextField("缺陷说明", blank=True)
    is_active = models.BooleanField("启用", default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "样张"
        verbose_name_plural = "样张"

    def __str__(self):
        return self.title


class KnowledgeEntry(models.Model):
    kid = models.CharField("编号", max_length=20, unique=True)
    title = models.CharField("名称", max_length=100)
    icon = models.CharField("图标", max_length=10, default="📷")
    summary = models.CharField("概述", max_length=200)
    description = models.TextField("详细描述", blank=True)
    causes = models.TextField("成因", blank=True)
    identification = models.TextField("识别方法", blank=True)
    solution = models.TextField("改善建议", blank=True)
    is_active = models.BooleanField("启用", default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "知识条目"
        verbose_name_plural = "知识条目"
        ordering = ["kid"]

    def __str__(self):
        return self.title


class ExamRecord(models.Model):
    DIFFICULTY_CHOICES = [("basic", "基础"), ("advanced", "进阶")]

    user = models.ForeignKey("auth.User", on_delete=models.CASCADE, verbose_name="用户")
    difficulty = models.CharField("难度", max_length=20, choices=DIFFICULTY_CHOICES)
    score = models.IntegerField("得分")
    correct_count = models.IntegerField("正确数")
    total_count = models.IntegerField("总题数")
    answers = models.JSONField("答题记录", default=dict)
    time_spent = models.IntegerField("用时(秒)", default=0)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "考试记录"
        verbose_name_plural = "考试记录"
        ordering = ["-created_at"]

    def __str__(self):
        return f"{self.user} - {self.get_difficulty_display()} - {self.score}"
