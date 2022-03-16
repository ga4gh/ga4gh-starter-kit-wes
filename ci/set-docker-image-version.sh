export DOCKER_IMG_VER=`cat build.gradle | grep "^version" | cut -f 2 -d ' ' | sed "s/'//g"`
