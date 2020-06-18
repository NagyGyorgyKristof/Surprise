FROM python:3
#FROM jupyter/datascience-notebook talan igy is mukodik majd !

ENV PYTHONUNBUFFERED 1
RUN mkdir /code
WORKDIR /code
COPY requirements.txt /code/
RUN pip install -r requirements.txt
COPY . /code/