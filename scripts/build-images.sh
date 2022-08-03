#
# Builds Docker images for microservices
#

set -euo pipefail


source "$(dirname "$0")"/functions.sh


function build_image_with_buildpacks() {
  path=$1
  mvnw spring-boot:build-image -f "$path" -DskipTests
}


function build_image_layered() {
  path=$1
  name=$2
  app_path="$(app_root_path)"

  echo "start build image for $path"

  # rebuild
  echo "rebuild..."
  mvnw clean package -f "$path" -DskipTests

  cd "$path"/target || exit 1

  # find jar file
  jar=$(find . -maxdepth 1 -type f -name "*.jar")

  # unpack jar
  echo "unpacking jar..."
  java -jar -Djarmode=layertools "$jar" extract

  # build image
  echo "building image..."
  docker build -f "$app_path"/docker/services/Dockerfile \
      -t "${name}":latest .

  cd - || exit 1
}


app_path=$(app_root_path)

build_image_layered "$app_path"/api-gateway app-eshop/api-gateway
build_image_layered "$app_path"/catalog-service/catalog-server app-eshop/catalog-server
build_image_layered "$app_path"/checkout-service/checkout-server app-eshop/checkout-server
build_image_layered "$app_path"/config-server app-eshop/config-server
build_image_layered "$app_path"/eureka-server app-eshop/eureka-server
build_image_layered "$app_path"/message-relay-service app-eshop/message-relay-service
build_image_layered "$app_path"/order-management-service/order-management-server app-eshop/order-management-server
build_image_layered "$app_path"/shopping-cart-service/shopping-cart-server app-eshop/shopping-cart-server
build_image_layered "$app_path"/warehouse-service/warehouse-server app-eshop/warehouse-server

#build_image_with_buildpacks "$app_path"/api-gateway
#build_image_with_buildpacks "$app_path"/catalog-service/catalog-server
#build_image_with_buildpacks "$app_path"/checkout-service/checkout-server
#build_image_with_buildpacks "$app_path"/config-server
#build_image_with_buildpacks "$app_path"/eureka-server
#build_image_with_buildpacks "$app_path"/message-relay-service
#build_image_with_buildpacks "$app_path"/order-management-service/order-management-server
#build_image_with_buildpacks "$app_path"/shopping-cart-service/shopping-cart-server
#build_image_with_buildpacks "$app_path"/warehouse-service/warehouse-server
