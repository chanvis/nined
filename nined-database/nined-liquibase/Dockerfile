### BUILDER IMAGE
FROM webdevops/liquibase:mysql

# Set liquibase home folder
ENV LB_HOME /liquibase

# create folder to save jdbc files
RUN mkdir $LB_HOME/jdbc
# create folder to save changelog files
RUN mkdir $LB_HOME/changelog

# Add the content of jdbc/ and changelog/ directory to image
COPY ./jdbc/mysql-connector-java-8.0.15.jar $LB_HOME/jdbc/
COPY ./changelog/ $LB_HOME/changelog/