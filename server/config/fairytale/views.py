import urllib

from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status
from rest_framework.parsers import JSONParser
from rest_framework.utils import json
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import StorySerializer,StoryElementSerializer
from .models import Story,StoryElement,User
from django.http import Http404, JsonResponse
from rest_framework.permissions import IsAuthenticated
import os

class StoryList(APIView):
    serializer_class=StorySerializer

    permission_classes = [IsAuthenticated]

    # 동화목록 조회
    def get(self,request):
        stories=Story.objects.filter(author=request.user.id)
        serializer=StorySerializer(stories,many=True)
        return Response(serializer.data)

    # 동화 등록
    def post(self,request):
        serializer=self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        story_data=serializer.data
        return Response(story_data,status=status.HTTP_201_CREATED)

     #동화 삭제
    def delete(self,request,pk):
        story = Story.objects.get(pk=pk)
        story.delete()
        return JsonResponse({'msg': 'SUCCESS'})
    
class StoryElementList(APIView):
    serializer_class=StoryElementSerializer

    def post(self,request):
        serializer=self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        story_data=serializer.data
        return Response(story_data,status=status.HTTP_201_CREATED)

    def put(self,request):
        serializer=self.serializer_class(data=request.data)
        #print(StoryElement.objects.all())
        serializer.is_valid(raise_exception=True)
        serializer.save()
        story_data=serializer.data
        return Response(story_data,status=status.HTTP_201_CREATED)



class changeStoryElement(APIView):
    serializer_class=StoryElementSerializer

    def get(self,pk):
        try:
            return StoryElement.objects.get(pk=pk)
        except StoryElement.DoesNotExist:
            raise Http404

    def put(self, request, pk ,format=None):
        StoryElement = self.get(pk)
        serializer=self.serializer_class(StoryElement,data=request.data)
        if serializer.is_valid():
            serializer.save()
            story_data=serializer.data
            return Response(story_data,status=status.HTTP_201_CREATED)
        return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)

class StoryDetail(APIView):
    permission_classes = [IsAuthenticated]
    # 동화 객체 가져오기
    def get_object(self,pk):
        try:
            return Story.objects.get(pk=pk)
        except Story.DoesNotExist:
            raise Http404
    
    # 동화 detail 보기
    def get(self,request,pk,fromat=None):
        story=self.get_object(pk)
        serializer=StorySerializer(story)
        return Response(serializer.data)

    # 동화 수정하기
    def put(self,request,pk,format=None):
        story=self.get_object(pk)
        serializer=StorySerializer(story,data=request.data)
        print(request.data)
        if serializer.is_valid():
            serializer.save()   
            return Response(serializer.data)
        return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)


    # 동화 삭제하기
    def delete(self,request,pk,format=None):
        story=self.get_object(pk)
        story.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


