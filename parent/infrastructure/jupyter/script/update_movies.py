#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np
from py2neo import Graph
import os
from urllib.request import urlopen, Request

import requests
from collections import OrderedDict
import json
import datetime
from io import BytesIO, StringIO
from csv import writer 
from ast import literal_eval
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity


# In[2]:


graph = Graph("bolt://host.docker.internal:7687", auth=("neo4j", "admin"), bolt=False)


# In[3]:


#helper function for get new movies as Dataframe
def get_new_movies():
    pagination_response = requests.get('https://api.themoviedb.org/3/movie/now_playing?api_key=8e9078a24db79a8b98e327f3e62276a6&language=en-US&page=1').json()
    num_pages = pagination_response['total_pages']
    row_movies_list=[]

    #TODO user the num_pages instead of the hardcoded 5 in the for loop
    for page in range(1, 5):
        si= str(page)
        response = requests.get('https://api.themoviedb.org/3/movie/now_playing?api_key=8e9078a24db79a8b98e327f3e62276a6&language=en-US&page='+si+'')
        json_response= response.json()
        row_movies_list.append(pd.DataFrame(json_response['results']))

    return pd.concat(row_movies_list)


# In[4]:


new_movies=get_new_movies()
new_movies.shape


# In[5]:


#factory method for get movie details as Dataframe
def movies_factory(movieId):
    request=Request('https://api.themoviedb.org/3/movie/'+movieId+'?api_key=8e9078a24db79a8b98e327f3e62276a6&language=en-US')
    response = urlopen(request)
    elevations = response.read()
    data = json.loads(elevations)
    return pd.json_normalize(data)


# In[6]:


# helper function for get keywords as raw json
def get_keywords_by_movieId(movieId):
    response = requests.get('https://api.themoviedb.org/3/movie/'+movieId+'/keywords?api_key=8e9078a24db79a8b98e327f3e62276a6')
    json_response= response.json()
    return json_response['keywords']
    


# In[7]:


#factory method for getting movie keywords as Dataframe
def keywords_factory(movieId):
    data = [[movieId, get_keywords_by_movieId(str(movieId))]] 
  
    return pd.DataFrame(data, columns = ['id', 'keywords']) 


# In[8]:


# helper function for get cast and crew as raw json
def get_cast_and_crew_by_movieId(movieId):
    response = requests.get('https://api.themoviedb.org/3/movie/'+movieId+'/credits?api_key=8e9078a24db79a8b98e327f3e62276a6')
    json_response= response.json()
    return json_response['cast'], json_response['crew'];


# In[9]:


#factory method for getting movie credits as Dataframe
def credits_factory(movieId):
    cast,crew = get_cast_and_crew_by_movieId(str(movieId))
    data = [[movieId,cast,crew]] 
  
    return pd.DataFrame(data, columns = ['id', 'cast','crew']) 


# In[10]:


#helper function for create the appropriate df by the factoryMethod
def get_df_by_factoryMethod(factoryMethod):
    df_list =[]

    for movieId in new_movies['id'].tolist():
        df = factoryMethod(str(movieId))
        df_list.append(df)

    return pd.concat(df_list)
     


# In[11]:


md=      get_df_by_factoryMethod(movies_factory)
credits= get_df_by_factoryMethod(credits_factory)
keywords=get_df_by_factoryMethod(keywords_factory)


# In[12]:


####################################### MOVIE ETL##############################################################
md['genres'] = md['genres'].fillna('[]').apply(str).apply(literal_eval).apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
md['year'] = pd.to_datetime(md['release_date'], errors='coerce').apply(lambda x: str(x).split('-')[0] if x != np.nan else np.nan)
md = md.drop(['adult','belongs_to_collection','budget','homepage','original_language','original_title','revenue','runtime','spoken_languages','poster_path','production_companies','release_date','production_countries','video','overview','tagline','popularity','belongs_to_collection.id','belongs_to_collection.name','belongs_to_collection.poster_path','belongs_to_collection.backdrop_path'], axis = 1)


# In[13]:


def weighted_rating(x):
    v = x['vote_count']
    R = x['vote_average']
    return (v/(v+m) * R) + (m/(m+v) * C)

vote_counts = md[md['vote_count'].notnull()]['vote_count'].astype('int')
vote_averages = md[md['vote_average'].notnull()]['vote_average'].astype('int')
C = vote_averages.mean()
m = vote_counts.quantile(0.6)

