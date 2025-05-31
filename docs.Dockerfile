FROM nginx:alpine
RUN mkdir -p /usr/share/nginx/html/telegram-tdlib
COPY docs/* /usr/share/nginx/html/telegram-tdlib/