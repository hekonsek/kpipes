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

baseDataDir = os.getenv('DATA_DIR', '/data')
dataId = os.getenv('DATA_ID')
dataDir = baseDataDir + '/' + dataId
print 'Using data set: ' + dataDir

### Model output

modelDirBase = os.getenv('DATA_DIR', '/model')
modelId = os.getenv('MODEL_ID')
modelDir = modelDirBase + "/" + modelId
ensureDirectoryExists(modelDir)

### Data type recognition

sampleFileSet = glob.glob(dataDir + '/*')
assert len(sampleFileSet) > 0, 'Dataset cannot be empty.'
sampleFile = sampleFileSet[0]
sampleJson = json.loads(open(sampleFile, 'r').read())
isTextData = isText(sampleJson)
print 'Detected text data: %s' % isTextData

### Data loading

featureVectors = []
labels = []
if(isTextData):
    texts = []
    for inputFile in glob.glob(dataDir + '/*'):
        input = json.loads(open(inputFile, "r").read())
        texts.append(input['text'])
        labels.append(input['label'])

    from sklearn.feature_extraction.text import CountVectorizer
    countVectorizer = CountVectorizer()
    wordCounts = countVectorizer.fit_transform(texts)
    vocabularyFile = modelDir +  "/vocabulary.pkl"
    print 'Writing vocabulary to: ' + vocabularyFile
    joblib.dump(countVectorizer.vocabulary_, vocabularyFile)

    from sklearn.feature_extraction.text import TfidfTransformer
    tfidf = TfidfTransformer()
    featureVectors = tfidf.fit_transform(wordCounts)
    joblib.dump(tfidf._idf_diag, modelDir +  "/tfidf.pkl")
else:
    print 'Loading raw feature vectors and labels.'
    for inputFile in glob.glob(dataDir + '/*'):
        input = json.loads(open(inputFile, "r").read())
        featureVectors.append(input['vector'])
        labels.append(input['label'])

from sklearn.naive_bayes import MultinomialNB
model = MultinomialNB().fit(featureVectors, labels)
joblib.dump(model, modelDir +  '/model.pkl')

print 'KPIPES:SUCCESS'