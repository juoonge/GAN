from rest_framework import serializers
from .models import StoryImage,InputText

class InputTextSerializer(serializers.ModelSerializer):
    class Meta:
        model=InputText
        field='__all__'

class StoryImageSerializer(serializers.ModelSerializer):
    class Meta:
        model=StoryImage
        field='__all__'
