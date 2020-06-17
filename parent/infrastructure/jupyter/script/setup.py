#!/usr/bin/env python
# coding: utf-8

# In[1]:


import pandas as pd
import numpy as np
from py2neo import Graph
import os


import datetime
import json
from io import BytesIO, StringIO
from csv import writer 
from ast import literal_eval
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity


# In[2]:



graph = Graph("bolt://host.docker.internal:7687", auth=("neo4j", "admin"),bolt=False)

print('started')


# In[3]:


movies_url="http://167.71.3.40/movies_metadata.csv"
md =pd.read_csv(movies_url)

md = md.loc[md['id'].str.isalnum()]
md['id']=md['id'].astype('int')


# In[4]:


link_small_url="http://167.71.3.40/links_small.csv"
link_small= pd.read_csv(link_small_url)
links_small = link_small[link_small['tmdbId'].notnull()]['tmdbId'].astype('int')


# In[5]:


ratings_url="http://167.71.3.40/ratings.csv"
ratings= pd.read_csv(ratings_url)


# In[6]:


# talan torolni kell ezt!!!
md = md[md['id'].isin(links_small)]


# In[7]:


users_df = pd.DataFrame(ratings['userId'].unique(), columns=['userId'])


# In[8]:


genres = [
    "Action",
    "Adventure",
    "Animation",
    "Children",
    "Comedy",
    "Crime",
    "Documentary",
    "Drama",
    "Fantasy",
    "Film-Noir",
    "Horror",
    "Musical",
    "Mystery",
    "Romance",
    "Sci-Fi",
    "Thriller",
    "War",
    "Western",
    "(no genres listed)"]
genres_df = pd.DataFrame(genres, columns=['genres'])


# In[9]:


users_movies_df = ratings.drop('timestamp', axis = 1)
users_movies_df['rating']=users_movies_df['rating']*2


# In[10]:


md['genres'] = md['genres'].fillna('[]').apply(literal_eval).apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
md['year'] = pd.to_datetime(md['release_date'], errors='coerce').apply(lambda x: str(x).split('-')[0] if x != np.nan else np.nan)
md = md.drop(['adult','belongs_to_collection','budget','homepage','original_language','original_title','revenue','runtime','spoken_languages','poster_path','production_companies','release_date','production_countries','video','overview','tagline','popularity'], axis = 1)


# In[11]:


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


# In[12]:


def get_movie_genres(movieId):
    movie = md[md['id']==movieId]
    tempgenres = [','.join(map(str, l)) for l in movie['genres']]
    df = pd.DataFrame([b for a in [i.split(',') for i in tempgenres] for b in a], columns=['genres'])
    df.insert(loc=0, column='movieId', value=movieId)
    return df


# In[13]:


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


# In[14]:


################################   Keywords, Credit ###########################################

keywords_url="http://167.71.3.40/keywords.csv"
keywords= pd.read_csv(keywords_url)


credits_url="http://167.71.3.40/credits.csv"
credits= pd.read_csv(credits_url)

print('ketwords, credits')


# In[15]:



keywords['id'] = keywords['id'].astype('int')
credits['id'] = credits['id'].astype('int')
md['id'] = md['id'].astype('int')


# In[16]:


md = md.merge(credits, on='id')
md = md.merge(keywords, on='id')


# In[17]:


md['cast'] = md['cast'].apply(literal_eval)
md['crew'] = md['crew'].apply(literal_eval)
md['keywords'] = md['keywords'].apply(literal_eval)
md['cast_size'] = md['cast'].apply(lambda x: len(x))
md['crew_size'] = md['crew'].apply(lambda x: len(x))


# In[18]:


def get_director(x):
    for i in x:
        if i['job'] == 'Director':
            return i['name']
    return np.nan


# In[19]:


md['director'] = md['crew'].apply(get_director)


# In[20]:


md['cast'] = md['cast'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
md['cast'] = md['cast'].apply(lambda x: x[:3] if len(x) >=3 else x)
md['keywords'] = md['keywords'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])


# In[21]:


md['cast'] = md['cast'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])
md['director'] = md['director'].astype('str').apply(lambda x: str.lower(x.replace(" ", "")))
md['director'] = md['director'].apply(lambda x: [x,x, x])


# In[22]:


s = md.apply(lambda x: pd.Series(x['keywords']),axis=1).stack().reset_index(level=1, drop=True)
s.name = 'keyword'

s = s.value_counts()
s = s[s > 1]


# In[23]:


def filter_keywords(x):
    words = []
    for i in x:
        if i in s:
            words.append(i)
    return words


# In[24]:


md['keywords'] = md['keywords'].apply(filter_keywords)
md['keywords'] = md['keywords'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])


# In[25]:


md['soup'] = md ['keywords']+md['cast'] + md['director'] + md['genres']
md['soup'] = md['soup'].apply(lambda x: ' '.join(x))


