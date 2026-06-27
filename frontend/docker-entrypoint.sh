#!/bin/sh
set -e

# 使用 BACKEND_URL 环境变量生成 nginx 配置，默认指向本地 docker-compose 服务名
BACKEND_URL=${BACKEND_URL:-http://backend:8080}
envsubst '${BACKEND_URL}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
