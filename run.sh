source ./env.sh
docker stop $BIC_CONT_PROJ
docker rm $BIC_CONT_PROJ
docker run -it -p $BIC_HOST:$BIC_PORT_EXT:$BIC_PORT_INT -v $PWD:$BIC_DIR_PROJ --name $BIC_CONT_PROJ $BIC_IMG_PROJ
