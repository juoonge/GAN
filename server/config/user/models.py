from django.utils import timezone
from django.db import models
from django.contrib.auth.models import AbstractBaseUser,BaseUserManager,PermissionsMixin
from rest_framework_simplejwt.tokens import RefreshToken

# BaseUserManager : Helper Class
# 모든 django model은 Manager통해서 QuerySet을 받는다.
class UserManager(BaseUserManager):
    def create_user(self,nickname,id,password=None,password2=None,**other_fields):
        if not nickname:
            raise TypeError('User should have an nickname.')
        if not id:
            raise TypeError('User should have a ID')

        user=self.model(
            nickname=nickname,
            id=id,
            **other_fields
        )
        user.set_password(password)
        user.save()
        return user
    
    def create_superuser(self,nickname,id,password=None,**other_fields):
        other_fields.setdefault('is_staff', True)
        other_fields.setdefault('is_superuser', True)
        other_fields.setdefault('is_active', True)

        if other_fields.get('is_staff') is not True:
            raise ValueError('Superuser must be assigned to is_staff=True.')
        if other_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must be assigned to is_superuser=True.')
        return self.create_user(nickname,id,password,**other_fields)

# AbstractUser
# 실제 모델이 상속받아 생성하는 클래스
class User(AbstractBaseUser,PermissionsMixin):
    nickname=models.CharField(max_length=20,null=False,unique=False)
    id=models.CharField(max_length=30,null=False,unique=True,primary_key=True)
    
    is_staff = models.BooleanField(default=False)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(default=timezone.now)

    objects=UserManager()
    
    # 사용자의 username field는 id으로 설정 (id로 로그인)
    USERNAME_FIELD='id'
    REQUIRED_FIELDS=['nickname']

    def userInfo(self):
        return {
            'id': self.id
        }

    def tokens(self):
        refresh = RefreshToken.for_user(self)
        return {
            'nickname': self.nickname,
            'refresh': str(refresh),
            'access': str(refresh.access_token)
        }

