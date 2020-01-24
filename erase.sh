source ./env/env.sh

docker rm $BIC_CONT_PROJ
docker rmi $BIC_IMG_PROJ
./vol_erase.sh
