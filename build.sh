source ./env/env.sh
DOCKER_BUILDKIT=1 docker image build -t "$BIC_IMG_PROJ" .
