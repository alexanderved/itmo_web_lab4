FROM gradle:jdk17-alpine AS opi-lab3-builder

ENV APP=/opi-lab3-backend
WORKDIR $APP

COPY ./src src/
COPY ./.git .git/
COPY ./build.gradle .
COPY ./settings.gradle .

RUN gradle build --no-daemon

FROM opi-lab3-builder AS opi-lab3-team

RUN gradle team

FROM opi-lab3-team AS opi-lab3-tests

RUN gradle test --no-daemon

FROM quay.io/wildfly/wildfly:latest-jdk17

COPY --from=opi-lab3-tests /opi-lab3-backend/build/libs/web-lab4.war /opt/jboss/wildfly/standalone/deployments/
COPY --from=opi-lab3-tests /opi-lab3-backend/build/revisions/previous_revisions.zip /opt/jboss

USER root
RUN microdnf install -y findutils
RUN microdnf install -y net-tools
USER jboss

RUN $JBOSS_HOME/bin/jboss-cli.sh --commands="embed-server --server-config=standalone.xml,/subsystem=undertow/application-security-domain=other:write-attribute(name=integrated-jaspi,value=false),stop-embedded-server"
RUN $JBOSS_HOME/bin/add-user.sh -u admin -p admin --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]