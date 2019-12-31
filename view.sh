source ./env.sh

docker images "*"$BIC_PROJ"*"
printf "\n"

docker ps --filter "name="$BIC_PROJ --filter status=exited --filter status=running
printf "\n"

docker volume ls --filter "name="$BIC_PROJ
