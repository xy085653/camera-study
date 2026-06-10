import random

from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.models import User
from rest_framework import status, viewsets
from rest_framework.decorators import action, api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response

from .models import ExamRecord, KnowledgeEntry, Question, Sample
from .serializers import (ExamRecordSerializer, ExamSubmitSerializer,
                          KnowledgeEntrySerializer, QuestionSerializer,
                          SampleSerializer)


# ─── 样张 API ───
class SampleViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Sample.objects.filter(is_active=True)
    serializer_class = SampleSerializer
    filterset_fields = ["category", "type"]


# ─── 题库 API ───
class QuestionViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Question.objects.filter(is_active=True)
    serializer_class = QuestionSerializer
    filterset_fields = ["difficulty", "category", "type"]

    @action(detail=False, methods=["get"])
    def random(self, request):
        difficulty = request.query_params.get("difficulty", "basic")
        count = int(request.query_params.get("count", 10))
        qs = list(self.get_queryset().filter(difficulty=difficulty))
        selected = random.sample(qs, min(count, len(qs)))

        # 不返回正确答案（考试时保密）
        data = QuestionSerializer(selected, many=True).data
        for item in data:
            item.pop("correct_answer", None)
            item.pop("explanation", None)
        return Response(data)


# ─── 知识库 API ───
class KnowledgeViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = KnowledgeEntry.objects.filter(is_active=True)
    serializer_class = KnowledgeEntrySerializer


# ─── 考试 API ───
@api_view(["POST"])
@permission_classes([IsAuthenticated])
def submit_exam(request):
    serializer = ExamSubmitSerializer(data=request.data)
    if not serializer.is_valid():
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    data = serializer.validated_data
    questions = list(Question.objects.filter(
        difficulty=data["difficulty"], is_active=True))
    selected = questions[:len(data["answers"])]

    correct_count = 0
    for q in selected:
        user_ans = data["answers"].get(q.qid, "")
        if user_ans == q.correct_answer:
            correct_count += 1

    score = int(round(correct_count / len(selected) * 100)) if selected else 0

    record = ExamRecord.objects.create(
        user=request.user,
        difficulty=data["difficulty"],
        score=score,
        correct_count=correct_count,
        total_count=len(selected),
        answers=data["answers"],
        time_spent=data["time_spent"],
    )
    return Response(ExamRecordSerializer(record).data, status=status.HTTP_201_CREATED)


# ─── 考试记录 API ───
class ExamRecordViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = ExamRecordSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return ExamRecord.objects.filter(user=self.request.user)


# ─── 登录/注册 API ───
@api_view(["POST"])
@permission_classes([AllowAny])
def register(request):
    username = request.data.get("username", "").strip()
    password = request.data.get("password", "")
    email = request.data.get("email", "")

    if not username or not password:
        return Response({"error": "用户名和密码不能为空"}, status=400)
    if User.objects.filter(username=username).exists():
        return Response({"error": "用户名已存在"}, status=400)

    user = User.objects.create_user(username=username, password=password, email=email)
    return Response({"id": user.id, "username": user.username}, status=201)


@api_view(["POST"])
@permission_classes([AllowAny])
def login_view(request):
    username = request.data.get("username", "")
    password = request.data.get("password", "")
    user = authenticate(request, username=username, password=password)
    if user is not None:
        login(request, user)
        return Response({"id": user.id, "username": user.username})
    return Response({"error": "用户名或密码错误"}, status=400)


@api_view(["POST"])
def logout_view(request):
    logout(request)
    return Response({"message": "已退出"})


@api_view(["GET"])
def me(request):
    if not request.user.is_authenticated:
        return Response({"is_authenticated": False})
    return Response({
        "is_authenticated": True,
        "id": request.user.id,
        "username": request.user.username,
    })


# ─── 版本检查 API ───
@api_view(["GET"])
@permission_classes([AllowAny])
def check_version(request):
    return Response({
        "latest_version": "1.0.0",
        "download_url": "https://github.com/xy085653/camera-study/releases/latest",
        "update_notes": "初始版本",
    })
