#!/usr/bin/env python
# coding: utf-8

# In[1]:

from common import *
import numpy as np
from py2neo import Graph
import os
import sys
import datetime

# In[2]:

graph = Graph("bolt://host.docker.internal:7687", auth=("neo4j", "admin"),bolt=False)

print('==> Python: Startup ETL flow was started')

# In[3]:

print('==> Getting movies_metadata.csv')
movies="/code/django-etl/initdata/movies_metadata.csv"
md =pd.read_csv(movies)

md = md.loc[md['id'].str.isalnum()]
md['id']=md['id'].astype('int')
print('==> movies_metadata.csv has arrived.')



# In[4]:

print('==> Getting links_small.csv')
link_small="/code/django-etl/initdata/links_small.csv"
link_small= pd.read_csv(link_small)
links_small = link_small[link_small['tmdbId'].notnull()]['tmdbId'].astype('int')
print('links_small.csv has been arrived.')


md = md[md['id'].isin(links_small)]

# In[5]:

print('==> Getting ratings.csv')
ratings="/code/django-etl/initdata/ratings.csv"
ratings= pd.read_csv(ratings)
print('==> ratings.csv has arrived.')

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

# Use IMDB recommendation for calculate the weighted average vote.
set_vote_data(md)

# In[14]:

movies_genres =get_movie_genres_df(md)

# In[14]:


################################   Keywords, Credit ###########################################

print('==> Getting keywords.csv')
keywords="/code/django-etl/initdata/keywords.csv"
keywords= pd.read_csv(keywords)
print('==> keywords.csv has arrived')

print('==> Getting credits.csv')
credits="/code/django-etl/initdata/credits.csv"
credits= pd.read_csv(credits)
print('==> credits.csv has arrived')




# In[15]:



keywords['id'] = keywords['id'].astype('int')
credits['id'] = credits['id'].astype('int')
md['id'] = md['id'].astype('int')


# In[16]:


md = md.merge(credits, on='id')
md = md.merge(keywords, on='id')


# In[17]:


md['soup'] = create_md_soup(md)

# In[26]:

movies_similarity = create_movies_similarity(md)


md = md.drop(['genres','vote_average','vote_count','cast','crew','keywords','cast_size','crew_size','director'], axis = 1)

# In[31]:


#/////////////////////////////////////////NEO4J IMPORT////////////////////////////////////////////////////////////////

print('==> NEO4J import has been started')
def execute_query(statement):
    tx = graph.begin(autocommit=True)
    tx.evaluate(statement)



# In[32]:


genres_df.to_csv('/code/django-etl/import/genres.csv', sep='|', header=True, index=False)

genres_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///genres.csv" AS row
FIELDTERMINATOR '|'
MERGE (:Genres {genres: row.genres});
"""

execute_query(genres_import_statement)


# In[33]:


md.to_csv('/code/django-etl/import/movies.csv', sep='|', header=True, index=False)

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


users_df.to_csv('/code/django-etl/import/users.csv', sep='|', header=True, index=False)

user_import_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///users.csv" AS row
FIELDTERMINATOR '|'
MERGE (:Users {userId: row.userId});
"""

execute_query(user_import_statement)


# In[35]:

movie_index_statement = """
CREATE INDEX movieIndex IF NOT EXISTS FOR (m:Movies) ON (m.movieId)
"""
execute_query(movie_index_statement)


# In[ ]:

user_index_statement = """
CREATE INDEX userIndex IF NOT EXISTS FOR (u:Users) ON (u.userId)
"""
execute_query(user_index_statement)


# In[36]:


users_movies_df.to_csv('/code/django-etl/import/users_movies.csv', sep='|', header=True, index=False)

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


users_movies_df.to_csv('/code/django-etl/import/users_movies.csv', sep='|', header=True, index=False)

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


movies_genres.to_csv('/code/django-etl/import/movies_genres.csv', sep='|', header=True, index=False)

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


movies_similarity.to_csv('/code/django-etl/import/movies_similarity.csv', sep='|', header=True, index=False)

movies_similarity_statement = """
USING PERIODIC COMMIT
LOAD CSV WITH HEADERS FROM "file:///movies_similarity.csv" AS row
FIELDTERMINATOR '|'
MATCH (movie1:Movies {movieId: row.id})
MATCH (movie2:Movies {movieId: row.sim_movieId})
MERGE (movie1)-[:MOVIE_SIMILAR {relevance: row.relevance}]->(movie2);
"""

execute_query(movies_similarity_statement)
print('==> All import statement has been executed successfully')

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

print('==> All graph build statement has been executed successfully')
print('==> Setup ETL flow has been finished successfully')


# In[ ]:





# In[ ]:




