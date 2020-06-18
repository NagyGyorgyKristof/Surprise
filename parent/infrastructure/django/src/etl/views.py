from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.conf.urls import url, include

@api_view(['GET','POST'])
def start_up(request):
    return Response({"message": "Hello it is the start up etl flow : )"})

@api_view(['GET','POST'])
def update_movies(request):
    return Response({"message": "Hello it is the update movies etl flow : )"})