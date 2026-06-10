import json
from pathlib import Path

from django.core.management.base import BaseCommand

from api.models import KnowledgeEntry, Question, Sample


class Command(BaseCommand):
    help = "从 JSON 文件导入题库、样图、知识库"

    def handle(self, *args, **options):
        data_dir = Path("data")

        # 导入考题
        questions_file = data_dir / "questions" / "basic.json"
        if questions_file.exists():
            with open(questions_file, encoding="utf-8") as f:
                items = json.load(f)
            for item in items:
                q, created = Question.objects.update_or_create(
                    qid=item["id"],
                    defaults={
                        "type": item.get("type", "choice"),
                        "category": item.get("category", ""),
                        "difficulty": item.get("difficulty", "basic"),
                        "question_text": item.get("questionText", ""),
                        "options": item.get("options", []),
                        "correct_answer": item.get("correctAnswer", ""),
                        "explanation": item.get("explanation", ""),
                    },
                )
                self.stdout.write(f"  {'✓' if created else '○'} {q.qid} - {q.question_text[:30]}")
            self.stdout.write(self.style.SUCCESS(f"  共 {len(items)} 道题"))

        advanced_file = data_dir / "questions" / "advanced.json"
        if advanced_file.exists():
            with open(advanced_file, encoding="utf-8") as f:
                items = json.load(f)
            for item in items:
                q, created = Question.objects.update_or_create(
                    qid=item["id"],
                    defaults={
                        "type": item.get("type", "choice"),
                        "category": item.get("category", ""),
                        "difficulty": item.get("difficulty", "advanced"),
                        "question_text": item.get("questionText", ""),
                        "options": item.get("options", []),
                        "correct_answer": item.get("correctAnswer", ""),
                        "explanation": item.get("explanation", ""),
                    },
                )
            self.stdout.write(self.style.SUCCESS(f"  进阶题 {len(items)} 道"))

        # 导入知识库
        knowledge_file = data_dir / "knowledge_base.json"
        if knowledge_file.exists():
            with open(knowledge_file, encoding="utf-8") as f:
                items = json.load(f)
            for item in items:
                k, created = KnowledgeEntry.objects.update_or_create(
                    kid=item["id"],
                    defaults={
                        "title": item["title"],
                        "icon": item.get("icon", "📷"),
                        "summary": item.get("summary", ""),
                        "description": item.get("description", ""),
                        "causes": item.get("causes", ""),
                        "identification": item.get("identification", ""),
                        "solution": item.get("solution", ""),
                    },
                )
            self.stdout.write(self.style.SUCCESS(f"  知识库 {len(items)} 条"))
