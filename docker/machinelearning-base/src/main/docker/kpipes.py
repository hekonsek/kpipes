import os

def ensureDirectoryExists(path):
    if not os.path.exists(path):
        os.makedirs(path)
    else:
        print 'Directory ' + path + ' exists. Reusing it.'

def isText(input):
    return 'text' in input
