from django.urls import path
from .views import UserCreate, LoginAPIView, LogoutAPIView, UserCheck,UserUpdate

app_name = 'user'

urlpatterns = [
    path('signup/', UserCreate.as_view(), name='create_user'),
    path('login/', LoginAPIView.as_view(), name='login'),
    path('logout/', LogoutAPIView.as_view(), name='logout'),
    path('check/', UserCheck.as_view(), name='check'),
    path('update/', UserUpdate.as_view(), name='update'),

]