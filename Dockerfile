# Use an official Python runtime as a parent image
From tomcat:8-jre8

#Directory to hold chemaxon license file
RUN mkdir /usr/local/tomcat/chemaxon

#Copy the Chemaxon License file. YOU WILL NEED TO USE YOUR OWN LICENSE FILE.
ADD license.cxl /usr/local/tomcat/chemaxon/

# SET ENV variable for chemaxon license file
ENV CHEMAXON_LICENSE_URL /usr/local/tomcat/chemaxon/license.cxl

#MKDIR For WEBMOLCS APP
RUN mkdir /usr/local/tomcat/webapps/webMolCS

CMD ["catalina.sh", "run"]
