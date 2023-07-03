from dataclasses import fields
from rest_framework import serializers,generics,mixins
from .models import Story,StoryElement


class StoryElementSerializer(serializers.ModelSerializer):
    class Meta:
        model=StoryElement 
        fields=('story_id','element_id','text','image')

class StorySerializer(serializers.ModelSerializer):
    contents=StoryElementSerializer(many=True,read_only=True)
    
    class Meta:
        model=Story
        fields=('id','title','author','contents','created_at','title_num')


