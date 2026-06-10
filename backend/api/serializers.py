from rest_framework import serializers

from .models import ExamRecord, KnowledgeEntry, Question, Sample


class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Question
        fields = ["qid", "type", "category", "difficulty", "question_text",
                   "options", "correct_answer", "explanation", "image"]


class SampleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sample
        fields = ["sid", "title", "type", "category", "file", "description"]


class KnowledgeEntrySerializer(serializers.ModelSerializer):
    class Meta:
        model = KnowledgeEntry
        exclude = ["id", "is_active", "created_at"]


class ExamSubmitSerializer(serializers.Serializer):
    difficulty = serializers.ChoiceField(choices=["basic", "advanced"])
    answers = serializers.DictField(child=serializers.CharField())
    time_spent = serializers.IntegerField(min_value=0)


class ExamRecordSerializer(serializers.ModelSerializer):
    class Meta:
        model = ExamRecord
        fields = ["id", "difficulty", "score", "correct_count",
                   "total_count", "time_spent", "created_at"]
