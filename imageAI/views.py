from django.shortcuts import render
import requests
from config import settings
from .serializers import StoryImageSerializer
from .models import StoryImage
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
import base64
import os
from django.http import JsonResponse
import tempfile

class StoryImageAPI(APIView):
    def post(self,request,format=None):
        serializer=StoryImageSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data,status=status.HTTP_201_CREATED)
        return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)


    def get(self,request):
        queryset=StoryImage.objects.all()
        serializer=StoryImageSerializer(queryset,many=True)
        return Response(serializer.data)

engine_id = "stable-diffusion-v1-5"
api_host = 'https://api.stability.ai'
api_key = "sk-BmyHbKcbW24dbNoZnXXJKb5WZbA77vgwznYXWGRofaS6LZO7"

def home(request):
    return render(request,'home.html')


# You can access the image with PIL.Image for example
def getImage(request):
    if request.method=='POST':
        text=request.POST['text']

    response = requests.post(
        f"{api_host}/v1/generation/{engine_id}/text-to-image",
        headers={
            "Content-Type": "application/json",
            "Accept": "application/json",
            "Authorization": f"Bearer {api_key}"
        },
        json={
            "text_prompts": [
                {
                    "text": text
                }
            ],
            "cfg_scale": 7,
            "clip_guidance_preset": "FAST_BLUE",
            "height": 512,
            "width": 512,
            "samples": 1,
            "steps": 30,
        },
    )

    if response.status_code != 200:
        raise Exception("Non-200 response: " + str(response.text))

    storyimage=StoryImage()
    storyimage.text=text

    data = response.json()
    
    path=str(os.path.join(settings.MEDIA_ROOT,'image/'))
    
    for i, image in enumerate(data["artifacts"]):
        filename=f"v1_txt2img_{i}.png"
        # 파일로 임시로 저장했다가 다시 업로드하는 방식
        #tmp_img=tempfile.NamedTemporaryFile() # 임시파일 생성

        with open(path+filename, "wb") as f:
            #storyimage.image.save(f"out/v1_txt2img_{i}.png",path_filenmae)
            f.write(base64.b64decode(image["base64"]))

    context={'text':text,'image':image}
    return JsonResponse(context)