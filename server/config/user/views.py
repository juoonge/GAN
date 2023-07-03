from django.shortcuts import render
from rest_framework import status, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.authentication import TokenAuthentication
from .serializers import SignupSerializer, LoginSerializer, LogoutSerializer
from django.http import JsonResponse
from user.models import User
import json

# Create your views here.

class UserCreate(APIView):
    serializer_class = SignupSerializer

    def post(self,request):
        serializer=self.serializer_class(data=request.data)
        if serializer.is_valid(raise_exception=True):
            serializer.save()
            user_data=serializer.data 
            return JsonResponse({'msg':'SUCCESS'}, status= 200)
        else:
            return JsonResponse({'msg':'FAIL'}, status= 400)


class LoginAPIView(APIView):
    serializer_class = LoginSerializer
    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

        
class LogoutAPIView(APIView):
    serializer_class = LogoutSerializer

    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception= True)
        serializer.save()

        return JsonResponse({'msg':'SUCCESS Logout'}, status= 400)
    
class UserCheck(APIView):
    
    def post(self,request):

        data = json.loads(request.body)

        if User.objects.filter(id = data['id']).exists():
            return JsonResponse({'message': 'ALREADY_EXISTS'}, status=440)
        else:          
            return JsonResponse({'msg': 'SUCCESS'})

class UserUpdate(APIView):
    
    def post(self,request):

        data = json.loads(request.body)

        if User.objects.filter(id = data['id']).exists():
            user = User.objects.get(id=data['id'])

            #if (user.objects.token == data['token']):
            user.nickname = data['nickname']
            user.save()

            return JsonResponse({'msg': 'SUCCESS'}, status=200)
        #else:         
            #return JsonResponse({'msg': 'FAIL'})        