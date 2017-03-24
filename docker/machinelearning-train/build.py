import tempfile
import glob

import os
pwd = os.getcwd()

### Should train model for numeric data
modelBase = tempfile.mkdtemp()
from subprocess import call
resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", modelBase + ":/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=numeric", "kpipes/machinelearning-train"])
assert resultCode == 0
assert len(glob.glob(modelBase + '/mymodel/model.pkl')) == 1

resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", modelBase + ":/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=text", "kpipes/machinelearning-train"])
assert resultCode == 0