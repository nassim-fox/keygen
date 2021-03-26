from django.urls import path


from keygenApp import views 

app_name = "keygenApp" 

urlpatterns = [
    path('',views.index,name="index"),
    path('about',views.about,name="about"),
    path('api/generate',views.generate,name="generate"),
    path('api/save',views.save,name="save"), 
    path('api/get_all',views.get_all,name="get_all"),
    path('api/get_key',views.get_key,name="get_key"),
    path('api/delete',views.delete,name="delete"),
    path('api/update',views.update,name="update")
]