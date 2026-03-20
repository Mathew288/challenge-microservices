# --- Build stage ---
FROM node:20-alpine AS build

WORKDIR /app

# Instalar deps
COPY package*.json ./
RUN npm ci

# Copiar fuente y compilar
COPY . .
RUN npm run build

# --- Runtime stage (nginx) ---
FROM nginx:1.27-alpine

# SPA + proxy al api-gateway
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar build Angular
# Angular (v17+) deja el output en /app/dist/<project-name>/browser
# Copiamos el contenido del browser al root que sirve nginx.
COPY --from=build /app/dist/*/browser/ /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
