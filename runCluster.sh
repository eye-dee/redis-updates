export REDIS_CLUSTER_IP=0.0.0.0

docker run  -e "IP=0.0.0.0" -p "7000:7000"\
 -p "7001:7001" \
 -p "7002:7002" \
 -p "7003:7003" \
 -p "7004:7004" \
 -p "7005:7005" \
 -p "7006:7006" \
 -p "7007:7007" \
 -p "7008:7008" \
 -p "7009:7009" \
 -e MASTERS=5 grokzen/redis-cluster:latest
