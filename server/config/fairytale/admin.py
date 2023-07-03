from django.contrib import admin
from .models import Story
from .models import StoryElement

class StoryAdmin(admin.ModelAdmin):
    list_display=('title','author',)

admin.site.register(StoryElement)
admin.site.register(Story,StoryAdmin)
