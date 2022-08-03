ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM elasticsearch:${ELASTIC_VERSION}

USER root

COPY . /

RUN set -eux; \
	mkdir /state; \
	chown elasticsearch /state; \
	chmod +x /entrypoint.sh

USER elasticsearch:root

ENTRYPOINT ["/entrypoint.sh"]
