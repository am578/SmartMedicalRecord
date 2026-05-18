FROM ghcr.io/cirruslabs/android-sdk:35

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

CMD ["./gradlew", "assembleDebug"]