FROM ubuntu:latest

EXPOSE 8080

COPY build/native/nativeCompile/letterboxd-api /app/letterboxd-api
RUN chmod +x /app/letterboxd-api
ENTRYPOINT ["/app/letterboxd-api"]