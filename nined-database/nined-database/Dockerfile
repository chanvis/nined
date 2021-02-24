### BUILDER IMAGE
FROM mysql:8.0.16

# Add the content of the sql-scripts/ directory to image
# All scripts in docker-entrypoint-initdb.d/ are automatically executed during container startup
COPY ./sql-scripts/ /docker-entrypoint-initdb.d/

ENTRYPOINT [ "sh", "-c", "/usr/local/bin/docker-entrypoint.sh --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --lower-case-table-names=1" ]