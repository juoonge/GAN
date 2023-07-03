from django.urls import path
from .views import StoryList,StoryDetail,StoryElementList, changeStoryElement
from. import views

app_name='fairytale'

urlpatterns=[
    path('list/',StoryList.as_view()),
    path('list/<int:pk>/delete/',StoryList.as_view()),
    path('listelement/',StoryElementList.as_view()),
    path('list/<int:pk>/',StoryDetail.as_view()),
    path('changelistelement/<int:pk>/',changeStoryElement.as_view()),

]