md['vote_count'] = md['vote_count'].astype('int')
md['vote_average'] = md['vote_average'].astype('int')

md['wr'] = md.apply(weighted_rating, axis=1)


# In[14]:


def get_movie_genres(movieId):
    movie = md[md['id']==movieId]
    tempgenres = [','.join(map(str, l)) for l in movie['genres']]
    df = pd.DataFrame([b for a in [i.split(',') for i in tempgenres] for b in a], columns=['genres'])
    df.insert(loc=0, column='movieId', value=movieId)
    return df


# In[15]:


output = StringIO()
csv_writer = writer(output)
csv_writer.writerow(['movieId','genres'])

for x in md['id'].tolist():
    for row in get_movie_genres(x).iterrows():
        csv_writer.writerow(row[1])

output.seek(0) # we need to get back to the start of the BytesIO
movies_genres = pd.read_csv(output)
output.flush()
output.close()


# In[16]:


keywords['id'] = keywords['id'].astype('int')
credits['id'] = credits['id'].astype('int')
md['id'] = md['id'].astype('int')


# In[17]:


md = md.merge(credits, on='id')
md = md.merge(keywords, on='id')


# In[18]:


md['cast'] = md['cast'].apply(str).apply(literal_eval)
md['crew'] = md['crew'].apply(str).apply(literal_eval)
md['keywords'] = md['keywords'].apply(str).apply(literal_eval)
md['cast_size'] = md['cast'].apply(lambda x: len(x))
md['crew_size'] = md['crew'].apply(lambda x: len(x))


# In[19]:


def get_director(x):
    for i in x:
        if i['job'] == 'Director':
            return i['name']
    return np.nan


# In[20]:


md['director'] = md['crew'].apply(get_director)


# In[21]:


md['cast'] = md['cast'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
md['cast'] = md['cast'].apply(lambda x: x[:3] if len(x) >=3 else x)
md['keywords'] = md['keywords'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])


# In[22]:


md['cast'] = md['cast'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])
md['director'] = md['director'].astype('str').apply(lambda x: str.lower(x.replace(" ", "")))
md['director'] = md['director'].apply(lambda x: [x,x, x])


# In[23]:


s = md.apply(lambda x: pd.Series(x['keywords']),axis=1).stack().reset_index(level=1, drop=True)
s.name = 'keyword'

s = s.value_counts()
s = s[s > 1]


# In[24]:


def filter_keywords(x):
    words = []
    for i in x:
        if i in s:
            words.append(i)
    return words


# In[25]:


md['keywords'] = md['keywords'].apply(filter_keywords)
md['keywords'] = md['keywords'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])


# In[26]:


# new movies after the ETL flow
md['soup'] = md ['keywords']+md['cast'] + md['director'] + md['genres']
md['soup'] = md['soup'].apply(lambda x: ' '.join(x))

md = md.drop(['genres','vote_average','vote_count','cast','crew','keywords','cast_size','crew_size','director','backdrop_path'], axis = 1)


# In[27]:


md.head()


# In[28]:


def execute_query(statement):
    tx = graph.begin(autocommit=True)
    tx.evaluate(statement)


# In[29]:


# complete the existing database with the new movies
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


tf = CountVectorizer(analyzer='word',ngram_range=(1, 2),min_df=0, stop_words='english')
count_matrix = tf.fit_transform(md['soup'])
cosine_sim = cosine_similarity(count_matrix, count_matrix)


# In[33]:


md.set_index(md['id'],inplace=True)
cols = md.index.values
inx = md.index
movies_sim = pd.DataFrame(cosine_sim, columns=cols, index=inx)


# In[34]:


def get_similar(movieId):
    df = movies_sim.loc[movies_sim.index == movieId].reset_index().             melt(id_vars='id', var_name='sim_moveId', value_name='relevance').             sort_values('relevance', axis=0, ascending=False)[1:6]
    return df


# In[35]:


output = StringIO()
csv_writer = writer(output)

csv_writer.writerow(['id','sim_movieId','relevance'])
for x in movies_sim.index.tolist():
    for row in get_similar(x).iterrows():
        csv_writer.writerow(row[1])

output.seek(0) # we need to get back to the start of the BytesIO
movies_similarity = pd.read_csv(output)
output.flush()
output.close()


# In[36]:


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


# In[ ]:




