./scripts/mvn-install.sh
./scripts/build-images.sh

cd docker/services

docker-compose \
#    --env-file .env.init-test-data \
    -f docker-compose.yml \
    -f docker-compose.init.yml \
    up -d

cd ..