# In[26]:


tf = CountVectorizer(analyzer='word',ngram_range=(1, 2),min_df=0, stop_words='english')
count_matrix = tf.fit_transform(md['soup'])
cosine_sim = cosine_similarity(count_matrix, count_matrix)


# In[27]:


md.set_index(md['id'],inplace=True)
cols = md.index.values
inx = md.index
movies_sim = pd.DataFrame(cosine_sim, columns=cols, index=inx)
movies_sim.head()


# In[28]:


def get_similar(movieId):
    df = movies_sim.loc[movies_sim.index == movieId].reset_index().             melt(id_vars='id', var_name='sim_moveId', value_name='relevance').             sort_values('relevance', axis=0, ascending=False)[1:6]
    return df


# In[29]:


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


# In[30]:


md = md.drop(['genres','vote_average','vote_count','cast','crew','keywords','cast_size','crew_size','director'], axis = 1)

md.head()


# In[31]:


#/////////////////////////////////////////NEO4J IMPORT////////////////////////////////////////////////////////////////

def execute_query(statement):
    tx = graph.begin(autocommit=True)
    tx.evaluate(statement)



# In[32]:


genres_df.to_csv('/import/genres.csv', sep='|', header=True, index=False)

genres_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///genres.csv" AS row
FIELDTERMINATOR '|'
MERGE (:Genres {genres: row.genres});
"""

execute_query(genres_import_statement)


# In[33]:


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


# In[34]:


users_df.to_csv('/import/users.csv', sep='|', header=True, index=False)

user_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///users.csv" AS row
FIELDTERMINATOR '|'
MERGE (:Users {userId: row.userId});
"""

execute_query(user_import_statement)


# In[35]:


movie_index_statement = """
CREATE INDEX FOR (n:Movies) ON (n.movieId);
"""

execute_query(movie_index_statement)


# In[ ]:


user_index_statement = """
CREATE INDEX FOR (n:Users) ON (n.userId);
"""

execute_query(user_index_statement)


# In[36]:


users_movies_df.to_csv('/import/users_movies.csv', sep='|', header=True, index=False)

user_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///users_movies.csv" AS row
FIELDTERMINATOR '|'
MATCH (user:Users {userId: row.userId})
MATCH (movie:Movies {movieId: row.movieId})
MERGE (user)-[:WATCHED {rating: row.rating}]->(movie);
"""

execute_query(user_import_statement)


# In[37]:


users_movies_df.to_csv('/import/users_movies.csv', sep='|', header=True, index=False)

user_movies_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///users_movies.csv" AS row
FIELDTERMINATOR '|'
MATCH (user:Users {userId: row.userId})
MATCH (movie:Movies {movieId: row.movieId})
MERGE (user)-[:WATCHED {rating: row.rating}]->(movie);
"""

execute_query(user_movies_import_statement)


# In[38]:


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


# In[39]:


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


# In[40]:


users_similarity_statement = """
//User similarity relationship
MATCH (u1:Users)-[r:WATCHED]->(m:Movies)
WITH u1, avg(toFloat(r.rating)) AS u1_mean

MATCH (u1)-[r1:WATCHED]->(m:Movies)<-[r2:WATCHED]-(u2)
WITH u1, u1_mean, u2, COLLECT({r1: r1, r2: r2}) AS ratings WHERE size(ratings) > 10

MATCH (u2)-[r:WATCHED]->(m:Movies)
WITH u1, u1_mean, u2, avg(toFloat(r.rating)) AS u2_mean, ratings

UNWIND ratings AS r

WITH sum( (toFloat(r.r1.rating)-u1_mean) * (toFloat(r.r2.rating)-u2_mean) ) AS nom,
     sqrt( sum( (toFloat(r.r1.rating) - u1_mean)^2) * sum( (toFloat(r.r2.rating) - u2_mean) ^2)) AS denom,
     u1, u2 WHERE denom <> 0
WITH u1,u2,nom/denom as sim
WHERE sim > 0.4
MERGE (u1)-[s:USER_SIMILAR]-(u2)
SET   s.similarity = sim
"""

execute_query(users_similarity_statement)


# In[41]:


user_favourite_genre_statement = """
//FAVOURITE_GENRE relationship
MATCH (u:Users)-[r:WATCHED]->(m:Movies)-[g:IN_GENRE]->(ge:Genres)
WITH u,COUNT(g) as GenreCount, ge
ORDER BY GenreCount DESC
WITH u,COLLECT({count: GenreCount, genre:ge }) as genreCountList
UNWIND genreCountList as genreTuple
WITH u,COLLECT(genreTuple.genre.genres)[0..3] as bestThreeGenre
MATCH (g:Genres)
WHERE g.genres IN bestThreeGenre
MERGE (u)-[:FAVOURITE_GENRE]->(g)
"""

execute_query(user_favourite_genre_statement)


# In[ ]:





# In[ ]:




