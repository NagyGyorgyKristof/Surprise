from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.conf.urls import url, include
import os
import logging


logger = logging.getLogger()

@api_view(['GET','POST'])
def start_up(request):
    profile = request.GET.get('profile', None)
    if profile is not None:
        logger.info('Movie setup has been started!')
        os.system('python3 script/setup.py ' + profile)
        logger.info('Movie setup has been finished successfully!')

        return Response({"message": "Movie setup was successful"})
    else:
        return Response({"message": "Profile param is required"})

@api_view(['GET','POST'])
def update_movies(request):
    profile = request.GET.get('profile', None)
    if profile is not None:
        logger.info('Movie update has been started!')
        os.system('python3 script/update_movies.py '+ profile)
        logger.info('Movie update has benn finished successfully')

        return Response({"message": "Movie update was successful"})
    else:
        return Response({"message": "Profile param is required"})


