from django.urls import path
from. import views

app_name='fairytale'

urlpatterns=[
    path('gpt/', views.Chat_gpt),
    path('papago/', views.papago),
    path('recommend_next/', views.recommend_next),
    path('compatibility/', views.compatibility),
    path('pick_word/', views.pick_word),
    path('first_recommend_next/', views.first_recommend_next),
]