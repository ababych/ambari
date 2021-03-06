#!/usr/bin/env python2.6

'''
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
'''
import json
import os.path
import logging
import subprocess
from manifestGenerator import generateManifest
from RepoInstaller import RepoInstaller
import pprint
from Grep import Grep

logger = logging.getLogger()

class puppetExecutor:

  """ Class that executes the commands that come from the server using puppet.
  This is the class that provides the pluggable point for executing the puppet"""

  # How many lines from command output send to server
  OUTPUT_LAST_LINES = 10
  # How many lines from command error output send to server (before Err phrase)
  ERROR_LAST_LINES_BEFORE = 10
  # How many lines from command error output send to server (after Err phrase)
  ERROR_LAST_LINES_AFTER = 30

  NO_ERROR = "none"

  def __init__(self, puppetModule, puppetInstall, facterInstall, tmpDir):
    self.puppetModule = puppetModule
    self.puppetInstall = puppetInstall
    self.facterInstall = facterInstall
    self.tmpDir = tmpDir

  def getPuppetBinary(self):
    return os.path.join(self.puppetInstall, "bin", "puppet") 
     
  def puppetCommand(self, sitepp):
    modules = self.puppetModule
    puppetcommand = [];
    puppetcommand.append(self.getPuppetBinary())
    puppetcommand.append("apply")
    puppetcommand.append("--confdir=" + modules)
    puppetcommand.append("--detailed-exitcodes")
    puppetcommand.append(sitepp)
    return puppetcommand
  
  def facterLib(self):
    return self.facterInstall + "/lib/"
    pass
  
  def puppetLib(self):
    return self.puppetInstall + "/lib"
    pass
      
  def runCommand(self, command):
    result = {}
    taskId = 0;
    grep = Grep()
    if command.has_key("taskId"):
      taskId = command['taskId']
      
    puppetEnv = os.environ
    #Install repos
    modulesdir = self.puppetModule + "/modules"
    repoInstaller = RepoInstaller(command, self.tmpDir, modulesdir, taskId)

    puppetFiles = repoInstaller.installRepos()
    siteppFileName = os.path.join(self.tmpDir, "site-" + str(taskId) + ".pp") 
    puppetFiles.append(siteppFileName)
    generateManifest(command, siteppFileName, modulesdir)
    #Run all puppet commands, from manifest generator and for repos installation
    #Appending outputs and errors, exitcode - maximal from all
    for puppetFile in puppetFiles:
      puppetcommand = self.puppetCommand(puppetFile)
      """ Run the command and make sure the output gets propagated"""
      rubyLib = ""
      if os.environ.has_key("RUBYLIB"):
        rubyLib = os.environ["RUBYLIB"]
        logger.info("Ruby Lib env from Env " + rubyLib)
      rubyLib = rubyLib + ":" + self.facterLib() + ":" + self.puppetLib()
      puppetEnv["RUBYLIB"] = rubyLib
      logger.info("Setting RUBYLIB as: " + rubyLib)
      logger.info("Running command " + pprint.pformat(puppetcommand))
      puppet = subprocess.Popen(puppetcommand,
                                    stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE,
                                    env=puppetEnv)
      stderr_out = puppet.communicate()
      error = "none"
      returncode = 0
      if (puppet.returncode != 0 and puppet.returncode != 2) :
        returncode = puppet.returncode
        error = stderr_out[1]
        logging.error("Error running puppet: \n" + stderr_out[1])
        pass
		
      if result.has_key("stderr"):
        result["stderr"] = result["stderr"] + os.linesep + error
      else:
        result["stderr"] = error
      puppetOutput = stderr_out[0]
      logger.info("Output from puppet :\n" + puppetOutput)
      if result.has_key("exitcode"):
        result["exitcode"] = max(returncode, result["exitcode"])
      else:
        result["exitcode"] = returncode
        

      if result.has_key("stdout"):
        result["stdout"] = result["stdout"] + os.linesep + puppetOutput
      else:
        result["stdout"] = puppetOutput

    if error == self.NO_ERROR:
      if result.has_key("stdout"):
        result["stdout"] = result["stdout"] + os.linesep + \
          str(grep.tail(puppetOutput, self.OUTPUT_LAST_LINES))
      else:
        result["stdout"] = grep.tail(puppetOutput, self.OUTPUT_LAST_LINES)
    else:
      if result.has_key("stdout"):
        result["stdout"] = result["stdout"] + os.linesep + \
        str(grep.grep(puppetOutput, "err", self.ERROR_LAST_LINES_BEFORE, 
                      self.ERROR_LAST_LINES_AFTER))
      else:
        result["stdout"] = str(grep.grep(puppetOutput, "err", 
                                         self.ERROR_LAST_LINES_BEFORE, 
                                         self.ERROR_LAST_LINES_AFTER))
	
    logger.info("ExitCode : "  + str(result["exitcode"]))
    return result
 
def main():
  logging.basicConfig(level=logging.DEBUG)    
  #test code
  jsonFile = open('test.json', 'r')
  jsonStr = jsonFile.read() 
  # Below is for testing only.
  
  puppetInstance = puppetExecutor("/home/centos/ambari_repo_info/ambari-agent/src/main/puppet/",
                                  "/usr/",
                                  "/root/workspace/puppet-install/facter-1.6.10/",
                                  "/tmp")
  jsonFile = open('test.json', 'r')
  jsonStr = jsonFile.read() 
  parsedJson = json.loads(jsonStr)
  result = puppetInstance.runCommand(parsedJson)
  logger.debug(result)
  
if __name__ == '__main__':
  main()

