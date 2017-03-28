### KPipes imports

import sys
sys.path.append('/kpipes')
from kpipes import ensureDirectoryExists
from kpipes import isText

### Imports

import os
import glob
import json
from sklearn.externals import joblib

### Data input

baseDataDir = os.getenv('EVENT_DIR', '/event')
inputEvent = json.loads(open(baseDataDir + '/request.json', 'r').read())
isTextData = isText(inputEvent)
inputData = ''
if(isTextData):
    inputData = inputEvent['text']
else:
    inputData = inputEvent['vector']

### Model input

modelDirBase = os.getenv('DATA_DIR', '/model')
modelId = os.getenv('MODEL_ID')
modelDir = modelDirBase + "/" + modelId
model = joblib.load(modelDir + '/model.pkl')

docs_new = [inputData]

newDocsNormalized = docs_new
if(isTextData):
    from sklearn.feature_extraction.text import CountVectorizer
    countVectorizer = CountVectorizer()
    countVectorizer.vocabulary_ = joblib.load(modelDir + '/vocabulary.pkl')

    from sklearn.feature_extraction.text import TfidfTransformer
    tfidf_transformer = TfidfTransformer()
    tfidf_transformer._idf_diag = joblib.load(modelDir + '/tfidf.pkl')

    X_new_counts = countVectorizer.transform(docs_new)
    newDocsNormalized = tfidf_transformer.transform(X_new_counts)

predicted = model.predict(newDocsNormalized)
print docs_new
response = {}
response['response'] = predicted[0]
open(baseDataDir + '/response.json', 'w').write(json.dumps(response))