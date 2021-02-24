# NineD Database Service
This component is responsible for starting MySQL docker container and creating all the required application specific users as part of database initialization. Also this service is responsible for applying liquibase changelogs.

## Prerequisite
Install Docker latest version (docker engine required 1.13.0+).

### Important Note
In the docker compose volume mapping for MySQL database files, make sure that host:path directory is empty. 

`<host:path>:/var/lib/mysql`

This is important because, as part of database initialization (first boot) it executes init.sql, which is responsible for creating all the required database users.

`/docker-entrypoint-initdb.d/init.sql`

##First time
Run this command 
### 'docker network create ninedDatabase'

## Run
In the root directory, run below command to start database container
#### `docker-compose up -d nined-db`

Once MySQL container is up and running after a few seconds:

Execute below command to run liquibase changelogs.
#### `docker-compose up --build nined-lb`

## Liquibase Changelogs Creation Process
Liquibase changelogs are managed at release level. For release 1.0 we have all the changelogs created for all the MySQL users (service specific table owners) with the below naming convention.

`nined-<mysql-user>-changelog-<release-number>.xml`

for e.g. `nined-userapp-changelog-1.0.xml`

For next release, when new changelog(s) is(are) created, we need to add a database tagging and then include the new changelogs in master changelog file.

```
<!-- changeset for database tagging -->
<changeSet author="master" id="v1.0">
   <tagDatabase tag="v1.0"/>
</changeSet>
```

Note: [Liquibase database tagging](https://www.liquibase.org/documentation/changes/tag_database.html) is important because in case required we can use the tag to rollback the release (database changes).
