 # renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.5.4

# Application image

FROM hmctspublic.azurecr.io/base/java:17-distroless

# Change to non-root privilege
USER hmcts

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/ia-case-access-api.jar /opt/app/

EXPOSE 8089
CMD [ "ia-case-access-api.jar" ]
