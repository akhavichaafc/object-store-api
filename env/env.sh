source ./env/env_proj.sh

export BIC_HOST='0.0.0.0'
#printenv BIC_HOST

export BIC_DIR='/usr/local/bic/services'
#printenv BIC_DIR

export BIC_DIR_PROJ=$BIC_DIR"/"$BIC_PROJ
#printenv BIC_DIR_PROJ

export BIC_CONT_PROJ="cont_"$BIC_PROJ
#printenv BIC_CONT_PROJ

export BIC_IMG_PROJ="img_"$BIC_PROJ
#printenv BIC_IMG_PROJ

export BIC_VOL_PROJ="vol_"$BIC_PROJ
printenv BIC_VOL_PROJ
