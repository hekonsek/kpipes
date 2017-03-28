from shutil import copyfile

import tempfile
import glob

import os
pwd = os.getcwd()

### Should train model for numeric data
modelBase = tempfile.mkdtemp()
from subprocess import call
resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", modelBase + ":/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=numeric", "kpipes/machinelearning-train"])
assert resultCode == 0

eventDir = tempfile.mkdtemp()
copyfile('src/test/data/numeric-predict/vector.json', eventDir + '/request.json')
resultCode = call(["docker", "run", "-v", pwd + "/src/test/text:/data", "-v", modelBase + ":/model", "-v", eventDir + ":/event", "-e", "MODEL_ID=mymodel", "kpipes/machinelearning-predict"])
assert resultCode == 0
import json
assert json.loads(open(eventDir + '/response.json').read())['response'] == 'big'

### Should train model for text data
modelBase = tempfile.mkdtemp()
from subprocess import call
resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", modelBase + ":/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=text", "kpipes/machinelearning-train"])
assert resultCode == 0

eventDir = tempfile.mkdtemp()
copyfile('src/test/data/text-predict/text.json', eventDir + '/request.json')
resultCode = call(["docker", "run", "-v", pwd + "/src/test/text:/data", "-v", modelBase + ":/model", "-v", eventDir + ":/event", "-e", "MODEL_ID=mymodel", "kpipes/machinelearning-predict"])
assert resultCode == 0
assert json.loads(open(eventDir + '/response.json').read())['response'] == 'politics'
