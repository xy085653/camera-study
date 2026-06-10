from django.contrib import admin

from .models import ExamRecord, KnowledgeEntry, Question, Sample


@admin.register(Question)
class QuestionAdmin(admin.ModelAdmin):
    list_display = ["qid", "type", "category", "difficulty", "is_active"]
    list_filter = ["difficulty", "type", "category", "is_active"]
    search_fields = ["qid", "question_text"]


@admin.register(Sample)
class SampleAdmin(admin.ModelAdmin):
    list_display = ["sid", "title", "type", "category", "is_active"]
    list_filter = ["type", "category", "is_active"]


@admin.register(KnowledgeEntry)
class KnowledgeEntryAdmin(admin.ModelAdmin):
    list_display = ["kid", "title", "icon"]
    search_fields = ["title", "description"]


@admin.register(ExamRecord)
class ExamRecordAdmin(admin.ModelAdmin):
    list_display = ["user", "difficulty", "score", "correct_count", "created_at"]
    list_filter = ["difficulty"]
