from user.models import User
from rest_framework import serializers
from django.contrib.auth.password_validation import validate_password
from django.contrib import auth
from rest_framework.exceptions import AuthenticationFailed
from rest_framework_simplejwt.tokens import RefreshToken, TokenError

class SignupSerializer(serializers.ModelSerializer):
    
    password = serializers.CharField(
        max_length=68,
        write_only=True,
        validators = [validate_password],
    )
    password2 = serializers.CharField(
        write_only=True, 
        required=True
    )

    class Meta:
        model=User 
        fields=('nickname','id','password','password2',)
    
    def validate(self,instance):
        if instance['password']!=instance['password2']:
            raise serializers.ValidationError(
                {'password':'Password Not Match'}
            )
        return instance

    def create(self,validated_data):
        return User.objects.create_user(**validated_data)

class LoginSerializer(serializers.ModelSerializer):
    id=serializers.CharField(max_length=30)
    password=serializers.CharField(max_length=68,write_only=True)

    tokens=serializers.CharField(max_length=68,read_only=True)

    class Meta:
        model=User
        fields=['id','password','tokens']

    def validate(self,instance):
        id=instance.get('id','')
        password=instance.get('password','')
    
        user=auth.authenticate(id=id,password=password)

        if not user:
            raise AuthenticationFailed('Invalid credentials, try again')
        return{
            'id':user.id,
            'tokens':user.tokens
        }

class LogoutSerializer(serializers.Serializer):
    refresh = serializers.CharField()

    default_error_messages = {
        'bad_token': ('Token is expired or invalid')
    }

    def validate(self, instance):
        self.token = instance['refresh']
        return instance

    def save(self, **kwargs):

        try:
            RefreshToken(self.token).blacklist()

        except TokenError:
            self.fail('bad_token')
