
pid=`ps -ef |grep shardingsphere-ui.jar |grep -v 'grep' |awk '{print $2}'`

if [ -n $pid ];then
   echo "pid=${pid}"
   kill $pid
   sleep 3
fi

nohup java -server -Xms512m -Xmx512m -jar shardingsphere-ui.jar > shardingsphere-ui-nohup.log 2>&1 &
