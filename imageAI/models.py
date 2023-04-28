from django.db import models

# Create your models here.
class InputText(models.Model):
    text=models.TextField(null=False)

class StoryImage(models.Model):
    image = models.ImageField(upload_to='images/',null=True)
    text=models.TextField(null=False)

    def __str__(self):
        return f'{self.text}'




