from django.urls import include, path
from rest_framework.routers import DefaultRouter

from . import views

router = DefaultRouter()
router.register("samples", views.SampleViewSet, basename="sample")
router.register("questions", views.QuestionViewSet, basename="question")
router.register("knowledge", views.KnowledgeViewSet, basename="knowledge")
router.register("records", views.ExamRecordViewSet, basename="record")

urlpatterns = [
    path("", include(router.urls)),
    path("exam/submit/", views.submit_exam, name="exam-submit"),
    path("auth/register/", views.register, name="register"),
    path("auth/login/", views.login_view, name="login"),
    path("auth/logout/", views.logout_view, name="logout"),
    path("auth/me/", views.me, name="me"),
    path("version/", views.check_version, name="version"),
]
