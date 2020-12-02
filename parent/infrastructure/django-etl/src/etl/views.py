from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.conf.urls import url, include
import os
import logging


logger = logging.getLogger()

@api_view(['GET','POST'])
def start_up(request):
    logger.info('Movie setup has been started!')
    os.system('python3 django-etl/script/setup.py')
    logger.info('Movie setup has been finished successfully!')

    return Response({"message": "Movie setup was successful"})

@api_view(['GET','POST'])
def update_movies(request):
    logger.info('Movie update has been started!')
    os.system('python3 django-etl/script/update_movies.py')
    logger.info('Movie update has benn finished successfully')

    return Response({"message": "Movie update was successful"})


