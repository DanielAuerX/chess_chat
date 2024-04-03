#!/bin/bash
host="dauer23"
image_name="chess-chat"
tag="latest"
image_full_name="$host/$image_name:$tag"

if !(docker images --format '{{.Repository}}:{{.Tag}}' | grep -q "$image_full_name"); then
  docker run -d --name $image_name -p 8080:8080 $image_full_name
  exit 1
fi

#get local docker image date
currentDate=$(docker inspect --format='{{json .Created}}' $image_full_name | awk -F'[.+]' '{print substr($1, 2, 17) "59.9999Z"}')
    echo "Local Docker image (date of creation): $currentDate"

response=$(curl -m 10 -s -X GET "https://hub.docker.com/v2/repositories/$host/$image_name/tags/$tag")

if [ $? -ne 0 ]; then
    echo "Error: Failed to retrieve data from Docker Hub."
    exit 1
fi

if echo "$response" | grep -q '"last_updated"'; then
    remoteImageTimestamp=$(echo "$response" | grep -oP '"last_updated":"\K[^",]+')
    echo "Remote Docker image (last update): $remoteImageTimestamp"
else
    echo "Error: Unable to retrieve image information from Docker Hub."
    exit 1
fi

./TimestampComparision "$currentDate" "$remoteImageTimestamp"

if [ $? -eq 100 ]; then
    echo "New version detected! Taking action..."
    if docker ps --format '{{.Names}}' | grep -q "$image_name"; then
        echo "Container $image_name is running..."
        echo "Stopping..."
        docker stop $image_name
        sleep 2
        docker rm $image_name
        echo "Starting..."
        docker run -d --name $image_name -p 8080:8080 $image_full_name
    elif docker ps -a --format '{{.Names}}' | grep -q "$image_name"; then
        echo "Container $image_name exists, but not running..."
        docker rm $image_name
        echo "Starting..."
        docker run -d --name $image_name -p 8080:8080 $image_full_name
    else
        echo "Container $image_name is not running. Starting it now..."
        docker run -d --name $image_name -p 8080:8080 $image_full_name
    fi
    echo "Thank you for using the script. Bye."
else
    echo "The local image is up to date. Stay frosty <3."
fi