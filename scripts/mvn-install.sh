#
# Recompile and install modules to mvn local
#

source "$(dirname "$0")"/functions.sh

mvnw clean install -DskipTests
