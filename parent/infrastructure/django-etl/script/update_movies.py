#!/usr/bin/env python
# coding: utf-8

# In[4]:

from common import *
import numpy as np
from py2neo import Graph
import os
from urllib.request import urlopen, Request
import requests
from collections import OrderedDict
import json
import datetime
import sys


# In[2]:


graph = Graph("bolt://host.docker.internal:7687", auth=("neo4j", "admin"), bolt=False)

BASE_URL='https://api.themoviedb.org/3/movie/'
API_KEY='8e9078a24db79a8b98e327f3e62276a6'

# In[6]:


#helper function for get new movies as Dataframe
def get_new_movies():
    pagination_response = requests.get(BASE_URL +'now_playing?api_key='+API_KEY+'&language=en-US&page=1').json()
    num_pages = pagination_response['total_pages']
    row_movies_list=[]

    for page in range(1, num_pages):
        spage= str(page)
        response = requests.get(BASE_URL +'now_playing?api_key='+API_KEY+'&language=en-US&page='+spage+'')
        json_response= response.json()
        row_movies_list.append(pd.DataFrame(json_response['results']))

    return pd.concat(row_movies_list)

# In[7]:

print('==> Get new movies')
new_movies=get_new_movies()
new_movies.shape


# In[8]:


#factory method for get movie details as Dataframe
def movies_factory(movieId):
    request=Request(BASE_URL + movieId + '?api_key='+API_KEY+'&language=en-US')
    response = urlopen(request)
    elevations = response.read()
    data = json.loads(elevations)
    return pd.json_normalize(data)


# In[9]:


# helper function for get keywords as raw json
def get_keywords_by_movieId(movieId):
    response = requests.get(BASE_URL + movieId + '/keywords?api_key='+API_KEY+'')
    json_response= response.json()
    return json_response['keywords']



# In[10]:


#factory method for getting movie keywords as Dataframe
def keywords_factory(movieId):
    data = [[movieId, get_keywords_by_movieId(str(movieId))]]

    return pd.DataFrame(data, columns = ['id', 'keywords'])


# In[11]:


# helper function for get cast and crew as raw json
def get_cast_and_crew_by_movieId(movieId):
    response = requests.get(BASE_URL + movieId + '/credits?api_key='+API_KEY+'')
    json_response= response.json()
    return json_response['cast'], json_response['crew']


# In[12]:


#factory method for getting movie credits as Dataframe
def credits_factory(movieId):
    cast,crew = get_cast_and_crew_by_movieId(str(movieId))
    data = [[movieId,cast,crew]]

    return pd.DataFrame(data, columns = ['id', 'cast','crew'])


# In[13]:


#helper function for create the appropriate df by the factoryMethod
def get_df_by_factoryMethod(factoryMethod):
    df_list =[]

    for movieId in new_movies['id'].tolist():
        df = factoryMethod(str(movieId))
        df_list.append(df)

    return pd.concat(df_list)



# In[14]:

print('==> Get new movie details')
md=      get_df_by_factoryMethod(movies_factory)

print('==> Get credits')
credits= get_df_by_factoryMethod(credits_factory)

print('==> Get keywords')
keywords=get_df_by_factoryMethod(keywords_factory)

print('==> All new movie date has been arrived successfully')
# In[12]:


####################################### MOVIE ETL ##############################################################
print('==> Movie ETL flow has been started')

md['genres'] = md['genres'].fillna('[]').apply(str).apply(literal_eval).apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
md['year'] = pd.to_datetime(md['release_date'], errors='coerce').apply(lambda x: str(x).split('-')[0] if x != np.nan else np.nan)
md = md.drop(['adult','belongs_to_collection','budget','homepage','original_language','original_title','revenue','runtime','spoken_languages','poster_path','production_companies','release_date','production_countries','video','overview','tagline','popularity','belongs_to_collection.id','belongs_to_collection.name','belongs_to_collection.poster_path','belongs_to_collection.backdrop_path'], axis = 1)


# In[13]:


set_vote_data(md)


# In[14]:

movies_genres =get_movie_genres_df(md)


# In[16]:


keywords['id'] = keywords['id'].astype('int')
credits['id'] = credits['id'].astype('int')
md['id'] = md['id'].astype('int')


# In[17]:


md = md.merge(credits, on='id')
md = md.merge(keywords, on='id')


md['soup'] = create_md_soup(md)

md = md.drop(['genres','vote_average','vote_count','cast','crew','keywords','cast_size','crew_size','director','backdrop_path'], axis = 1)


# In[27]:


md.head()


# In[28]:


def execute_query(statement):
    tx = graph.begin(autocommit=True)
    tx.evaluate(statement)


# In[29]:

print('==> Complete the existing database with the new movies')

md.to_csv('/import/movies.csv', sep='|', header=True, index=False)

movie_import_statement = """
//movies upload
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///movies.csv" AS row
FIELDTERMINATOR '|'
with row where row.id is not null
MERGE (:Movies {movieId: row.id, title: row.title, rating_mean: row.wr,year: row.year,soup: row.soup});
"""

execute_query(movie_import_statement)


# In[30]:


movies_genres.to_csv('/import/movies_genres.csv', sep='|', header=True, index=False)

movies_genres_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///movies_genres.csv" AS row
FIELDTERMINATOR '|'
MATCH (movie:Movies {movieId: row.movieId})
MATCH (genres:Genres {genres: row.genres})
MERGE (movie)-[:IN_GENRE]->(genres);
"""

execute_query(movies_genres_import_statement)


# In[31]:


#read up the completed database as DataFrame
md= graph.run("MATCH (n:Movies) RETURN n.movieId as id ,n.soup as soup").to_data_frame()


# In[32]:

movies_similarity = create_movies_similarity(md)

# In[36]:

print('==> Update movies_similarity relationship')
movies_similarity.to_csv('/import/movies_similarity.csv', sep='|', header=True, index=False)

movies_similarity_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///movies_similarity.csv" AS row
FIELDTERMINATOR '|'
MATCH (movie1:Movies {movieId: row.id})
MATCH (movie2:Movies {movieId: row.sim_movieId})
MERGE (movie1)-[:MOVIE_SIMILAR {relevance: row.relevance}]->(movie2);
"""

execute_query(movies_similarity_statement)


print('==> Update movies ETL flow has been finished successfully')

# In[ ]:




