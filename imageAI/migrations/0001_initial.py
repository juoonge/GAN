# Generated by Django 3.1.3 on 2023-04-14 09:25

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='StoryImage',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('image', models.ImageField(blank=True, upload_to='%Y-%m-%d')),
                ('text', models.TextField(max_length=500)),
            ],
        ),
    ]
