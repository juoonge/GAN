from django.shortcuts import render
import requests
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser

from .serializers import StoryImageSerializer
from .models import StoryImage
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
import base64
import os
from django.http import JsonResponse
import tempfile
import urllib
from rest_framework.utils import json


class StoryImageAPI(APIView):
    def post(self, request, format=None):
        serializer = StoryImageSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def get(self, request):
        queryset = StoryImage.objects.all()
        serializer = StoryImageSerializer(queryset, many=True)
        return Response(serializer.data)


engine_id = "stable-diffusion-v1-5"
api_host = 'https://api.stability.ai'
api_key = "sk-4vZokVc4p7vYBmBhm4JTnqBTlgrBEWPhsim9PM1aq2ArWzYB"


# You can access the image with PIL.Image for example
def getImage(request):
    if request.method == 'GET':
        text = request.GET['text']

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

    storyimage = StoryImage()
    storyimage.text = text

    data = response.json()

    # path=str(os.path.join(settings.MEDIA_ROOT,'image/'))
    image = data["artifacts"][0]["base64"]
    # for i, image in enumerate(data["artifacts"]):
    # filename=f"v1_txt2img_{i}.png"
    # 파일로 임시로 저장했다가 다시 업로드하는 방식
    # tmp_img=tempfile.NamedTemporaryFile() # 임시파일 생성

    # with open(path+filename, "wb") as f:
    # storyimage.image.save(f"out/v1_txt2img_{i}.png",path_filenmae)
    # f.write(base64.b64decode(image["base64"]))

    context = {'text': text, 'image': image}
    return JsonResponse(context)


@csrf_exempt
def get_image(request):
    if request.method == 'POST':
        # 번역 부분
        data = JSONParser().parse(request)
        trans_text = data['prompt']
        style=data['style']
        print(trans_text)
        origin_text = trans_text

        client_id = "ebIYl6a2X8eQiRLd3WHa"  # 개발자센터에서 발급받은 Client ID 값
        client_secret = "BHMLx01Wnm"  # 개발자센터에서 발급받은 Client Secret 값

        encText = urllib.parse.quote(trans_text)
        data = "source=ko&target=en&text=" + encText
        url = "https://openapi.naver.com/v1/papago/n2mt"
        request = urllib.request.Request(url)
        request.add_header("X-Naver-Client-Id", client_id)
        request.add_header("X-Naver-Client-Secret", client_secret)
        response = urllib.request.urlopen(request, data=data.encode("utf-8"))
        rescode = response.getcode()
        if (rescode == 200):
            response_body = response.read().decode()
            response_json = json.loads(response_body)
        else:
            print("Error Code:" + rescode)

        # 그림 받아오는 부분
        search_name = response_json['message']['result']['translatedText']
        print(search_name)
        engine_id = "stable-diffusion-v1-5"
        api_host = os.getenv('API_HOST', 'https://api.stability.ai')
        api_key = "sk-eRYdjqu0U90MQ9naC5oJmDC6MKTKiu3z9zIFHbGKJ7SDZbya"

        if api_key is None:
            raise Exception("Missing Stability API key.")

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
                        "text": f"{search_name}"
                    }
                ],
                "cfg_scale": 7,
                "clip_guidance_preset": "FAST_BLUE",
                "height": 512,
                "width": 512,
                "samples": 1,
                "steps": 30,
                "style_preset":style,
            },
        )

        if response.status_code != 200:
            raise Exception("Non-200 response: " + str(response.text))

        data = response.json()
        image = data["artifacts"][0]["base64"]
        context = {'text': origin_text, 'image': image}

        ##for i, image in enumerate(data["artifacts"]):
        ##    with open(f"./out/v1_txt2img_{i}.png", "wb") as f:
        ##        f.write(base64.b64decode(image["base64"]))

        return JsonResponse(context, safe=False)