#!/bin/bash

#Specify absolute path to webmolCS folder
webmolcs="ABSOLUTE_PATH_TO_WEBMOLCS"

#Run docker container
docker run --mount type=bind,source=$webmolcs,target=/usr/local/tomcat/webapps/webMolCS -p 8080:8080 webmolcs
