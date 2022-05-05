./scripts/mvn-install.sh
./scripts/build-images.sh

docker-compose \
#    --env-file .env.init-test-data \
    -f docker/elk/docker-compose.yml \
    -f docker/services/docker-compose.yml \
    -f docker/services/docker-compose.init.yml \
    up -d