import os
pwd = os.getcwd()

from subprocess import call
resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", "/tmp/kpipesmlstate:/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=numeric", "kpipes/machinelearning-train"])
assert resultCode == 0

resultCode = call(["docker", "run", "-v", pwd + "/src/test/data:/data", "-v", "/tmp/kpipesmlmodel:/model", "-e", "MODEL_ID=mymodel", "-e", "DATA_ID=text", "kpipes/machinelearning-train"])
assert resultCode == 0