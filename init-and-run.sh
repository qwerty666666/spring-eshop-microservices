./scripts/mvn-install.sh
./scripts/build-images.sh

# run elk

docker-compose \
    -f docker/elk/docker-compose.yml \
    up -d

# run services

docker-compose \
    -f docker/services/docker-compose.yml \
    -f docker/services/docker-compose.init.yml \
    up -d

# run monitoring

docker-compose
    --env-file docker/monitoring/.env.prod
    -f docker/monitoring/docker-compose.yml \
    up -d