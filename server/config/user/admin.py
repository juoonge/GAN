from django.contrib import admin
from . import models
from django.db import models
from django.contrib.auth.admin import UserAdmin
from .models import User
from django.forms import  Textarea

# Register your models here.

class UserAdmin(UserAdmin):
    model=User
    search_fields = ("id", "nickname")
    list_filter = ('id', 'nickname', 'is_active', 'is_staff')
    ordering = ('-created_at',)
    list_display = ('id','nickname', 'is_active', 'is_staff')

    fieldsets = (
        (None, {'fields': ('id','nickname')}),
        ('Permissions', {'fields': ('is_staff','is_active')}),
    )

    formfield_overrides = {
        models.TextField: {'widget': Textarea(attrs={'rows':20,'cols':60})},
    }

    add_fieldsets = (
        (None, {
            'classes' : ('wide',),
            'fields': ('id', 'nickname', 'password1', 'password2', 'is_active', 'is_staff',)
        }),
    )

admin.site.register(User, UserAdmin)
