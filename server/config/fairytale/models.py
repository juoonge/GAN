from django.db import models
from user.models import User

class Story(models.Model):
    id = models.AutoField(primary_key=True,unique=True)
    title=models.CharField(max_length=200,null=False,blank=True)
    author=models.ForeignKey(User,on_delete=models.CASCADE,related_name="story")
    created_at=models.DateTimeField(auto_now_add=True)
    updated_at=models.DateTimeField(auto_now=True)
    title_num = models.CharField(max_length=200, null=False,blank=True)

    def __str__(self):
        return self.title

class StoryElement(models.Model):
    story_id=models.ForeignKey(Story,on_delete=models.CASCADE,related_name="contents",db_column="story_id")
    element_id=models.AutoField(primary_key=True)
    text=models.CharField(max_length=400,null=True,blank=False)
    image=models.TextField(null=True,blank=False)
    #voice

    def __str__(self):
        return self.text


    
