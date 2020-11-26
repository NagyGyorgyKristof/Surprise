from io import BytesIO, StringIO
from csv import writer
import pandas as pd
from ast import literal_eval
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity


# Use IMDB recommendation for calculate the weighted average vote.
def weighted_rating(x,m,C):
    v = x['vote_count']
    R = x['vote_average']
    return (v/(v+m) * R) + (m/(m+v) * C)

def set_vote_data(md):
    vote_counts = md[md['vote_count'].notnull()]['vote_count'].astype('int')
    vote_averages = md[md['vote_average'].notnull()]['vote_average'].astype('int')
    C = vote_averages.mean()
    m = vote_counts.quantile(0.6)

    md['vote_count'] = md['vote_count'].astype('int')
    md['vote_average'] = md['vote_average'].astype('int')

    md['wr'] = md.apply((lambda x: weighted_rating(x,m,C)), axis=1)
    print('set_vote_data finished')



# In[12]:


def get_movie_genres(movieId,md):
    movie = md[md['id']==movieId]
    tempgenres = [','.join(map(str, l)) for l in movie['genres']]
    df = pd.DataFrame([b for a in [i.split(',') for i in tempgenres] for b in a], columns=['genres'])
    df.insert(loc=0, column='movieId', value=movieId)
    return df


# In[13]:

#Append rows with csv_writer duet to performance issues
def get_movie_genres_df(md):
    output = StringIO()
    csv_writer = writer(output)
    csv_writer.writerow(['movieId','genres'])

    for x in md['id'].tolist():
        for row in get_movie_genres(x,md).iterrows():
            csv_writer.writerow(row[1])

    # we need to get back to the start of the BytesIO
    output.seek(0)
    movies_genres = pd.read_csv(output)
    output.flush()
    output.close()
    print('get_movie_genres_df finished')
    return movies_genres

def filter_keywords(x,s):
    words = []
    for i in x:
        if i in s:
            words.append(i)
    return words

    # In[19]:


def get_director(x):
    for i in x:
        if i['job'] == 'Director':
            return i['name']
    return np.nan

def create_md_soup(md):
    md['cast'] = md['cast'].apply(str).apply(literal_eval)
    md['crew'] = md['crew'].apply(str).apply(literal_eval)
    md['keywords'] = md['keywords'].apply(str).apply(literal_eval)
    md['cast_size'] = md['cast'].apply(lambda x: len(x))
    md['crew_size'] = md['crew'].apply(lambda x: len(x))
    md['director'] = md['crew'].apply(get_director)
    md['cast'] = md['cast'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
    md['cast'] = md['cast'].apply(lambda x: x[:3] if len(x) >=3 else x)
    md['keywords'] = md['keywords'].apply(lambda x: [i['name'] for i in x] if isinstance(x, list) else [])
    md['cast'] = md['cast'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])
    md['director'] = md['director'].astype('str').apply(lambda x: str.lower(x.replace(" ", "")))
    md['director'] = md['director'].apply(lambda x: [x,x, x])
    s = md.apply(lambda x: pd.Series(x['keywords']),axis=1).stack().reset_index(level=1, drop=True)
    s.name = 'keyword'
    s = s.value_counts()
    s = s[s > 1]
    md['keywords'] = md['keywords'].apply((lambda x: filter_keywords(x,s)))
    md['keywords'] = md['keywords'].apply(lambda x: [str.lower(i.replace(" ", "")) for i in x])
    md['soup'] = md ['keywords']+md['cast'] + md['director'] + md['genres']
    md['soup'] = md['soup'].apply(lambda x: ' '.join(x))
    return md['soup']

def get_similar(movieId,movies_sim):
    df = movies_sim.loc[movies_sim.index == movieId].reset_index().melt(id_vars='id', var_name='sim_moveId', value_name='relevance').sort_values('relevance', axis=0, ascending=False)[1:6]
    return df

def create_movies_similarity(md):
    tf = CountVectorizer(analyzer='word',ngram_range=(1, 2),min_df=0, stop_words='english')
    count_matrix = tf.fit_transform(md['soup'])
    cosine_sim = cosine_similarity(count_matrix, count_matrix)
    md.set_index(md['id'],inplace=True)
    cols = md.index.values
    inx = md.index
    movies_sim = pd.DataFrame(cosine_sim, columns=cols, index=inx)

    output = StringIO()
    csv_writer = writer(output)

    csv_writer.writerow(['id','sim_movieId','relevance'])
    for x in movies_sim.index.tolist():
        for row in get_similar(x,movies_sim).iterrows():
            csv_writer.writerow(row[1])

    output.seek(0) # we need to get back to the start of the BytesIO
    movies_similarity = pd.read_csv(output)
    output.flush()
    output.close()
    return movies_similarity
