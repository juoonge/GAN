from django.db import models

# Create your models here.
class InputText(models.Model):
    text=models.TextField(null=False)
    
    def __str__(self):
        return self.text

class StoryImage(models.Model):
    text=models.TextField(null=False)
    image = models.TextField(null=True)

    def __str__(self):
        return self.image




