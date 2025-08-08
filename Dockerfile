FROM harbor-test.ebonex.io/opensource/openjdk:17-jdk

RUN useradd ebangapp && \
    mkdir -p /ebang/{app,log,tmp,data,etc}/trade-openapi && \
    ln -s /ebang/log/trade-openapi /ebang/app/trade-openapi/logs && \
    chown -R ebangapp:ebangapp /ebang
USER ebangapp
WORKDIR /ebang/app/trade-openapi
COPY trade-openapi-deploy/target/trade-openapi.jar /ebang/app/trade-openapi/trade-openapi.jar

ENV LANG=en_US.UTF-8
ENV DEFAULT_JAVA_OPTS="-server -Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true -verbosegc -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Xlog:gc*,safepoint:./logs/gc.log:time,uptime:filecount=10,filesize=100M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintFlagsFinal -XX:+AlwaysPreTouch"
ENV JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT exec java ${DEFAULT_JAVA_OPTS} ${DEFAULT_JAVA_HEAP_OPTS} ${JAVA_HEAP_OPTS} ${JAVA_OPTS} -jar /ebang/app/trade-openapi/trade-openapi.jar
