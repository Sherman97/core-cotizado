FROM node:20-alpine

WORKDIR /app

COPY package.json angular.json tsconfig.json tsconfig.app.json /app/
COPY src /app/src

RUN npm install

CMD ["npm", "start"]
