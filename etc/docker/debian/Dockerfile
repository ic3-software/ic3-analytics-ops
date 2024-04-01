FROM azul/zulu-openjdk-debian:21.0.2-21.32

#
# Configure the "en_US.UTF-8" locale.
#
RUN set -eux; \
	if [ -f /etc/dpkg/dpkg.cfg.d/docker ]; then \
# if this file exists, we're likely in "debian:xxx-slim", and locales are thus being excluded
# so we need to remove that exclusion (since we need locales)
		grep -q '/usr/share/locale' /etc/dpkg/dpkg.cfg.d/docker; \
		sed -ri '/\/usr\/share\/locale/d' /etc/dpkg/dpkg.cfg.d/docker; \
		! grep -q '/usr/share/locale' /etc/dpkg/dpkg.cfg.d/docker; \
	fi \
	&& apt-get update  \
    && apt-get install -y --no-install-recommends locales \
	&& localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
	&& rm -rf /var/lib/apt/lists/*

ENV LANG en_US.utf8

#
# Several tools.
#
RUN set -eux \
	&& apt-get update  \
    && apt-get install -y --no-install-recommends procps curl unzip vim \
	&& rm -rf /var/lib/apt/lists/* \
