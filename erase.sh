source ./env.sh

docker rm $BIC_CONT_PROJ
docker rmi $BIC_IMG_PROJ
docker rmi $BIC_IMG_PROJ_BASE

